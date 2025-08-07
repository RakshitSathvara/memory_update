package com.narmada.measure.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.narmada.measure.BuildConfig
import com.narmada.measure.screens.admin_user.admin_dashboard.model.AddFaceDataResponse
import com.narmada.measure.screens.attendance.model.AttendanceHistoryResponse
import com.narmada.measure.screens.badeli_serdi_report.model.BadeliSerdiReportRequest
import com.narmada.measure.screens.badeli_serdi_report.model.BadeliSerdiReportResponse
import com.narmada.measure.screens.bareliserdi.model.AddBareliSerdiResponse
import com.narmada.measure.screens.bareliserdi.model.CommonDataResponse
import com.narmada.measure.screens.bareliserdi.model.GetFormNumberRequest
import com.narmada.measure.screens.bareliserdi.model.GetFormNumberResponse
import com.narmada.measure.screens.bareliserdi.model.PaniBandhDetailRequest
import com.narmada.measure.screens.bareliserdi.model.PaniBandhDetailResponse
import com.narmada.measure.screens.dashboard.model.CommonResponse
import com.narmada.measure.screens.dashboard.model.YearListResponse
import com.narmada.measure.screens.kapni_complete_report.model.KapniCompleteReportResponse
import com.narmada.measure.screens.kapni_complete_report.model.KhetarCodeResponse
import com.narmada.measure.screens.kapni_complete_report.model.WeightListResponse
import com.narmada.measure.screens.kapni_complete_report_view.model.KapniPuriMahitiReportDownloadResponse
import com.narmada.measure.screens.kapni_complete_report_view.model.KapniPuriThayaniMahitiListResponse
import com.narmada.measure.screens.kapni_supervisor.chalan_history.model.DeliveryChalanHistoryResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.AddDeliveryChalanRequest
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.AddDeliveryChalanResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.CaneTypeListResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.KapniPaniBandhDetailResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.MukadamNumberListResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.VahanNumberListResponse
import com.narmada.measure.screens.khetar_mapni_report.model.KhetarMapniReportRequest
import com.narmada.measure.screens.khetar_mapni_report.model.KhetarMapniReportResponse
import com.narmada.measure.screens.khetarmapni.model.AccountMemberRequest
import com.narmada.measure.screens.khetarmapni.model.AccountMemberResponse
import com.narmada.measure.screens.khetarmapni.model.CheckComputerCodeRequest
import com.narmada.measure.screens.khetarmapni.model.CheckComputerCodeResponse
import com.narmada.measure.screens.khetarmapni.model.CommonApiResponse
import com.narmada.measure.screens.khetarmapni.model.SearchSabhaSadResponse
import com.narmada.measure.screens.khetarmapni.model.SubmitKhetarMapniResponse
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.VillageListResponse
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerListResponse
import com.narmada.measure.screens.login.model.LoginRequest
import com.narmada.measure.screens.login.model.LoginResponse
import com.narmada.measure.screens.login.model.SupervisorFaceResponse
import com.narmada.measure.screens.pani_bandh_register.model.FarmMeasurementReportRequest
import com.narmada.measure.screens.pani_bandh_register.model.FarmMeasurementReportResponse
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterListResponse
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterRequest
import com.narmada.measure.screens.pani_bandh_register.model.PaniBandhRegisterResponse
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneListResponse
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiListResponse
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiRequest
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiResponse
import com.narmada.measure.screens.pani_bandh_yaadi.model.RopanYearDates
import com.narmada.measure.screens.ropan_register_report.model.RopanRegisterReportRequest
import com.narmada.measure.screens.ropan_register_report.model.RopanRegisterReportResponse
import com.narmada.measure.screens.sabhasad_mahiti.model.SabhasadMahitiResponse
import com.narmada.measure.screens.sabhasad_mahiti_update.model.MemberDetailByCodeRequest
import com.narmada.measure.screens.sabhasad_mahiti_update.model.MemberDetailByCodeResponse
import com.narmada.measure.screens.sabhasad_mahiti_update.model.SabhasadMahitiUpdateRequest
import com.narmada.measure.screens.sabhasad_mahiti_update.model.SabhasadMahitiUpdateResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.time.Year
import java.util.concurrent.TimeUnit


/**
 * Created by SUMIT R@THOD on 22-05-2021.
 */

interface RetrofitService {

    @POST("authofficer/login")
    suspend fun login(@Body jsonObject: LoginRequest): Response<LoginResponse>

    @POST("authofficer/logout")
    suspend fun logout(): Response<CommonResponse>

