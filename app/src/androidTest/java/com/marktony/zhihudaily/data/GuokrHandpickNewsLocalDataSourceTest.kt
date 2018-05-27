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
import com.marktony.zhihudaily.data.source.local.GuokrHandpickNewsLocalDataSource
import com.marktony.zhihudaily.database.AppDatabase
import com.marktony.zhihudaily.util.SingleExecutors
import com.marktony.zhihudaily.util.runBlockingSilent
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class GuokrHandpickNewsLocalDataSourceTest {

    private lateinit var localDataSource: GuokrHandpickNewsLocalDataSource
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
        private val DEFAULT_ID_1 = 442977
        private val DEFAULT_ID_2 = 442963
        private val DEFAULT_ID_3 = 442937
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
        private val DEFAULT_AUTHORS = listOf(DEFAULT_AUTHOR)
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
        private val DEFAULT_IS_FAVORITED = false
        private val DEFAULT_TIMESTAMP_1 = 1527333963L
        private val DEFAULT_TIMESTAMP_2 = 1527337920L
        private val DEFAULT_TIMESTAMP_3 = 1527340803L
    }

    @Before
    fun setup() {
        // Using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                AppDatabase::class.java
        ).build()

        // Make sure that we're not keeping a reference to the wrong instance.
        GuokrHandpickNewsLocalDataSource.clearInstance()
        localDataSource = GuokrHandpickNewsLocalDataSource.getInstance(SingleExecutors(), database.guokrHandpickNewsDao())
    }

    @After
    fun cleanUp() {
        database.close()
        GuokrHandpickNewsLocalDataSource.clearInstance()
    }

    @Test
    fun testPreConditions() {
        Assert.assertNotNull(localDataSource)
    }

    @Test
    fun saveItem_retrievesItem() = runBlockingSilent {
        // Given several pieces of new guokr handpick news
        val news1 = GuokrHandpickNewsResult(
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID_1,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_AUTHORS,
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
                DEFAULT_RESOURCE_URL,
                DEFAULT_IS_FAVORITED,
                DEFAULT_TIMESTAMP_1)
        val news2 = GuokrHandpickNewsResult(
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID_2,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_AUTHORS,
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
                DEFAULT_RESOURCE_URL,
                DEFAULT_IS_FAVORITED,
                DEFAULT_TIMESTAMP_2)
        val news3 = GuokrHandpickNewsResult(
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID_3,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_AUTHORS,
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
                DEFAULT_RESOURCE_URL,
                DEFAULT_IS_FAVORITED,
                DEFAULT_TIMESTAMP_3)

        with(localDataSource) {
            // When saved into the persistent repository
            saveAll(listOf(news1, news2, news3))

            val allResult = getGuokrHandpickNews(false, false, 0, 10)
            assertThat(allResult, instanceOf(Result.Success::class.java))

            if (allResult is Result.Success) {
                assertThat(allResult.data.size, `is`(3))
            }

            // Then the guokr handpick news can be retrieved from the persistent repository
            val result = getItem(news1.id)
            assertThat(result, instanceOf(Result.Success::class.java))
            if (result is Result.Success) {
                assertItem(result.data,
                        DEFAULT_IMAGE,
                        DEFAULT_IS_REPLYABLE,
                        DEFAULT_CHANNELS,
                        DEFAULT_CHANNEL_KEYS,
                        DEFAULT_PREFACE,
                        DEFAULT_ID_1,
                        DEFAULT_SUBJECT,
                        DEFAULT_COPYRIGHT,
                        DEFAULT_AUTHOR,
                        DEFAULT_IMAGE_DESCRIPTION,
                        DEFAULT_IS_SHOW_SUMMARY,
                        DEFAULT_MINISITE_KEY,
                        DEFAULT_IMAGE_INFO,
                        DEFAULT_SUBJECT_KEY,
                        DEFAULT_MINISITE,
                        DEFALUT_TAGS,
                        DEFAULT_DATE_PUBLISHED,
                        DEFAULT_AUTHORS,
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
                        DEFAULT_RESOURCE_URL,
                        DEFAULT_IS_FAVORITED,
                        DEFAULT_TIMESTAMP_1)
            }
        }
    }

    @Test
    fun favoriteItem_retrievesFavorites() = runBlockingSilent {
        // Given a piece of new guokr handpick news
        val news = GuokrHandpickNewsResult(
                DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID_1,
                DEFAULT_SUBJECT,
                DEFAULT_COPYRIGHT,
                DEFAULT_AUTHOR,
                DEFAULT_IMAGE_DESCRIPTION,
                DEFAULT_IS_SHOW_SUMMARY,
                DEFAULT_MINISITE_KEY,
                DEFAULT_IMAGE_INFO,
                DEFAULT_SUBJECT_KEY,
                DEFAULT_MINISITE,
                DEFALUT_TAGS,
                DEFAULT_DATE_PUBLISHED,
                DEFAULT_AUTHORS,
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
                DEFAULT_RESOURCE_URL,
                DEFAULT_IS_FAVORITED,
                DEFAULT_TIMESTAMP_1)
        localDataSource.saveAll(listOf(news))

        // When favorited in the persistent repository
        localDataSource.favoriteItem(news.id, true)

        // Then the guokr handpick news can be retrieved from the persistent repository and is favorited
        val result = localDataSource.getItem(news.id)
        assertThat(result, instanceOf(Result.Success::class.java))
        if (result is Result.Success) {
            assertItem(result.data,
                    DEFAULT_IMAGE,
                    DEFAULT_IS_REPLYABLE,
                    DEFAULT_CHANNELS,
                    DEFAULT_CHANNEL_KEYS,
                    DEFAULT_PREFACE,
                    DEFAULT_ID_1,
                    DEFAULT_SUBJECT,
                    DEFAULT_COPYRIGHT,
                    DEFAULT_AUTHOR,
                    DEFAULT_IMAGE_DESCRIPTION,
                    DEFAULT_IS_SHOW_SUMMARY,
                    DEFAULT_MINISITE_KEY,
                    DEFAULT_IMAGE_INFO,
                    DEFAULT_SUBJECT_KEY,
                    DEFAULT_MINISITE,
                    DEFALUT_TAGS,
                    DEFAULT_DATE_PUBLISHED,
                    DEFAULT_AUTHORS,
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
                    DEFAULT_RESOURCE_URL,
                    true,
                    DEFAULT_TIMESTAMP_1)
            assertThat(result.data.isFavorite, `is`(true))
        }

        // Get all the favorites, the favorited guokr handpick news will be listed
        val favorites = localDataSource.getFavorites()
        assertThat(result, instanceOf(Result.Success::class.java))
        if (favorites is Result.Success) {
            assertThat(favorites.data.size, `is`(1))
            assertItem(favorites.data.first(),
                    DEFAULT_IMAGE,
                    DEFAULT_IS_REPLYABLE,
                    DEFAULT_CHANNELS,
                    DEFAULT_CHANNEL_KEYS,
                    DEFAULT_PREFACE,
                    DEFAULT_ID_1,
                    DEFAULT_SUBJECT,
                    DEFAULT_COPYRIGHT,
                    DEFAULT_AUTHOR,
                    DEFAULT_IMAGE_DESCRIPTION,
                    DEFAULT_IS_SHOW_SUMMARY,
                    DEFAULT_MINISITE_KEY,
                    DEFAULT_IMAGE_INFO,
                    DEFAULT_SUBJECT_KEY,
                    DEFAULT_MINISITE,
                    DEFALUT_TAGS,
                    DEFAULT_DATE_PUBLISHED,
                    DEFAULT_AUTHORS,
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
                    DEFAULT_RESOURCE_URL,
                    true,
                    DEFAULT_TIMESTAMP_1)
        }
    }

    private fun assertItem(item: GuokrHandpickNewsResult?,
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
                           isShowSummary: Boolean,
                           minisiteKey: String,
                           imageInfo: GuokrHandpickContentImageInfo,
                           subjectKkey: String,
                           minisite: GuokrHandpickContentMinisite,
                           tags: List<String>,
                           datePublished: String,
                           authors: List<GuokrHandpickNewsAuthor>,
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
                           resourceUrl: String,
                           isFavorited: Boolean,
                           timestamp: Long) {
        assertThat<GuokrHandpickNewsResult>(item as GuokrHandpickNewsResult, notNullValue())
        assertThat(item.image, `is`(image))
        assertThat(item.isReplyable, `is`(isReplyable))
        assertThat(item.channels, `is`(channels))
        assertThat(item.channelKeys, `is`(channelKeys))
        assertThat(item.preface, `is`(preface))
        assertThat(item.id, `is`(id))
        assertThat(item.subject, `is`(subject))
        assertThat(item.copyright, `is`(copyright))
        assertThat(item.author, `is`(author))
        assertThat(item.imageDescription, `is`(imageDescription))
        assertThat(item.isShowSummary, `is`(isShowSummary))
        assertThat(item.minisiteKey, `is`(minisiteKey))
        assertThat(item.imageInfo, `is`(imageInfo))
        assertThat(item.subjectKey, `is`(subjectKkey))
        assertThat(item.minisite, `is`(minisite))
        assertThat(item.tags, `is`(tags))
        assertThat(item.datePublished, `is`(datePublished))
        assertThat(item.authors, `is`(authors))
        assertThat(item.repliesCount, `is`(repliesCount))
        assertThat(item.isAuthorExternal, `is`(isAuthorExternal))
        assertThat(item.recommendsCount, `is`(recommendsCount))
        assertThat(item.titleHide, `is`(titleHide))
        assertThat(item.dateModified, `is`(dateModified))
        assertThat(item.url, `is`(url))
        assertThat(item.title, `is`(title))
        assertThat(item.smallImage, `is`(smallImage))
        assertThat(item.summary, `is`(summary))
        assertThat(item.ukeyAuthor, `is`(ukeyAuthor))
        assertThat(item.dateCreated, `is`(dateCreated))
        assertThat(item.resourceUrl, `is`(resourceUrl))
        assertThat(item.isFavorite, `is`(isFavorited))
        assertThat(item.timestamp, `is`(timestamp))
    }

}