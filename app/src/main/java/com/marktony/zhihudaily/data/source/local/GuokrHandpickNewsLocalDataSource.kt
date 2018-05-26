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
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.source.LocalDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickDataSource
import com.marktony.zhihudaily.database.dao.GuokrHandpickNewsDao
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Concrete implementation of a [GuokrHandpickNewsResult] data source as database.
 */

class GuokrHandpickNewsLocalDataSource private constructor(
        val mAppExecutors: AppExecutors,
        val mGuokrHandpickNewsDao: GuokrHandpickNewsDao
) : GuokrHandpickDataSource {

    companion object {

        private var INSTANCE: GuokrHandpickNewsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, guokrHandpickNewsDao: GuokrHandpickNewsDao): GuokrHandpickNewsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(GuokrHandpickNewsLocalDataSource::javaClass) {
                    INSTANCE = GuokrHandpickNewsLocalDataSource(appExecutors, guokrHandpickNewsDao)
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
        val list = mGuokrHandpickNewsDao.queryAllByOffsetAndLimit(offset, limit)
        if (list.isNotEmpty()) Result.Success(list) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun getFavorites(): Result<List<GuokrHandpickNewsResult>> = withContext(mAppExecutors.ioContext) {
        val favorites = mGuokrHandpickNewsDao.queryAllFavorites()
        if (favorites.isNotEmpty()) Result.Success(favorites) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun getItem(itemId: Int): Result<GuokrHandpickNewsResult> = withContext(mAppExecutors.ioContext) {
        val item = mGuokrHandpickNewsDao.queryItemById(itemId)
        if (item != null) Result.Success(item) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun favoriteItem(itemId: Int, favorite: Boolean) {
        withContext(mAppExecutors.ioContext) {
            mGuokrHandpickNewsDao.queryItemById(itemId)?.let {
                it.isFavorite = favorite
                mGuokrHandpickNewsDao.update(it)
            }
        }
    }

    override suspend fun saveAll(list: List<GuokrHandpickNewsResult>) {
        withContext(mAppExecutors.ioContext) {
            mGuokrHandpickNewsDao.insertAll(list)
        }
    }

}
