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

package com.marktony.zhihudaily.database.dao

import android.arch.persistence.room.*
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion

/**
 * Created by lizhaotailang on 2017/6/15.
 *
 * Interface for database access on [com.marktony.zhihudaily.data.ZhihuDailyNews] related operations.
 */

@Dao
interface ZhihuDailyNewsDao {

    @Query("SELECT * FROM zhihu_daily_news WHERE timestamp <= :timestamp ORDER BY timestamp ASC")
    fun queryAllByDate(timestamp: Long): List<ZhihuDailyNewsQuestion>

    @Query("SELECT * FROM zhihu_daily_news WHERE id = :id")
    fun queryItemById(id: Int): ZhihuDailyNewsQuestion?

    @Query("SELECT * FROM zhihu_daily_news WHERE favorite = 1")
    fun queryAllFavorites(): List<ZhihuDailyNewsQuestion>

    @Query("SELECT * FROM zhihu_daily_news WHERE (timestamp < :timestamp) AND (favorite = 0)")
    fun queryAllTimeoutItems(timestamp: Long): List<ZhihuDailyNewsQuestion>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<ZhihuDailyNewsQuestion>)

    @Update
    fun update(item: ZhihuDailyNewsQuestion)

    @Delete
    fun delete(item: ZhihuDailyNewsQuestion)

}
