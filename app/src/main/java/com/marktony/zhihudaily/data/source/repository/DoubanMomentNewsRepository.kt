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

import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentNewsDataSource
import com.marktony.zhihudaily.util.formatDoubanMomentDateStringToLong
import java.util.*

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Concrete implementation to load [DoubanMomentNewsPosts] from the data sources into a cache.
 *
 * Use the remote data source firstly, which is obtained from the server.
 * If the remote data was not available, then use the local data source,
 * which was from the locally persisted in database.
 */

class DoubanMomentNewsRepository private constructor(
        private val mRemoteDataSource: DoubanMomentNewsDataSource,
        private val mLocalDataSource: DoubanMomentNewsDataSource
) : DoubanMomentNewsDataSource {

    private var mCachedItems: MutableMap<Int, DoubanMomentNewsPosts> = LinkedHashMap()

    companion object {

        private var INSTANCE: DoubanMomentNewsRepository? = null

        fun getInstance(remoteDataSource: DoubanMomentNewsDataSource,
                        localDataSource: DoubanMomentNewsDataSource): DoubanMomentNewsRepository {
            if (INSTANCE == null) {
                INSTANCE = DoubanMomentNewsRepository(remoteDataSource, localDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override suspend fun getDoubanMomentNews(forceUpdate: Boolean, clearCache: Boolean, date: Long): Result<List<DoubanMomentNewsPosts>> {
        if (!forceUpdate) {
            return Result.Success(mCachedItems.values.toList())
        }

        val remoteResult = mRemoteDataSource.getDoubanMomentNews(false, clearCache, date)
        return if (remoteResult is Result.Success) {
            refreshCache(clearCache, remoteResult.data)
            saveAll(remoteResult.data)

            remoteResult
        } else {
            mLocalDataSource.getDoubanMomentNews(false, clearCache, date).also {
                if (it is Result.Success) {
                    refreshCache(clearCache, it.data)
                }
            }
        }
    }

    override suspend fun getFavorites(): Result<List<DoubanMomentNewsPosts>> = mLocalDataSource.getFavorites()

    override suspend fun getItem(id: Int): Result<DoubanMomentNewsPosts> {
        val cachedItem = getItemWithId(id)

        if (cachedItem != null) {
            return Result.Success(cachedItem)
        }

        return mLocalDataSource.getItem(id).also {
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

    override suspend fun saveAll(list: List<DoubanMomentNewsPosts>) {
        for (item in list) {
            item.timestamp = formatDoubanMomentDateStringToLong(item.publishedTime)
            mCachedItems[item.id] = item
        }

        mLocalDataSource.saveAll(list)
    }

    private fun refreshCache(clearCache: Boolean, list: List<DoubanMomentNewsPosts>) {
        if (clearCache) {
            mCachedItems.clear()
        }
        for (item in list) {
            mCachedItems[item.id] = item
        }
    }

    private fun getItemWithId(id: Int): DoubanMomentNewsPosts? = if (mCachedItems.isEmpty()) null else mCachedItems[id]

}
