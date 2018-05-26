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

import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.data.ContentType
import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.repository.*
import com.marktony.zhihudaily.util.launchSilent
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Listen to user actions from the UI ([DetailsFragment]),
 * retrieves the data and updates the UI as required.
 */

class DetailsPresenter : DetailsContract.Presenter {

    private val mView: DetailsContract.View
    private val uiContext: CoroutineContext = UI

    private var mDoubanNewsRepository: DoubanMomentNewsRepository? = null
    private var mDoubanContentRepository: DoubanMomentContentRepository? = null

    private var mZhihuNewsRepository: ZhihuDailyNewsRepository? = null
    private var mZhihuContentRepository: ZhihuDailyContentRepository? = null

    private var mGuokrNewsRepository: GuokrHandpickNewsRepository? = null
    private var mGuokrContentRepository: GuokrHandpickContentRepository? = null

    constructor(view: DetailsContract.View,
                doubanNewsRepository: DoubanMomentNewsRepository,
                doubanContentRepository: DoubanMomentContentRepository) {
        mView = view
        mView.mPresenter = this
        mDoubanContentRepository = doubanContentRepository
        mDoubanNewsRepository = doubanNewsRepository
    }

    constructor(view: DetailsContract.View,
                zhihuNewsRepository: ZhihuDailyNewsRepository,
                zhihuContentRepository: ZhihuDailyContentRepository) {
        mView = view
        mView.mPresenter = this
        mZhihuNewsRepository = zhihuNewsRepository
        mZhihuContentRepository = zhihuContentRepository
    }

    constructor(view: DetailsContract.View,
                guokrNewsRepository: GuokrHandpickNewsRepository,
                guokrContentRepository: GuokrHandpickContentRepository) {
        mView = view
        mView.mPresenter = this
        mGuokrNewsRepository = guokrNewsRepository
        mGuokrContentRepository = guokrContentRepository
    }

    override fun start() {

    }

    override fun favorite(type: ContentType, id: Int, favorite: Boolean) = launchSilent(uiContext) {
        when {
            type === ContentType.TYPE_ZHIHU_DAILY -> mZhihuNewsRepository?.favoriteItem(id, favorite)
            type === ContentType.TYPE_DOUBAN_MOMENT -> mDoubanNewsRepository?.favoriteItem(id, favorite)
            else -> mGuokrNewsRepository?.favoriteItem(id, favorite)
        }
    }

    override fun loadDoubanContent(id: Int) = launchSilent(uiContext) {
        val result = mDoubanContentRepository?.getDoubanMomentContent(id)
        val newsResult = mDoubanNewsRepository?.getItem(id)
        if (mView.isActive) {
            if (result != null
                    && newsResult != null
                    && result is Result.Success
                    && newsResult is Result.Success) {
                mView.showDoubanMomentContent(result.data, newsResult.data.thumbs)
            } else {
                mView.showMessage(R.string.something_wrong)
            }
        }
    }

    override fun loadZhihuDailyContent(id: Int) = launchSilent(uiContext) {
        val result = mZhihuContentRepository?.getZhihuDailyContent(id)
        if (mView.isActive) {
            if (result != null && result is Result.Success) {
                mView.showZhihuDailyContent(result.data)
            } else {
                mView.showMessage(R.string.something_wrong)
            }
        }
    }

    override fun loadGuokrHandpickContent(id: Int) = launchSilent(uiContext) {
        val result = mGuokrContentRepository?.getGuokrHandpickContent(id)
        if (mView.isActive) {
            if (result != null && result is Result.Success) {
                mView.showGuokrHandpickContent(result.data)
            } else {
                mView.showMessage(R.string.something_wrong)
            }
        }
    }

    override fun getLink(type: ContentType, requestCode: Int, id: Int) = launchSilent(uiContext) {
        when (type) {
            ContentType.TYPE_ZHIHU_DAILY -> {
                val result = mZhihuContentRepository?.getZhihuDailyContent(id)
                if (mView.isActive) {
                    if (result != null && result is Result.Success) {
                        val url = result.data.shareUrl
                        if (requestCode == DetailsFragment.REQUEST_SHARE) {
                            mView.share(url)
                        } else if (requestCode == DetailsFragment.REQUEST_COPY_LINK) {
                            mView.copyLink(url)
                        } else if (requestCode == DetailsFragment.REQUEST_OPEN_WITH_BROWSER) {
                            mView.openWithBrowser(url)
                        }
                    } else {
                        mView.showMessage(R.string.share_error)
                    }
                }
            }
            ContentType.TYPE_DOUBAN_MOMENT -> {
                val result = mDoubanContentRepository?.getDoubanMomentContent(id)
                if (mView.isActive) {
                    if (result != null && result is Result.Success) {
                        val url = result.data.url
                        if (requestCode == DetailsFragment.REQUEST_SHARE) {
                            mView.share(url)
                        } else if (requestCode == DetailsFragment.REQUEST_COPY_LINK) {
                            mView.copyLink(url)
                        } else if (requestCode == DetailsFragment.REQUEST_OPEN_WITH_BROWSER) {
                            mView.openWithBrowser(url)
                        }
                    } else {
                        mView.showMessage(R.string.share_error)
                    }
                }
            }
            ContentType.TYPE_GUOKR_HANDPICK -> {
                val result = mGuokrContentRepository?.getGuokrHandpickContent(id)
                if (mView.isActive) {
                    if (result != null && result is Result.Success) {
                        val url = result.data.url
                        if (requestCode == DetailsFragment.REQUEST_SHARE) {
                            mView.share(url)
                        } else if (requestCode == DetailsFragment.REQUEST_COPY_LINK) {
                            mView.copyLink(url)
                        } else if (requestCode == DetailsFragment.REQUEST_OPEN_WITH_BROWSER) {
                            mView.openWithBrowser(url)
                        }
                    } else {
                        mView.showMessage(R.string.share_error)
                    }
                }
            }
        }
    }
}
