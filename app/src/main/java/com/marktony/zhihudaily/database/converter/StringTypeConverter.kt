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

package com.marktony.zhihudaily.database.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Created by lizhaotailang on 2017/6/17.
 *
 * Type converters. Converts a list of [String] to a [String] and back.
 */

class StringTypeConverter {

    companion object {

        @TypeConverter
        @JvmStatic
        fun stringListToString(strings: List<String>): String = Gson().toJson(strings)

        @TypeConverter
        @JvmStatic
        fun stringToStringList(string: String): List<String> {
            val listType = object : TypeToken<ArrayList<String>>() {}.type
            return Gson().fromJson<List<String>>(string, listType)
        }

    }

}
