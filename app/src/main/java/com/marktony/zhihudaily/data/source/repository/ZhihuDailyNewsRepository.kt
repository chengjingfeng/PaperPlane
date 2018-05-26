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

package com.marktony.zhihudaily.data.source.repository

import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyNewsDataSource
import java.util.*

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Concrete implementation to load [ZhihuDailyNewsQuestion]s from the data sources into a cache.
 *
 * Use the remote data source firstly, which is obtained from the server.
 * If the remote data was not available, then use the local data source,
 * which was from the locally persisted in database.
 */

class ZhihuDailyNewsRepository private constructor(
        private val mRemoteDataSource: ZhihuDailyNewsDataSource,
        private val mLocalDataSource: ZhihuDailyNewsDataSource
) : ZhihuDailyNewsDataSource {

    private var mCachedItems: MutableMap<Int, ZhihuDailyNewsQuestion> = LinkedHashMap()

    companion object {

        private var INSTANCE: ZhihuDailyNewsRepository? = null

        fun getInstance(remoteDataSource: ZhihuDailyNewsDataSource,
                        localDataSource: ZhihuDailyNewsDataSource): ZhihuDailyNewsRepository {
            if (INSTANCE == null) {
                INSTANCE = ZhihuDailyNewsRepository(remoteDataSource, localDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override suspend fun getZhihuDailyNews(forceUpdate: Boolean, clearCache: Boolean, date: Long): Result<List<ZhihuDailyNewsQuestion>> {
        if (!forceUpdate) {
            return Result.Success(mCachedItems.values.toList())
        }

        val result = mRemoteDataSource.getZhihuDailyNews(false, clearCache, date)
        return if (result is Result.Success) {
            refreshCache(clearCache, result.data)
            saveAll(result.data)

            result
        } else {
            mLocalDataSource.getZhihuDailyNews(false, false, date).also {
                if (it is Result.Success) {
                    refreshCache(clearCache, it.data)
                }
            }
        }
    }

    override suspend fun getFavorites(): Result<List<ZhihuDailyNewsQuestion>> = mLocalDataSource.getFavorites()

    override suspend fun getItem(itemId: Int): Result<ZhihuDailyNewsQuestion> {
        val cachedItem = getItemWithId(itemId)

        if (cachedItem != null) {
            return Result.Success(cachedItem)
        }

        return mLocalDataSource.getItem(itemId).also {
            if (it is Result.Success) {
                mCachedItems[it.data.id] = it.data
            }
        }
    }

    override suspend fun favoriteItem(itemId: Int, favorite: Boolean) {
        mLocalDataSource.favoriteItem(itemId, favorite)

        val cachedItem = getItemWithId(itemId)
        if (cachedItem != null) {
            cachedItem.isFavorite = favorite
        }
    }

    override suspend fun saveAll(list: List<ZhihuDailyNewsQuestion>) {
        mLocalDataSource.saveAll(list)

        for (item in list) {
            mCachedItems[item.id] = item
        }
    }

    private fun refreshCache(clearCache: Boolean, list: List<ZhihuDailyNewsQuestion>) {

        if (clearCache) {
            mCachedItems.clear()
        }
        for (item in list) {
            mCachedItems[item.id] = item
        }
    }

    private fun getItemWithId(id: Int): ZhihuDailyNewsQuestion? = if (mCachedItems.isEmpty()) null else mCachedItems[id]

}
