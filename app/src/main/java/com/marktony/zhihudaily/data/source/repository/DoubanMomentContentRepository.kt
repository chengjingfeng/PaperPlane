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

import com.marktony.zhihudaily.data.DoubanMomentContent
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentContentDataSource

/**
 * Created by lizhaotailang on 2017/5/25.
 *
 * Concrete implementation to load [DoubanMomentContent] from the data sources into a cache.
 *
 * Use the remote data source firstly, which is obtained from the server.
 * If the remote data was not available, then use the local data source,
 * which was from the locally persisted in database.
 *
 */

class DoubanMomentContentRepository private constructor(
        private val mRemoteDataSource: DoubanMomentContentDataSource,
        private val mLocalDataSource: DoubanMomentContentDataSource
) : DoubanMomentContentDataSource {

    private var mContent: DoubanMomentContent? = null

    companion object {

        private var INSTANCE: DoubanMomentContentRepository? = null

        fun getInstance(remoteDataSource: DoubanMomentContentDataSource,
                        localDataSource: DoubanMomentContentDataSource): DoubanMomentContentRepository {
            if (INSTANCE == null) {
                INSTANCE = DoubanMomentContentRepository(remoteDataSource, localDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getDoubanMomentContent(id: Int, callback: DoubanMomentContentDataSource.LoadDoubanMomentContentCallback) {
        mContent?.let {
            callback.onContentLoaded(it)
            return
        }

        // Get data from net first.
        mRemoteDataSource.getDoubanMomentContent(id, object : DoubanMomentContentDataSource.LoadDoubanMomentContentCallback {
            override fun onContentLoaded(content: DoubanMomentContent) {
                if (mContent == null) {
                    mContent = content
                    saveContent(content)
                }
                callback.onContentLoaded(content)
            }

            override fun onDataNotAvailable() {
                mLocalDataSource.getDoubanMomentContent(id, object : DoubanMomentContentDataSource.LoadDoubanMomentContentCallback {
                    override fun onContentLoaded(content: DoubanMomentContent) {
                        if (mContent == null) {
                            mContent = content
                        }
                        callback.onContentLoaded(content)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun saveContent(content: DoubanMomentContent) {
        mLocalDataSource.saveContent(content)
        mRemoteDataSource.saveContent(content)
    }

}
