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

package com.marktony.zhihudaily.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * A util class for formatting the date between string and long.
 */

fun formatZhihuDailyDateLongToString(date: Long): String {
    val sDate: String
    val d = Date(date + 24 * 60 * 60 * 1000)
    val format = SimpleDateFormat("yyyyMMdd")
    sDate = format.format(d)

    return sDate
}

fun formatZhihuDailyDateStringToLong(date: String): Long {
    var d: Date? = null
    try {
        d = SimpleDateFormat("yyyyMMdd").parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return if (d == null) 0 else d.time
}

fun formatDoubanMomentDateLongToString(date: Long): String {
    val sDate: String
    val d = Date(date)
    val format = SimpleDateFormat("yyyy-MM-dd")
    sDate = format.format(d)

    return sDate
}

fun formatDoubanMomentDateStringToLong(date: String): Long {
    var d: Date? = null
    try {
        d = SimpleDateFormat("yyyy-MM-dd").parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return if (d == null) 0 else d.time
}

fun formatGuokrHandpickTimeStringToLong(date: String): Long {
    var d: Date? = null
    try {
        d = SimpleDateFormat("yyyy-MM-dd").parse(date.substring(0, 10))
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return if (d == null) 0 else d.time
}

