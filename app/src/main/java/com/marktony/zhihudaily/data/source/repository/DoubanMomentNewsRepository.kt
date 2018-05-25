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

    override fun getDoubanMomentNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback) {

        if (!forceUpdate) {
            callback.onNewsLoaded(ArrayList(mCachedItems.values))
            return
        }

        mRemoteDataSource.getDoubanMomentNews(false, clearCache, date, object : DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback {
            override fun onNewsLoaded(list: List<DoubanMomentNewsPosts>) {
                refreshCache(clearCache, list)
                callback.onNewsLoaded(ArrayList(mCachedItems.values))

                saveAll(list)
            }

            override fun onDataNotAvailable() {
                mLocalDataSource.getDoubanMomentNews(false, clearCache, date, object : DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback {
                    override fun onNewsLoaded(list: List<DoubanMomentNewsPosts>) {
                        refreshCache(clearCache, list)
                        callback.onNewsLoaded(ArrayList(mCachedItems.values))
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })

    }

    override fun getFavorites(callback: DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback) {
        mLocalDataSource.getFavorites(object : DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback {
            override fun onNewsLoaded(list: List<DoubanMomentNewsPosts>) {
                callback.onNewsLoaded(list)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getItem(id: Int, callback: DoubanMomentNewsDataSource.GetNewsItemCallback) {
        val cachedItem = getItemWithId(id)

        if (cachedItem != null) {
            callback.onItemLoaded(cachedItem)
            return
        }

        mLocalDataSource.getItem(id, object : DoubanMomentNewsDataSource.GetNewsItemCallback {
            override fun onItemLoaded(item: DoubanMomentNewsPosts) {
                if (false) {
                    mCachedItems = LinkedHashMap()
                }
                mCachedItems[item.id] = item
                callback.onItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                mRemoteDataSource.getItem(id, object : DoubanMomentNewsDataSource.GetNewsItemCallback {
                    override fun onItemLoaded(item: DoubanMomentNewsPosts) {
                        mCachedItems[item.id] = item
                        callback.onItemLoaded(item)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun favoriteItem(itemId: Int, favorite: Boolean) {
        mRemoteDataSource.favoriteItem(itemId, favorite)
        mLocalDataSource.favoriteItem(itemId, favorite)

        val cachedItem = getItemWithId(itemId)
        if (cachedItem != null) {
            cachedItem.isFavorite = favorite
        }
    }

    override fun saveAll(list: List<DoubanMomentNewsPosts>) {
        for (item in list) {
            // Set the timestamp.
            item.timestamp = formatDoubanMomentDateStringToLong(item.publishedTime)
            mCachedItems[item.id] = item
        }

        mLocalDataSource.saveAll(list)
        mRemoteDataSource.saveAll(list)
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
