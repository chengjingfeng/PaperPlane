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

package com.marktony.zhihudaily.retrofit

import com.marktony.zhihudaily.data.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Interface of retrofit requests. API included.
 */

interface RetrofitService {

    companion object {

        const val ZHIHU_DAILY_BASE = "https://news-at.zhihu.com/api/4/news/"

        const val DOUBAN_MOMENT_BASE = "https://moment.douban.com/api/"

        const val GUOKR_HANDPICK_BASE = "http://apis.guokr.com/minisite/"

    }

    interface ZhihuDailyService {

        @GET("before/{date}")
        fun getZhihuList(@Path("date") date: String): Call<ZhihuDailyNews>

        @GET("{id}")
        fun getZhihuContent(@Path("id") id: Int): Call<ZhihuDailyContent>

    }

    interface DoubanMomentService {

        @GET("stream/date/{date}")
        fun getDoubanList(@Path("date") date: String): Call<DoubanMomentNews>

        @GET("post/{id}")
        fun getDoubanContent(@Path("id") id: Int): Call<DoubanMomentContent>

    }

    interface GuokrHandpickService {

        @GET("article.json?retrieve_type=by_minisite")
        fun getGuokrHandpick(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<GuokrHandpickNews>

        @GET("article/{id}.json")
        fun getGuokrContent(@Path("id") id: Int): Call<GuokrHandpickContent>

    }

}
