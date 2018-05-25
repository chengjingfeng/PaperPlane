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

import android.support.annotation.StringRes
import com.marktony.zhihudaily.BasePresenter
import com.marktony.zhihudaily.BaseView
import com.marktony.zhihudaily.data.*

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * This specifies contract between the view and the presenter.
 */

interface DetailsContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun showMessage(@StringRes stringRes: Int)

        fun showZhihuDailyContent(content: ZhihuDailyContent)

        fun showDoubanMomentContent(content: DoubanMomentContent, list: List<DoubanMomentNewsThumbs>?)

        fun showGuokrHandpickContent(content: GuokrHandpickContentResult)

        fun share(link: String?)

        fun copyLink(link: String?)

        fun openWithBrowser(link: String?)

    }

    interface Presenter : BasePresenter {

        fun favorite(type: ContentType, id: Int, favorite: Boolean)

        fun loadDoubanContent(id: Int)

        fun loadZhihuDailyContent(id: Int)

        fun loadGuokrHandpickContent(id: Int)

        fun getLink(type: ContentType, requestCode: Int, id: Int)

    }

}
