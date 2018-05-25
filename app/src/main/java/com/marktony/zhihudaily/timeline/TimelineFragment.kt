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

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.data.source.local.DoubanMomentNewsLocalDataSource
import com.marktony.zhihudaily.data.source.local.GuokrHandpickNewsLocalDataSource
import com.marktony.zhihudaily.data.source.local.ZhihuDailyNewsLocalDataSource
import com.marktony.zhihudaily.data.source.remote.DoubanMomentNewsRemoteDataSource
import com.marktony.zhihudaily.data.source.remote.GuokrHandpickNewsRemoteDataSource
import com.marktony.zhihudaily.data.source.remote.ZhihuDailyNewsRemoteDataSource
import com.marktony.zhihudaily.data.source.repository.DoubanMomentNewsRepository
import com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository
import com.marktony.zhihudaily.data.source.repository.ZhihuDailyNewsRepository
import kotlinx.android.synthetic.main.fragment_timeline.*

/**
 * Created by lizhaotailang on 2017/5/20.
 *
 * Main UI for displaying the [ViewPager]
 * which was set up with [TabLayout].
 */

class TimelineFragment : Fragment() {

    private lateinit var mZhihuFragment: ZhihuDailyFragment
    private lateinit var mDoubanFragment: DoubanMomentFragment
    private lateinit var mGuokrFragment: GuokrHandpickFragment

    companion object {

        fun newInstance(): TimelineFragment = TimelineFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mZhihuFragment = ZhihuDailyFragment.newInstance()
        mDoubanFragment = DoubanMomentFragment.newInstance()
        mGuokrFragment = GuokrHandpickFragment.newInstance()

        context?.let {
            ZhihuDailyPresenter(mZhihuFragment, ZhihuDailyNewsRepository.getInstance(
                    ZhihuDailyNewsRemoteDataSource.instance,
                    ZhihuDailyNewsLocalDataSource.getInstance(it)))

            DoubanMomentPresenter(mDoubanFragment, DoubanMomentNewsRepository.getInstance(
                    DoubanMomentNewsRemoteDataSource.instance,
                    DoubanMomentNewsLocalDataSource.getInstance(it)))

            GuokrHandpickPresenter(mGuokrFragment, GuokrHandpickNewsRepository.getInstance(
                    GuokrHandpickNewsRemoteDataSource.instance,
                    GuokrHandpickNewsLocalDataSource.getInstance(it)))

            view_pager.adapter = TimelineFragmentPagerAdapter(
                    childFragmentManager,
                    it,
                    mZhihuFragment,
                    mDoubanFragment,
                    mGuokrFragment)
        }

        view_pager.offscreenPageLimit = 3
        tab_layout.setupWithViewPager(view_pager)

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 2) {
                    fab.hide()
                } else {
                    fab.show()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        fab.setOnClickListener {
            if (tab_layout.selectedTabPosition == 0) {
                mZhihuFragment.showDatePickerDialog()
            } else {
                mDoubanFragment.showDatePickerDialog()
            }
        }
    }

}
