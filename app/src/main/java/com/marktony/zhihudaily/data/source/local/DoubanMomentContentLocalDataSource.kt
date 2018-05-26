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
import com.marktony.zhihudaily.data.DoubanMomentContent
import com.marktony.zhihudaily.data.source.LocalDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentContentDataSource
import com.marktony.zhihudaily.database.dao.DoubanMomentContentDao
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext

/**
 * Created by lizhaotailang on 2017/5/25.
 *
 * Concrete implementation of a [DoubanMomentContent] data source as database.
 */

class DoubanMomentContentLocalDataSource private constructor(
        private val mAppExecutors: AppExecutors,
        private val mDoubanMomentContentDao: DoubanMomentContentDao
) : DoubanMomentContentDataSource {

    companion object {

        private var INSTANCE: DoubanMomentContentLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, doubanMomentContentDao: DoubanMomentContentDao): DoubanMomentContentLocalDataSource {
            if (INSTANCE == null) {
                synchronized(DoubanMomentContentLocalDataSource::javaClass) {
                    INSTANCE = DoubanMomentContentLocalDataSource(appExecutors, doubanMomentContentDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }

    }

    override suspend fun getDoubanMomentContent(id: Int): Result<DoubanMomentContent> = withContext(mAppExecutors.ioContext) {
        val content = mDoubanMomentContentDao.queryContentById(id)
        if (content != null) Result.Success(content) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun saveContent(content: DoubanMomentContent) {
        withContext(mAppExecutors.ioContext) {
            mDoubanMomentContentDao.insert(content)
        }
    }

}
