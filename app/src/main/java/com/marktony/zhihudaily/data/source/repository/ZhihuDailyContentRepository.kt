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

import com.marktony.zhihudaily.data.ZhihuDailyContent
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyContentDataSource

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Concrete implementation to load [ZhihuDailyContent] from the data sources into a cache.
 *
 * Use the remote data source firstly, which is obtained from the server.
 * If the remote data was not available, then use the local data source,
 * which was from the locally persisted in database.
 */

class ZhihuDailyContentRepository private constructor(
        private val mRemoteDataSource: ZhihuDailyContentDataSource,
        private val mLocalDataSource: ZhihuDailyContentDataSource
) : ZhihuDailyContentDataSource {

    private var mContent: ZhihuDailyContent? = null

    companion object {

        var INSTANCE: ZhihuDailyContentRepository? = null

        fun getInstance(remoteDataSource: ZhihuDailyContentDataSource,
                        localDataSource: ZhihuDailyContentDataSource): ZhihuDailyContentRepository {
            if (INSTANCE == null) {
                INSTANCE = ZhihuDailyContentRepository(remoteDataSource, localDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getZhihuDailyContent(id: Int, callback: ZhihuDailyContentDataSource.LoadZhihuDailyContentCallback) {
        mContent?.let {
            callback.onContentLoaded(it)
            return
        }

        mRemoteDataSource.getZhihuDailyContent(id, object : ZhihuDailyContentDataSource.LoadZhihuDailyContentCallback {
            override fun onContentLoaded(content: ZhihuDailyContent) {
                if (mContent == null) {
                    mContent = content
                    saveContent(content)
                }
                callback.onContentLoaded(content)
            }

            override fun onDataNotAvailable() {
                mLocalDataSource.getZhihuDailyContent(id, object : ZhihuDailyContentDataSource.LoadZhihuDailyContentCallback {
                    override fun onContentLoaded(content: ZhihuDailyContent) {
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

    override fun saveContent(content: ZhihuDailyContent) {
        // Note: Setting of timestamp was done in the {@link ZhihuDailyContentLocalDataSource} class.
        mLocalDataSource.saveContent(content)
        mRemoteDataSource.saveContent(content)
    }

}
