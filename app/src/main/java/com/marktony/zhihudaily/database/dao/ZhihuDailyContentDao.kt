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
import com.marktony.zhihudaily.data.ZhihuDailyContent


/**
 * Created by lizhaotailang on 2017/6/15.
 *
 * Interface for database access on [ZhihuDailyContent] related operations.
 */

@Dao
interface ZhihuDailyContentDao {

    @Query("SELECT * FROM zhihu_daily_content WHERE id = :id")
    fun queryContentById(id: Int): ZhihuDailyContent?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(content: ZhihuDailyContent)

    @Update
    fun update(content: ZhihuDailyContent)

    @Delete
    fun delete(content: ZhihuDailyContent)

}
