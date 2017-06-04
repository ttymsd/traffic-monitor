package jp.bglb.bonboru.trafficmonitor.example;


/*
 * Copyright (C) 2017 Tetsuya Masuda
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Application;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import jp.bglb.bonboru.trafficmonitor.DisplayConfig;
import jp.bglb.bonboru.trafficmonitor.DisplayPosition;
import jp.bglb.bonboru.trafficmonitor.TrafficMonitor;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.canDrawOverlays(this)) {
        DisplayConfig config = new DisplayConfig(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, DisplayPosition.BOTTOM_RIGHT);
        TrafficMonitor.startMonitoring(this, config);
      }
    } else {
      TrafficMonitor.startMonitoring(this);
    }
  }

  @Override
  public void onTerminate() {
    TrafficMonitor.stopMonitoring(this);
    super.onTerminate();
  }
}
