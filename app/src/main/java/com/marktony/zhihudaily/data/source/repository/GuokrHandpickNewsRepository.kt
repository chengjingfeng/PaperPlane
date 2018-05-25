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

import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickDataSource
import com.marktony.zhihudaily.util.formatGuokrHandpickTimeStringToLong
import java.util.*

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Concrete implementation to load [GuokrHandpickNewsResult] from the data sources into a cache.
 *
 * Use the remote data source firstly, which is obtained from the server.
 * If the remote data was not available, then use the local data source,
 * which was from the locally persisted in database.
 */

class GuokrHandpickNewsRepository private constructor(
        private val mRemoteDataSource: GuokrHandpickDataSource,
        private val mLocalDataSource: GuokrHandpickDataSource
) : GuokrHandpickDataSource {

    private var mCachedItems: MutableMap<Int, GuokrHandpickNewsResult> = LinkedHashMap()

    companion object {

        private var INSTANCE: GuokrHandpickNewsRepository? = null

        fun getInstance(remoteDataSource: GuokrHandpickDataSource, localDataSource: GuokrHandpickDataSource): GuokrHandpickNewsRepository {
            if (INSTANCE == null) {
                INSTANCE = GuokrHandpickNewsRepository(remoteDataSource, localDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getGuokrHandpickNews(forceUpdate: Boolean, clearCache: Boolean, offset: Int, limit: Int, callback: GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback) {

        if (!forceUpdate) {
            callback.onNewsLoad(ArrayList(mCachedItems.values))
            return
        }

        mRemoteDataSource.getGuokrHandpickNews(false, clearCache, offset, limit, object : GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback {
            override fun onNewsLoad(list: List<GuokrHandpickNewsResult>) {
                refreshCache(clearCache, list)
                callback.onNewsLoad(ArrayList(mCachedItems.values))

                // Save whole list to database.
                saveAll(list)
            }

            override fun onDataNotAvailable() {
                mLocalDataSource.getGuokrHandpickNews(false, clearCache, offset, limit, object : GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback {
                    override fun onNewsLoad(list: List<GuokrHandpickNewsResult>) {
                        refreshCache(clearCache, list)
                        callback.onNewsLoad(ArrayList(mCachedItems.values))
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun getFavorites(callback: GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback) {

        mLocalDataSource.getFavorites(object : GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback {
            override fun onNewsLoad(list: List<GuokrHandpickNewsResult>) {
                callback.onNewsLoad(list)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })

    }

    override fun getItem(itemId: Int, callback: GuokrHandpickDataSource.GetNewsItemCallback) {
        val item = getItemWithId(itemId)

        if (item != null) {
            callback.onItemLoaded(item)
            return
        }

        mLocalDataSource.getItem(itemId, object : GuokrHandpickDataSource.GetNewsItemCallback {

            override fun onItemLoaded(item: GuokrHandpickNewsResult) {
                mCachedItems[item.id] = item
                callback.onItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                mRemoteDataSource.getItem(itemId, object : GuokrHandpickDataSource.GetNewsItemCallback {
                    override fun onItemLoaded(item: GuokrHandpickNewsResult) {
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

    override fun saveAll(list: List<GuokrHandpickNewsResult>) {
        for (item in list) {
            // Set the timestamp.
            item.timestamp = formatGuokrHandpickTimeStringToLong(item.datePublished)
            mCachedItems[item.id] = item
        }

        mLocalDataSource.saveAll(list)
        mRemoteDataSource.saveAll(list)

    }

    private fun refreshCache(clearCache: Boolean, list: List<GuokrHandpickNewsResult>) {
        if (clearCache) {
            mCachedItems.clear()
        }
        for (item in list) {
            mCachedItems[item.id] = item
        }
    }

    private fun getItemWithId(itemId: Int): GuokrHandpickNewsResult? = if (mCachedItems.isEmpty()) null else mCachedItems[itemId]

}
