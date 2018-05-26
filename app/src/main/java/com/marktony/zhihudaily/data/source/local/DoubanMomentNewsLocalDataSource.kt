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
import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.source.LocalDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentNewsDataSource
import com.marktony.zhihudaily.database.dao.DoubanMomentNewsDao
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * * Concrete implementation of a [DoubanMomentNewsPosts] data source as database .
 */

class DoubanMomentNewsLocalDataSource private constructor(
        private val mAppExecutors: AppExecutors,
        private val mDoubanMomentNewsDao: DoubanMomentNewsDao
) : DoubanMomentNewsDataSource {

    companion object {

        private var INSTANCE: DoubanMomentNewsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, doubanMomentNewsDao: DoubanMomentNewsDao): DoubanMomentNewsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(DoubanMomentNewsLocalDataSource::javaClass) {
                    INSTANCE = DoubanMomentNewsLocalDataSource(appExecutors, doubanMomentNewsDao)
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
        val news = mDoubanMomentNewsDao.queryAllTimeoutItems(date)
        if (news.isNotEmpty()) Result.Success(news) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun getFavorites(): Result<List<DoubanMomentNewsPosts>> = withContext(mAppExecutors.ioContext) {
        val favorites = mDoubanMomentNewsDao.queryAllFavorites()
        if (favorites.isNotEmpty()) Result.Success(favorites) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun getItem(id: Int): Result<DoubanMomentNewsPosts> = withContext(mAppExecutors.ioContext) {
        val item = mDoubanMomentNewsDao.queryItemById(id)
        if (item != null) Result.Success(item) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun favoriteItem(itemId: Int, favorite: Boolean) {
        withContext(mAppExecutors.ioContext) {
            mDoubanMomentNewsDao.queryItemById(itemId)?.let {
                it.isFavorite = favorite
                mDoubanMomentNewsDao.update(it)
            }
        }
    }

    override suspend fun saveAll(list: List<DoubanMomentNewsPosts>) {
        withContext(mAppExecutors.ioContext) {
            mDoubanMomentNewsDao.insertAll(list)
        }
    }

}
