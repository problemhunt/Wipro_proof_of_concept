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
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * [BaseRecyclerAdapter] :
 *
 * Base [RecyclerView.Adapter] that implements basic functionality to be used as [RecyclerView.Adapter].
 * It has basic implementation of [BaseRecyclerAdapter.Builder] class used as builder pattern.
 *
 * So that you don't need to do some extra stuff to setup new adapter instead use this.
 *
 * ### Example: ###
 *
 *     val adapter = BaseRecyclerAdapter.Builder()
 *             .setLayoutResId(R.layout.item_example)
 *             .setViewHolderClass(ViewHolder::class.java)
 *             .onCreateHolderCallback { holder, adapter -> }
 *             .onBindViewHolderCallback { holder, pos, adapter -> }
 *             .build()
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 1/22/2019
 */
class BaseRecyclerAdapter private constructor(builder: Builder) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "BaseRecyclerAdapter"
    }

    /**
     * [MutableList] object containing [Any] nullable type
     */
    var list: MutableList<Any?> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    @LayoutRes
    private var itemResId: Int = -1
    private var viewHolderClass: Class<out RecyclerView.ViewHolder>? = null
    private var callback: ((RecyclerView.ViewHolder, Int, BaseRecyclerAdapter) -> Unit)?
    private var onCreateCallback: ((RecyclerView.ViewHolder, BaseRecyclerAdapter) -> Unit)?
    private var hasStable: Boolean = false

    init {
        this.list = builder.list ?: ArrayList()
        this.itemResId = builder.itemResId
        this.viewHolderClass = builder.viewHolderClass
        this.callback = builder.callback
        this.onCreateCallback = builder.onCreateCallback
        this.hasStable = builder.hasStable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        try {
            val className: Class<*> = Class.forName(viewHolderClass!!.name)
            val viewHolderConst = className.getConstructor(View::class.java)
            val holderObj = viewHolderConst.newInstance(
                LayoutInflater.from(parent.context).inflate(
                    itemResId,
                    parent,
                    false
                )
            ) as RecyclerView.ViewHolder
            onCreateCallback?.let { it(holderObj, this) }
            return holderObj
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }

    override fun getItemCount(): Int = if (list.isNotEmpty()) list.size else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        callback?.let { it(holder, holder.adapterPosition, this) }
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
     * Method to notify change in item passed as [data]
     *
     * @param data as [T] indicating type of item
     */
    fun <T> notifyItemChanged(data: T) where T : Any? {
        notifyItemChanged(list.indexOf(data))
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
     * [Builder] :
     *
     * Builder class for [BaseRecyclerAdapter] to provide builder pattern so that api changes doesn't cause refactor problem.
     *
     * Use this class in congestion with [BaseRecyclerAdapter] to create new object of [RecyclerView.Adapter]
     * and set it to your `RecyclerView` like usual.
     *
     * Check [BaseRecyclerAdapter] for more details.
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/22/2019
     * @see [BaseRecyclerAdapter]
     */
    open class Builder(list: MutableList<Any?>? = ArrayList()) {
        /**
         * [MutableList] variable of [Any] type to hold list of data for this adapter
         */
        var list: MutableList<Any?>? = list
            private set
        /**
         * [Int] variable of [LayoutRes] type to hold item layout for this adapter
         */
        @LayoutRes
        var itemResId: Int = -1
            private set
        /**
         * [Class] variable of [RecyclerView.ViewHolder] type to hold class representation of holder that holds item layout logic for this adapter
         */
        var viewHolderClass: Class<out RecyclerView.ViewHolder>? = null
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.onBindViewHolder]
         */
        lateinit var callback: (RecyclerView.ViewHolder, Int, BaseRecyclerAdapter) -> Unit
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.onCreateViewHolder]
         */
        var onCreateCallback: ((RecyclerView.ViewHolder, BaseRecyclerAdapter) -> Unit)? = null
            private set
        var hasStable: Boolean = false
            private set

        init {
            this.list = list
        }

        /**
         * Method to set layout resource id for this adapter,
         * holds [LayoutRes] id to be used inside [RecyclerView.Adapter.onCreateViewHolder] when inflating item layout
         *
         * @param itemResId as [Int] id of type [LayoutRes] to be inflated
         *
         * @return [Builder] to be used further for another setup method
         */
        fun setLayoutResId(@LayoutRes itemResId: Int): Builder {
            this.itemResId = itemResId
            return this
        }

        /**
         * Method to set view holder for this adapter,
         * holds [Class] of [RecyclerView.ViewHolder] type to be used inside
         * [RecyclerView.Adapter.onCreateViewHolder] when creating instance of new class type runtime
         *
         * @param viewHolderClass as [Class] of type [RecyclerView.ViewHolder]
         *
         * @return [Builder] to be used further for another setup method
         */
        fun setViewHolderClass(viewHolderClass: Class<out RecyclerView.ViewHolder>): Builder {
            this.viewHolderClass = viewHolderClass
            return this
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
        fun onCreateHolderCallback(onCreateCallback: (RecyclerView.ViewHolder, BaseRecyclerAdapter) -> Unit): Builder {
            this.onCreateCallback = onCreateCallback
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
        fun onBindViewHolderCallback(callback: (RecyclerView.ViewHolder, Int, BaseRecyclerAdapter) -> Unit): Builder {
            this.callback = callback
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
         * Method finally builds new [BaseRecyclerAdapter] object or throw exception if setup is improper.
         *
         * @return [BaseRecyclerAdapter] newly created object when build is successful
         */
        fun build(): BaseRecyclerAdapter {
            return BaseRecyclerAdapter(this)
        }
    }
}