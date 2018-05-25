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
import com.marktony.zhihudaily.database.converter.DoubanTypeConverters
import kotlinx.android.parcel.Parcelize

/**
 * Created by lizhaotailang on 2017/5/20.
 *
 * Immutable model class for douban moment details.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */

@Entity(tableName = "douban_moment_content")
@TypeConverters(DoubanTypeConverters::class)
@Parcelize
@SuppressLint("ParcelCreator")
data class DoubanMomentContent(

        @ColumnInfo(name = "display_style")
        @Expose
        @SerializedName("display_style")
        val displayStyle: Int,

        @ColumnInfo(name = "short_url")
        @Expose
        @SerializedName("short_url")
        val shortUrl: String,

        @ColumnInfo(name = "abstract")
        @Expose
        @SerializedName("abstract")
        val abs: String,

        @ColumnInfo(name = "app_css")
        @Expose
        @SerializedName("app_css")
        val appCss: Int,

        @ColumnInfo(name = "like_count")
        @Expose
        @SerializedName("like_count")
        val likeCount: Int,

        @ColumnInfo(name = "thumbs")
        @Expose
        @SerializedName("thumbs")
        val thumbs: List<DoubanMomentNewsThumbs>,

        @ColumnInfo(name = "created_time")
        @Expose
        @SerializedName("created_time")
        val createdTime: String,

        @ColumnInfo(name = "id")
        @PrimaryKey
        @Expose
        @SerializedName("id")
        val id: Int = 0,

        @ColumnInfo(name = "is_editor_choice")
        @Expose
        @SerializedName("is_editor_choice")
        val isEditorChoice: Boolean,

        @ColumnInfo(name = "original_url")
        @Expose
        @SerializedName("original_url")
        val originalUrl: String,

        @ColumnInfo(name = "content")
        @Expose
        @SerializedName("content")
        val content: String,

        @ColumnInfo(name = "share_pic_url")
        @Expose
        @SerializedName("share_pic_url")
        val sharePicUrl: String,

        @ColumnInfo(name = "type")
        @Expose
        @SerializedName("type")
        val type: String,

        @ColumnInfo(name = "is_liked")
        @Expose
        @SerializedName("is_liked")
        val isLiked: Boolean,

        @ColumnInfo(name = "photos")
        @Expose
        @SerializedName("photos")
        val photos: List<DoubanMomentNewsThumbs>,

        @ColumnInfo(name = "published_time")
        @Expose
        @SerializedName("published_time")
        val publishedTime: String,

        @ColumnInfo(name = "url")
        @Expose
        @SerializedName("url")
        val url: String,

        @ColumnInfo(name = "column")
        @Expose
        @SerializedName("column")
        val column: String,

        @ColumnInfo(name = "comments_count")
        @Expose
        @SerializedName("comments_count")
        val commentsCount: Int,

        @ColumnInfo(name = "title")
        @Expose
        @SerializedName("title")
        val title: String

) : Parcelable
