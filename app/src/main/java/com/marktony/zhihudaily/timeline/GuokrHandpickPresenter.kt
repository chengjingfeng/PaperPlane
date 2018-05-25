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

import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickDataSource
import com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Listens to user actions from UI ([GuokrHandpickFragment]),
 * retrieves data and update the UI as required.
 */

class GuokrHandpickPresenter(
        private val mView: GuokrHandpickContract.View,
        private val mRepository: GuokrHandpickNewsRepository
) : GuokrHandpickContract.Presenter {

    init {
        mView.mPresenter = this
    }

    override fun start() {

    }

    override fun load(forceUpdate: Boolean, clearCache: Boolean, offset: Int, limit: Int) {

        mRepository.getGuokrHandpickNews(forceUpdate, clearCache, offset, limit, object : GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback {
            override fun onNewsLoad(list: List<GuokrHandpickNewsResult>) {
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
