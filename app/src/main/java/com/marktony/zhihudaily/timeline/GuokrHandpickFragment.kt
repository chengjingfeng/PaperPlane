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

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.data.ContentType
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.PostType
import com.marktony.zhihudaily.details.DetailsActivity
import com.marktony.zhihudaily.interfaze.OnRecyclerViewItemOnClickListener
import com.marktony.zhihudaily.service.CacheService
import kotlinx.android.synthetic.main.fragment_timeline_page.*

/**
 * Created by lizhaotailang on 2017/5/24.
 *
 * Main UI for the guokr handpick news.
 * Displays a grid of [GuokrHandpickNewsResult]s.
 */

class GuokrHandpickFragment : Fragment(), GuokrHandpickContract.View {

    override lateinit var mPresenter: GuokrHandpickContract.Presenter

    private var mAdapter: GuokrHandpickNewsAdapter? = null
    private lateinit var mLayoutManager: LinearLayoutManager

    private var mOffset = 0
    private var mIsFirstLoad = true
    private var mListSize = 0

    override val isActive: Boolean
        get() = isAdded && isResumed

    companion object {
        fun newInstance(): GuokrHandpickFragment = GuokrHandpickFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_timeline_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            refresh_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        }
        refresh_layout.setOnRefreshListener {
            mPresenter.load(true, true, 0, 20)
            mOffset = 0
        }

        mLayoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && mLayoutManager.findLastCompletelyVisibleItemPosition() == mListSize - 1) {
                    loadMore()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
        setLoadingIndicator(mIsFirstLoad)
        if (mIsFirstLoad) {
            mPresenter.load(true, false, mOffset, 20)
            mIsFirstLoad = false
        } else {
            mPresenter.load(false, false, mOffset, 20)
        }
    }

    override fun setLoadingIndicator(active: Boolean) {
        refresh_layout.post {
            refresh_layout.isRefreshing = active
        }
    }

    override fun showResult(list: MutableList<GuokrHandpickNewsResult>) {
        mOffset = list.size
        if (mAdapter == null) {
            mAdapter = GuokrHandpickNewsAdapter(list)
            mAdapter?.setItemClickListener(object : OnRecyclerViewItemOnClickListener {

                override fun onItemClick(v: View, position: Int) {
                    val intent = Intent(activity, DetailsActivity::class.java).apply {
                        putExtra(DetailsActivity.KEY_ARTICLE_ID, list[position].id)
                        putExtra(DetailsActivity.KEY_ARTICLE_TYPE, ContentType.TYPE_GUOKR_HANDPICK)
                        putExtra(DetailsActivity.KEY_ARTICLE_TITLE, list[position].title)
                        putExtra(DetailsActivity.KEY_ARTICLE_IS_FAVORITE, list[position].isFavorite)
                    }
                    startActivity(intent)
                }

            })
            recycler_view.adapter = mAdapter
        } else {
            mAdapter?.updateData(list)
        }

        mListSize = list.size

        empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        recycler_view.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE

        for ((_, _, _, _, _, id) in list) {
            val intent = Intent(CacheService.BROADCAST_FILTER_ACTION)
            intent.putExtra(CacheService.FLAG_ID, id)
            intent.putExtra(CacheService.FLAG_TYPE, PostType.GUOKR)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        }

    }

    private fun loadMore() {
        mPresenter.load(true, false, mOffset, 20)
    }

}