    @FormUrlEncoded
//    @POST("common/yearList")
    @POST("common/years")
    suspend fun yearList(@Field("currentAppVersion") appVersion: String): Response<YearListResponse>

//    @GET("common/zoneOfficerList")
    @GET("officer/supervisor-list")
    suspend fun zoneOfficerList(): Response<ZoneOfficerListResponse>

//    @GET("common/supervisorZoneList")
    @GET("officer/{supervisor_id}/zones")
    suspend fun supervisorZoneList(@Path("supervisor_id") text: String): Response<SupervisorZoneListResponse>

//    @GET("common/list")
    @GET("common/list")
    suspend fun commonApi(@Query("year") year: String): Response<CommonApiResponse>

//    @GET("common/getAccountMemberListByName")
    @GET("farmer/get-list-by-name")
    suspend fun searchSabhaSad(@Query("name") text: String): Response<SearchSabhaSadResponse>

//    @POST("farmmeasurement/checkComputerCodeExist")
    @POST("farm-measurement/check-computer-code-exist")
    suspend fun checkComputerCode(@Body jsonObject: CheckComputerCodeRequest): Response<CheckComputerCodeResponse>

//    @POST("common/villageMasterList")
    @POST("common/villageMasterList")
    suspend fun villageList(@Body jsonObject: VillageListRequest): Response<VillageListResponse>

//    @POST("user/accountMemberDetail")
    @POST("farmer/farmer-detail")
    suspend fun accountMemberDetail(@Body jsonObject: AccountMemberRequest): Response<AccountMemberResponse>

    @POST("burnedcane/paniBandhDetail")
    suspend fun paniBandhDetail(@Body requestModel: PaniBandhDetailRequest): Response<PaniBandhDetailResponse>

    @POST("burnedcane/getFormNumber")
    suspend fun getFormNumber(@Body requestModel: GetFormNumberRequest): Response<GetFormNumberResponse>

    @Multipart
    @POST("burnedcane/addBurnedCaneData")
    suspend fun addBurnedCaneData(
        @PartMap params: HashMap<String, RequestBody>,
        @Part mapImage: MultipartBody.Part
    ): Response<AddBareliSerdiResponse>

    @GET("common/list")
    suspend fun getCommonData(): Response<CommonDataResponse>

    @POST("panibandh/generatePaniBandhRegisterReportBySuperVisor")
    suspend fun paniBandhRegisterReport(@Body requestModel: PaniBandhRegisterRequest): Response<PaniBandhRegisterResponse>

    @GET("panibandh/getPaniBandhRegisterReport")
    suspend fun getPaniBandhRegisterReportList(@QueryMap requestModel: HashMap<String, String>): Response<PaniBandhRegisterListResponse>

    @GET("panibandh/getRopanYearWiseDates")
    suspend fun getRopanYearWiseDates(@Query("Working_Year") text: String): Response<RopanYearDates>

    @POST("panibandh/generatePaniBandhListReport")
    suspend fun generatePaniBandhYaadiReport(@Body requestModel: PaniBandhYaadiRequest): Response<PaniBandhYaadiResponse>

    @GET("panibandh/getPaniBandhList")
    suspend fun getPaniBandhYaadiReportList(@QueryMap requestModel: HashMap<String, String>): Response<PaniBandhYaadiListResponse>

    @POST("farmmeasurement/generateFarmMeasurementReport")
    suspend fun generateFarmMeasurementReport(@Body requestModel: FarmMeasurementReportRequest): Response<FarmMeasurementReportResponse>

    @POST("burnedcane/generateBadeliSherdiSupervisorReport")
    suspend fun badeliSerdiReport(@Body requestModel: BadeliSerdiReportRequest): Response<BadeliSerdiReportResponse>

    @POST("farmmeasurement/getFarmMeasurementReportByVillage")
    suspend fun khetarMapniReport(@Body requestModel: KhetarMapniReportRequest): Response<KhetarMapniReportResponse>

    @POST("panibandh/generateRopanRegisterReport")
    suspend fun ropanRegisterReport(@Body requestModel: RopanRegisterReportRequest): Response<RopanRegisterReportResponse>

    @GET("common/generateAccountMemberReportByVillage")
    suspend fun sabhasadMahitiReport(@Query("village_id") text: String): Response<SabhasadMahitiResponse>

//    @POST("common/getAccountMemberDetailByCode")
    @GET("farmer/get-detail-by-code")
    suspend fun getMemberDetailByCode(@Query("farmer_id") farmerId : String): Response<MemberDetailByCodeResponse>

//    @POST("common/updateAccountMemberDetail")
    @POST("farmer/update-detail")
    suspend fun updateMemberDetailByCode(@Body requestModel: SabhasadMahitiUpdateRequest): Response<SabhasadMahitiUpdateResponse>

