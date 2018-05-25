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
import com.marktony.zhihudaily.database.converter.GuokrResultTypeConverter
import kotlinx.android.parcel.Parcelize

/**
 * Created by lizhaotailang on 2017/6/17.
 *
 * Immutable model class for guokr news result.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */

@Entity(tableName = "guokr_handpick_news")
@TypeConverters(GuokrResultTypeConverter::class)
@Parcelize
@SuppressLint("ParcelCreator")
data class GuokrHandpickNewsResult(

        @ColumnInfo(name = "image")
        @Expose
        @SerializedName("image")
        val image: String,

        @ColumnInfo(name = "is_replyable")
        @Expose
        @SerializedName("is_replyable")
        val isReplyable: Boolean,

        @ColumnInfo(name = "channels")
        @Expose
        @SerializedName("channels")
        val channels: List<GuokrHandpickContentChannel>?,

        @ColumnInfo(name = "channel_keys")
        @Expose
        @SerializedName("channel_keys")
        val channelKeys: List<String>?,

        @ColumnInfo(name = "preface")
        @Expose
        @SerializedName("preface")
        val preface: String,

        @PrimaryKey
        @ColumnInfo(name = "id")
        @Expose
        @SerializedName("id")
        val id: Int = 0,

        @Embedded
        @Expose
        @SerializedName("subject")
        val subject: GuokrHandpickContentChannel,

        @ColumnInfo(name = "copyright")
        @Expose
        @SerializedName("copyright")
        val copyright: String,

        @Embedded
        @Expose
        @SerializedName("author")
        val author: GuokrHandpickNewsAuthor,

        @ColumnInfo(name = "image_description")
        @Expose
        @SerializedName("image_description")
        val imageDescription: String,

        @ColumnInfo(name = "is_show_summary")
        @Expose
        @SerializedName("is_show_summary")
        val isShowSummary: Boolean,

        @ColumnInfo(name = "minisite_key")
        @Expose
        @SerializedName("minisite_key")
        val minisiteKey: String,

        @Embedded
        @Expose
        @SerializedName("image_info")
        val imageInfo: GuokrHandpickContentImageInfo,

        @ColumnInfo(name = "subject_key")
        @Expose
        @SerializedName("subject_key")
        val subjectKey: String,

        @Embedded
        @Expose
        @SerializedName("minisite")
        val minisite: GuokrHandpickContentMinisite,

        @ColumnInfo(name = "tags")
        @Expose
        @SerializedName("tags")
        val tags: List<String>?,

        @ColumnInfo(name = "date_published")
        @Expose
        @SerializedName("date_published")
        val datePublished: String,

        @ColumnInfo(name = "avatar")
        @Expose
        @SerializedName("avatar")
        val authors: List<GuokrHandpickNewsAuthor>?,

        @ColumnInfo(name = "replies_count")
        @Expose
        @SerializedName("replies_count")
        val repliesCount: Int,

        @ColumnInfo(name = "is_author_external")
        @Expose
        @SerializedName("is_author_external")
        val isAuthorExternal: Boolean,

        @ColumnInfo(name = "recommends_count")
        @Expose
        @SerializedName("recommends_count")
        val recommendsCount: Int,

        @ColumnInfo(name = "title_hide")
        @Expose
        @SerializedName("title_hide")
        val titleHide: String,

        @ColumnInfo(name = "date_modified")
        @Expose
        @SerializedName("date_modified")
        val dateModified: String,

        @ColumnInfo(name = "url")
        @Expose
        @SerializedName("url")
        val url: String,

        @ColumnInfo(name = "title")
        @Expose
        @SerializedName("title")
        val title: String,

        @ColumnInfo(name = "small_image")
        @Expose
        @SerializedName("small_image")
        val smallImage: String,

        @ColumnInfo(name = "summary")
        @Expose
        @SerializedName("summary")
        val summary: String,

        @ColumnInfo(name = "ukey_author")
        @Expose
        @SerializedName("ukey_author")
        val ukeyAuthor: String,

        @ColumnInfo(name = "date_created")
        @Expose
        @SerializedName("date_created")
        val dateCreated: String,

        @ColumnInfo(name = "resource_url")
        @Expose
        @SerializedName("resource_url")
        val resourceUrl: String,

        @ColumnInfo(name = "favorite")
        @Expose
        var isFavorite: Boolean = false,

        @ColumnInfo(name = "timestamp")
        @Expose
        var timestamp: Long

) : Parcelable
