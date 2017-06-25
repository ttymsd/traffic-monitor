package jp.bglb.bonboru.trafficmonitor


/*
 * Copyright (C) 2017 Tetsuya Masuda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.RelativeLayout
import android.widget.TextView
import jp.bglb.bonboru.tafficmonitor.R
import jp.bglb.bonboru.trafficmonitor.observer.Observer
import jp.bglb.bonboru.trafficmonitor.observer.TrafficObserver
import java.text.DecimalFormat


class TrafficMonitor {

  private object observer {

    var realObserver: Observer = TrafficObserver

    fun changeObserver(observer: Observer) {
      this.realObserver = observer
    }

    fun subscribe(subscriber: Subscriber) {
      this.realObserver.subscribe(subscriber)
    }

    fun startObserve() {
      realObserver.startObserve()
    }

    fun stopObserve() {
      realObserver.stopObserve()
    }
  }

  private object lifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
      Display.lastMobileTraffic = observer.realObserver.getInitialTraffic()
      Display.lastWifiTraffic = Display.lastMobileTraffic
      Display.mobileTraffic = 0L
      Display.wifiTraffic = 0L
      Display.snapMobileTraffic = 0L
      Display.snapWifiTraffic = 0L
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }
  }

  private object Display : Subscriber {

    private val LOG_TAG = "TrafficMonitor"

    private lateinit var windowManager: WindowManager
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var layoutParams: LayoutParams
    private var monitor: View? = null
    private val meter: TextView? by lazy {
      monitor?.findViewById(R.id.meter) as TextView
    }

    var lastMobileTraffic = 0L
    var lastWifiTraffic = 0L

    var mobileTraffic = 0L
    var wifiTraffic = 0L

    var snapMobileTraffic = 0L
    var snapWifiTraffic = 0L

    fun prepare(application: Application): Display {
      windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      layoutParams = LayoutParams().apply {
        width = WRAP_CONTENT
        height = WRAP_CONTENT
        type = LayoutParams.TYPE_TOAST
        flags = LayoutParams.FLAG_KEEP_SCREEN_ON or LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL
        format = PixelFormat.TRANSLUCENT
        gravity = DisplayPosition.BOTTOM_RIGHT.gravity
      }
      monitor = LayoutInflater.from(application).inflate(R.layout.monitor, RelativeLayout(application))
      return this
    }

    fun applyConfig(config: DisplayConfig): Display {
      layoutParams.type = config.layer
      layoutParams.gravity = config.position.gravity
      return this
    }

    fun start() {
      windowManager.addView(monitor, layoutParams)
      observer.subscribe(this)
      observer.startObserve()
    }

    fun updatePosition(position: DisplayPosition) {
        layoutParams.gravity = position.gravity
    }

    fun stop() {
      observer.stopObserve()
      windowManager.removeView(monitor)
    }

    fun isWifi(): Boolean {
      val netInfo = connectivityManager.activeNetworkInfo
      return netInfo != null && netInfo.isConnected && netInfo.type == TYPE_WIFI
    }

    fun snap(name: String) {
      Log.d(LOG_TAG, "$name:${getTrafficString(arrayOf(mobileTraffic - snapMobileTraffic, wifiTraffic - snapWifiTraffic), name)}")
      snapMobileTraffic = mobileTraffic
      snapWifiTraffic = wifiTraffic
    }

    override fun update(latestTraffic: Long) {
      if (isWifi()) {
        wifiTraffic += latestTraffic - lastWifiTraffic
        lastWifiTraffic = latestTraffic
      } else {
        mobileTraffic += latestTraffic - lastMobileTraffic
        lastMobileTraffic = latestTraffic
      }
      meter?.text = getTrafficString(arrayOf(mobileTraffic, wifiTraffic))
    }

    private fun getTrafficString(traffics: Array<Long> = arrayOf<Long>(), name: String = "Activity")
        = traffics.map {
      var count = 0
      do {
        count++
      } while (it > Math.pow(1024.0, count.toDouble()) && count < 5)
      when (count) {
        1 -> DecimalFormat("#,###.##B").format(it)
        2 -> DecimalFormat("#,###.##KB").format(it / 1024.0)
        3 -> DecimalFormat("#,###.##MB").format(it / 1024.0 / 1024.0)
        else -> DecimalFormat("#,###.##GB").format(it / 1024.0 / 1024.0 / 1024.0)
      }
    }.joinToString("\n", "$name:\n")
  }

  companion object {

    @JvmStatic fun startMonitoring(application: Application) = startMonitoring(application, DisplayConfig())

    @JvmStatic fun startMonitoring(application: Application, config: DisplayConfig) {
      application.registerActivityLifecycleCallbacks(lifecycleCallback)
      Display.prepare(application).applyConfig(config).start()
    }

    @JvmStatic fun stopMonitoring(application: Application) {
      application.unregisterActivityLifecycleCallbacks(lifecycleCallback)
      Display.stop()
    }

    @JvmStatic fun setObserver(observer: Observer) {
      TrafficMonitor.observer.changeObserver(observer)
    }

    @JvmStatic fun snap(name: String) {
      TrafficMonitor.Display.snap(name)
    }

    @JvmStatic fun updatePosition(position: DisplayPosition) {
      Display.stop()
      Display.updatePosition(position)
      Display.start()
    }
  }

}
