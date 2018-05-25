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
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by lizhaotailang on 2017/6/17.
 *
 * Immutable model class for douban moment new author.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */
@Parcelize
@SuppressLint("ParcelCreator")
data class DoubanMomentNewsAuthor(

        @ColumnInfo(name = "is_followed")
        @Expose
        @SerializedName("is_followed")
        val isFollowed: Boolean,

        @ColumnInfo(name = "uid")
        @Expose
        @SerializedName("uid")
        val uid: String,

        @ColumnInfo(name = "author_url")
        @Expose
        @SerializedName("url")
        val url: String,

        @ColumnInfo(name = "author_avatar")
        @Expose
        @SerializedName("avatar")
        val avatar: String,

        @ColumnInfo(name = "author_name")
        @Expose
        @SerializedName("name")
        val name: String,

        @ColumnInfo(name = "author_is_special_user")
        @Expose
        @SerializedName("is_special_user")
        val isSpecialUser: Boolean,

        @ColumnInfo(name = "author_n_posts")
        @Expose
        @SerializedName("n_posts")
        val nPosts: Int,

        @ColumnInfo(name = "author_alt")
        @Expose
        @SerializedName("alt")
        val alt: String,

        @ColumnInfo(name = "author_large_avatar")
        @Expose
        @SerializedName("large_avatar")
        val largeAvatar: String,

        @ColumnInfo(name = "author_id")
        @Expose
        @SerializedName("id")
        val id: String,

        @ColumnInfo(name = "author_is_auth_author")
        @Expose
        @SerializedName("is_auth_author")
        val isAuthAuthor: Boolean

) : Parcelable
