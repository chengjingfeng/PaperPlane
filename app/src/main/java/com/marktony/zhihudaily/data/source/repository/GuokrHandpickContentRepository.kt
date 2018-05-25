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

import com.marktony.zhihudaily.data.GuokrHandpickContentResult
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickContentDataSource

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Concrete implementation to load [GuokrHandpickContentResult] from the data sources into a cache.
 *
 * Use the remote data source firstly, which is obtained from the server.
 * If the remote data was not available, then use the local data source,
 * which was from the locally persisted in database.
 */

class GuokrHandpickContentRepository private constructor(
        private val mRemoteDataSource: GuokrHandpickContentDataSource,
        private val mLocalDataSource: GuokrHandpickContentDataSource
) : GuokrHandpickContentDataSource {

    private var mContent: GuokrHandpickContentResult? = null

    companion object {

        private var INSTANCE: GuokrHandpickContentRepository? = null

        fun getInstance(remoteDataSource: GuokrHandpickContentDataSource,
                        localDataSource: GuokrHandpickContentDataSource): GuokrHandpickContentRepository {
            if (INSTANCE == null) {
                INSTANCE = GuokrHandpickContentRepository(remoteDataSource, localDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getGuokrHandpickContent(id: Int, callback: GuokrHandpickContentDataSource.LoadGuokrHandpickContentCallback) {
        mContent?.let {
            callback.onContentLoaded(it)
            return
        }

        mRemoteDataSource.getGuokrHandpickContent(id, object : GuokrHandpickContentDataSource.LoadGuokrHandpickContentCallback {
            override fun onContentLoaded(content: GuokrHandpickContentResult) {
                if (mContent == null) {
                    mContent = content
                    saveContent(content)
                }
                callback.onContentLoaded(content)
            }

            override fun onDataNotAvailable() {
                mLocalDataSource.getGuokrHandpickContent(id, object : GuokrHandpickContentDataSource.LoadGuokrHandpickContentCallback {
                    override fun onContentLoaded(content: GuokrHandpickContentResult) {
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

    override fun saveContent(content: GuokrHandpickContentResult) {
        mLocalDataSource.saveContent(content)
        mRemoteDataSource.saveContent(content)
    }

}
