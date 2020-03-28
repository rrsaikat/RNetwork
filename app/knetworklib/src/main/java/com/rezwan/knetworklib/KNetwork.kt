package com.rezwan.knetworklib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import de.keyboardsurfer.android.widget.crouton.Configuration
import de.keyboardsurfer.android.widget.crouton.Crouton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


object KNetwork {
    @SuppressLint("StaticFieldLeak") // application context is safe
    private var context: Context? = null

    /**
     * Initialize KNetwork. This should be done in the application class.
     */
    fun initialize(context: Context) {
        KNetwork.context = context.applicationContext
    }

    /**
     * Create a new request to get connectivity information about a device.
     *
     * @param context the application context
     * @return a new Request instance.
     */
    fun bind(context: Context, lifecycle: Lifecycle): Request {
        return Request(context.applicationContext, lifecycle)
    }

    class Request(context: Context, lifecycle: Lifecycle) : LifecycleObserver {
        var context: Context
        var mlayoutResId: Int = 0
        var mShowDialog:Boolean = false
        lateinit var activity: Activity
        val disposable = CompositeDisposable()

        var knDialog:KNDialog? = null
        var mOnNetWorkConnectivityListener: OnNetWorkConnectivityListener? = null
        var isSuccessShown: Boolean = false

        val CONFIGURATION_INFINITE = Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE)
                .build()
        val CONFIGURATION_SHORT = Configuration.Builder()
                .setDuration(Configuration.DURATION_SHORT)
                .build()

        init {
            this.context = context
            lifecycle.addObserver(this)
        }

        fun init(activity: Activity):Request{
            this.activity = activity
            this.knDialog = KNDialog(activity)
            return this
        }

        fun showKNDialog(isShow:Boolean):Request{
            this.mShowDialog = isShow
            return this
        }


        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        private fun onActivityCreate() {

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onActivityStart() {
            Log.e("KNetWork", "onActivityStart" + isSuccessShown)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        private fun onActivityResume() {
            isSuccessShown = false
            start()
            Log.e("KNetWork", "onActivityResume" + isSuccessShown)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        private fun onActivityPause() {
            Log.e("KNetWork", "onActivityPause")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        private fun onActivityStop() {
            clearDisposable()
            Log.e("KNetWork", "onActivityStop")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        private fun onActivityDestroy() {
            clearAll()
            Log.e("KNetWork", "onActivityDestroy")
        }

        fun setConnectivityListener(onNetWorkConnectivityListener: OnNetWorkConnectivityListener): Request {
            this.mOnNetWorkConnectivityListener = onNetWorkConnectivityListener
            return this
        }

        fun start(): Request {
            disposable.clear()
            disposable.add(
                    ReactiveNetwork
                            .observeNetworkConnectivity(context)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { connectivity: Connectivity? ->
                                checkConnectivity(connectivity)
                            })

            return this
        }


        fun clearAll(): Request {
            clearDisposable()
            clearStackkedViews()
            return this
        }

        fun clearDisposable(): Request{
            disposable.clear()
            return this
        }

        fun clearStackkedViews(): Request {
            Crouton.cancelAllCroutons()
            Crouton.clearCroutonsForActivity(activity)
            return this
        }

        fun stop(): Request {
            disposable.dispose()
            return this
        }

        fun checkConnectivity(connectivity: Connectivity?): Request {
            clearStackkedViews()
            connectivity?.let { conn ->
                if (conn.available()) {
                    Log.e("KNetWork", "conn.available()" + isSuccessShown)
                    if (isSuccessShown){
                        mOnNetWorkConnectivityListener?.onConnected()
                        showDialog(false)
                        showSuccessView()
                    }

                    isSuccessShown = true

                } else {
                    showErrorView()
                    showDialog(mShowDialog)
                    mOnNetWorkConnectivityListener?.onDisConnected()
                    isSuccessShown = true
                }
            }
            return this
        }

        private fun showDialog(show:Boolean){
            if (show){
                knDialog?.show()
            }else{
                knDialog?.hide()
            }
        }


        private fun showSuccessView(): Request {
            val c = Crouton.make(activity, activity.layoutInflater.inflate(R.layout.crouton_success_layout, null))
            c.setConfiguration(CONFIGURATION_SHORT)
            c.show()
            return this
        }

        private fun showErrorView(): Request {
            val c = Crouton.make(activity, activity.layoutInflater.inflate(R.layout.crouton_error_layout, null))
            c.setConfiguration(CONFIGURATION_INFINITE)
            c.show()
            return this
        }

        private fun getLayoutRes(layoutType: LayoutType): View {
            when (layoutType) {
                LayoutType.SUCCRESS() -> {
                    return activity.layoutInflater.inflate(R.layout.crouton_success_layout, null)
                }

                LayoutType.ERROR() -> {
                    return activity.layoutInflater.inflate(R.layout.crouton_error_layout, null)
                }
            }

            return getLayoutRes(LayoutType.SUCCRESS())
        }

    }

    internal sealed class LayoutType {
        class SUCCRESS : LayoutType()
        class ERROR : LayoutType()
    }


    interface OnNetWorkConnectivityListener {
        fun onConnected()
        fun onDisConnected()
    }

    @SuppressLint("PrivateApi")
    private fun context(): Context? {
        if (context != null) return context

        // We didn't use to require holding onto the application context so let's cheat a little.
        try {
            return Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null, null as Array<Any?>?) as Application
        } catch (ignored: Exception) {
        }

        // Last attempt at hackery
        try {
            return Class.forName("android.app.AppGlobals")
                    .getMethod("getInitialApplication")
                    .invoke(null, null as Array<Any?>?) as Application
        } catch (ignored: Exception) {
        }
        throw RuntimeException("DeviceName must be initialized before usage.")
    }
}