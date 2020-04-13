package com.rezwan.knetworklib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
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
    private var activity: Activity? = null
    private var lifecycle: Lifecycle? = null


    /**
     * Initialize KNetwork. This should be done in the application class.
     */
    fun initialize(context: Context) {
        KNetwork.context = context.applicationContext
    }

    /**
     * Create a new request to get connectivity information about a device.
     *
     * @param activity the network binding activity
     * @param lifecycle the activity lifecycle
     * @return a new Request instance.
     */
    fun bind(activity: Activity, lifecycle: Lifecycle): Request {
        this.activity = activity
        this.lifecycle = lifecycle
        return Request()
    }


    /*
    *
    *  NOTE: Initial behaviour for Network status view shows into top of any layouts.
    *
    *  SETUP MANUAL -
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

    class Request : LifecycleObserver {
        private var viewGroupResId: Int = 0
        private var mShowDialog: Boolean = false
        private var croutonSuccess: Crouton? = null
        private var croutonError: Crouton? = null
        private var successLayout: Int = R.layout.crouton_success_layout
        private var errorLayout: Int = R.layout.crouton_error_layout
        private var animIn = R.anim.top_in
        private var animOut = R.anim.top_out

        private var netWorkType: NetWorkType

        private var knDialog: KNDialog? = null
        private var mOnNetWorkConnectivityListener: OnNetWorkConnectivityListener? = null
        private var isSuccessShown: Boolean = false

        private val disposable = CompositeDisposable()
        private val configuration = Configuration.Builder().setInAnimation(animIn).setOutAnimation(animOut)

        private val CONFIGURATION_INFINITE = configuration
                .setDuration(Configuration.DURATION_INFINITE)
                .build()
        private val CONFIGURATION_SHORT = configuration
                .setDuration(Configuration.DURATION_SHORT)
                .build()

        init {
            this.netWorkType = NetWorkType.ALL()
            this.knDialog = KNDialog(activity!!)
            initListener()
            lifecycle?.addObserver(this)
        }

        protected fun initListener() {
            croutonError?.setOnClickListener {
                activity?.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            }
        }


        fun showKNDialog(isShow: Boolean): Request {
            this.mShowDialog = isShow
            return this
        }

        fun setViewGroupResId(@IdRes viewGroupResId: Int): Request {
            this.viewGroupResId = viewGroupResId
            return this
        }

        private fun setSuccessLayout(@LayoutRes layoutResId: Int): Request {
            this.successLayout = layoutResId
            return this
        }

        private fun setErrorLayout(@LayoutRes layoutResId: Int): Request {
            this.errorLayout = layoutResId
            return this
        }

        /*fun setNetWorkStatusType(netWorkType: NetWorkType): Request {
            this.netWorkType = netWorkType
            return this
        }*/

        fun setInAnimation(@AnimRes animresId: Int): Request {
            this.configuration.setInAnimation(animresId)
            return this
        }

        fun setOutAnimation(@AnimRes animresId: Int): Request {
            this.configuration.setOutAnimation(animresId)
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
            stop()
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
                                connectivity?.let {
                                    onConnectivityChanged(connectivity)
                                }
                            })

            return this
        }


        fun clearAll(): Request {
            clearDisposable()
            clearStackkedViews()
            return this
        }

        fun clearDisposable(): Request {
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

        fun onConnectivityChanged(connectivity: Connectivity): Request {
            //clearStackkedViews()
            croutonError?.hide()
            croutonSuccess?.hide()
            checkConnectivity(connectivity)

            return this
        }

        fun checkConnectivity(conn: Connectivity): Request {
            Handler().postDelayed(Runnable {
                if (conn.available()) {
                    Log.e("KNetWork", "conn.available()" + isSuccessShown)
                    mOnNetWorkConnectivityListener?.onNetConnected()
                    if (isSuccessShown) {
                        showAutoDialog(false)
                        showSuccessView()
                    }

                    isSuccessShown = true

                } else {
                    showErrorView()
                    showAutoDialog(mShowDialog)
                    mOnNetWorkConnectivityListener?.onNetDisConnected()
                    isSuccessShown = true
                }
            }, 700)


            return this
        }

        private fun showAutoDialog(show: Boolean) {
            if (show) {
                knDialog?.show()
            } else {
                knDialog?.hide()
            }
        }


        private fun showSuccessView(): Request {
            configuration.setDuration(Configuration.DURATION_SHORT)

            croutonSuccess = Crouton.make(activity, activity?.layoutInflater?.inflate(R.layout.crouton_success_layout, null), viewGroupResId)
            croutonSuccess?.setConfiguration(configuration.build())
            croutonSuccess?.show()
            return this
        }

        private fun showErrorView(): Request {
            configuration.setDuration(Configuration.DURATION_INFINITE)

            croutonError = Crouton.make(activity, activity?.layoutInflater?.inflate(R.layout.crouton_error_layout, null), viewGroupResId)
            croutonError?.setConfiguration(configuration.build())
            croutonError?.show()
            return this
        }


        private fun getLayoutRes(layoutType: LayoutType): View? {
            when (layoutType) {
                LayoutType.SUCCRESS() -> {
                    return activity?.layoutInflater?.inflate(R.layout.crouton_success_layout, null)
                }

                LayoutType.ERROR() -> {
                    return activity?.layoutInflater?.inflate(R.layout.crouton_error_layout, null)
                }
            }

            return getLayoutRes(LayoutType.SUCCRESS())
        }

    }

    internal sealed class LayoutType {
        class SUCCRESS : LayoutType()
        class ERROR : LayoutType()
    }

    internal sealed class NetWorkType {
        class ALL : NetWorkType()
        class WIFI : NetWorkType()
        class MOBILE : NetWorkType()
    }


    interface OnNetWorkConnectivityListener {
        fun onNetConnected()
        fun onNetDisConnected()
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