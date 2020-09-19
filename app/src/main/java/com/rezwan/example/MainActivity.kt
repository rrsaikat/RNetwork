package com.rezwan.example

import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.rezwan.knetworklib.KNetwork
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), KNetwork.OnNetWorkConnectivityListener {
    lateinit var wifimanager: WifiManager
    lateinit var knRequest: KNetwork.Request


    /*
    *
    * In this example we take wifi connection to show its network status by KNetwork Library
    * Let's get started.
    *
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()

        /*
        *
        *  Initial behaviour for Network status view shows into top of any layouts.
        *
        *  STEP 1: KNetwork.initialize(this) - must declare this into Application.
        *  STEP 2: KNetwork.bind(this, lifecycle) - bind the targeted activity in which you want to show network status.
        *
        *
        *  Available additinal features:
        *
        *  showKNDialog() - set true for show dialog when net connection goes off.
        *  setConnectivityListener() - connected, disconnected callback into activity
        *  setInAnimation() - custom animation setup
        *  setOutAnimation() - custom animation setup
        *  setViewGroupResId() - targeted viewgroup to show network status views.
        *
        */
        knRequest = KNetwork.bind(this, lifecycle)
                .showKNDialog(false)
                .setConnectivityListener(this)


        wifimanager = getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager
        switchWifi.isChecked = wifimanager.isWifiEnabled


        /*
        *
        *  Google has limited the wifi action request.
        *  Turn on/off for wifi may not work for sometimes.
        *  But will work after 2 minutes when on/off requested from app.
        *  See more details at https://developer.android.com/guide/topics/connectivity/wifi-scan
        *
        *
        */
        switchWifi.setOnCheckedChangeListener { buttonView, isChecked ->
            internetOnOffAction(buttonView, isChecked)
        }

        checkBoxTop.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxBottom.isChecked = false

                //for showing network status view below the toolbar
                knRequest.setInAnimation(R.anim.top_in)
                        .setOutAnimation(R.anim.top_out)
                        .setViewGroupResId(R.id.crouton_top)
            }

        }
        checkBoxBottom.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxTop.isChecked = false

                //for showing network status view bottom of the bottombar
                knRequest.setInAnimation(R.anim.bottom_in)
                        .setOutAnimation(R.anim.bottom_out)
                        .setViewGroupResId(R.id.crouton_bottom)
            }
        }
    }

    private fun initToolbar() {
        with(mtoolbar) {
            setSupportActionBar(this)
            setTitleTextColor(Color.WHITE)
            setSubtitleTextColor(Color.WHITE)
            title = "RNetwork"
            //subtitle = "Sharing is caring"
        }
    }

    private fun internetOnOffAction(buttonView: CompoundButton, checked: Boolean) {
        if (checked) {
            enableWifi()
            buttonView.text = "TURN OFF WIFI"
        } else {
            disableWifi()
            buttonView.text = "TURN ON WIFI"
        }
    }

    /*
    *
    *  Note :
    *  @method setWifiEnabled is deprecated, will remove soon after android P
    *
    */
    @Suppress("DEPRECATION")
    private fun enableWifi() {
        with(wifimanager) {
            isWifiEnabled = true
        }
    }

    /*
     *
     *  Note :
     *  @method setWifiEnabled is deprecated, will remove soon after android P
     *
     */
    @Suppress("DEPRECATION")
    private fun disableWifi() {
        with(wifimanager) {
            isWifiEnabled = false
        }
    }

    override fun onNetConnected() {
        Log.e("main", "connected")
    }

    override fun onNetDisConnected() {
        Log.e("main", "disconnected")
    }
}
