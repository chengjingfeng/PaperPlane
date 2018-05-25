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

package com.marktony.zhihudaily.data.source.datasource

import com.marktony.zhihudaily.data.DoubanMomentNewsPosts

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Main entry point for accessing [DoubanMomentNewsPosts]s data.
 */

interface DoubanMomentNewsDataSource {

    interface LoadDoubanMomentDailyCallback {

        fun onNewsLoaded(list: List<DoubanMomentNewsPosts>)

        fun onDataNotAvailable()

    }

    interface GetNewsItemCallback {

        fun onItemLoaded(item: DoubanMomentNewsPosts)

        fun onDataNotAvailable()

    }

    fun getDoubanMomentNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: LoadDoubanMomentDailyCallback)

    fun getFavorites(callback: LoadDoubanMomentDailyCallback)

    fun getItem(id: Int, callback: GetNewsItemCallback)

    fun favoriteItem(itemId: Int, favorite: Boolean)

    fun saveAll(list: List<DoubanMomentNewsPosts>)

}
