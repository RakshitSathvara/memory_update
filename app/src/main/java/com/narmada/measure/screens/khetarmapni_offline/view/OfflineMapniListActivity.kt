package com.narmada.measure.screens.khetarmapni_offline.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.narmada.measure.R
import com.narmada.measure.databinding.ActivityOfflineMapniListBinding
import com.narmada.measure.network.Constants
import com.narmada.measure.room.AppDatabase
import com.narmada.measure.room.entity.OfflineMapni
import com.narmada.measure.screens.dashboard.model.YearIntentModel
import com.narmada.measure.screens.khetarmapni_offline.view.adapter.MapniListAdapter
import com.narmada.measure.screens.khetarmapni_offline.viewmodel.KhetarMapniOfflineRepository
import com.narmada.measure.screens.khetarmapni_offline.viewmodel.KhetarMapniOfflineViewModel
import com.narmada.measure.screens.khetarmapni_offline.viewmodel.KhetarMapniOfflineViewModelFactory
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.Const.getSerializable
import com.narmada.measure.utils.LoadingDialog
import com.narmada.measure.utils.SharedPreferenceUtil


class OfflineMapniListActivity : AppCompatActivity(), View.OnClickListener,
    MapniListAdapter.onClick {

    private val binding by lazy { ActivityOfflineMapniListBinding.inflate(layoutInflater) }
    private val toolbarBinding by lazy { binding.toolbar }
    private val dialog: LoadingDialog by lazy { LoadingDialog(this) }
    private val roomService by lazy { AppDatabase.getInstance(applicationContext) }
    private lateinit var viewModel: KhetarMapniOfflineViewModel
    private var offlineMapniList: MutableList<OfflineMapni> = arrayListOf()
    private var mapniListAdapter: MapniListAdapter? = null
    private var position: Int? = null
    var yearIntentModel: YearIntentModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        yearIntentModel = intent.getSerializable("yearData", YearIntentModel::class.java)

        toolbarBinding.txtTitle.setText(R.string.offline_khetar_mapni)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerMapni.layoutManager = layoutManager
        mapniListAdapter = MapniListAdapter(offlineMapniList, this)
        binding.recyclerMapni.adapter = mapniListAdapter
        viewClickListener()
        setupViewModel()
    }

    private fun viewClickListener() {
        toolbarBinding.ivBack.setOnClickListener(this)
        binding.cardNew.setOnClickListener(this)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            KhetarMapniOfflineViewModelFactory(
                KhetarMapniOfflineRepository(roomService)
            )
        )[KhetarMapniOfflineViewModel::class.java]

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

        viewModel.mapniList.observe(this) {
            offlineMapniList.clear()
            if (it.isNotEmpty()) {
                binding.tvNoHistory.visibility = View.GONE
                binding.linearTitle.visibility = View.VISIBLE
                offlineMapniList.addAll(it)
            } else {
                binding.linearTitle.visibility = View.GONE
                binding.tvNoHistory.visibility = View.VISIBLE
            }
            mapniListAdapter?.notifyDataSetChanged()

            //Copy large .mbtiles to files directory on first load...
            viewModel.loadMBTilesToCacheDir(this)
        }

        viewModel.deleteOfflineMapni.observe(this) {
            Const.showSnackBar(this, getString(R.string.mapni_form_delete_successfully))
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {
            toolbarBinding.ivBack.id -> {
                finish()
            }

            binding.cardNew.id -> {
                val intent = Intent(this, KhetarMapniOfflineActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showDeleteDialog(offlineMapni: OfflineMapni) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_delete)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val relativeContinue = dialog.findViewById(R.id.relative_continue) as RelativeLayout
        val imgClose = dialog.findViewById(R.id.img_close) as ImageView
        val tvBack = dialog.findViewById(R.id.txt_view) as TextView

        relativeContinue.setOnClickListener {
            dialog.dismiss()
            viewModel.deleteOfflineMapni(offlineMapni)
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        tvBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDeleteClick(position: Int, offlineMapni: OfflineMapni) {
        this.position = position
        showDeleteDialog(offlineMapni)
    }

    override fun onEditClick(position: Int, offlineMapni: OfflineMapni) {
        if (SharedPreferenceUtil.getBoolean(Constants.IS_LOGIN, false)) {
            if (Const.isInternetAvailable(this)) {
                if (yearIntentModel!!.years!!.isNotEmpty()) {
                    val intent = Intent(this, KhetarMapniSyncActivity::class.java)
                    intent.putExtra("data", offlineMapni)
                    intent.putExtra("yearData", yearIntentModel)
                    startActivity(intent)
                } else {
                    Const.showSnackBar(this, getString(R.string.year_list_not_found))
                }
            } else {
                Const.showSnackBar(this, getString(R.string.check_internet_connection))
            }
        } else {
            Const.showSnackBar(this, getString(R.string.please_login))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllMapni()
    }
}