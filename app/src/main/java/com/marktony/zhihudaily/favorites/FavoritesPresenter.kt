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

import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentNewsDataSource
import com.marktony.zhihudaily.data.source.datasource.GuokrHandpickDataSource
import com.marktony.zhihudaily.data.source.datasource.ZhihuDailyNewsDataSource
import com.marktony.zhihudaily.data.source.repository.DoubanMomentNewsRepository
import com.marktony.zhihudaily.data.source.repository.GuokrHandpickNewsRepository
import com.marktony.zhihudaily.data.source.repository.ZhihuDailyNewsRepository

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
        private val mGuokrRepository: GuokrHandpickNewsRepository
) : FavoritesContract.Presenter {

    init {
        mView.mPresenter = this
    }

    override fun start() {

    }

    override fun loadFavorites() {
        mZhihuRepository.getFavorites(object : ZhihuDailyNewsDataSource.LoadZhihuDailyNewsCallback {
            override fun onNewsLoaded(zhihuList: List<ZhihuDailyNewsQuestion>) {

                mDoubanRepository.getFavorites(object : DoubanMomentNewsDataSource.LoadDoubanMomentDailyCallback {
                    override fun onNewsLoaded(doubanList: List<DoubanMomentNewsPosts>) {

                        mGuokrRepository.getFavorites(object : GuokrHandpickDataSource.LoadGuokrHandpickNewsCallback {
                            override fun onNewsLoad(guokrList: List<GuokrHandpickNewsResult>) {
                                if (mView.isActive) {
                                    mView.showFavorites(zhihuList.toMutableList(), doubanList.toMutableList(), guokrList.toMutableList())
                                }
                                mView.setLoadingIndicator(false)
                            }

                            override fun onDataNotAvailable() {
                                mView.setLoadingIndicator(false)
                            }
                        })
                    }

                    override fun onDataNotAvailable() {

                    }
                })
            }

            override fun onDataNotAvailable() {

            }
        })
    }
}
