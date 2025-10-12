package com.who_summoned_the_cloud.eromoro.app

import android.app.Application
import com.naver.maps.map.NaverMapSdk

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NCP_KEY_ID)
    }
}