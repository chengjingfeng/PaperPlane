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
import com.marktony.zhihudaily.data.GuokrHandpickContentResult
import com.marktony.zhihudaily.data.source.LocalDataNotFoundException
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickContentDataSource
import com.marktony.zhihudaily.database.dao.GuokrHandpickContentDao
import com.marktony.zhihudaily.util.AppExecutors
import kotlinx.coroutines.experimental.withContext

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Concrete implementation of a [GuokrHandpickContentResult] data source as database.
 */

class GuokrHandpickContentLocalDataSource private constructor(
        val mAppExecutors: AppExecutors,
        val mGuokrHandpickContentDao: GuokrHandpickContentDao
) : GuokrHandpickContentDataSource {

    companion object {

        private var INSTANCE: GuokrHandpickContentLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, guokrHandpickContentDao: GuokrHandpickContentDao): GuokrHandpickContentLocalDataSource {
            if (INSTANCE == null) {
                synchronized(GuokrHandpickContentLocalDataSource::javaClass) {
                    INSTANCE = GuokrHandpickContentLocalDataSource(appExecutors, guokrHandpickContentDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }

    override suspend fun getGuokrHandpickContent(id: Int): Result<GuokrHandpickContentResult> = withContext(mAppExecutors.ioContext) {
        val content = mGuokrHandpickContentDao.queryContentById(id)
        if (content != null) Result.Success(content) else Result.Error(LocalDataNotFoundException())
    }

    override suspend fun saveContent(content: GuokrHandpickContentResult) {
        withContext(mAppExecutors.ioContext) {
            mGuokrHandpickContentDao.insert(content)
        }
    }

}
