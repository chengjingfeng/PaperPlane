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

import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Main entry point for accessing the [ZhihuDailyNewsQuestion]s data.
 */

interface ZhihuDailyNewsDataSource {

    interface LoadZhihuDailyNewsCallback {

        fun onNewsLoaded(list: List<ZhihuDailyNewsQuestion>)

        fun onDataNotAvailable()

    }

    interface GetNewsItemCallback {

        fun onItemLoaded(item: ZhihuDailyNewsQuestion)

        fun onDataNotAvailable()

    }

    fun getZhihuDailyNews(forceUpdate: Boolean, clearCache: Boolean, date: Long, callback: LoadZhihuDailyNewsCallback)

    fun getFavorites(callback: LoadZhihuDailyNewsCallback)

    fun getItem(itemId: Int, callback: GetNewsItemCallback)

    fun favoriteItem(itemId: Int, favorite: Boolean)

    fun saveAll(list: List<ZhihuDailyNewsQuestion>)

}
