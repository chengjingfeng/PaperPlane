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

import android.support.annotation.VisibleForTesting
import com.marktony.zhihudaily.BuildConfig
import com.marktony.zhihudaily.data.ZhihuDailyContent
import com.marktony.zhihudaily.data.source.RemoteDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyContentDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Implementation of the [ZhihuDailyContent] data source that accesses network.
 */

class ZhihuDailyContentRemoteDataSource private constructor(
        private val mAppExecutors: AppExecutors
) : ZhihuDailyContentDataSource {

    private val mZhihuDailyService: RetrofitService.ZhihuDailyService by lazy {
        val httpClientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        httpClientBuilder.retryOnConnectionFailure(true)

        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.ZHIHU_DAILY_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build()

        retrofit.create(RetrofitService.ZhihuDailyService::class.java)
    }

    companion object {

        private var INSTANCE: ZhihuDailyContentRemoteDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors): ZhihuDailyContentRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(ZhihuDailyContentRemoteDataSource::javaClass) {
                    INSTANCE = ZhihuDailyContentRemoteDataSource(appExecutors)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }

    }

    override suspend fun getZhihuDailyContent(id: Int): Result<ZhihuDailyContent> = withContext(mAppExecutors.ioContext) {
        try {
            val response = mZhihuDailyService.getZhihuContent(id).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: run {
                    Result.Error(RemoteDataNotFoundException())
                }
            } else {
                Result.Error(RemoteDataNotFoundException())
            }
        } catch (e: Exception) {
            Result.Error(RemoteDataNotFoundException())
        }
    }

    override suspend fun saveContent(content: ZhihuDailyContent) {
        // Not required because the [com.marktony.zhihudaily.data.source.repository.ZhihuDailyNewsRepository] handles the logic of refreshing the
        // news from all the available data sources.
    }

}
