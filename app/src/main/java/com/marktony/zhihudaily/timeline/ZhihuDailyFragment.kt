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
import android.support.design.widget.FloatingActionButton
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
import com.marktony.zhihudaily.data.PostType
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.details.DetailsActivity
import com.marktony.zhihudaily.interfaze.OnRecyclerViewItemOnClickListener
import com.marktony.zhihudaily.service.CacheService
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_timeline_page.*
import java.util.*

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Main UI for the zhihu daily news screen.
 * Displays a grid of [ZhihuDailyNewsQuestion]s.
 */

class ZhihuDailyFragment : Fragment(), ZhihuDailyContract.View {

    override lateinit var mPresenter: ZhihuDailyContract.Presenter

    private var mAdapter: ZhihuDailyNewsAdapter? = null
    private lateinit var mLayoutManager: LinearLayoutManager

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0

    private var mIsFirstLoad = true
    private var mListSize = 0

    override val isActive: Boolean
        get() = isAdded && isResumed

    companion object {

        fun newInstance(): ZhihuDailyFragment = ZhihuDailyFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+08")
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_timeline_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            refresh_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        }
        refresh_layout.setOnRefreshListener {
            val c = Calendar.getInstance()
            c.timeZone = TimeZone.getTimeZone("GMT+08")
            mPresenter.loadNews(true, true, c.timeInMillis)
        }

        mLayoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    activity?.findViewById<FloatingActionButton>(R.id.fab)?.hide()
                    if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mListSize - 1) {
                        loadMore()
                    }
                } else {
                    activity?.findViewById<FloatingActionButton>(R.id.fab)?.show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+08")
        c.set(mYear, mMonth, mDay)
        setLoadingIndicator(mIsFirstLoad)
        if (mIsFirstLoad) {
            mPresenter.loadNews(true, false, c.timeInMillis)
            mIsFirstLoad = false
        } else {
            mPresenter.loadNews(false, false, c.timeInMillis)
        }

    }

    override fun setLoadingIndicator(active: Boolean) {
        refresh_layout.post {
            refresh_layout.isRefreshing = active
        }
    }

    override fun showResult(list: MutableList<ZhihuDailyNewsQuestion>) {
        if (mAdapter == null) {
            mAdapter = ZhihuDailyNewsAdapter(list)
            mAdapter?.setItemClickListener(object : OnRecyclerViewItemOnClickListener {

                override fun onItemClick(v: View, position: Int) {
                    val intent = Intent(activity, DetailsActivity::class.java).apply {
                        putExtra(DetailsActivity.KEY_ARTICLE_ID, list[position].id)
                        putExtra(DetailsActivity.KEY_ARTICLE_TYPE, ContentType.TYPE_ZHIHU_DAILY)
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

        // Cache data of the items
        for ((_, _, id) in list) {
            val intent = Intent(CacheService.BROADCAST_FILTER_ACTION)
            intent.putExtra(CacheService.FLAG_ID, id)
            intent.putExtra(CacheService.FLAG_TYPE, PostType.ZHIHU)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        }
    }

    private fun loadMore() {
        val c = Calendar.getInstance()
        c.set(mYear, mMonth, --mDay)
        mPresenter.loadNews(true, false, c.timeInMillis)
    }

    fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        c.set(mYear, mMonth, mDay)
        val dialog = DatePickerDialog.newInstance({ _, year, monthOfYear, dayOfMonth ->
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            c.set(mYear, monthOfYear, mDay)

            mPresenter.loadNews(true, true, c.timeInMillis)

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        dialog.maxDate = Calendar.getInstance()

        val minDate = Calendar.getInstance()
        minDate.set(2013, 5, 20)
        dialog.minDate = minDate
        dialog.vibrate(false)

        dialog.show(activity!!.fragmentManager, ZhihuDailyFragment::class.java.simpleName)

    }

}
