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
import android.arch.persistence.room.Embedded
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by lizhaotailang on 2017/6/17.
 *
 * Immutable model class for guokr handpick new author. See the json string for more details.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */
@Parcelize
@SuppressLint("ParcelCreator")
data class GuokrHandpickNewsAuthor(

        @ColumnInfo(name = "author_ukey")
        @Expose
        @SerializedName("ukey")
        val ukey: String,

        @ColumnInfo(name = "author_is_title_authorized")
        @Expose
        @SerializedName("is_title_authorized")
        val isTitleAuthorized: Boolean,

        @ColumnInfo(name = "author_nickname")
        @Expose
        @SerializedName("nickname")
        val nickname: String,

        @ColumnInfo(name = "author_master_category")
        @Expose
        @SerializedName("master_category")
        val masterCategory: String,

        @ColumnInfo(name = "author_amended_reliability")
        @Expose
        @SerializedName("amended_reliability")
        val amendedReliability: String,

        @ColumnInfo(name = "author_is_exists")
        @Expose
        @SerializedName("is_exists")
        val isExists: Boolean,

        @ColumnInfo(name = "author_title")
        @Expose
        @SerializedName("title")
        val title: String,

        @ColumnInfo(name = "author_url")
        @Expose
        @SerializedName("url")
        val url: String,

        @ColumnInfo(name = "author_gender")
        @Expose
        @SerializedName("gender")
        val gender: String?,

        @ColumnInfo(name = "author_followers_count")
        @Expose
        @SerializedName("followers_count")
        val followersCount: Int,

        @Embedded
        @Expose
        @SerializedName("avatar")
        val avatar: GuokrHandpickNewsAvatar,

        @ColumnInfo(name = "author_resource_url")
        @Expose
        @SerializedName("resource_url")
        val resourceUrl: String

) : Parcelable
