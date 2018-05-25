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

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.data.ContentType
import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.details.DetailsActivity
import com.marktony.zhihudaily.interfaze.OnRecyclerViewItemOnClickListener
import kotlinx.android.synthetic.main.framgent_favorites.*

/**
 * Created by lizhaotailang on 2017/6/6.
 *
 * Main UI for the favorites screen.
 * Displays a grid of [ZhihuDailyNewsQuestion]s', [DoubanMomentNewsPosts]' and
 * [GuokrHandpickNewsResult]s' favorites.
 */

class FavoritesFragment : Fragment(), FavoritesContract.View {

    override lateinit var mPresenter: FavoritesContract.Presenter

    private var mAdapter: FavoritesAdapter? = null

    override val isActive: Boolean
        get() = isAdded && isResumed

    companion object {

        fun newInstance(): FavoritesFragment = FavoritesFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.framgent_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh_layout.setOnRefreshListener {
            mPresenter.loadFavorites()
        }

        recycler_view.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
        mPresenter.loadFavorites()
    }

    override fun setLoadingIndicator(active: Boolean) {
        refresh_layout.post {
            refresh_layout.isRefreshing = active
        }
    }

    override fun showFavorites(zhihuList: MutableList<ZhihuDailyNewsQuestion>,
                               doubanList: MutableList<DoubanMomentNewsPosts>,
                               guokrList: MutableList<GuokrHandpickNewsResult>) {

        if (mAdapter == null) {
            context?.let {
                mAdapter = FavoritesAdapter(it, zhihuList, doubanList, guokrList)
            }
            mAdapter?.setOnItemClickListener(object : OnRecyclerViewItemOnClickListener {

                override fun onItemClick(v: View, position: Int) {
                    val viewType = mAdapter?.getItemViewType(position)

                    if (viewType == FavoritesAdapter.ItemWrapper.TYPE_ZHIHU) {
                        val intent = Intent(activity, DetailsActivity::class.java).apply {
                            mAdapter?.let {
                                putExtra(DetailsActivity.KEY_ARTICLE_ID, zhihuList[it.getOriginalIndex(position)].id)
                                putExtra(DetailsActivity.KEY_ARTICLE_TYPE, ContentType.TYPE_ZHIHU_DAILY)
                                putExtra(DetailsActivity.KEY_ARTICLE_TITLE, zhihuList[it.getOriginalIndex(position)].title)
                                putExtra(DetailsActivity.KEY_ARTICLE_IS_FAVORITE, zhihuList[it.getOriginalIndex(position)].isFavorite)
                            }

                        }
                        startActivity(intent)

                    } else if (viewType == FavoritesAdapter.ItemWrapper.TYPE_DOUBAN || viewType == FavoritesAdapter.ItemWrapper.TYPE_DOUBAN_NO_IMG) {
                        val intent = Intent(activity, DetailsActivity::class.java).apply {
                            mAdapter?.let {
                                putExtra(DetailsActivity.KEY_ARTICLE_ID, doubanList[it.getOriginalIndex(position)].id)
                                putExtra(DetailsActivity.KEY_ARTICLE_TYPE, ContentType.TYPE_DOUBAN_MOMENT)
                                putExtra(DetailsActivity.KEY_ARTICLE_TITLE, doubanList[it.getOriginalIndex(position)].title)
                                putExtra(DetailsActivity.KEY_ARTICLE_IS_FAVORITE, doubanList[it.getOriginalIndex(position)].isFavorite)
                            }
                        }
                        startActivity(intent)

                    } else if (viewType == FavoritesAdapter.ItemWrapper.TYPE_GUOKR) {
                        val intent = Intent(activity, DetailsActivity::class.java).apply {
                            mAdapter?.let {
                                putExtra(DetailsActivity.KEY_ARTICLE_ID, guokrList[it.getOriginalIndex(position)].id)
                                putExtra(DetailsActivity.KEY_ARTICLE_TYPE, ContentType.TYPE_GUOKR_HANDPICK)
                                putExtra(DetailsActivity.KEY_ARTICLE_TITLE, guokrList[it.getOriginalIndex(position)].title)
                                putExtra(DetailsActivity.KEY_ARTICLE_IS_FAVORITE, guokrList[it.getOriginalIndex(position)].isFavorite)
                            }
                        }

                        startActivity(intent)

                    }
                }

            })
            recycler_view.adapter = mAdapter
        } else {
            mAdapter?.updateData(zhihuList, doubanList, guokrList)
        }
        recycler_view.visibility = if (zhihuList.isEmpty() && doubanList.isEmpty() && guokrList.isEmpty()) View.GONE else View.VISIBLE
        empty_view.visibility = if (zhihuList.isEmpty() && doubanList.isEmpty() && guokrList.isEmpty()) View.VISIBLE else View.GONE
    }

}
