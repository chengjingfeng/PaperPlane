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
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickDataSource
import com.marktony.zhihudaily.database.AppDatabase

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Concrete implementation of a [GuokrHandpickNewsResult] data source as database.
 */

class GuokrHandpickNewsLocalDataSource private constructor(context: Context) : GuokrHandpickDataSource {

    private var mDb: AppDatabase = AppDatabase.getInstance(context)

    companion object {

        private var INSTANCE: GuokrHandpickNewsLocalDataSource? = null

        fun getInstance(context: Context): GuokrHandpickNewsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = GuokrHandpickNewsLocalDataSource(context)
            }
            return INSTANCE!!
        }
    }

    override fun getGuokrHandpickNews(forceUpdate: Boolean, clearCache: Boolean, offset: Int, limit: Int, callback: GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback) {

        object : AsyncTask<Void, Void, List<GuokrHandpickNewsResult>>() {

            override fun doInBackground(vararg voids: Void): List<GuokrHandpickNewsResult> {
                return mDb.guokrHandpickNewsDao().queryAllByOffsetAndLimit(offset, limit)
            }

            override fun onPostExecute(list: List<GuokrHandpickNewsResult>?) {
                super.onPostExecute(list)
                if (list == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onNewsLoad(list)
                }
            }
        }.execute()
    }

    override fun getFavorites(callback: GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback) {

        object : AsyncTask<Void, Void, List<GuokrHandpickNewsResult>>() {

            override fun doInBackground(vararg voids: Void): List<GuokrHandpickNewsResult> {
                return mDb.guokrHandpickNewsDao().queryAllFavorites()
            }

            override fun onPostExecute(list: List<GuokrHandpickNewsResult>?) {
                super.onPostExecute(list)
                if (list == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onNewsLoad(list)
                }
            }
        }.execute()
    }

    override fun getItem(itemId: Int, callback: GuokrHandpickDataSource.GetNewsItemCallback) {
        object : AsyncTask<Void, Void, GuokrHandpickNewsResult>() {

            override fun doInBackground(vararg voids: Void): GuokrHandpickNewsResult {
                return mDb.guokrHandpickNewsDao().queryItemById(itemId)
            }

            override fun onPostExecute(item: GuokrHandpickNewsResult?) {
                super.onPostExecute(item)
                if (item == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onItemLoaded(item)
                }
            }
        }.execute()
    }

    override fun favoriteItem(itemId: Int, favorite: Boolean) {
        Thread {
            val tmp = mDb.guokrHandpickNewsDao().queryItemById(itemId)
            tmp.isFavorite = favorite
            mDb.guokrHandpickNewsDao().update(tmp)
        }.start()
    }

    override fun saveAll(list: List<GuokrHandpickNewsResult>) {
        Thread {
            mDb.beginTransaction()
            try {
                mDb.guokrHandpickNewsDao().insertAll(list)
                mDb.setTransactionSuccessful()
            } finally {
                mDb.endTransaction()
            }
        }.start()
    }

}
