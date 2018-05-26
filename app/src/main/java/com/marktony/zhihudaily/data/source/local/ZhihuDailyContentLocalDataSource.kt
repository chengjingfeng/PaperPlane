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
import com.marktony.zhihudaily.data.ZhihuDailyContent
import com.marktony.zhihudaily.data.source.LocalDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyContentDataSource
import com.marktony.zhihudaily.database.dao.ZhihuDailyContentDao
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Concrete implementation of a [ZhihuDailyContent] data source as database.
 */

class ZhihuDailyContentLocalDataSource private constructor(
        val mAppExecutors: AppExecutors,
        val mZhihuDailyContentDao: ZhihuDailyContentDao
) : ZhihuDailyContentDataSource {

    companion object {

        private var INSTANCE: ZhihuDailyContentLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, zhihuDailyContentDao: ZhihuDailyContentDao): ZhihuDailyContentLocalDataSource {
            if (INSTANCE == null) {
                synchronized(ZhihuDailyContentLocalDataSource::javaClass) {
                    INSTANCE = ZhihuDailyContentLocalDataSource(appExecutors, zhihuDailyContentDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }

    }

    override suspend fun getZhihuDailyContent(id: Int): Result<ZhihuDailyContent> = withContext(mAppExecutors.ioContext) {
        val content = mZhihuDailyContentDao.queryContentById(id)
        if (content != null) Result.Success(content) else Result.Error(LocalDataNotFoundException())
    }


    override suspend fun saveContent(content: ZhihuDailyContent) {
        withContext(mAppExecutors.ioContext) {
            mZhihuDailyContentDao.insert(content)
        }
    }

}