    @GET("weight/kheterList")
    suspend fun khetarCodeList(@QueryMap requestModel: HashMap<String, String>): Response<KhetarCodeResponse>

    @GET("weight/SupervisorWeightList")
    suspend fun supervisorWeightList(@QueryMap requestModel: HashMap<String, String>): Response<WeightListResponse>

    @Multipart
    @POST("caneutaradata/add")
    suspend fun addKapniCompleteReport(
        @PartMap params: HashMap<String, RequestBody>,
        @Part faceImage: MultipartBody.Part,
    ): Response<KapniCompleteReportResponse>

    @GET("caneutaradata/list")
    suspend fun kapniCompleteReportList(@QueryMap requestModel: HashMap<String, String>): Response<KapniPuriThayaniMahitiListResponse>

    @GET("caneutaradata/download")
    suspend fun kapniCompleteReportDownload(@QueryMap requestModel: HashMap<String, String>): Response<KapniPuriMahitiReportDownloadResponse>

    @GET("common/getCaneTypeList")
    suspend fun getCaneTypeList(): Response<CaneTypeListResponse>

    @GET("panibandh/kapniSupervisorPaniBandhDetail")
    suspend fun kapniSupervisorPaniBandhDetail(@QueryMap requestModel: HashMap<String, String>): Response<KapniPaniBandhDetailResponse>

    @GET("transport/list")
    suspend fun getVahanNumberList(@QueryMap requestModel: HashMap<String, String>): Response<VahanNumberListResponse>

    @GET("mukadam/list")
    suspend fun getMukadamNumberList(@QueryMap requestModel: HashMap<String, String>): Response<MukadamNumberListResponse>

    @POST("canedeliverychallan/add")
    suspend fun addCaneDeliveryChalan(@Body requestModel: AddDeliveryChalanRequest): Response<AddDeliveryChalanResponse>

    @GET("canedeliverychallan/list")
    suspend fun getDeliveryChalanList(@QueryMap requestModel: HashMap<String, String>): Response<DeliveryChalanHistoryResponse>

    @Multipart
    @POST("farmmeasurement/addRopanNondh")
    suspend fun addKhetarMapni(
        @PartMap params: HashMap<String, RequestBody>,
        @Part mapImage: MultipartBody.Part,
        @Part khetarImage: MultipartBody.Part,
    ): Response<SubmitKhetarMapniResponse>

    @FormUrlEncoded
    @POST("attendance/list")
    suspend fun getAttendanceHistory(
        @Field("from_date") fromDate: String,
        @Field("to_date") toDate: String
    ): Response<AttendanceHistoryResponse>

    @Multipart
//    @POST("attendance/add")
    @POST("attendance")
    suspend fun addAttendance(
        @PartMap params: HashMap<String, RequestBody>,
        @Part profileImage: MultipartBody.Part
    ): Response<CommonResponse>

    @Multipart
    @POST("faceregistration/add")
    suspend fun addSupervisorFace(
        @Part("supervisor_id") supervisorId: RequestBody,
        @Part("face_data") faceData: RequestBody,
        @Part supervisorImage: MultipartBody.Part,
        @HeaderMap headers: HashMap<String, String>

    ): Response<AddFaceDataResponse>


    @DELETE("faceregistration/delete")
    suspend fun deleteSupervisorFace(
        @Query("supervisor_id") supervisorId: String,
        @HeaderMap headers: HashMap<String, String>
    ): Response<CommonResponse>

    //    @GET("faceregistration/view")
    @GET("face-registration/{supervisor_id}")
    suspend fun getSupervisorFace(
        @Path("supervisor_id") supervisorId: String
    ): Response<SupervisorFaceResponse>

    companion object {
        private var retrofitService: RetrofitService? = null
        private var retrofitAdminService: RetrofitService? = null
        private var gson: Gson? = null

        //Create the Retrofit service instance using the retrofit.
        fun getInstance(): RetrofitService? {
            if (gson == null)
                gson = GsonBuilder()
                    .setLenient()
                    .create()

            val interceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                interceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build()

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.base_url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson!!))
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService
        }

        //Create the Retrofit service instance using the retrofit.
        fun getAdminInstance(): RetrofitService? {
            if (gson == null)
                gson = GsonBuilder()
                    .setLenient()
                    .create()

            val interceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                interceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(HeaderAdminInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build()

            if (retrofitAdminService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.base_url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson!!))
                    .build()
                retrofitAdminService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitAdminService
        }
    }

}