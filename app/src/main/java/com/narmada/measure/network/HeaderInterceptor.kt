package com.narmada.measure.network

import com.narmada.measure.utils.SharedPreferenceUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response
    {
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Device-Id", Constants.DEVICE_ID)
            .addHeader("Referer", "https://memberappapi.narmadasugar.com/")
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                "Bearer " + SharedPreferenceUtil.getString(Constants.TOKEN, "")
            )
            .build()
        return chain.proceed(request)
    }
}