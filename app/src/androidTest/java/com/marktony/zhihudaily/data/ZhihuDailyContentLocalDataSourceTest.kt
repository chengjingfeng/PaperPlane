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

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.local.ZhihuDailyContentLocalDataSource
import com.marktony.zhihudaily.database.AppDatabase
import com.marktony.zhihudaily.util.SingleExecutors
import com.marktony.zhihudaily.util.runBlockingSilent
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ZhihuDailyContentLocalDataSourceTest {

    private lateinit var localDataSource: ZhihuDailyContentLocalDataSource
    private lateinit var database: AppDatabase

    companion object {
        private val DEFAULT_BODY = "<div class=\\\"main-wrap content-wrap\\\">\\n<div class=\\\"headline\\\">\\n\\n..."
        private val DEFAULT_IMAGE_SOURCE = "《安妮 · 霍尔》"
        private val DEFAULT_TITLE = "小事 · 直到麻木了，就到了要走的时候"
        private val DEFAULT_IMAGE = "https:\\/\\/pic1.zhimg.com\\/v2-85aa2c5a36962571b329ea65cff8bf74.jpg"
        private val DEFAULT_SHARE_URL = "http:\\/\\/daily.zhihu.com\\/story\\/9683773"
        private val DEFAULT_JS = listOf<String>()
        private val DEFAULT_ID = 9683773
        private val DEFAULT_GA_PREFIX = "052422"
        private val DEFAULT_IMAGES = listOf("https:\\/\\/pic1.zhimg.com\\/v2-5c5f75baa911c58f2476334180f5cda0.jpg")
        private val DEFAULT_TYPE = 0
        private val DEFAULT_CSS = listOf("http:\\/\\/news-at.zhihu.com\\/css\\/news_qa.auto.css?v=4b3e3")
    }

    @Before
    fun setup() {
        // Using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase::class.java)
                .build()

        // Make sure that we're not keeping a reference to the wrong instance.
        ZhihuDailyContentLocalDataSource.clearInstance()
        localDataSource = ZhihuDailyContentLocalDataSource.getInstance(SingleExecutors(), database.zhihuDailyContentDao())
    }

    @After
    fun cleanUp() {
        database.close()
        ZhihuDailyContentLocalDataSource.clearInstance()
    }

    @Test
    fun testPreConditions() {
        assertNotNull(localDataSource)
    }

    @Test
    fun saveContent_retrievesContent() = runBlockingSilent {
        // Given a piece of new zhihu daily content
        val content = ZhihuDailyContent(DEFAULT_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_GA_PREFIX, DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_CSS)

        with(localDataSource) {
            // When saved into the persistent repository
            saveContent(content)

            // Then the zhihu daily content can be retrieved from the persistent repository
            val result = getZhihuDailyContent(content.id)
            assertThat(result, instanceOf(Result.Success::class.java))
            if (result is Result.Success) {
                assertContent(result.data, DEFAULT_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TYPE, DEFAULT_CSS)
            }
        }
    }

    private fun assertContent(content: ZhihuDailyContent?,
                              body: String,
                              imageSource: String,
                              title: String,
                              image: String,
                              shareUrl: String,
                              js: List<String>,
                              id: Int,
                              gaPrefix: String,
                              type: Int,
                              css: List<String>) {
        assertThat<ZhihuDailyContent>(content as ZhihuDailyContent, notNullValue())
        assertThat(content.body, `is`(body))
        assertThat(content.imageSource, `is`(imageSource))
        assertThat(content.title, `is`(title))
        assertThat(content.image, `is`(image))
        assertThat(content.shareUrl, `is`(shareUrl))
        assertThat(content.js, `is`(js))
        assertThat(content.id, `is`(id))
        assertThat(content.gaPrefix, `is`(gaPrefix))
        assertThat(content.type, `is`(type))
        assertThat(content.css, `is`(css))
    }

}