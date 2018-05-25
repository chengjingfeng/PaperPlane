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

import com.marktony.zhihudaily.data.ZhihuDailyContent
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyContentDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Implementation of the [ZhihuDailyContent] data source that accesses network.
 */

class ZhihuDailyContentRemoteDataSource private constructor() : ZhihuDailyContentDataSource {

    companion object {

        private var INSTANCE: ZhihuDailyContentRemoteDataSource? = null

        val instance: ZhihuDailyContentRemoteDataSource
            get() {
                if (INSTANCE == null) {
                    INSTANCE = ZhihuDailyContentRemoteDataSource()
                }
                return INSTANCE!!
            }
    }

    override fun getZhihuDailyContent(id: Int, callback: ZhihuDailyContentDataSource.LoadZhihuDailyContentCallback) {
        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.ZHIHU_DAILY_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(RetrofitService.ZhihuDailyService::class.java)

        service.getZhihuContent(id)
                .enqueue(object : Callback<ZhihuDailyContent> {
                    override fun onResponse(call: Call<ZhihuDailyContent>, response: Response<ZhihuDailyContent>) {
                        callback.onContentLoaded(response.body()!!)
                    }

                    override fun onFailure(call: Call<ZhihuDailyContent>, t: Throwable) {
                        callback.onDataNotAvailable()
                    }
                })
    }

    override fun saveContent(content: ZhihuDailyContent) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

}
