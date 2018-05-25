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

import android.content.Context
import android.os.AsyncTask
import com.marktony.zhihudaily.data.ZhihuDailyContent
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyContentDataSource
import com.marktony.zhihudaily.database.AppDatabase

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Concrete implementation of a [ZhihuDailyContent] data source as database.
 */

class ZhihuDailyContentLocalDataSource private constructor(context: Context) : ZhihuDailyContentDataSource {

    private var mDb: AppDatabase = AppDatabase.getInstance(context)

    companion object {

        private var INSTANCE: ZhihuDailyContentLocalDataSource? = null

        fun getInstance(context: Context): ZhihuDailyContentLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = ZhihuDailyContentLocalDataSource(context)
            }
            return INSTANCE!!
        }
    }

    override fun getZhihuDailyContent(id: Int, callback: ZhihuDailyContentDataSource.LoadZhihuDailyContentCallback) {
        object : AsyncTask<Void, Void, ZhihuDailyContent>() {

            override fun doInBackground(vararg voids: Void): ZhihuDailyContent {
                return mDb!!.zhihuDailyContentDao().queryContentById(id)
            }

            override fun onPostExecute(content: ZhihuDailyContent?) {
                super.onPostExecute(content)
                if (content == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onContentLoaded(content)
                }
            }

        }.execute()
    }

    override fun saveContent(content: ZhihuDailyContent) {
        Thread {
            mDb.beginTransaction()
            try {
                mDb.zhihuDailyContentDao().insert(content)
                mDb.setTransactionSuccessful()
            } finally {
                mDb.endTransaction()
            }
        }.start()
    }

}
