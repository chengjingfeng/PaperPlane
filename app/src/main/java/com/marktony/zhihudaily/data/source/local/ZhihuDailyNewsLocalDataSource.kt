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

package com.marktony.zhihudaily.data.source.local

import android.support.annotation.VisibleForTesting
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.data.source.LocalDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyNewsDataSource
import com.marktony.zhihudaily.database.dao.ZhihuDailyNewsDao
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Concrete implementation of a [ZhihuDailyNewsQuestion] data source as database.
 */

class ZhihuDailyNewsLocalDataSource private constructor(
        val mAppExecutors: AppExecutors,
        val mZhihuDailyNewsDao: ZhihuDailyNewsDao
) : ZhihuDailyNewsDataSource {

    companion object {
        private var INSTANCE: ZhihuDailyNewsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, zhihuDailyNewsDao: ZhihuDailyNewsDao): ZhihuDailyNewsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(ZhihuDailyNewsLocalDataSource::javaClass) {
                    INSTANCE = ZhihuDailyNewsLocalDataSource(appExecutors, zhihuDailyNewsDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }

    override suspend fun getZhihuDailyNews(forceUpdate: Boolean, clearCache: Boolean, date: Long): Result<List<ZhihuDailyNewsQuestion>> = withContext(mAppExecutors.ioContext) {
        val news = mZhihuDailyNewsDao.queryAllByDate(date)
        if (news.isNotEmpty()) Result.Success(news) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun getFavorites(): Result<List<ZhihuDailyNewsQuestion>> = withContext(mAppExecutors.ioContext) {
        val favorites = mZhihuDailyNewsDao.queryAllFavorites()
        if (favorites.isNotEmpty()) Result.Success(favorites) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun getItem(itemId: Int): Result<ZhihuDailyNewsQuestion> = withContext(mAppExecutors.ioContext) {
        val item = mZhihuDailyNewsDao.queryItemById(itemId)
        if (item != null) Result.Success(item) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun favoriteItem(itemId: Int, favorite: Boolean) {
        withContext(mAppExecutors.ioContext) {
            mZhihuDailyNewsDao.queryItemById(itemId)?.let {
                it.isFavorite = favorite
                mZhihuDailyNewsDao.update(it)
            }
        }
    }

    override suspend fun saveAll(list: List<ZhihuDailyNewsQuestion>) {
        withContext(mAppExecutors.ioContext) {
            mZhihuDailyNewsDao.insertAll(list)
        }
    }

}
