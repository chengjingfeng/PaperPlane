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
import com.marktony.zhihudaily.data.source.local.ZhihuDailyNewsLocalDataSource
import com.marktony.zhihudaily.database.AppDatabase
import com.marktony.zhihudaily.util.SingleExecutors
import com.marktony.zhihudaily.util.runBlockingSilent
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ZhihuDailyNewsLocalDataSourceTest {

    private lateinit var localDataSource: ZhihuDailyNewsLocalDataSource
    private lateinit var database: AppDatabase

    companion object {
        private val DEFAULT_IMAGES_1 = listOf("https:\\/\\/pic1.zhimg.com\\/v2-5c5f75baa911c58f2476334180f5cda0.jpg")
        private val DEFAULT_IMAGES_2 = listOf("https:\\/\\/pic4.zhimg.com\\/v2-fe3110793602fba3e4e6dea967f41f47.jpg")
        private val DEFAULT_IMAGES_3 = listOf("https:\\/\\/pic2.zhimg.com\\/v2-7fbd7d4bc19c79608e775cd2b4e57545.jpg")
        private val DEFAULT_TYPE = 0
        private val DEFAULT_ID_1 = 9683773
        private val DEFAULT_ID_2 = 9683986
        private val DEFAULT_ID_3 = 9683959
        private val DEFAULT_GA_PREFIX_1 = "052422"
        private val DEFAULT_GA_PREFIX_2 = "052421"
        private val DEFAULT_GA_PREFIX_3 = "052419"
        private val DEFAULT_TITLE_1 = "小事 · 直到麻木了，就到了要走的时候"
        private val DEFAULT_TITLE_2 = "十年后再看《蓝宇》"
        private val DEFAULT_TITLE_3 = "作为咨询师，遇见不止一次，来访者威胁说要自杀……"
        private val DEFAULT_IS_FAVORITE_1 = false
        private val DEFAULT_IS_FAVORITE_2 = false
        private val DEFAULT_IS_FAVORITE_3 = false
        private val DEFAULT_TIMESTAMP_1 = 1527333963L
        private val DEFAULT_TIMESTAMP_2 = 1527337920L
        private val DEFAULT_TIMESTAMP_3 = 1527340803L
    }

    @Before
    fun setup() {
        // Using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase::class.java)
                .build()

        // Make sure that we're not keeping a reference to the wrong instance.
        ZhihuDailyNewsLocalDataSource.clearInstance()
        localDataSource = ZhihuDailyNewsLocalDataSource.getInstance(SingleExecutors(), database.zhihuDailyNewsDao())
    }

    @After
    fun cleanUp() {
        database.close()
        ZhihuDailyNewsLocalDataSource.clearInstance()
    }

    @Test
    fun testPreConditions() {
        assertNotNull(localDataSource)
    }

    @Test
    fun saveItem_retrievesItem() = runBlockingSilent {
        // Given several new zhihu daily news stories
        val newStory1 = ZhihuDailyNewsQuestion(DEFAULT_IMAGES_1, DEFAULT_TYPE, DEFAULT_ID_1, DEFAULT_GA_PREFIX_1, DEFAULT_TITLE_1, DEFAULT_IS_FAVORITE_1, DEFAULT_TIMESTAMP_1)
        val newStory2 = ZhihuDailyNewsQuestion(DEFAULT_IMAGES_2, DEFAULT_TYPE, DEFAULT_ID_2, DEFAULT_GA_PREFIX_2, DEFAULT_TITLE_2, DEFAULT_IS_FAVORITE_2, DEFAULT_TIMESTAMP_2)
        val newStory3 = ZhihuDailyNewsQuestion(DEFAULT_IMAGES_3, DEFAULT_TYPE, DEFAULT_ID_3, DEFAULT_GA_PREFIX_3, DEFAULT_TITLE_3, DEFAULT_IS_FAVORITE_3, DEFAULT_TIMESTAMP_3)

        with(localDataSource) {
            // When saved into the persistent repository
            saveAll(listOf(newStory1, newStory2, newStory3))

            val allResult = getZhihuDailyNews(false, false, DEFAULT_TIMESTAMP_3)
            assertThat(allResult, instanceOf(Result.Success::class.java))

            if (allResult is Result.Success) {
                assertThat(allResult.data.size, `is`(3))
            }

            // Then the zhihu daily news stories can be retrieved from the persistent repository
            val result = getItem(newStory1.id)
            assertThat(result, instanceOf(Result.Success::class.java))
            if (result is Result.Success) {
                assertItem(result.data, DEFAULT_IMAGES_1, DEFAULT_TYPE, DEFAULT_ID_1, DEFAULT_GA_PREFIX_1, DEFAULT_TITLE_1, DEFAULT_IS_FAVORITE_1, DEFAULT_TIMESTAMP_1)
            }
        }
    }

    @Test
    fun favoriteItem_retrievesFavorites() = runBlockingSilent {
        // Given a new zhihu daily news story
        val newStory = ZhihuDailyNewsQuestion(DEFAULT_IMAGES_1, DEFAULT_TYPE, DEFAULT_ID_1, DEFAULT_GA_PREFIX_1, DEFAULT_TITLE_1, DEFAULT_IS_FAVORITE_1, DEFAULT_TIMESTAMP_1)
        localDataSource.saveAll(listOf(newStory))

        // When favorited in the persistent repository
        localDataSource.favoriteItem(newStory.id, true)

        // Then the zhihu news story can be retrieved from the persistent repository and is favorited
        val result = localDataSource.getItem(newStory.id)
        assertThat(result, instanceOf(Result.Success::class.java))
        if (result is Result.Success) {
            assertItem(result.data, DEFAULT_IMAGES_1, DEFAULT_TYPE, DEFAULT_ID_1, DEFAULT_GA_PREFIX_1, DEFAULT_TITLE_1, true, DEFAULT_TIMESTAMP_1)
            assertThat(result.data.isFavorite, `is`(true))
        }

        // Get all the favorites, the favorited zhihu news story will be listed
        val favorites = localDataSource.getFavorites()
        assertThat(result, instanceOf(Result.Success::class.java))
        if (favorites is Result.Success) {
            assertThat(favorites.data.size, `is`(1))
            assertItem(favorites.data.first(), DEFAULT_IMAGES_1, DEFAULT_TYPE, DEFAULT_ID_1, DEFAULT_GA_PREFIX_1, DEFAULT_TITLE_1, true, DEFAULT_TIMESTAMP_1)
        }
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
        MatcherAssert.assertThat<ZhihuDailyNewsQuestion>(item as ZhihuDailyNewsQuestion, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(item.images, `is`(images))
        MatcherAssert.assertThat(item.type, `is`(type))
        MatcherAssert.assertThat(item.id, `is`(id))
        MatcherAssert.assertThat(item.gaPrefix, `is`(gaPrefix))
        MatcherAssert.assertThat(item.title, `is`(title))
        MatcherAssert.assertThat(item.isFavorite, `is`(isFavorite))
        MatcherAssert.assertThat(item.timestamp, `is`(timestamp))
    }

}