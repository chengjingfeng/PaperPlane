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

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.data.DoubanMomentNewsPosts
import com.marktony.zhihudaily.data.GuokrHandpickNewsResult
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.glide.loadImage
import com.marktony.zhihudaily.interfaze.OnRecyclerViewItemOnClickListener
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_universal_layout.view.*

/**
 * Created by lizhaotailang on 2017/6/14.
 *
 * Adapter between favorites item and [RecyclerView].
 */

class FavoritesAdapter(
        private val mContext: Context,
        private val mZhihuList: MutableList<ZhihuDailyNewsQuestion>,
        private val mDoubanList: MutableList<DoubanMomentNewsPosts>,
        private val mGuokrList: MutableList<GuokrHandpickNewsResult>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    private val mWrapperList: MutableList<ItemWrapper> = mutableListOf()

    private var mListener: OnRecyclerViewItemOnClickListener? = null

    init {
        mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_ZHIHU_CATEGORY))
        if (mZhihuList.isEmpty()) {
            mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_EMPTY))
        } else {
            for (i in mZhihuList.indices) {
                val iw = ItemWrapper(ItemWrapper.TYPE_ZHIHU)
                iw.index = i
                mWrapperList.add(iw)
            }
        }

        mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_DOUBAN_CATEGORY))
        if (mDoubanList.isEmpty()) {
            mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_EMPTY))
        } else {
            for (i in mDoubanList.indices) {
                if (mDoubanList[i].thumbs.isEmpty()) {
                    val iw = ItemWrapper(ItemWrapper.TYPE_DOUBAN_NO_IMG)
                    iw.index = i
                    mWrapperList.add(iw)
                } else {
                    val iw = ItemWrapper(ItemWrapper.TYPE_DOUBAN)
                    iw.index = i
                    mWrapperList.add(iw)
                }
            }
        }

        mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_GUOKR_CATEGORY))
        if (mGuokrList.isEmpty()) {
            mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_EMPTY))
        } else {
            for (i in mGuokrList.indices) {
                val iw = ItemWrapper(ItemWrapper.TYPE_GUOKR)
                iw.index = i
                mWrapperList.add(iw)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        ItemWrapper.TYPE_EMPTY -> EmptyViewHolder(mLayoutInflater.inflate(R.layout.item_empty, viewGroup, false))
        ItemWrapper.TYPE_ZHIHU -> ZhihuItemViewHolder(mLayoutInflater.inflate(R.layout.item_universal_layout, viewGroup, false), mListener)
        ItemWrapper.TYPE_DOUBAN -> DoubanItemHolder(mLayoutInflater.inflate(R.layout.item_universal_layout, viewGroup, false), mListener)
        ItemWrapper.TYPE_DOUBAN_NO_IMG -> DoubanNoImageHolder(mLayoutInflater.inflate(R.layout.item_universal_without_image, viewGroup, false), mListener)
        ItemWrapper.TYPE_GUOKR -> GuokrViewHolder(mLayoutInflater.inflate(R.layout.item_universal_layout, viewGroup, false), mListener)
        else -> CategoryViewHolder(mLayoutInflater.inflate(R.layout.item_category, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val iw = mWrapperList[i]

        when (iw.viewType) {
            ItemWrapper.TYPE_ZHIHU -> {
                val (images, _, _, _, title) = mZhihuList[iw.index]
                with((viewHolder as ZhihuItemViewHolder).itemView) {
                    text_view_title.text = title
                    image_view_cover.loadImage(images?.get(0))
                }
            }
            ItemWrapper.TYPE_DOUBAN -> {
                val (_, _, _, _, _, _, _, _, _, _, _, _, _, thumbs, _, title) = mDoubanList[iw.index]
                with((viewHolder as DoubanItemHolder).itemView) {
                    text_view_title.text = title
                    image_view_cover.loadImage(thumbs[0].medium.url)
                }
            }
            ItemWrapper.TYPE_DOUBAN_NO_IMG -> {
                val (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, title) = mDoubanList[iw.index]
                with((viewHolder as DoubanNoImageHolder).itemView) {
                    findViewById<AppCompatTextView>(R.id.text_view_title).text = title
                }
            }
            ItemWrapper.TYPE_GUOKR -> {
                val (_, _, _, _, _, _, _, _, _, _, _, _, imageInfo, _, _, _, _, _, _, _, _, _, _, _, title) = mGuokrList[iw.index]
                with((viewHolder as GuokrViewHolder).itemView) {
                    image_view_cover.loadImage(imageInfo.url)
                    text_view_title.text = title
                }
            }
            ItemWrapper.TYPE_ZHIHU_CATEGORY -> {
                (viewHolder as CategoryViewHolder).itemView.text_view_category.text = mContext.getString(R.string.zhihu_daily)
            }

            ItemWrapper.TYPE_DOUBAN_CATEGORY -> {
                (viewHolder as CategoryViewHolder).itemView.text_view_category.text = mContext.getString(R.string.douban_moment)
            }

            ItemWrapper.TYPE_GUOKR_CATEGORY -> {
                (viewHolder as CategoryViewHolder).itemView.text_view_category.text = mContext.getString(R.string.guokr_handpick)
            }
        }
    }

    override fun getItemCount(): Int = mWrapperList.size

    override fun getItemViewType(position: Int): Int = mWrapperList[position].viewType

    fun setOnItemClickListener(listener: OnRecyclerViewItemOnClickListener) {
        this.mListener = listener
    }

    fun getOriginalIndex(position: Int): Int = mWrapperList[position].index

    fun updateData(zhihuDailyNewsList: List<ZhihuDailyNewsQuestion>,
                   doubanMomentNewsList: List<DoubanMomentNewsPosts>,
                   guokrHandpickNewsList: List<GuokrHandpickNewsResult>) {

        mZhihuList.clear()
        mDoubanList.clear()
        mGuokrList.clear()
        mWrapperList.clear()

        mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_ZHIHU_CATEGORY))
        if (zhihuDailyNewsList.isEmpty()) {
            mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_EMPTY))
        } else {
            for (i in zhihuDailyNewsList.indices) {
                val iw = ItemWrapper(ItemWrapper.TYPE_ZHIHU)
                iw.index = i
                mWrapperList.add(iw)
                mZhihuList.add(zhihuDailyNewsList[i])
            }
        }

        mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_DOUBAN_CATEGORY))
        if (doubanMomentNewsList.isEmpty()) {
            mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_EMPTY))
        } else {
            for (i in doubanMomentNewsList.indices) {
                if (doubanMomentNewsList[i].thumbs.isEmpty()) {
                    val iw = ItemWrapper(ItemWrapper.TYPE_DOUBAN_NO_IMG)
                    iw.index = i
                    mWrapperList.add(iw)
                } else {
                    val iw = ItemWrapper(ItemWrapper.TYPE_DOUBAN)
                    iw.index = i
                    mWrapperList.add(iw)
                }
                mDoubanList.add(doubanMomentNewsList[i])
            }
        }

        mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_GUOKR_CATEGORY))
        if (guokrHandpickNewsList.isEmpty()) {
            mWrapperList.add(ItemWrapper(ItemWrapper.TYPE_EMPTY))
        } else {
            for (i in guokrHandpickNewsList.indices) {
                val iw = ItemWrapper(ItemWrapper.TYPE_GUOKR)
                iw.index = i
                mWrapperList.add(iw)
                mGuokrList.add(guokrHandpickNewsList[i])
            }
        }

        notifyDataSetChanged()
    }

    class ZhihuItemViewHolder(
            itemView: View,
            private val listener: OnRecyclerViewItemOnClickListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener?.onItemClick(view, layoutPosition)
        }
    }

    class DoubanItemHolder(
            itemView: View,
            private val listener: OnRecyclerViewItemOnClickListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener?.onItemClick(view, layoutPosition)
        }
    }

    class DoubanNoImageHolder(
            itemView: View,
            private val listener: OnRecyclerViewItemOnClickListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener?.onItemClick(view, layoutPosition)
        }
    }

    class GuokrViewHolder(
            itemView: View,
            private val listener: OnRecyclerViewItemOnClickListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener?.onItemClick(view, layoutPosition)
        }

    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemWrapper(val viewType: Int) {

        var index: Int = 0

        companion object {

            val TYPE_ZHIHU = 0x00
            val TYPE_DOUBAN = 0x01
            val TYPE_GUOKR = 0x02
            val TYPE_EMPTY = 0x03
            val TYPE_DOUBAN_NO_IMG = 0x04
            val TYPE_ZHIHU_CATEGORY = 0x05
            val TYPE_DOUBAN_CATEGORY = 0x06
            val TYPE_GUOKR_CATEGORY = 0x07

        }

    }

}
