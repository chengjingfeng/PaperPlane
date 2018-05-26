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
import com.marktony.zhihudaily.data.DoubanMomentNewsPosts

/**
 * Created by lizhaotailang on 2017/6/15.
 *
 * Interface for database access on [DoubanMomentNewsPosts] related operations.
 */

@Dao
interface DoubanMomentNewsDao {

    @Query("SELECT * FROM douban_moment_news WHERE timestamp BETWEEN (:timestamp - 24*60*60*1000 + 1) AND :timestamp ORDER BY timestamp ASC")
    fun queryAllByDate(timestamp: Long): List<DoubanMomentNewsPosts>

    @Query("SELECT * FROM douban_moment_news WHERE id = :id")
    fun queryItemById(id: Int): DoubanMomentNewsPosts?

    @Query("SELECT * FROM douban_moment_news WHERE favorite = 1")
    fun queryAllFavorites(): List<DoubanMomentNewsPosts>

    @Query("SELECT * FROM douban_moment_news WHERE (timestamp < :timestamp) AND (favorite = 0)")
    fun queryAllTimeoutItems(timestamp: Long): List<DoubanMomentNewsPosts>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<DoubanMomentNewsPosts>)

    @Update
    fun update(item: DoubanMomentNewsPosts)

    @Delete
    fun delete(item: DoubanMomentNewsPosts)

}
