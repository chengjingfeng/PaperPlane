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

package com.marktony.zhihudaily.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.preference.PreferenceManager
import com.marktony.zhihudaily.data.PostType
import com.marktony.zhihudaily.database.AppDatabase
import com.marktony.zhihudaily.retrofit.RetrofitService
import com.marktony.zhihudaily.util.KEY_TIME_OF_SAVING_ARTICLES
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

/**
 * Created by lizhaotailang on 2017/6/19.
 *
 * Cache the content of specific ids which are passed by broadcaset
 * and remove timeout items and contents.
 */

class CacheService : Service() {

    private lateinit var mDb: AppDatabase

    private var mReceiver: LocalReceiver? = null

    private lateinit var mZhihuService: RetrofitService.ZhihuDailyService
    private lateinit var mDoubanService: RetrofitService.DoubanMomentService
    private lateinit var mGuokrService: RetrofitService.GuokrHandpickService

    private var mZhihuCacheDone = false
    private var mDoubanCacheDone = false
    private var mGuokrCacheDone = false

    private val mHandler = Handler { message ->
        when (message.what) {
            MSG_CLEAR_CACHE_DONE -> this.stopSelf()
            else -> {
            }
        }
        true
    }

    companion object {

        val FLAG_ID = "flag_id"
        val FLAG_TYPE = "flag_type"

        val BROADCAST_FILTER_ACTION = "com.marktony.zhihudaily.LOCAL_BROADCAST"

        private val MSG_CLEAR_CACHE_DONE = 1

    }


    override fun onCreate() {
        super.onCreate()

        mDb = AppDatabase.getInstance(this)

        mReceiver = LocalReceiver()

        val filter = IntentFilter()
        filter.addAction(BROADCAST_FILTER_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver!!, filter)

        mZhihuService = Retrofit.Builder()
                .baseUrl(RetrofitService.ZHIHU_DAILY_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService.ZhihuDailyService::class.java)

        mDoubanService = Retrofit.Builder()
                .baseUrl(RetrofitService.DOUBAN_MOMENT_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService.DoubanMomentService::class.java)

        mGuokrService = Retrofit.Builder()
                .baseUrl(RetrofitService.GUOKR_HANDPICK_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService.GuokrHandpickService::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        // DO NOT forget to unregister the receiver.
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver!!)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // A local broadcast receiver that receives broadcast from the corresponding fragment.
    internal inner class LocalReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getIntExtra(FLAG_ID, 0)
            val type = intent.getSerializableExtra(FLAG_TYPE) as PostType
            when (type) {
                PostType.ZHIHU -> cacheZhihuDailyContent(id)
                PostType.DOUBAN -> cacheDoubanContent(id)
                PostType.GUOKR -> cacheGuokrContent(id)
                else -> {
                }
            }
        }

    }

    // Get zhihu content data by accessing network.
    private fun cacheZhihuDailyContent(id: Int) {

        Thread {
            mDb.beginTransaction()
            try {
                // Call execute() rather than enqueue()
                // or you will go back to main thread in onResponse() function.
                val response = mZhihuService.getZhihuContent(id).execute()
                if (response.isSuccessful && response.body() != null) {
                    mDb.zhihuDailyContentDao().insert(response.body()!!)
                    mDb.setTransactionSuccessful()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mDb.endTransaction()
                mZhihuCacheDone = true
                if (mDoubanCacheDone && mGuokrCacheDone) {
                    clearTimeoutContent()
                }
            }
        }.start()
    }

    // Get douban content data by accessing network.
    private fun cacheDoubanContent(id: Int) {

        Thread {
            mDb.beginTransaction()
            try {
                val response = mDoubanService.getDoubanContent(id).execute()
                if (response.isSuccessful && response.body() != null) {
                    mDb.doubanMomentContentDao().insert(response.body()!!)
                    mDb.setTransactionSuccessful()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mDb.endTransaction()
                mDoubanCacheDone = true
                if (mZhihuCacheDone && mGuokrCacheDone) {
                    clearTimeoutContent()
                }
            }
        }.start()
    }

    // Get guokr content data by accessing network.
    private fun cacheGuokrContent(id: Int) {
        Thread {
            mDb.beginTransaction()
            try {
                // Call execute() rather than enqueue()
                // or you will go back to main thread in onResponse() function.
                val response = mGuokrService.getGuokrContent(id).execute()
                if (response.isSuccessful && response.body()?.result != null) {
                    mDb.guokrHandpickContentDao().insert(response.body()?.result!!)
                    mDb.setTransactionSuccessful()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mDb.endTransaction()
                mGuokrCacheDone = true
                if (mZhihuCacheDone && mDoubanCacheDone) {
                    clearTimeoutContent()
                }
            }
        }.start()
    }

    private fun clearTimeoutContent() {

        val dayCount = getDayOfSavingArticles(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this@CacheService).getString(KEY_TIME_OF_SAVING_ARTICLES, "2")))

        Thread {
            mDb.beginTransaction()
            try {
                val timeInMillis = Calendar.getInstance().timeInMillis - dayCount * 24 * 60 * 60 * 1000

                // Clear cache of zhihu daily
                val zhihuTimeoutItems = mDb.zhihuDailyNewsDao().queryAllTimeoutItems(timeInMillis)
                zhihuTimeoutItems.forEach {
                    mDb.zhihuDailyNewsDao().delete(it)
                    mDb.zhihuDailyContentDao().queryContentById(it.id)?.let {
                        mDb.zhihuDailyContentDao().delete(it)
                    }
                }

                // Clear cache of guokr handpick
                val guokrTimeoutNews = mDb.guokrHandpickNewsDao().queryAllTimeoutItems(timeInMillis)
                for (r in guokrTimeoutNews) {
                    mDb.guokrHandpickNewsDao().delete(r)
                    mDb.guokrHandpickContentDao().queryContentById(r.id)?.let {
                        mDb.guokrHandpickContentDao().delete(it)
                    }
                }

                // Clear cache of douban moment
                val doubanTimeoutNews = mDb.doubanMomentNewsDao().queryAllTimeoutItems(timeInMillis)
                for (p in doubanTimeoutNews) {
                    mDb.doubanMomentNewsDao().delete(p)
                    mDb.doubanMomentContentDao().queryContentById(p.id)?.let {
                        mDb.doubanMomentContentDao().delete(it)
                    }
                }

                mHandler.sendEmptyMessage(MSG_CLEAR_CACHE_DONE)

            } finally {
                mDb.endTransaction()
            }

        }.start()
    }

    private fun getDayOfSavingArticles(id: Int): Int = when (id) {
        0 -> 1
        1 -> 5
        2 -> 15
        3 -> 30
        else -> 15
    }

}
