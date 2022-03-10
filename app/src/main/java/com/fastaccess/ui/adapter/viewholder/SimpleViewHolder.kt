package com.fastaccess.ui.adapter.viewholder

import android.view.View
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.R

/**
 * Created by Kosh on 31 Dec 2016, 3:12 PM
 */
class SimpleViewHolder<O>(
    itemView: View,
    adapter: BaseRecyclerAdapter<O, SimpleViewHolder<O>, OnItemClickListener<O>>?
) :
    BaseViewHolder<O>(itemView, adapter) {
    val title: FontTextView = itemView.findViewById(R.id.title)
    override fun bind(o: O) {
        title.text = o.toString()
    }
}