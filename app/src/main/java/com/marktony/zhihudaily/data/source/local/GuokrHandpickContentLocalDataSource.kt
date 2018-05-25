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
import com.marktony.zhihudaily.data.GuokrHandpickContentResult
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickContentDataSource
import com.marktony.zhihudaily.database.AppDatabase

/**
 * Created by lizhaotailang on 2017/5/26.
 *
 * Concrete implementation of a [GuokrHandpickContentResult] data source as database.
 */

class GuokrHandpickContentLocalDataSource private constructor(context: Context) : GuokrHandpickContentDataSource {

    private var mDb: AppDatabase = AppDatabase.getInstance(context)

    companion object {

        private var INSTANCE: GuokrHandpickContentLocalDataSource? = null

        fun getInstance(context: Context): GuokrHandpickContentLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = GuokrHandpickContentLocalDataSource(context)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getGuokrHandpickContent(id: Int, callback: GuokrHandpickContentDataSource.LoadGuokrHandpickContentCallback) {

        object : AsyncTask<Void, Void, GuokrHandpickContentResult>() {

            override fun doInBackground(vararg voids: Void): GuokrHandpickContentResult {
                return mDb!!.guokrHandpickContentDao().queryContentById(id)
            }

            override fun onPostExecute(content: GuokrHandpickContentResult?) {
                super.onPostExecute(content)
                if (content == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onContentLoaded(content)
                }
            }
        }.execute()
    }

    override fun saveContent(content: GuokrHandpickContentResult) {
        if (true) {
            Thread {
                mDb.beginTransaction()
                try {
                    mDb.guokrHandpickContentDao().insert(content)
                    mDb.setTransactionSuccessful()
                } finally {
                    mDb.endTransaction()
                }
            }.start()
        }
    }

}
