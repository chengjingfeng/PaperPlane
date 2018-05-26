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
import com.marktony.zhihudaily.data.source.Result

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Main entry point for accessing the [ZhihuDailyNewsQuestion]s data.
 */

interface ZhihuDailyNewsDataSource {

    suspend fun getZhihuDailyNews(forceUpdate: Boolean, clearCache: Boolean, date: Long): Result<List<ZhihuDailyNewsQuestion>>

    suspend fun getFavorites(): Result<List<ZhihuDailyNewsQuestion>>

    suspend fun getItem(itemId: Int): Result<ZhihuDailyNewsQuestion>

    suspend fun favoriteItem(itemId: Int, favorite: Boolean)

    suspend fun saveAll(list: List<ZhihuDailyNewsQuestion>)

}
