package jp.bglb.bonboru.trafficmonitor.observer


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

import android.net.TrafficStats
import android.os.Handler
import android.os.Process

object TrafficObserver : Observer() {

  private object handler : Handler()

  private var interval = 100L

  private object runnable : Runnable {
    override fun run() {
      subscribers.forEach {
        it.update(getTraffic())
      }
      handler.postDelayed(runnable, interval)
    }
  }

  fun updateInterval(interval: Long) {
    TrafficObserver.interval = interval
  }

  override fun startObserve() {
    subscribers.forEach {
      it.update(getTraffic())
    }
    handler.postDelayed(runnable, interval)
  }

  override fun stopObserve() {
    handler.removeCallbacks(runnable)
  }

  override fun getInitialTraffic(): Long = getTraffic()

  private fun getTraffic() = TrafficStats.getUidTxBytes(Process.myUid()) + TrafficStats.getUidRxBytes(Process.myUid())

}
