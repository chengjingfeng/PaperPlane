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
 * Immutable model class for douban moment news large image. See the json string for more details.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */
@Parcelize
@SuppressLint("ParcelCreator")
data class DoubanMomentNewsLarge(

        @ColumnInfo(name = "large_url")
        @Expose
        @SerializedName("url")
        val url: String,

        @ColumnInfo(name = "large_width")
        @Expose
        @SerializedName("width")
        val width: Int,

        @ColumnInfo(name = "large_height")
        @Expose
        @SerializedName("height")
        val height: Int

) : Parcelable
