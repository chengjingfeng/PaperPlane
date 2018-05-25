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

import com.marktony.zhihudaily.data.GuokrHandpickNews
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 *
 * Implementation of the [GuokrHandpickNews] data source that accesses network.
 */

class GuokrHandpickNewsRemoteDataSource private constructor() : GuokrHandpickDataSource {

    companion object {

        private var INSTANCE: GuokrHandpickNewsRemoteDataSource? = null

        val instance: GuokrHandpickNewsRemoteDataSource
            get() {
                if (INSTANCE == null) {
                    INSTANCE = GuokrHandpickNewsRemoteDataSource()
                }
                return INSTANCE!!
            }
    }

    override fun getGuokrHandpickNews(forceUpdate: Boolean, clearCache: Boolean, offset: Int, limit: Int, callback: GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback) {
        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.GUOKR_HANDPICK_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(RetrofitService.GuokrHandpickService::class.java)

        service.getGuokrHandpick(offset, limit)
                .enqueue(object : Callback<GuokrHandpickNews> {
                    override fun onResponse(call: Call<GuokrHandpickNews>, response: Response<GuokrHandpickNews>) {
                        callback.onNewsLoad(response.body()!!.result)
                    }

                    override fun onFailure(call: Call<GuokrHandpickNews>, t: Throwable) {
                        callback.onDataNotAvailable()
                    }
                })
    }

    override fun getFavorites(callback: GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun getItem(itemId: Int, callback: GuokrHandpickDataSource.GetNewsItemCallback) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun favoriteItem(itemId: Int, favorite: Boolean) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun saveAll(list: List<GuokrHandpickNewsResult>) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

}
