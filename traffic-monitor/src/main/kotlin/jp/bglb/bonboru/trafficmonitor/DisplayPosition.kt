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


import android.view.Gravity

enum class DisplayPosition(val gravity: Int) {
  TOP_LEFT(Gravity.LEFT or Gravity.TOP),
  TOP_CENTER(Gravity.CENTER_HORIZONTAL or Gravity.TOP),
  TOP_RIGHT(Gravity.RIGHT or Gravity.TOP),
  CENTER_LEFT(Gravity.CENTER_VERTICAL or Gravity.LEFT),
  CENTER(Gravity.CENTER),
  CENTER_RIGHT(Gravity.CENTER_VERTICAL or Gravity.RIGHT),
  BOTTOM_LEFT(Gravity.LEFT or Gravity.BOTTOM),
  BOTTOM_CENTER(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM),
  BOTTOM_RIGHT(Gravity.RIGHT or Gravity.BOTTOM)
}
