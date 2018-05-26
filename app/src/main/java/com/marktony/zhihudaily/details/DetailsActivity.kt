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

package com.marktony.zhihudaily.details

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.data.ContentType
import com.marktony.zhihudaily.data.source.repository.DoubanMomentContentRepository
import com.marktony.zhihudaily.data.source.repository.GuokrHandpickContentRepository
import com.marktony.zhihudaily.data.source.repository.ZhihuDailyContentRepository
import com.marktony.zhihudaily.injection.Injection

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Activity for displaying the details of content.
 */

class DetailsActivity : AppCompatActivity() {

    private lateinit var mDetailsFragment: DetailsFragment

    private var mType: ContentType = ContentType.TYPE_ZHIHU_DAILY

    companion object {
        const val KEY_ARTICLE_TYPE = "KEY_ARTICLE_TYPE"
        const val KEY_ARTICLE_ID = "KEY_ARTICLE_ID"
        const val KEY_ARTICLE_TITLE = "KEY_ARTICLE_TITLE"
        const val KEY_ARTICLE_IS_FAVORITE = "KEY_ARTICLE_IS_FAVORITE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame)

        if (savedInstanceState != null) {
            mDetailsFragment = supportFragmentManager.getFragment(savedInstanceState, DetailsFragment::class.java.simpleName) as DetailsFragment
        } else {
            mDetailsFragment = DetailsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, mDetailsFragment, DetailsFragment::class.java.simpleName)
                    .commit()
        }

        mType = intent.getSerializableExtra(KEY_ARTICLE_TYPE) as ContentType
        when {
            mType === ContentType.TYPE_ZHIHU_DAILY -> DetailsPresenter(
                    mDetailsFragment,
                    Injection.provideZhihuDailyNewsRepository(this@DetailsActivity),
                    Injection.provideZhihuDailyContentRepository(this@DetailsActivity))
            mType === ContentType.TYPE_DOUBAN_MOMENT -> DetailsPresenter(
                    mDetailsFragment,
                    Injection.provideDoubanMomentNewsRepository(this@DetailsActivity),
                    Injection.provideDoubanMomentContentRepository(this@DetailsActivity))
            mType === ContentType.TYPE_GUOKR_HANDPICK -> DetailsPresenter(
                    mDetailsFragment,
                    Injection.provideGuokrHandpickNewsRepository(this@DetailsActivity),
                    Injection.provideGuokrHandpickContentRepository(this@DetailsActivity))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ZhihuDailyContentRepository.destroyInstance()
        DoubanMomentContentRepository.destroyInstance()
        GuokrHandpickContentRepository.destroyInstance()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mDetailsFragment.isAdded) {
            supportFragmentManager.putFragment(outState, DetailsFragment::class.java.simpleName, mDetailsFragment)
        }
    }

}
