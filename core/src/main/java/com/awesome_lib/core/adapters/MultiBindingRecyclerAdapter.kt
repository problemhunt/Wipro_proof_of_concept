/*
 * Copyright (C) 2019 Jeel Vankhedde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.awesome_lib.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.awesome_lib.core.adapters.MultiBindingRecyclerAdapter.BindingViewHolder

/**
 * [MultiBindingRecyclerAdapter] :
 *
 * Base [RecyclerView.Adapter] that implements basic functionality to be used as [RecyclerView.Adapter].
 * It has basic implementation of [BaseRecyclerAdapter.Builder] class used as builder pattern.
 *
 * Class uses Data-binding, so implementation requires no `ViewHolder` because, it uses [BindingViewHolder].
 *
 * So that you don't need to do some extra stuff to setup new adapter instead use this.
 *
 * ### Example: ###
 *
 *     val adapter = MultiBindingRecyclerAdapter.Builder()
 *             .onItemViewType { pos, adapter -> return if(pos==1) R.layout.item1 else R.layout.item2 }
 *             .onCreateHolderCallback { holder, adapter -> }
 *             .onBindViewHolderCallback { holder, pos, adapter -> }
 *             .build()
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 1/22/2019
 */
class MultiBindingRecyclerAdapter private constructor(builder: Builder) :
    RecyclerView.Adapter<BindingViewHolder>() {
    companion object {
        // Tag for logcat.
        const val TAG = "MultiBindingAdapter"
    }

    /**
     * [MutableList] object containing [Any] nullable type
     */
    var list: MutableList<Any?> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var onCreateCallback: ((BindingViewHolder, MultiBindingRecyclerAdapter) -> Unit)?
    private var itemViewType: ((Int, MultiBindingRecyclerAdapter) -> Int)?
    private var callback: ((BindingViewHolder, Int, MultiBindingRecyclerAdapter) -> Unit)?
    private var hasStable: Boolean = false

    init {
        this.list = builder.list ?: ArrayList()
        this.onCreateCallback = builder.onCreateCallback
        this.itemViewType = builder.itemViewType
        this.callback = builder.onBindHolderCallback
        this.hasStable = builder.hasStable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val holder = BindingViewHolder(
            DataBindingUtil.inflate<ViewDataBinding?>(
                LayoutInflater.from(parent.context),
                viewType,
                parent,
                false
            ) as ViewDataBinding
        )
        onCreateCallback?.let { it(holder, this) }
        return holder
    }

    override fun getItemCount(): Int = if (list.isNotEmpty()) list.size else 0

    override fun getItemViewType(position: Int): Int {
        if (itemViewType != null) {
            return itemViewType!!(position, this)
        } else
            throw IllegalArgumentException("Invalid layout type")
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        callback?.let { it(holder, holder.adapterPosition, this) }
        holder.binding.executePendingBindings()
    }

    override fun getItemId(position: Int) =
        if (hasStable) position.toLong() else super.getItemId(position)

    /**
     * Clear all data from adapter
     */
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    /**
     * Method to add all data to list which removes all previously added data
     *
     * @param data as [MutableList] of type [T] to be added
     */
    fun <T> addAllItems(data: MutableList<T>?) where T : Any? {
        data?.let { items ->
            list.clear()
            list.addAll(items)
            notifyDataSetChanged()
        }
    }

    /**
     * Method to add all data to list which append data at the end
     *
     * @param data as [MutableList] of type [T] to be added
     */
    fun <T> appendAll(data: MutableList<T>?) where T : Any? {
        data?.let { items ->
            val startIndex = list.size
            list.addAll(items)
            notifyItemRangeChanged(startIndex, items.size)
        }
    }

    /**
     * Method to add [data] to list at the end
     *
     * @param data as [T] to be added at end
     */
    fun <T> addItem(data: T) where T : Any? {
        list.add(data)
        notifyItemInserted(list.size - 1)
    }

    /**
     * Method to indicate that particular item at [position] as [data] item is updated
     *
     * @param position as [Int] indicating position of item
     * @param data as [T] to be update
     */
    fun <T> updateItem(position: Int, data: T) where T : Any? {
        list[position] = data
        notifyItemChanged(position, data)
    }

    /**
     * Method to remove item at [position]
     *
     * @param position as [Int] indicating position of item
     */
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Method to notify change in item passed as [data]
     *
     * @param data as [T] indicating type of item
     */
    fun <T> notifyItemChanged(data: T) where T : Any? {
        notifyItemChanged(list.indexOf(data))
    }

    /**
     * [Builder] :
     *
     * Builder class for [MultiBindingRecyclerAdapter] to provide builder pattern so that api changes doesn't cause refactor problem.
     *
     * Use this class in congestion with [MultiBindingRecyclerAdapter] to create new object of [RecyclerView.Adapter]
     * and set it to your `RecyclerView` like usual.
     *
     * Check [MultiBindingRecyclerAdapter] for more details.
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/22/2019
     * @see [MultiBindingRecyclerAdapter]
     */
    open class Builder {
        /**
         * [MutableList] variable of [Any] type to hold list of data for this adapter
         */
        var list: MutableList<Any?>? = null
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.onCreateViewHolder]
         */
        var onCreateCallback: ((BindingViewHolder, MultiBindingRecyclerAdapter) -> Unit)? = null
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.getItemViewType]
         */
        lateinit var itemViewType: (Int, MultiBindingRecyclerAdapter) -> Int
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.onBindViewHolder]
         */
        lateinit var onBindHolderCallback: (BindingViewHolder, Int, MultiBindingRecyclerAdapter) -> Unit
            private set
        var hasStable: Boolean = false
            private set

        init {
            this.list = ArrayList()
        }

        /**
         * Method to provide callback upon [RecyclerView.Adapter]'s [RecyclerView.Adapter.onCreateViewHolder] method,
         * indicates that [RecyclerView.Adapter.onCreateViewHolder] happened so that any implementation
         * should be resolved like setting up some click listeners.
         *
         * @param onCreateCallback as Method expression parameter for providing `onCreateViewHolder` callback.
         *
         * @return [Builder] to be used further for another setup method
         */
        fun onCreateHolderCallback(onCreateCallback: (BindingViewHolder, MultiBindingRecyclerAdapter) -> Unit): Builder {
            this.onCreateCallback = onCreateCallback
            return this
        }

        /**
         * Method to provide callback upon [RecyclerView.Adapter]'s [RecyclerView.Adapter.getItemViewType] method,
         * indicates that [RecyclerView.Adapter.getItemViewType] happened so that any implementation
         * should be resolved like setting up multiple views positionally.
         *
         * @param itemViewType as Method expression parameter for providing `getItemViewType` callback.
         *
         * @return [Builder] to be used further for another setup method
         */
        fun onItemViewType(itemViewType: (Int, MultiBindingRecyclerAdapter) -> Int): Builder {
            this.itemViewType = itemViewType
            return this
        }

        /**
         * Method to provide callback upon [RecyclerView.Adapter]'s [RecyclerView.Adapter.onBindViewHolder] method,
         * indicates that [RecyclerView.Adapter.onBindViewHolder] happened so that any implementation
         * should be resolved like setting up some views and binding data to it.
         *
         * @param callback as Method expression parameter for providing `onBindViewHolder` callback.
         *
         * @return [Builder] to be used further for another setup method
         */
        fun onBindViewHolderCallback(callback: (BindingViewHolder, Int, MultiBindingRecyclerAdapter) -> Unit): Builder {
            this.onBindHolderCallback = callback
            return this
        }

        /**
         * Method to set boolean flag about setting stable ids of ViewHolder in from this adapter
         *
         * @param hasStable as [Boolean] to set if ViewHolder should have stable ids or not
         *
         * @return [Builder] to be used further for another setup method
         */
        fun hasStableIds(hasStable: Boolean): Builder {
            this.hasStable = hasStable
            return this
        }

        /**
         * Method finally builds new [MultiBindingRecyclerAdapter] object or throw exception if setup is improper.
         *
         * @return [MultiBindingRecyclerAdapter] newly created object when build is successful
         */
        fun build(): MultiBindingRecyclerAdapter {
            return MultiBindingRecyclerAdapter(this)
        }
    }

    /**
     * [BindingViewHolder] :
     *
     * ViewHolder class that holds [binding] object for data-binding values from list to item view.
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/22/2019
     * @see [RecyclerView.ViewHolder]
     * @see [MultiBindingRecyclerAdapter]
     */
    open class BindingViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root)
}