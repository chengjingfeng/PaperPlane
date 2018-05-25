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
import android.arch.persistence.room.*
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.marktony.zhihudaily.database.converter.DoubanTypeConverters
import kotlinx.android.parcel.Parcelize

/**
 * Created by lizhaotailang on 2017/6/17.
 *
 * Immutable model class for douban moment news posts. See the json string for more details.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */
@Entity(tableName = "douban_moment_news")
@TypeConverters(DoubanTypeConverters::class)
@Parcelize
@SuppressLint("ParcelCreator")
data class DoubanMomentNewsPosts(

        @ColumnInfo(name = "display_style")
        @Expose
        @SerializedName("display_style")
        val displayStyle: Int,

        @ColumnInfo(name = "is_editor_choice")
        @Expose
        @SerializedName("is_editor_choice")
        val is_editor_choice: Boolean,

        @ColumnInfo(name = "published_time")
        @Expose
        @SerializedName("published_time")
        val publishedTime: String,

        @ColumnInfo(name = "url")
        @Expose
        @SerializedName("url")
        val url: String,

        @ColumnInfo(name = "short_url")
        @Expose
        @SerializedName("short_url")
        val shortUrl: String,

        @ColumnInfo(name = "is_liked")
        @Expose
        @SerializedName("is_liked")
        val is_liked: Boolean,

        @Embedded
        @Expose
        @SerializedName("author")
        val author: DoubanMomentNewsAuthor,

        @ColumnInfo(name = "column")
        @Expose
        @SerializedName("column")
        val column: String,

        @ColumnInfo(name = "app_css")
        @Expose
        @SerializedName("app_css")
        val appCss: Int,

        @ColumnInfo(name = "abstract")
        @Expose
        @SerializedName("abstract")
        val abs: String,

        @ColumnInfo(name = "date")
        @Expose
        @SerializedName("date")
        val date: String,

        @ColumnInfo(name = "like_count")
        @Expose
        @SerializedName("like_count")
        val likeCount: Int,

        @ColumnInfo(name = "comments_count")
        @Expose
        @SerializedName("comments_count")
        val commentsCount: Int,

        @ColumnInfo(name = "thumbs")
        @Expose
        @SerializedName("thumbs")
        val thumbs: List<DoubanMomentNewsThumbs>,

        @ColumnInfo(name = "created_time")
        @Expose
        @SerializedName("created_time")
        val createdTime: String,

        @ColumnInfo(name = "title")
        @Expose
        @SerializedName("title")
        val title: String,

        @ColumnInfo(name = "share_pic_url")
        @Expose
        @SerializedName("share_pic_url")
        val sharePicUrl: String,

        @ColumnInfo(name = "type")
        @Expose
        @SerializedName("type")
        val type: String,

        @ColumnInfo(name = "id")
        @PrimaryKey
        @Expose
        @SerializedName("id")
        val id: Int = 0,

        @ColumnInfo(name = "favorite")
        @Expose
        var isFavorite: Boolean,

        @ColumnInfo(name = "timestamp")
        @Expose
        var timestamp: Long

) : Parcelable
