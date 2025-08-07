package com.narmada.measure.screens.pani_bandh_register.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.narmada.measure.BuildConfig
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityPaniBandhRegisterListBinding
import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.pani_bandh_register.model.FarmMeasurementReportRequest
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhData
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterIntent
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterListRequest
import com.narmada.measure.screens.pani_bandh_register.view.adapter.PaniBandhRegisterListAdapter
import com.narmada.measure.screens.pani_bandh_register.viewmodel.PaniBandhRegisterListViewModel
import com.narmada.measure.screens.pani_bandh_register.viewmodel.PaniBandhRegisterRepository
import com.narmada.measure.screens.pani_bandh_register.viewmodel.PaniBandhRegisterViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.LoadingDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class PaniBandhRegisterListActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityPaniBandhRegisterListBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }

    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val retrofitService by lazy { RetrofitService.getInstance() }
    private lateinit var viewModel: PaniBandhRegisterListViewModel

    private lateinit var paniBandhIntent: PaniBandhRegisterIntent

    var downloadID = 0

    private lateinit var paniBandhYaadiListAdapter: PaniBandhRegisterListAdapter
    private val yaadiList = ArrayList<PaniBandhData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarBinding.txtTitle.setText(R.string.pani_bandh_register)
        toolbarBinding.ivBack.setOnClickListener(this)

        paniBandhIntent = intent.getSerializable("intent", PaniBandhRegisterIntent::class.java)

        binding.recyclerInfo.layoutManager = LinearLayoutManager(this)
        paniBandhYaadiListAdapter = PaniBandhRegisterListAdapter(
            paniBandhIntent.village.villageId ?: "",
            paniBandhIntent.village.villageName ?: "",
            yaadiList
        ) { paniBandhData ->
            val farmMeasurementReportRequest = FarmMeasurementReportRequest(
                working_year = paniBandhIntent.workingYear,
                computer_code = paniBandhData.computerCode,
            )
            viewModel.generateFarmMeasurementReportApi(farmMeasurementReportRequest)
        }
        binding.recyclerInfo.adapter = paniBandhYaadiListAdapter

        setupViewModel()
        getPaniBandhListApi()

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->

            if (scrollY >= v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if(!viewModel.isLoading && !viewModel.isLastPage) {
                    viewModel.currentPage++
                    getPaniBandhListApi()
                }

            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupViewModel() {
        binding.etFromDate.setText(dateFormatDynamic(paniBandhIntent.fromDate, dateApi, dateDisplay))
        binding.etToDate.setText(dateFormatDynamic(paniBandhIntent.toDate, dateApi, dateDisplay))
        binding.tvSupervisor.text = paniBandhIntent.supervisorName
        binding.tvZone.text = paniBandhIntent.zone.zoneName
        binding.tvLaamRopan.text = paniBandhIntent.mapniType.name
        binding.tvVillage.text = "${paniBandhIntent.village.villageId ?: ""} / ${paniBandhIntent.village.villageName ?: ""}"
        binding.tvKhetarCode.text = paniBandhIntent.khetarCode
        binding.tvSabhasadCode.text = paniBandhIntent.sabhasadCode

        if(paniBandhIntent.khetarCode.isEmpty()) binding.layoutKhetarCode.visibility = View.GONE
        if(paniBandhIntent.sabhasadCode.isEmpty()) binding.layoutSabhasadCode.visibility = View.GONE

        viewModel = ViewModelProvider(this, PaniBandhRegisterViewModelFactory(PaniBandhRegisterRepository(retrofitService!!)))[PaniBandhRegisterListViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            Const.showSnackBar(this, it)
        }

        viewModel.errorIntMessage.observe(this) {
            Const.showSnackBar(this, getString(it))
        }

        viewModel.progressObservable.observe(this) {
            if (it == true) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

        viewModel.paginationProgressObservable.observe(this) {
            if (it == true) {
                binding.progressbar.visibility = View.VISIBLE
//                binding.scrollView.scrollTo(0, binding.scrollView.bottom)
            } else {
                binding.progressbar.visibility = View.GONE
                binding.scrollView.scrollTo(0, binding.scrollView.scrollY)
            }
        }

        viewModel.paniBandhRegisterListResponse.observe(this) {
            try {

                if(it.data?.villageData != null) {
                    binding.tvPilanSeason.text = it.data?.villageData?.pilanSeason
                    binding.tvRopanSeason.text = it.data?.villageData?.ropanSeason
                }

                yaadiList.addAll(it.data?.paniBandhData ?: arrayListOf())
                paniBandhYaadiListAdapter.notifyDataSetChanged()

                if(yaadiList.isEmpty()) {
                    binding.layoutNoHistory.visibility = View.VISIBLE
                } else {
                    binding.layoutNoHistory.visibility = View.GONE
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

        viewModel.farmMeasurementReportResponse.observe(this) {
            try {
                it.url?.let { url -> downloading(url) } ?: Const.showSnackBar(this, it.message ?: getString(R.string.something_went_wrong))
            } catch (e: Exception) {
                e.printStackTrace()
                Const.showSnackBar(this, getString(R.string.something_went_wrong))
            }
        }

    }

    private fun getPaniBandhListApi() {

        val reportListRequest = PaniBandhRegisterListRequest(
            working_year = paniBandhIntent.workingYear,
            from_date = paniBandhIntent.fromDate,
            to_date = paniBandhIntent.toDate,
            supervisor_id = paniBandhIntent.supervisorId,
            zone_id = paniBandhIntent.zone.zoneId.toString(),
            village_id = paniBandhIntent.village.villageId.toString(),
            mapni_type = paniBandhIntent.mapniType.id?.toString() ?: "",
            computer_code = paniBandhIntent.khetarCode,
            sabhasad_code = paniBandhIntent.sabhasadCode
        )

        viewModel.getPaniBandhRegisterListApi(reportListRequest)
    }

    private val dateDisplay = "dd-MM-yyyy"
    private val dateApi = "yyyy-MM-dd"

    private fun dateFormatDynamic(dateString: String?, fromFormat: String, toFormat: String): String {
        if (dateString == null) return ""
        try {
            val from = SimpleDateFormat(fromFormat, Locale.US)
            val date = from.parse(dateString)
            val to = SimpleDateFormat(toFormat, Locale.US)
            val result = to.format(date)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            toolbarBinding.ivBack.id -> {
                finish()
            }

        }
    }

    private fun downloading(fileUrl: String) {
        var downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        downloadFolder = File(downloadFolder.path, "${getString(R.string.app_name)}/${getString(R.string.farm_measure_guj)}")
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs()
        }

        val fileName = getFileNameFromUri(fileUrl)
        downloadID = PRDownloader.download(fileUrl, downloadFolder.path, fileName)
            .build()
            .setOnStartOrResumeListener {
//                Const.showToast(this@PaniBandhRegisterActivity, getString(R.string.please_wait))
                dialog.show()
            }
            .setOnProgressListener { _ -> // getting the progress of download
//                val progressPer = progress.currentBytes * 100 / progress.totalBytes
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    dialog.dismiss()
                    openPDF(downloadFolder.path, fileName)
                    MediaScannerConnection.scanFile(this@PaniBandhRegisterListActivity, arrayOf(downloadFolder.path), null, null)
                }

                override fun onError(error: com.downloader.Error?) {
                    dialog.dismiss()
                    downloadID = 0
                    Const.showToast(this@PaniBandhRegisterListActivity, getString(R.string.something_went_wrong))
                }
            })
    }

    fun openPDF(path: String, name: String) {
        val newIntent = Intent(Intent.ACTION_VIEW)
        newIntent.data = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider", File("$path/$name")
        )
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(newIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileNameFromUri(url: String): String {
        return url.substring(url.lastIndexOf('/') + 1, url.length)
    }

}