## RNetwork 

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RNetwork-orange.svg?style=flat)](https://android-arsenal.com/details/1/8104)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![API](https://img.shields.io/badge/API-11%2B-brightgreen.svg?style=plastic)](https://android-arsenal.com/api?level=11)

![Feature Image](https://repository-images.githubusercontent.com/250891537/077fef00-7d4d-11ea-9cd5-898435e61d2e)

A light weight and lifecycle awared live internet connection status library using reactivenetwork and crouton.

<p align="start">
  <img src="https://github.com/rrsaikat/RNetwork/blob/master/app/top.gif" height="450" width="220"/>
  <img src="https://github.com/rrsaikat/RNetwork/blob/master/app/bottom.gif" height="450" width="220"/>
</p>


How To
-----------------
How does another developer add this as a dependency?

STEP 1:  Reference your Bintray repository into project-level build.gradle:    

        allprojects {
          repositories {
            // ...
            maven { url 'https://dl.bintray.com/rrsaikat/RNetwork' }
          }
        }
        
STEP 2: Reference the library itself in your module-level build.gradle:      

        implementation 'com.rezwan.knetworklib:knetworklib:1.0.4'

STEP 3: KNetwork.initialize(this) - must declare this into Application.

        class App:Application() {
            override fun onCreate() {
                super.onCreate()
                KNetwork.initialize(this)
            }
        }
        
   nb: Don't forget to register your custom Application class into your manifest.xml file.
        
            <manifest xmlns:android="http://schemas.android.com/apk/res/android">
              <application
                android:name=".App"
                 ....>
               </application>
            </manifest>


STEP 4: KNetwork.bind(this, lifecycle) - bind the targeted activity in which you want to show network status.

        KNetwork.bind(this, lifecycle)
                       .setConnectivityListener(this)
                       
                       
## NOTE:
1. Enable java 8:

        compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
        }


Available additinal methods:
-----------------

    *  setSuccessLayout() - set your customized layout
    *  setErrorLayout() - set your customized layout
    *  showCroutons() - set false if you don't want to show croutons
    *  showKNDialog() - set true for show dialog when net connection goes off.
    *  setConnectivityListener() - connected, disconnected callback into activity
    *  setInAnimation() - custom animation setup
    *  setOutAnimation() - custom animation setup
    *  setViewGroupResId() - targeted viewgroup to show network status views.

Example project is here for better understanding: 
[GO TO Example](https://github.com/rrsaikat/RNetwork/blob/master/app/src/main/java/com/rezwan/example/MainActivity.kt)

Video Sample (Installaion or setup)
------------------
<a href="http://www.youtube.com/watch?feature=player_embedded&v=v55QLdqhwKQ
" target="_blank"><img src="http://img.youtube.com/vi/v55QLdqhwKQ/0.jpg" 
alt="IMAGE ALT TEXT HERE" width="240" height="180" border="10" /></a>

[GO TO Tutorial](https://www.youtube.com/watch?v=v55QLdqhwKQ)

Proguard
-----------------------
    ################ rxjava2 ###########
    -dontwarn java.util.concurrent.Flow*
    ##### END ##############

    ##ReactNetwork####
    -dontwarn com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
    -dontwarn io.reactivex.functions.Function
    -dontwarn rx.internal.util.**
    -dontwarn sun.misc.Unsafe
    ##END#####
        
Apps that are used this library:

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/images/badge_new.png)](https://play.google.com/store/apps/details?id=com.rezwan.routeradmin.wifisetup)

## Author

👤 **RRSaikat**

- FaceBook: [@engr.rezwan](https://www.facebook.com/engr.rezwan)
- Youtube: [Rezwanrehman](http://www.youtube.com/c/Rezwanrehman)
- Blog: [blog.rrsaikat.com](https://blog.rrsaikat.com)

## Used Libraries
1. [ReactiveNetwork](https://github.com/pwittchen/ReactiveNetwork)
1. [Crouton](https://github.com/keyboardsurfer/Crouton)


License
-----------------

      Copyright (c) 2020-present, RRsaikat

      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing,
      software distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
