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
import com.marktony.zhihudaily.data.source.Result
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

    override suspend fun getZhihuDailyContent(id: Int): Result<ZhihuDailyContent> {
        if (mContent != null && mContent?.id == id) {
            return Result.Success(mContent!!)
        }

        val remoteResult = mRemoteDataSource.getZhihuDailyContent(id)
        return if (remoteResult is Result.Success) {
            mContent = remoteResult.data
            saveContent(remoteResult.data)

            remoteResult
        } else {
            mLocalDataSource.getZhihuDailyContent(id).also {
                if (it is Result.Success) {
                    mContent = it.data
                }
            }
        }
    }

    override suspend fun saveContent(content: ZhihuDailyContent) {
        mLocalDataSource.saveContent(content)
    }

}
