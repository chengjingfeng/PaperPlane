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
import com.marktony.zhihudaily.data.DoubanMomentContent
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentContentDataSource
import com.marktony.zhihudaily.database.AppDatabase

/**
 * Created by lizhaotailang on 2017/5/25.
 *
 * Concrete implementation of a [DoubanMomentContent] data source as database.
 */

class DoubanMomentContentLocalDataSource private constructor(context: Context) : DoubanMomentContentDataSource {

    private val mDb: AppDatabase = AppDatabase.getInstance(context)

    companion object {

        private var INSTANCE: DoubanMomentContentLocalDataSource? = null

        fun getInstance(context: Context): DoubanMomentContentLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = DoubanMomentContentLocalDataSource(context)
            }
            return INSTANCE!!
        }
    }

    override fun getDoubanMomentContent(id: Int, callback: DoubanMomentContentDataSource.LoadDoubanMomentContentCallback) {

        object : AsyncTask<Void, Void, DoubanMomentContent>() {

            override fun doInBackground(vararg voids: Void): DoubanMomentContent {
                return mDb!!.doubanMomentContentDao().queryContentById(id)
            }

            override fun onPostExecute(content: DoubanMomentContent?) {
                super.onPostExecute(content)
                if (content == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onContentLoaded(content)
                }
            }
        }.execute()
    }

    override fun saveContent(content: DoubanMomentContent) {
        if (true) {
            Thread {
                mDb.beginTransaction()
                try {
                    mDb.doubanMomentContentDao().insert(content)
                    mDb.setTransactionSuccessful()
                } finally {
                    mDb.endTransaction()
                }
            }.start()
        }
    }

}
