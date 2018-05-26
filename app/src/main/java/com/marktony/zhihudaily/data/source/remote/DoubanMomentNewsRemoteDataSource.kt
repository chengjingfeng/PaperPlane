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
import com.marktony.zhihudaily.data.DoubanMomentNews
import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.source.RemoteDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentNewsDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import com.marktony.zhihudaily.util.AppExecutors
import com.marktony.zhihudaily.util.formatDoubanMomentDateLongToString
import kotlinx.coroutines.experimental.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Implementation of the [DoubanMomentNews] data source that accesses network.
 */

class DoubanMomentNewsRemoteDataSource private constructor(private val mAppExecutors: AppExecutors) : DoubanMomentNewsDataSource {

    private val mDoubanMomentService: RetrofitService.DoubanMomentService by lazy {
        val httpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(24, TimeUnit.SECONDS)
                .readTimeout(24, TimeUnit.SECONDS)
                .writeTimeout(24, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        httpClientBuilder.retryOnConnectionFailure(true)

        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.DOUBAN_MOMENT_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build()

        retrofit.create(RetrofitService.DoubanMomentService::class.java)
    }

    companion object {

        private var INSTANCE: DoubanMomentNewsRemoteDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors): DoubanMomentNewsRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(DoubanMomentNewsRemoteDataSource::javaClass) {
                    INSTANCE = DoubanMomentNewsRemoteDataSource(appExecutors)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }

    }

    override suspend fun getDoubanMomentNews(forceUpdate: Boolean, clearCache: Boolean, date: Long): Result<List<DoubanMomentNewsPosts>> = withContext(mAppExecutors.ioContext) {
        try {
            val response = mDoubanMomentService.getDoubanList(formatDoubanMomentDateLongToString(date)).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.posts.isNotEmpty()) {
                        Result.Success(it.posts)
                    } else {
                        Result.Error(RemoteDataNotFoundException())
                    }
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

    // Not required because the [com.marktony.zhihudaily.data.source.repository.DoubanMomentNewsRepository] handles the logic of refreshing the
    // news from all the available data sources.
    override suspend fun getFavorites(): Result<List<DoubanMomentNewsPosts>> = Result.Error(RemoteDataNotFoundException())

    // Not required because the [com.marktony.zhihudaily.data.source.repository.DoubanMomentNewsRepository] handles the logic of refreshing the
    // news from all the available data sources.
    override suspend fun getItem(id: Int): Result<DoubanMomentNewsPosts> = Result.Error(RemoteDataNotFoundException())

    override suspend fun favoriteItem(itemId: Int, favorite: Boolean) {
        // Not required because the [com.marktony.zhihudaily.data.source.repository.DoubanMomentNewsRepository] handles the logic of refreshing the
        // news from all the available data sources.
    }

    override suspend fun saveAll(list: List<DoubanMomentNewsPosts>) {
        // Not required because the [com.marktony.zhihudaily.data.source.repository.DoubanMomentNewsRepository] handles the logic of refreshing the
        // news from all the available data sources.
    }

}
