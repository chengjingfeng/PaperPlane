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

import com.marktony.zhihudaily.data.DoubanMomentNews
import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentNewsDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import com.marktony.zhihudaily.util.formatDoubanMomentDateLongToString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Implementation of the [DoubanMomentNews] data source that accesses network.
 */

class DoubanMomentNewsRemoteDataSource private constructor() : DoubanMomentNewsDataSource {

    companion object {

        private var INSTANCE: DoubanMomentNewsRemoteDataSource? = null

        val instance: DoubanMomentNewsRemoteDataSource
            get() {
                if (INSTANCE == null) {
                    INSTANCE = DoubanMomentNewsRemoteDataSource()
                }
                return INSTANCE!!
            }
    }

    override fun getDoubanMomentNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback) {
        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.DOUBAN_MOMENT_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(RetrofitService.DoubanMomentService::class.java)

        service.getDoubanList(formatDoubanMomentDateLongToString(date))
                .enqueue(object : Callback<DoubanMomentNews> {
                    override fun onResponse(call: Call<DoubanMomentNews>, response: Response<DoubanMomentNews>) {
                        callback.onNewsLoaded(response.body()!!.posts)
                    }

                    override fun onFailure(call: Call<DoubanMomentNews>, t: Throwable) {
                        callback.onDataNotAvailable()
                    }
                })
    }

    override fun getFavorites(callback: DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun getItem(id: Int, callback: DoubanMomentNewsDataSource.GetNewsItemCallback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun favoriteItem(itemId: Int, favorite: Boolean) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun saveAll(list: List<DoubanMomentNewsPosts>) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

}
