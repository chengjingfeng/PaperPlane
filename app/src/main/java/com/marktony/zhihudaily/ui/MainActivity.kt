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

package com.marktony.zhihudaily.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.favorites.FavoritesFragment
import com.marktony.zhihudaily.favorites.FavoritesPresenter
import com.marktony.zhihudaily.injection.Injection
import com.marktony.zhihudaily.service.CacheService
import com.marktony.zhihudaily.timeline.TimelineFragment
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by lizhaotailang on 2017/5/20.
 *
 *
 * Main activity of the app.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var mTimelineFragment: TimelineFragment
    private lateinit var mInfoFragment: InfoFragment
    private lateinit var mFavoritesFragment: FavoritesFragment

    companion object {

        private val KEY_BOTTOM_NAVIGATION_VIEW_SELECTED_ID = "KEY_BOTTOM_NAVIGATION_VIEW_SELECTED_ID"
        val ACTION_FAVORITES = "com.marktony.zhihudaily.favorites"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragments(savedInstanceState)

        val appExecutors = AppExecutors()
        FavoritesPresenter(
                mFavoritesFragment,
                Injection.provideZhihuDailyNewsRepository(this@MainActivity),
                Injection.provideDoubanMomentNewsRepository(this@MainActivity),
                Injection.provideGuokrHandpickNewsRepository(this@MainActivity))

        if (savedInstanceState != null) {
            val id = savedInstanceState.getInt(KEY_BOTTOM_NAVIGATION_VIEW_SELECTED_ID, R.id.nav_timeline)
            when (id) {
                R.id.nav_timeline -> showFragment(mTimelineFragment)
                R.id.nav_favorites -> showFragment(mFavoritesFragment)
                R.id.nav_info -> showFragment(mInfoFragment)
            }
        } else {
            if (intent.action == ACTION_FAVORITES) {
                showFragment(mFavoritesFragment)
                bottom_nav.selectedItemId = R.id.nav_favorites
            } else {
                showFragment(mTimelineFragment)
            }
        }

        bottom_nav.setOnNavigationItemSelectedListener { menuItem ->
            val ft = supportFragmentManager.beginTransaction()
            when (menuItem.itemId) {
                R.id.nav_timeline -> showFragment(mTimelineFragment)

                R.id.nav_favorites -> showFragment(mFavoritesFragment)

                R.id.nav_info -> showFragment(mInfoFragment)

                else -> {
                }
            }
            ft.commit()
            true
        }

        // Start the caching service.
        startService(Intent(this@MainActivity, CacheService::class.java))

    }

    private fun initFragments(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager
        if (savedInstanceState == null) {
            mTimelineFragment = TimelineFragment.newInstance()
            mInfoFragment = InfoFragment.newInstance()
            mFavoritesFragment = FavoritesFragment.newInstance()
        } else {
            mTimelineFragment = fm.getFragment(savedInstanceState, TimelineFragment::class.java.simpleName) as TimelineFragment
            mFavoritesFragment = fm.getFragment(savedInstanceState, FavoritesFragment::class.java.simpleName) as FavoritesFragment
            mInfoFragment = fm.getFragment(savedInstanceState, InfoFragment::class.java.simpleName) as InfoFragment
        }

        if (!mTimelineFragment.isAdded) {
            fm.beginTransaction()
                    .add(R.id.container, mTimelineFragment, TimelineFragment::class.java.simpleName)
                    .commit()
        }

        if (!mFavoritesFragment.isAdded) {
            fm.beginTransaction()
                    .add(R.id.container, mFavoritesFragment, FavoritesFragment::class.java.simpleName)
                    .commit()

        }

        if (!mInfoFragment.isAdded) {
            fm.beginTransaction()
                    .add(R.id.container, mInfoFragment, InfoFragment::class.java.simpleName)
                    .commit()
        }
    }

    private fun showFragment(fragment: Fragment?) {
        val fm = supportFragmentManager
        when (fragment) {
            is TimelineFragment -> fm.beginTransaction()
                    .show(mTimelineFragment)
                    .hide(mInfoFragment)
                    .hide(mFavoritesFragment)
                    .commit()
            is InfoFragment -> fm.beginTransaction()
                    .show(mInfoFragment)
                    .hide(mTimelineFragment)
                    .hide(mFavoritesFragment)
                    .commit()
            is FavoritesFragment -> fm.beginTransaction()
                    .show(mFavoritesFragment)
                    .hide(mTimelineFragment)
                    .hide(mInfoFragment)
                    .commit()
        }
    }

}
