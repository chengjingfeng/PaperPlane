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

package com.marktony.zhihudaily.timeline

import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyNewsDataSource
import com.marktony.zhihudaily.data.source.repository.ZhihuDailyNewsRepository

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Listens to user actions from UI ([ZhihuDailyFragment]),
 * retrieves the data and update the ui as required.
 */

class ZhihuDailyPresenter(
        private val mView: ZhihuDailyContract.View,
        private val mRepository: ZhihuDailyNewsRepository
) : ZhihuDailyContract.Presenter {

    init {
        mView.mPresenter = this
    }

    override fun start() {

    }

    override fun loadNews(forceUpdate: Boolean, clearCache: Boolean, date: Long) {

        mRepository.getZhihuDailyNews(forceUpdate, clearCache, date, object : ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback {
            override fun onNewsLoaded(list: List<ZhihuDailyNewsQuestion>) {
                if (mView.isActive) {
                    mView.showResult(list.toMutableList())
                    mView.setLoadingIndicator(false)
                }
            }

            override fun onDataNotAvailable() {
                if (mView.isActive) {
                    mView.setLoadingIndicator(false)
                }
            }
        })
    }

}