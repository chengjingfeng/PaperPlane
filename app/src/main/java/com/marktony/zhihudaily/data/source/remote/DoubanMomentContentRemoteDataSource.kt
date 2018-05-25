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

import com.marktony.zhihudaily.data.DoubanMomentContent
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentContentDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/25.
 *
 * Implementation of the [DoubanMomentContent] data source that accesses network.
 */

class DoubanMomentContentRemoteDataSource private constructor() : DoubanMomentContentDataSource {

    companion object {

        private var INSTANCE: DoubanMomentContentRemoteDataSource? = null

        val instance: DoubanMomentContentRemoteDataSource
            get() {
                if (INSTANCE == null) {
                    INSTANCE = DoubanMomentContentRemoteDataSource()
                }
                return INSTANCE!!
            }
    }

    override fun getDoubanMomentContent(id: Int, callback: DoubanMomentContentDataSource.LoadDoubanMomentContentCallback) {

        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.DOUBAN_MOMENT_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(RetrofitService.DoubanMomentService::class.java)

        service.getDoubanContent(id).enqueue(object : Callback<DoubanMomentContent> {
            override fun onResponse(call: Call<DoubanMomentContent>, response: Response<DoubanMomentContent>) {
                callback.onContentLoaded(response.body()!!)
            }

            override fun onFailure(call: Call<DoubanMomentContent>, t: Throwable) {
                callback.onDataNotAvailable()
            }
        })

    }

    override fun saveContent(content: DoubanMomentContent) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

}
