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

import com.marktony.zhihudaily.data.GuokrHandpickContent
import com.marktony.zhihudaily.data.GuokrHandpickContentResult
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickContentDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 *
 * Implementation of the [GuokrHandpickContent] data source that accesses network.
 */

class GuokrHandpickContentRemoteDataSource private constructor() : GuokrHandpickContentDataSource {

    companion object {

        private var INSTANCE: GuokrHandpickContentRemoteDataSource? = null

        val instance: GuokrHandpickContentRemoteDataSource
            get() {
                if (INSTANCE == null) {
                    INSTANCE = GuokrHandpickContentRemoteDataSource()
                }
                return INSTANCE!!
            }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getGuokrHandpickContent(id: Int, callback: GuokrHandpickContentDataSource.LoadGuokrHandpickContentCallback) {
        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.GUOKR_HANDPICK_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(RetrofitService.GuokrHandpickService::class.java)

        service.getGuokrContent(id)
                .enqueue(object : Callback<GuokrHandpickContent> {
                    override fun onResponse(call: Call<GuokrHandpickContent>, response: Response<GuokrHandpickContent>) {
                        callback.onContentLoaded(response.body()!!.result)
                    }

                    override fun onFailure(call: Call<GuokrHandpickContent>, t: Throwable) {

                        callback.onDataNotAvailable()
                    }
                })
    }

    override fun saveContent(content: GuokrHandpickContentResult) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

}
