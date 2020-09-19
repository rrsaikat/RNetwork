package com.rezwan.example

import android.app.Application
import com.rezwan.knetworklib.KNetwork

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        KNetwork.initialize(this) //Must be initialized
    }
}