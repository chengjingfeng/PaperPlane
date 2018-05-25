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
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Immutable model class for guokr handpick news.
 * Entity class for [com.google.gson.Gson] and [android.arch.persistence.room.Room].
 */

@Parcelize
@SuppressLint("ParcelCreator")
data class GuokrHandpickNews(

        @Expose
        @SerializedName("now")
        val now: String,

        @Expose
        @SerializedName("ok")
        val isOk: Boolean,

        @Expose
        @SerializedName("limit")
        val limit: Int,

        @Expose
        @SerializedName("result")
        val result: List<GuokrHandpickNewsResult>,

        @Expose
        @SerializedName("offset")
        val offset: Int,

        @Expose
        @SerializedName("total")
        val total: Int

) : Parcelable
