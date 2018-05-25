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
 * Immutable model class for douban moment news thumbs. See the json string for more details.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */
@Parcelize
@SuppressLint("ParcelCreator")
data class DoubanMomentNewsThumbs(

        @Embedded
        @Expose
        @SerializedName("medium")
        val medium: DoubanMomentNewsMedium,

        @ColumnInfo(name = "thumb_description")
        @Expose
        @SerializedName("description")
        val description: String,

        @Embedded
        @Expose
        @SerializedName("large")
        val large: DoubanMomentNewsLarge,

        @Expose
        @SerializedName("tag_name")
        val tagName: String,

        @Embedded
        @Expose
        @SerializedName("small")
        val small: DoubanMomentNewsSmall,

        @ColumnInfo(name = "thumb_id")
        @Expose
        @SerializedName("id")
        val id: Int = 0

) : Parcelable
