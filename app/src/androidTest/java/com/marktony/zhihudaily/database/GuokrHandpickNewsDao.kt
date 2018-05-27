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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuokrHandpickNewsDao {

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
        private val DEFAULT_TIMESTAMP = 1527333963L

        private val DEFAULT_GUOKR_HANDPICK_NEWS = GuokrHandpickNewsResult(
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
                DEFAULT_TIMESTAMP)

        private val NEW_IS_FAVORITED = true
        private val NEW_TIMESTAMP = 1527337920L
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
    fun insertGuokrHandpickNewsAndGetById() {
        // When inserting a piece of guokr handpick news
        database.guokrHandpickNewsDao().insertAll(listOf(DEFAULT_GUOKR_HANDPICK_NEWS))

        // When getting the guokr handpick news by id from the database
        val loaded = database.guokrHandpickNewsDao().queryItemById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertItem(loaded, DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID,
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
                DEFAULT_TIMESTAMP)
    }

    @Test
    fun insertItemIgnoredOnConflict() {
        // Given that a piece of guokr handpick news is inserted
        database.guokrHandpickNewsDao().insertAll(listOf(DEFAULT_GUOKR_HANDPICK_NEWS))

        // When a piece of guokr handpick news with the same id is inserted
        val newGuokrHandpickNews = GuokrHandpickNewsResult(DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID,
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
                NEW_IS_FAVORITED,
                DEFAULT_TIMESTAMP)
        database.guokrHandpickNewsDao().insertAll(listOf(newGuokrHandpickNews))

        // When getting the guokr handpick news by id from the database
        val loaded = database.guokrHandpickNewsDao().queryItemById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertItem(loaded, DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID,
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
                DEFAULT_TIMESTAMP)
    }

    @Test
    fun updateItemAndGetById() {
        // When inserting a piece of guokr handpick news
        database.guokrHandpickNewsDao().insertAll(listOf(DEFAULT_GUOKR_HANDPICK_NEWS))

        // When the guokr handpick news is updated
        val updatedItem = GuokrHandpickNewsResult(
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
                NEW_IS_FAVORITED,
                DEFAULT_TIMESTAMP)
        database.guokrHandpickNewsDao().update(updatedItem)

        val loaded = database.guokrHandpickNewsDao().queryItemById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertItem(loaded, DEFAULT_IMAGE,
                DEFAULT_IS_REPLYABLE,
                DEFAULT_CHANNELS,
                DEFAULT_CHANNEL_KEYS,
                DEFAULT_PREFACE,
                DEFAULT_ID,
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
                NEW_IS_FAVORITED,
                DEFAULT_TIMESTAMP)
    }

    @Test
    fun favoriteItemByIdAndGettingFavorites() {
        // When inserting a piece of guokr daily news
        database.guokrHandpickNewsDao().insertAll(listOf(DEFAULT_GUOKR_HANDPICK_NEWS))

        // Query all the favorites
        val list = database.guokrHandpickNewsDao().queryAllFavorites()
        assertThat(list.size, `is`(0))

        // Favorite one item
        database.guokrHandpickNewsDao().queryItemById(DEFAULT_ID)?.let {
            it.isFavorite = true
            database.guokrHandpickNewsDao().update(it)
        }

        // The list size is 1
        val newList = database.guokrHandpickNewsDao().queryAllFavorites()
        assertThat(newList.size, `is`(1))
    }

    @Test
    fun insertAndGettingTimeoutItems() {
        // Given a piece of guokr handpick news inserted
        database.guokrHandpickNewsDao().insertAll(listOf(DEFAULT_GUOKR_HANDPICK_NEWS))

        // when querying the timeout news by timestamp
        val result = database.guokrHandpickNewsDao().queryAllTimeoutItems(NEW_TIMESTAMP)

        // The list size is 1
        assertThat(result.size, `is`(1))
    }

    @Test
    fun deleteItemAndGettingItems() {
        // Given a piece of guokr handpick news inserted
        database.guokrHandpickNewsDao().insertAll(listOf(DEFAULT_GUOKR_HANDPICK_NEWS))

        // When deleting a piece of guokr handpick news by id
        database.guokrHandpickNewsDao().delete(DEFAULT_GUOKR_HANDPICK_NEWS)

        val result = database.guokrHandpickNewsDao().queryAllByOffsetAndLimit(0, 20)

        // The list is empty
        assertThat(result.size, `is`(0))
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