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

import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentNewsDataSource
import com.marktony.zhihudaily.database.AppDatabase

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * * Concrete implementation of a [DoubanMomentNewsPosts] data source as database .
 */

class DoubanMomentNewsLocalDataSource private constructor(context: Context) : DoubanMomentNewsDataSource {

    private var mDb: AppDatabase = AppDatabase.getInstance(context)

    companion object {

        private var INSTANCE: DoubanMomentNewsLocalDataSource? = null

        fun getInstance(context: Context): DoubanMomentNewsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = DoubanMomentNewsLocalDataSource(context)
            }
            return INSTANCE!!
        }
    }

    override fun getDoubanMomentNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback) {

        object : AsyncTask<Void, Void, List<DoubanMomentNewsPosts>>() {

            override fun doInBackground(vararg voids: Void): List<DoubanMomentNewsPosts> {
                return mDb.doubanMomentNewsDao().queryAllByDate(date)
            }

            override fun onPostExecute(list: List<DoubanMomentNewsPosts>?) {
                super.onPostExecute(list)
                if (list == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onNewsLoaded(list)
                }
            }
        }.execute()
    }

    override fun getFavorites(callback: DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback) {

        object : AsyncTask<Void, Void, List<DoubanMomentNewsPosts>>() {

            override fun doInBackground(vararg voids: Void): List<DoubanMomentNewsPosts> {
                return mDb.doubanMomentNewsDao().queryAllFavorites()
            }

            override fun onPostExecute(list: List<DoubanMomentNewsPosts>?) {
                super.onPostExecute(list)
                if (list == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onNewsLoaded(list)
                }
            }
        }.execute()
    }

    override fun getItem(id: Int, callback: DoubanMomentNewsDataSource.GetNewsItemCallback) {

        object : AsyncTask<Void, Void, DoubanMomentNewsPosts>() {

            override fun doInBackground(vararg voids: Void): DoubanMomentNewsPosts {
                return mDb.doubanMomentNewsDao().queryItemById(id)
            }

            override fun onPostExecute(item: DoubanMomentNewsPosts?) {
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
            val tmp = mDb.doubanMomentNewsDao().queryItemById(itemId)
            tmp.isFavorite = favorite
            mDb.doubanMomentNewsDao().update(tmp)
        }.start()
    }

    override fun saveAll(list: List<DoubanMomentNewsPosts>) {
        Thread {
            mDb.beginTransaction()
            try {
                mDb.doubanMomentNewsDao().insertAll(list)
                mDb.setTransactionSuccessful()
            } finally {
                mDb.endTransaction()
            }
        }.start()
    }

}
