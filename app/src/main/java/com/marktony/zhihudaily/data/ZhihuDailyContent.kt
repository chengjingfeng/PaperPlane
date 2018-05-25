/*
 * Copyright 2016 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License"),
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

@Entity(tableName = "zhihu_daily_content")
@TypeConverters(StringTypeConverter::class)
@Parcelize
@SuppressLint("ParcelCreator")
data class ZhihuDailyContent(

        @ColumnInfo(name = "body")
        @Expose
        @SerializedName("body")
        val body: String,

        @ColumnInfo(name = "image_source")
        @Expose
        @SerializedName("image_source")
        val imageSource: String,

        @ColumnInfo(name = "title")
        @Expose
        @SerializedName("title")
        val title: String,

        @ColumnInfo(name = "image")
        @Expose
        @SerializedName("image")
        val image: String,

        @ColumnInfo(name = "share_url")
        @Expose
        @SerializedName("share_url")
        val shareUrl: String,

        @ColumnInfo(name = "js")
        @Expose
        @SerializedName("js")
        val js: List<String>,

        @Expose
        @SerializedName("ga_prefix")
        val gaPrefix: String,

        @ColumnInfo(name = "images")
        @Expose
        @SerializedName("images")
        val images: List<String>,

        @ColumnInfo(name = "type")
        @Expose
        @SerializedName("type")
        val type: Int,

        @PrimaryKey
        @ColumnInfo(name = "id")
        @Expose
        @SerializedName("id")
        val id: Int = 0,

        @ColumnInfo(name = "css")
        @Expose
        @SerializedName("css")
        val css: List<String>

) : Parcelable