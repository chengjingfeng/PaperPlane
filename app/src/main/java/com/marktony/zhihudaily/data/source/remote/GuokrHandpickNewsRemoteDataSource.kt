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
import com.marktony.zhihudaily.data.GuokrHandpickNews
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.source.RemoteDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickDataSource
import com.marktony.zhihudaily.retrofit.RetrofitService
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 *
 * Implementation of the [GuokrHandpickNews] data source that accesses network.
 */

class GuokrHandpickNewsRemoteDataSource private constructor(private val mAppExecutors: AppExecutors) : GuokrHandpickDataSource {

    private val mGuokrHandpickService: RetrofitService.GuokrHandpickService by lazy {
        val httpClientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        httpClientBuilder.retryOnConnectionFailure(true)

        val retrofit = Retrofit.Builder()
                .baseUrl(RetrofitService.GUOKR_HANDPICK_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build()

        retrofit.create(RetrofitService.GuokrHandpickService::class.java)
    }

    companion object {

        private var INSTANCE: GuokrHandpickNewsRemoteDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors): GuokrHandpickNewsRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(GuokrHandpickNewsRemoteDataSource::javaClass) {
                    INSTANCE = GuokrHandpickNewsRemoteDataSource(appExecutors)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }

    }

    override suspend fun getGuokrHandpickNews(forceUpdate: Boolean, clearCache: Boolean, offset: Int, limit: Int): Result<List<GuokrHandpickNewsResult>> = withContext(mAppExecutors.ioContext) {
        try {
            val response = mGuokrHandpickService.getGuokrHandpick(offset, limit).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.result.isNotEmpty()) {
                        Result.Success(it.result)
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

    // Not required because the [com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository] handles the logic of refreshing the
    // news from all the available data sources.
    override suspend fun getFavorites(): Result<List<GuokrHandpickNewsResult>> = Result.Error(RemoteDataNotFoundException())

    // Not required because the [com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository] handles the logic of refreshing the
    // news from all the available data sources.
    override suspend fun getItem(itemId: Int): Result<GuokrHandpickNewsResult> = Result.Error(RemoteDataNotFoundException())

    override suspend fun favoriteItem(itemId: Int, favorite: Boolean) {
        // Not required because the [com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository] handles the logic of refreshing the
        // news from all the available data sources.
    }

    override suspend fun saveAll(list: List<GuokrHandpickNewsResult>) {
        // Not required because the [com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository] handles the logic of refreshing the
        // news from all the available data sources.
    }

}
