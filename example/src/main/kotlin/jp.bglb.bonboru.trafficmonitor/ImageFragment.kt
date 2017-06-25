package jp.bglb.bonboru.trafficmonitor

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import jp.bglb.bonboru.trafficmonitor.example.R
import java.util.concurrent.TimeUnit

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

class ImageFragment : Fragment() {

  companion object {
    fun newInstance(number: Int) = ImageFragment().apply {
      val b = Bundle()
      b.putInt("number", number)
      arguments = b
    }
  }

  var task: AsyncTask<Int, Unit, Int>? = null

  var count = 0;

  val imageView: ImageView? by lazy {
    view?.findViewById(R.id.image) as ImageView
  }

  val button: Button? by lazy {
    view?.findViewById(R.id.update) as Button
  }


  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_image, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val number = arguments.getInt("number")
    task = SleepTask(number)
    task?.execute(0)
    button?.setOnClickListener {
      when (count) {
        0 -> TrafficMonitor.updatePosition(DisplayPosition.TOP_LEFT)
        1 -> TrafficMonitor.updatePosition(DisplayPosition.TOP_CENTER)
        2 -> TrafficMonitor.updatePosition(DisplayPosition.TOP_RIGHT)
        3 -> TrafficMonitor.updatePosition(DisplayPosition.CENTER_LEFT)
        4 -> TrafficMonitor.updatePosition(DisplayPosition.CENTER)
        5 -> TrafficMonitor.updatePosition(DisplayPosition.CENTER_RIGHT)
        6 -> TrafficMonitor.updatePosition(DisplayPosition.BOTTOM_LEFT)
        7 -> TrafficMonitor.updatePosition(DisplayPosition.BOTTOM_CENTER)
        else -> TrafficMonitor.updatePosition(DisplayPosition.BOTTOM_RIGHT)
      }
      count = (count+1) % 9
    }
  }

  override fun onDetach() {
    super.onDetach()
    task?.cancel(true)
  }

  inner class SleepTask(private val number: Int) : AsyncTask<Int, Unit, Int>() {

    override fun doInBackground(vararg params: Int?): Int? {
      Thread.sleep(TimeUnit.SECONDS.toMillis(1))
      return if (params.isEmpty()) 0 else params[0]
    }

    override fun onPostExecute(p: Int) {
      imageView?.let {
        Glide.with(it).asBitmap().load("http://placehold.it/500x200?text=$number:$p").into(it)
        if (p < 10) {
          task = SleepTask(number)
          task?.execute(p + 1)
        }
      }
    }
  }
}
