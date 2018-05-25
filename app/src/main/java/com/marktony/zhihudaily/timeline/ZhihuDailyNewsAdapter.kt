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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marktony.zhihudaily.R
import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.glide.loadImage
import com.marktony.zhihudaily.interfaze.OnRecyclerViewItemOnClickListener
import kotlinx.android.synthetic.main.item_universal_layout.view.*

/**
 * Created by lizhaotailang on 2017/5/21.
 *
 * Adapter between the data of [ZhihuDailyNewsQuestion] and [RecyclerView].
 */

class ZhihuDailyNewsAdapter(private val mList: MutableList<ZhihuDailyNewsQuestion>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnRecyclerViewItemOnClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_universal_layout, viewGroup, false), mListener)

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val (images, _, _, _, title) = mList[i]

        with((viewHolder as ItemViewHolder).itemView) {
            image_view_cover.loadImage(images?.get(0))
            text_view_title.text = title
        }
    }

    override fun getItemCount(): Int = mList.size

    fun setItemClickListener(listener: OnRecyclerViewItemOnClickListener) {
        this.mListener = listener
    }

    fun updateData(list: List<ZhihuDailyNewsQuestion>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
        notifyItemRemoved(list.size)
    }

    class ItemViewHolder(
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

}
