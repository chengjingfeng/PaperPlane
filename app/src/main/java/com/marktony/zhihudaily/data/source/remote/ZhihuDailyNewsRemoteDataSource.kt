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

package com.marktony.zhihudaily.data.source.remote

import android.util.Log

import com.marktony.zhihudaily.data.ZhihuDailyNews
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyNewsDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import com.marktony.zhihudaily.util.formatZhihuDailyDateLongToString
import com.marktony.zhihudaily.util.formatZhihuDailyDateStringToLong
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Implementation of the [ZhihuDailyNews] data source that accesses network.
 */

class ZhihuDailyNewsRemoteDataSource private constructor() : ZhihuDailyNewsDataSource {

    companion object {

        private var INSTANCE: ZhihuDailyNewsRemoteDataSource? = null

        val instance: ZhihuDailyNewsRemoteDataSource
            get() {
                if (INSTANCE == null) {
                    INSTANCE = ZhihuDailyNewsRemoteDataSource()
                }
                return INSTANCE!!
            }
    }

    // The parameter forceUpdate and addToCache are ignored.
    override fun getZhihuDailyNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback) {

        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.ZHIHU_DAILY_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(RetrofitService.ZhihuDailyService::class.java)

        service.getZhihuList(formatZhihuDailyDateLongToString(date))
                .enqueue(object : Callback<ZhihuDailyNews> {
                    override fun onResponse(call: Call<ZhihuDailyNews>, response: Response<ZhihuDailyNews>) {

                        // Note: Only the timestamp of zhihu daily was set in remote source.
                        // The other two was set in repository due to structure of returning json.
                        val timestamp = formatZhihuDailyDateStringToLong(response.body()!!.date)

                        Log.d("TAG", "onResponse: timestamp $timestamp")

                        for (item in response.body()!!.stories) {
                            item.timestamp = timestamp
                        }
                        callback.onNewsLoaded(response.body()!!.stories)
                    }

                    override fun onFailure(call: Call<ZhihuDailyNews>, t: Throwable) {
                        callback.onDataNotAvailable()
                    }
                })

    }

    override fun getFavorites(callback: ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun getItem(itemId: Int, callback: ZhihuDailyNewsDataSource.GetNewsItemCallback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun favoriteItem(itemId: Int, favorite: Boolean) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun saveAll(list: List<ZhihuDailyNewsQuestion>) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

}