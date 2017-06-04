package jp.bglb.bonboru.trafficmonitor

import android.app.Application
import android.os.Build
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.WindowManager


/**
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

class ImagePageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

  override fun getItem(p0: Int): Fragment = ImageFragment.newInstance(p0)

  override fun getCount() = 10

  class App: Application() {
    override fun onCreate() {
      super.onCreate()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Settings.canDrawOverlays(this)) {
          val config = DisplayConfig(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, DisplayPosition.BOTTOM_RIGHT)
          TrafficMonitor.startMonitoring(this, config)
        }
      } else {
        TrafficMonitor.startMonitoring(this)
      }
    }

    override fun onTerminate() {
      TrafficMonitor.stopMonitoring(this)
      super.onTerminate()
    }
  }
}
