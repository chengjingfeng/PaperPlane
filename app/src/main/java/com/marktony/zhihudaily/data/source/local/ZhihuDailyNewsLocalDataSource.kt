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
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyNewsDataSource
import com.marktony.zhihudaily.database.AppDatabase

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Concrete implementation of a [ZhihuDailyNewsQuestion] data source as database.
 */

class ZhihuDailyNewsLocalDataSource private constructor(context: Context) : ZhihuDailyNewsDataSource {

    private var mDb: AppDatabase = AppDatabase.getInstance(context)

    companion object {

        private var INSTANCE: ZhihuDailyNewsLocalDataSource? = null

        fun getInstance(context: Context): ZhihuDailyNewsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = ZhihuDailyNewsLocalDataSource(context)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getZhihuDailyNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback) {

        object : AsyncTask<Void, Void, List<ZhihuDailyNewsQuestion>>() {

            override fun doInBackground(vararg voids: Void): List<ZhihuDailyNewsQuestion> {
                return mDb.zhihuDailyNewsDao().queryAllByDate(date)
            }

            override fun onPostExecute(list: List<ZhihuDailyNewsQuestion>?) {
                super.onPostExecute(list)
                if (list == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onNewsLoaded(list)
                }
            }

        }.execute()
    }

    override fun getFavorites(callback: ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback) {

        object : AsyncTask<Void, Void, List<ZhihuDailyNewsQuestion>>() {

            override fun doInBackground(vararg voids: Void): List<ZhihuDailyNewsQuestion> {
                return mDb.zhihuDailyNewsDao().queryAllFavorites()
            }

            override fun onPostExecute(list: List<ZhihuDailyNewsQuestion>?) {
                super.onPostExecute(list)
                if (list == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onNewsLoaded(list)
                }
            }
        }.execute()
    }

    override fun getItem(itemId: Int, callback: ZhihuDailyNewsDataSource.GetNewsItemCallback) {

        object : AsyncTask<Void, Void, ZhihuDailyNewsQuestion>() {
            override fun doInBackground(vararg voids: Void): ZhihuDailyNewsQuestion {
                return mDb.zhihuDailyNewsDao().queryItemById(itemId)
            }

            override fun onPostExecute(item: ZhihuDailyNewsQuestion?) {
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
            val tmp = mDb.zhihuDailyNewsDao().queryItemById(itemId)
            tmp.isFavorite = favorite
            mDb.zhihuDailyNewsDao().update(tmp)
        }.start()
    }

    override fun saveAll(list: List<ZhihuDailyNewsQuestion>) {
        Thread {
            mDb.beginTransaction()
            try {
                mDb.zhihuDailyNewsDao().insertAll(list)
                mDb.setTransactionSuccessful()
            } finally {
                mDb.endTransaction()
            }
        }.start()
    }

}
