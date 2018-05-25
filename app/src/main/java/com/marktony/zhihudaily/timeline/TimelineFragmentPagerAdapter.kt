/*
 * Copyright 2016 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marktony.zhihudaily.timeline

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.marktony.zhihudaily.R


/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * [FragmentPagerAdapter] of [TimelineFragment].
 */

class TimelineFragmentPagerAdapter(
        fm: FragmentManager,
        context: Context,
        private val mZhihuFragment: ZhihuDailyFragment,
        private val mDoubanFragment: DoubanMomentFragment,
        private val mGuokrFragment: GuokrHandpickFragment
) : FragmentPagerAdapter(fm) {

    private val pageCount = 3
    private val titles: Array<String> = arrayOf(context.getString(
            R.string.zhihu_daily),
            context.getString(R.string.douban_moment),
            context.getString(R.string.guokr_handpick))

    override fun getItem(i: Int): Fragment = when (i) {
        0 -> mZhihuFragment
        1 -> mDoubanFragment
        else -> mGuokrFragment
    }

    override fun getCount(): Int = pageCount

    override fun getPageTitle(position: Int): CharSequence? = titles[position]

}
