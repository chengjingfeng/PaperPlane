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
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZhihuDailyNewsDaoTest {

    private lateinit var database: AppDatabase

    companion object {
        private val DEFAULT_IMAGES: List<String> = listOf("https:\\/pic1.zhimg.com\\/v2-5c5f75baa911c58f2476334180f5cda0.jpg")
        private val DEFAULT_TYPE: Int = 0
        private val DEFAULT_ID: Int = 9683773
        private val DEFAULT_GA_PREFIX: String = "052422"
        private val DEFAULT_TITLE: String = "小事 · 直到麻木了，就到了要走的时候"
        private val DEFAULT_IS_FAVORITE: Boolean = false
        private val DEFAULT_TIMESTAMP: Long = 1527333963L

        private val DEFAULT_ZHIHU_DAILY_NEWS_STORY = ZhihuDailyNewsQuestion(DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TITLE, DEFAULT_IS_FAVORITE, DEFAULT_TIMESTAMP)

        private val NEW_IS_FAVORITE = true
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
    fun insertZhihuDailyNewsAndGetById() {
        // When inserting a zhihu daily news story
        database.zhihuDailyNewsDao().insertAll(listOf(DEFAULT_ZHIHU_DAILY_NEWS_STORY))

        // When getting the zhihu daily news story by id from the database
        val loaded = database.zhihuDailyNewsDao().queryItemById(DEFAULT_ZHIHU_DAILY_NEWS_STORY.id)

        // The loaded data contains the expected values
        assertItem(loaded, DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TITLE, DEFAULT_IS_FAVORITE, DEFAULT_TIMESTAMP)
    }

    @Test
    fun insertItemIgnoredOnConflict() {
        // Given that a zhihu daily news story is inserted
        database.zhihuDailyNewsDao().insertAll(listOf(DEFAULT_ZHIHU_DAILY_NEWS_STORY))

        // When a zhihu daily news with the same id is inserted
        val newZhihuDailyNewsStory = ZhihuDailyNewsQuestion(DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TITLE, NEW_IS_FAVORITE, DEFAULT_TIMESTAMP)
        database.zhihuDailyNewsDao().insertAll(listOf(newZhihuDailyNewsStory))

        // When getting the zhihu daily news story by id from the database
        val loaded = database.zhihuDailyNewsDao().queryItemById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertItem(loaded, DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TITLE, DEFAULT_IS_FAVORITE, DEFAULT_TIMESTAMP)
    }

    @Test
    fun updateItemAndGetById() {
        // When inserting a zhihu daily news story
        database.zhihuDailyNewsDao().insertAll(listOf(DEFAULT_ZHIHU_DAILY_NEWS_STORY))

        // When the zhihu daily news story is updated
        val updatedItem = ZhihuDailyNewsQuestion(DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TITLE, NEW_IS_FAVORITE, DEFAULT_TIMESTAMP)
        database.zhihuDailyNewsDao().update(updatedItem)

        val loaded = database.zhihuDailyNewsDao().queryItemById(DEFAULT_ID)

        // The loaded data contains the expected values
        assertItem(loaded, DEFAULT_IMAGES, DEFAULT_TYPE, DEFAULT_ID, DEFAULT_GA_PREFIX, DEFAULT_TITLE, NEW_IS_FAVORITE, DEFAULT_TIMESTAMP)
    }

    @Test
    fun favoriteItemByIdAndGettingFavorites() {
        // When inserting a zhihu daily news story
        database.zhihuDailyNewsDao().insertAll(listOf(DEFAULT_ZHIHU_DAILY_NEWS_STORY))

        // Query all the favorites
        val stories = database.zhihuDailyNewsDao().queryAllFavorites()
        assertThat(stories.size, `is`(0))

        // Favorite one item
        database.zhihuDailyNewsDao().queryItemById(DEFAULT_ID)?.let {
            it.isFavorite = true
            database.zhihuDailyNewsDao().update(it)
        }

        // The list size is 1
        val newStories = database.zhihuDailyNewsDao().queryAllFavorites()
        assertThat(newStories.size, `is`(1))
    }

    @Test
    fun insertAndGettingTimeoutItems() {
        // Given a zhihu daily news story inserted
        database.zhihuDailyNewsDao().insertAll(listOf(DEFAULT_ZHIHU_DAILY_NEWS_STORY))

        // when querying the timeout stories by timestamp
        val stories = database.zhihuDailyNewsDao().queryAllTimeoutItems(NEW_TIMESTAMP)

        // The list size is 1
        assertThat(stories.size, `is`(1))
    }

    @Test
    fun deleteItemAndGettingItems() {
        // Given a zhihu daily news story inserted
        database.zhihuDailyNewsDao().insertAll(listOf(DEFAULT_ZHIHU_DAILY_NEWS_STORY))

        // When deleting a zhihu daily news story by id
        database.zhihuDailyNewsDao().delete(DEFAULT_ZHIHU_DAILY_NEWS_STORY)

        // When getting the zhihu daily news stories
        val stories = database.zhihuDailyNewsDao().queryAllByDate(DEFAULT_TIMESTAMP)

        // The list is empty
        assertThat(stories.size, `is`(0))
    }

    // Assert a zhihu daily news story
    private fun assertItem(item: ZhihuDailyNewsQuestion?,
                           images: List<String>,
                           type: Int,
                           id: Int,
                           gaPrefix: String,
                           title: String,
                           isFavorite: Boolean,
                           timestamp: Long) {
        assertThat<ZhihuDailyNewsQuestion>(item as ZhihuDailyNewsQuestion, notNullValue())
        assertThat(item.images, `is`(images))
        assertThat(item.type, `is`(type))
        assertThat(item.id, `is`(id))
        assertThat(item.gaPrefix, `is`(gaPrefix))
        assertThat(item.title, `is`(title))
        assertThat(item.isFavorite, `is`(isFavorite))
        assertThat(item.timestamp, `is`(timestamp))
    }

}