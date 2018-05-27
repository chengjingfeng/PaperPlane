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

package com.marktony.zhihudaily.database

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.marktony.zhihudaily.data.ZhihuDailyContent
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZhihuDailyContentDaoTest {

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

        private val DEFAULT_ZHIHU_CONTENT = ZhihuDailyContent(DEFAULT_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_GA_PREFIX, DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_CSS)

        private val NEW_BODY = "NEW BODY"
    }

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertZhihuDailyContentAndGetById() {
        // When inserting a piece of zhihu daily content
        database.zhihuDailyContentDao().insert(DEFAULT_ZHIHU_CONTENT)

        // When getting the zhihu daily news story by id from the database
        val loaded = database.zhihuDailyContentDao().queryContentById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertContent(loaded, DEFAULT_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TYPE, DEFAULT_CSS)
    }

    @Test
    fun insertContentIgnoredOnConflict() {
        // Given that a piece of zhihu daily content is inserted
        database.zhihuDailyContentDao().insert(DEFAULT_ZHIHU_CONTENT)

        // When a piece of zhihu daily content with the same id is inserted
        val newContent = ZhihuDailyContent(NEW_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_GA_PREFIX, DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_CSS)
        database.zhihuDailyContentDao().insert(newContent)

        // When getting the zhihu daily news story by id from the database
        val loaded = database.zhihuDailyContentDao().queryContentById(DEFAULT_ID)

        // The loaded data contains the expected values
        // The insertion of a piece of zhihu daily content whose id already existed in database
        // will be ignored.
        assertContent(loaded, DEFAULT_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TYPE, DEFAULT_CSS)
    }

    @Test
    fun updateContentAndGetById() {
        // When inserting a piece of zhihu daily content
        database.zhihuDailyContentDao().insert(DEFAULT_ZHIHU_CONTENT)

        // When the zhihu daily news content is updated
        val updatedContent = ZhihuDailyContent(NEW_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_GA_PREFIX, DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_CSS)
        database.zhihuDailyContentDao().update(updatedContent)

        val loaded = database.zhihuDailyContentDao().queryContentById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertContent(loaded, NEW_BODY, DEFAULT_IMAGE_SOURCE, DEFAULT_TITLE, DEFAULT_IMAGE, DEFAULT_SHARE_URL, DEFAULT_JS, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TYPE, DEFAULT_CSS)
    }

    @Test
    fun deleteContentAndGettingAllContent() {
        // Given a piece of zhihu daily content inserted
        database.zhihuDailyContentDao().insert(DEFAULT_ZHIHU_CONTENT)

        // When deleting a piece of zhihu daily content by id
        database.zhihuDailyContentDao().delete(DEFAULT_ZHIHU_CONTENT)

        // When getting the zhihu daily content
        val content = database.zhihuDailyContentDao().queryContentById(DEFAULT_ID)

        // The content is null
        assertThat(content, `is`(nullValue()))
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