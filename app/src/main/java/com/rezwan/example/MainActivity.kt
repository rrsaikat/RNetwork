package com.rezwan.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rezwan.knetworklib.KNetwork
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), KNetwork.OnNetWorkConnectivityListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mtoolbar)

        KNetwork.bind(this, lifecycle).init(this)
                .showKNDialog(false)
                .setConnectivityListener(this)
    }

    override fun onConnected() {
        Log.e("main", "connected")
    }

    override fun onDisConnected() {
        Log.e("main", "disconnected")
    }
}
