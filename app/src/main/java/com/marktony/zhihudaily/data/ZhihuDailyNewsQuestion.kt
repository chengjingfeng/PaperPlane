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

package com.marktony.zhihudaily.data

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.marktony.zhihudaily.database.converter.StringTypeConverter
import kotlinx.android.parcel.Parcelize

/**
 * Created by lizhaotailang on 2017/6/17.
 *
 * Immutable model class for zhihu daily news question.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */
@Entity(tableName = "zhihu_daily_news")
@TypeConverters(StringTypeConverter::class)
@Parcelize
@SuppressLint("ParcelCreator")
data class ZhihuDailyNewsQuestion(

        @ColumnInfo(name = "images")
        @Expose
        @SerializedName("images")
        val images: List<String>?,

        @ColumnInfo(name = "type")
        @Expose
        @SerializedName("type")
        val type: Int,

        @PrimaryKey
        @ColumnInfo(name = "id")
        @Expose
        @SerializedName("id")
        val id: Int = 0,

        @ColumnInfo(name = "ga_prefix")
        @Expose
        @SerializedName("ga_prefix")
        val gaPrefix: String,

        @ColumnInfo(name = "title")
        @Expose
        @SerializedName("title")
        val title: String,

        @ColumnInfo(name = "favorite")
        @Expose
        var isFavorite: Boolean = false,

        @ColumnInfo(name = "timestamp")
        @Expose
        var timestamp: Long = 0

) : Parcelable
