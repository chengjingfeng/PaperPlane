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
import com.marktony.zhihudaily.data.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuokrHandpickContentDao {

    private lateinit var database: AppDatabase

    companion object {
        private val DEFAULT_IMAGE = ""
        private val DEFAULT_IS_REPLYABLE = true
        private val DEFAULT_CHANNELS = listOf(GuokrHandpickContentChannel(
                "http://www.guokr.com/scientific/channel/hot/",
                "2014-05-23T16:22:09.282129+08:00",
                "\\u70ed\\u70b9",
                "hot", 1916))
        private val DEFAULT_CHANNEL_KEYS = listOf("hot")
        private val DEFAULT_PREFACE = ""
        private val DEFAULT_ID = 442977
        private val DEFAULT_SUBJECT = GuokrHandpickContentChannel(
                "http://www.guokr.com/scientific/subject/food/",
                "2014-05-23T16:22:09.282129+08:00",
                "\\u98df\\u7269",
                "food",
                596)
        private val DEFAULT_COPYRIGHT = "owned_by_guokr"
        private val DEFAULT_AUTHOR = GuokrHandpickNewsAuthor(
                "08fpc7",
                false,
                "\\u7389\\u5b50\\u6851",
                "personal",
                "0",
                true,
                "\\u98df\\u54c1\\u79d1\\u5b66\\u7855\\u58eb\\uff0c\\u679c\\u58f3\\u7f51\\u7f16\\u8f91",
                "http://www.guokr.com/i/0014169607/",
                null,
                22306,
                GuokrHandpickNewsAvatar("https://3-im.guokr.com/1XElhEGExSSNXC7Z3F2ZC_M7akiW_uq9rKeXbAwVotQ4AAAAOAAAAFBO.png?imageView2/1/w/160/h/160",
                        "https://3-im.guokr.com/1XElhEGExSSNXC7Z3F2ZC_M7akiW_uq9rKeXbAwVotQ4AAAAOAAAAFBO.png?imageView2/1/w/24/h/24",
                        "https://3-im.guokr.com/1XElhEGExSSNXC7Z3F2ZC_M7akiW_uq9rKeXbAwVotQ4AAAAOAAAAFBO.png?imageView2/1/w/48/h/48"),
                "http://apis.guokr.com/community/user/08fpc7.json")
        private val DEFAULT_IMAGE_DESCRIPTION = ""
        private val DEFAULT_CONTENT = "<div><p>\\u6700\\u8fd1\\uff0c\\u201c\\u4e09\\u6587\\u9c7c\\u201d\\u6210\\u4e3a\\u4e86\\u7f51\\u7edc\\u70ed\\u70b9\\u3002..."
        private val DEFAULT_IS_SHOW_SUMMARY = false
        private val DEFAULT_MINISITE_KEY = "health"
        private val DEFAULT_IMAGE_INFO = GuokrHandpickContentImageInfo(
                url = "https://1-im.guokr.com/CfbPYahkN89mPQFzHw_6hPRiXLhJX-jUWnr9Me1RzQRKAQAAuQAAAEpQ.jpg",
                width = 330,
                height = 185)
        private val DEFAULT_SUBJECT_KEY = "food"
        private val DEFAULT_MINISITE = GuokrHandpickContentMinisite(
                name = "\\u5065\\u5eb7\\u671d\\u4e5d\\u665a\\u4e94",
                url = "http://www.guokr.com/site/health/",
                introduction = "\\u6bcf\\u5929\\u7684\\u671d\\u4e5d\\u665a\\u4e94\\uff0c\\u4e0a\\u51e0\\u7bc7\\u5065\\u5eb7\\u5c0f\\u6587\\uff0c\\u6709\\u8da3\\u3001\\u9760\\u8c31\\uff0c\\u8fd8\\u5f88\\u7ed9\\u529b",
                key = "health",
                dateCreated = "2010-10-20T16:20:43+08:00",
                icon = "https://2-im.guokr.com/gDNaY8kFHIUnyDRLBlb1hWY3fwK9eAuV5icTVSuh7TNuAAAAWgAAAEpQ.jpg")
        private val DEFALUT_TAGS = listOf<String>()
        private val DEFAULT_DATE_PUBLISHED = "2018-05-26T12:45:25+08:00"
        private val DEFAULT_REPLY_COUNT = 5
        private val DEFAULT_IS_AUTHOR_EXTERNAL = false
        private val DEFAULT_RECOMMANDS_COUNT = 5
        private val DEFAULT_TITLE_HIDE = "\\u8679\\u9cdf\\u4e0d\\u662f\\u4e09\\u6587\\u9c7c\\uff0c\\u751f\\u5403\\u53ef\\u80fd\\u8981\\u4eba\\u547d"
        private val DEFAULT_DATE_MODIFIED = "2018-05-26T13:35:25.341478+08:00"
        private val DEFAULT_URL = "http://www.guokr.com/article/442977/"
        private val DEFAULT_TITLE = "\\u8679\\u9cdf\\u4e0d\\u662f\\u4e09\\u6587\\u9c7c\\uff0c\\u751f\\u5403\\u53ef\\u80fd\\u8981\\u4eba\\u547d"
        private val DEFAULT_SMALL_IMAGE = "https://1-im.guokr.com/CfbPYahkN89mPQFzHw_6hPRiXLhJX-jUWnr9Me1RzQRKAQAAuQAAAEpQ.jpg"
        private val DEFAULT_SUMMARY = "\\u4e0d\\u5149\\u8679\\u9cdf\\uff0c\\u6de1\\u6c34\\u9c7c\\u867e\\u90fd\\u4e0d\\u8981\\u751f\\u5403\\u3002"
        private val DEFAULT_UKEY_AUTHOR = "08fpc7"
        private val DEFAULT_DATE_CREATED = "2018-05-26T12:45:25+08:00"
        private val DEFAULT_RESOURCE_URL = "http://apis.guokr.com/minisite/article/442977.json"

        private val DEFAULT_GUOKR_HANDPICK_CONTENT_RESULT = GuokrHandpickContentResult(
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                DEFAULT_CONTENT,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_REPLY_COUNT,
                DEFAULT_IS_AUTHOR_EXTERNAL,
                DEFAULT_RECOMMANDS_COUNT,
                DEFAULT_TITLE_HIDE,
                DEFAULT_DATE_MODIFIED,
                DEFAULT_URL,
                DEFAULT_TITLE,
                DEFAULT_ID,
                DEFAULT_SMALL_IMAGE,
                DEFAULT_SUMMARY,
                DEFAULT_UKEY_AUTHOR,
                DEFAULT_DATE_CREATED,
                DEFAULT_RESOURCE_URL)

        private val NEW_CONTENT = "new content"
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
    fun insertGuokrHandpickContentAndGetById() {
        // When inserting a piece of guokr handpick content
        database.guokrHandpickContentDao().insert(DEFAULT_GUOKR_HANDPICK_CONTENT_RESULT)

        // When getting the guokr handpick by id from the database
        val loaded = database.guokrHandpickContentDao().queryContentById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertContent(
                loaded,
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                DEFAULT_CONTENT,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_REPLY_COUNT,
                DEFAULT_IS_AUTHOR_EXTERNAL,
                DEFAULT_RECOMMANDS_COUNT,
                DEFAULT_TITLE_HIDE,
                DEFAULT_DATE_MODIFIED,
                DEFAULT_URL,
                DEFAULT_TITLE,
                DEFAULT_SMALL_IMAGE,
                DEFAULT_SUMMARY,
                DEFAULT_UKEY_AUTHOR,
                DEFAULT_DATE_CREATED,
                DEFAULT_RESOURCE_URL)
    }

    @Test
    fun insertContentIgnoredOnConflict() {
        // Given that a piece of guokr handpick content is inserted
        database.guokrHandpickContentDao().insert(DEFAULT_GUOKR_HANDPICK_CONTENT_RESULT)

        // When a piece of guokr handpick content with the same id is inserted
        val newContent = GuokrHandpickContentResult(
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                NEW_CONTENT, // new content
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_REPLY_COUNT,
                DEFAULT_IS_AUTHOR_EXTERNAL,
                DEFAULT_RECOMMANDS_COUNT,
                DEFAULT_TITLE_HIDE,
                DEFAULT_DATE_MODIFIED,
                DEFAULT_URL,
                DEFAULT_TITLE,
                DEFAULT_ID,
                DEFAULT_SMALL_IMAGE,
                DEFAULT_SUMMARY,
                DEFAULT_UKEY_AUTHOR,
                DEFAULT_DATE_CREATED,
                DEFAULT_RESOURCE_URL)
        database.guokrHandpickContentDao().insert(newContent)

        // When getting the guokr handpick content by id from the database
        val loaded = database.guokrHandpickContentDao().queryContentById(DEFAULT_ID)

        // The loaded data contains the expected values
        // The insertion of a piece of guokr handpick content whose id already existed in database
        // will be ignored.
        assertContent(loaded, DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                DEFAULT_CONTENT,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_REPLY_COUNT,
                DEFAULT_IS_AUTHOR_EXTERNAL,
                DEFAULT_RECOMMANDS_COUNT,
                DEFAULT_TITLE_HIDE,
                DEFAULT_DATE_MODIFIED,
                DEFAULT_URL,
                DEFAULT_TITLE,
                DEFAULT_SMALL_IMAGE,
                DEFAULT_SUMMARY,
                DEFAULT_UKEY_AUTHOR,
                DEFAULT_DATE_CREATED,
                DEFAULT_RESOURCE_URL)
    }

    @Test
    fun updateContentAndGetById() {
        // When inserting a piece of guokr handpick content
        database.guokrHandpickContentDao().insert(DEFAULT_GUOKR_HANDPICK_CONTENT_RESULT)

        // When the guokr handpick content is updated
        val updatedContent = GuokrHandpickContentResult(
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                NEW_CONTENT, // new content
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_REPLY_COUNT,
                DEFAULT_IS_AUTHOR_EXTERNAL,
                DEFAULT_RECOMMANDS_COUNT,
                DEFAULT_TITLE_HIDE,
                DEFAULT_DATE_MODIFIED,
                DEFAULT_URL,
                DEFAULT_TITLE,
                DEFAULT_ID,
                DEFAULT_SMALL_IMAGE,
                DEFAULT_SUMMARY,
                DEFAULT_UKEY_AUTHOR,
                DEFAULT_DATE_CREATED,
                DEFAULT_RESOURCE_URL)
        database.guokrHandpickContentDao().update(updatedContent)

        val loaded = database.guokrHandpickContentDao().queryContentById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertContent(loaded,
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                NEW_CONTENT,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_REPLY_COUNT,
                DEFAULT_IS_AUTHOR_EXTERNAL,
                DEFAULT_RECOMMANDS_COUNT,
                DEFAULT_TITLE_HIDE,
                DEFAULT_DATE_MODIFIED,
                DEFAULT_URL,
                DEFAULT_TITLE,
                DEFAULT_SMALL_IMAGE,
                DEFAULT_SUMMARY,
                DEFAULT_UKEY_AUTHOR,
                DEFAULT_DATE_CREATED,
                DEFAULT_RESOURCE_URL)
    }

    @Test
    fun deleteContentAndGettingAllContent() {
        // Given a piece of guokr handpick content inserted
        database.guokrHandpickContentDao().insert(DEFAULT_GUOKR_HANDPICK_CONTENT_RESULT)

        // When deleting a piece of guokr handpick content by id
        database.guokrHandpickContentDao().delete(DEFAULT_GUOKR_HANDPICK_CONTENT_RESULT)

        // When getting the guokr handpick content
        val content = database.guokrHandpickContentDao().queryContentById(DEFAULT_ID)

        // The content is null
        assertThat(content, `is`(nullValue()))
    }

    private fun assertContent(result: GuokrHandpickContentResult?,
                              image: String,
                              isReplyable: Boolean,
                              channels: List<GuokrHandpickContentChannel>,
                              channelKeys: List<String>,
                              preface: String,
                              id: Int,
                              subject: GuokrHandpickContentChannel,
                              copyright: String,
                              author: GuokrHandpickNewsAuthor,
                              imageDescription: String,
                              content: String,
                              isShowSummary: Boolean,
                              minisiteKey: String,
                              imageInfo: GuokrHandpickContentImageInfo,
                              subjectKkey: String,
                              minisite: GuokrHandpickContentMinisite,
                              tags: List<String>,
                              datePublished: String,
                              repliesCount: Int,
                              isAuthorExternal: Boolean,
                              recommendsCount: Int,
                              titleHide: String,
                              dateModified: String,
                              url: String,
                              title: String,
                              smallImage: String,
                              summary: String,
                              ukeyAuthor: String,
                              dateCreated: String,
                              resourceUrl: String) {
        assertThat<GuokrHandpickContentResult>(result as GuokrHandpickContentResult, notNullValue())
        assertThat(result.image, `is`(image))
        assertThat(result.isReplyable, `is`(isReplyable))
        assertThat(result.channels, `is`(channels))
        assertThat(result.channelKeys, `is`(channelKeys))
        assertThat(result.preface, `is`(preface))
        assertThat(result.subject, `is`(subject))
        assertThat(result.copyright, `is`(copyright))
        assertThat(result.author, `is`(author))
        assertThat(result.imageDescription, `is`(imageDescription))
        assertThat(result.content, `is`(content))
        assertThat(result.isShowSummary, `is`(isShowSummary))
        assertThat(result.minisiteKey, `is`(minisiteKey))
        assertThat(result.imageInfo, `is`(imageInfo))
        assertThat(result.subjectKey, `is`(subjectKkey))
        assertThat(result.minisite, `is`(minisite))
        assertThat(result.tags, `is`(tags))
        assertThat(result.datePublished, `is`(datePublished))
        assertThat(result.repliesCount, `is`(repliesCount))
        assertThat(result.isAuthorExternal, `is`(isAuthorExternal))
        assertThat(result.recommendsCount, `is`(recommendsCount))
        assertThat(result.titleHide, `is`(titleHide))
        assertThat(result.dateModified, `is`(dateModified))
        assertThat(result.url, `is`(url))
        assertThat(result.title, `is`(title))
        assertThat(result.id, `is`(id))
        assertThat(result.smallImage, `is`(smallImage))
        assertThat(result.summary, `is`(summary))
        assertThat(result.ukeyAuthor, `is`(ukeyAuthor))
        assertThat(result.dateCreated, `is`(dateCreated))
        assertThat(result.resourceUrl, `is`(resourceUrl))
    }

}