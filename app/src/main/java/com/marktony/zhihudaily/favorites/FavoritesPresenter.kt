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

package com.marktony.zhihudaily.favorites

import com.marktony.zhihudaily.data.source.Result
import com.marktony.zhihudaily.data.source.repository.DoubanMomentNewsRepository
import com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository
import com.marktony.zhihudaily.data.source.repository.ZhihuDailyNewsRepository
import com.marktony.zhihudaily.util.launchSilent
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by lizhaotailang on 2017/6/6.
 *
 * Listens the actions from UI ([FavoritesFragment]),
 * retrieves the data and update the UI as required.
 */

class FavoritesPresenter(
        private val mView: FavoritesContract.View,
        private val mZhihuRepository: ZhihuDailyNewsRepository,
        private val mDoubanRepository: DoubanMomentNewsRepository,
        private val mGuokrRepository: GuokrHandpickNewsRepository,
        private val uiContext: CoroutineContext = UI
) : FavoritesContract.Presenter {

    init {
        mView.mPresenter = this
    }

    override fun start() {

    }

    override fun loadFavorites() = launchSilent(uiContext) {
        val zhihuResult = mZhihuRepository.getFavorites()
        val doubanResult = mDoubanRepository.getFavorites()
        val guokrResult = mGuokrRepository.getFavorites()

        if (mView.isActive) {
            mView.showFavorites(
                    (zhihuResult as? Result.Success)?.data?.toMutableList() ?: mutableListOf(),
                    (doubanResult as? Result.Success)?.data?.toMutableList() ?: mutableListOf(),
                    (guokrResult as? Result.Success)?.data?.toMutableList() ?: mutableListOf())
            mView.setLoadingIndicator(false)
        }
    }
}
