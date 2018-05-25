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
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyNewsDataSource
import java.util.*

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Concrete implementation to load [ZhihuDailyNewsQuestion]s from the data sources into a cache.
 *
 *
 * Use the remote data source firstly, which is obtained from the server.
 * If the remote data was not available, then use the local data source,
 * which was from the locally persisted in database.
 */

class ZhihuDailyNewsRepository// Prevent direct instantiation.
private constructor(
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

    override fun getZhihuDailyNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback) {

        if (!forceUpdate) {
            callback.onNewsLoaded(ArrayList(mCachedItems.values))
            return
        }

        // Get data by accessing network first.
        mRemoteDataSource.getZhihuDailyNews(false, clearCache, date, object : ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback {
            override fun onNewsLoaded(list: List<ZhihuDailyNewsQuestion>) {
                refreshCache(clearCache, list)
                callback.onNewsLoaded(ArrayList(mCachedItems.values))
                // Save these item to database.
                saveAll(list)
            }

            override fun onDataNotAvailable() {
                mLocalDataSource.getZhihuDailyNews(false, false, date, object : ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback {
                    override fun onNewsLoaded(list: List<ZhihuDailyNewsQuestion>) {
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

    override fun getFavorites(callback: ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback) {
        mLocalDataSource.getFavorites(object : ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback {
            override fun onNewsLoaded(list: List<ZhihuDailyNewsQuestion>) {
                callback.onNewsLoaded(list)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getItem(itemId: Int, callback: ZhihuDailyNewsDataSource.GetNewsItemCallback) {
        val cachedItem = getItemWithId(itemId)

        if (cachedItem != null) {
            callback.onItemLoaded(cachedItem)
            return
        }

        mLocalDataSource.getItem(itemId, object : ZhihuDailyNewsDataSource.GetNewsItemCallback {
            override fun onItemLoaded(item: ZhihuDailyNewsQuestion) {
                if (false) {
                    mCachedItems = LinkedHashMap()
                }
                mCachedItems[item.id] = item
                callback.onItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                mRemoteDataSource.getItem(itemId, object : ZhihuDailyNewsDataSource.GetNewsItemCallback {
                    override fun onItemLoaded(item: ZhihuDailyNewsQuestion) {
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

    override fun saveAll(list: List<ZhihuDailyNewsQuestion>) {
        mLocalDataSource.saveAll(list)
        mRemoteDataSource.saveAll(list)

        for (item in list) {
            // Note:  Setting of timestamp was done in the {@link ZhihuDailyNewsRemoteDataSource} class.
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
