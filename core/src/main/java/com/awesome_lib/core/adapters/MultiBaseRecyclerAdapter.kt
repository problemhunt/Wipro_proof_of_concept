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
import androidx.recyclerview.widget.RecyclerView

/**
 * [MultiBaseRecyclerAdapter] :
 *
 * Base [RecyclerView.Adapter] that implements basic functionality to be used as [RecyclerView.Adapter].
 * It has basic implementation of [BaseRecyclerAdapter.Builder] class used as builder pattern.
 *
 * Class provides implementation for multi-type adapter with different item views by positional-condition.
 *
 * So that you don't need to do some extra stuff to setup new adapter instead use this.
 *
 * ### Example: ###
 *
 *     val adapter = MultiBaseRecyclerAdapter.Builder()
 *             .provideHolderClass { pos, adapter -> return if(pos==1) ViewHolder::class.java else ViewHolder1::class.java }
 *             .provideItemViewType { pos, adapter -> return if(pos==1) R.layout.item1 else R.layout.item2 }
 *             .onCreateHolderCallback { holder, adapter -> }
 *             .onBindViewHolderCallback { holder, pos, adapter -> }
 *             .build()
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 1/22/2019
 */
class MultiBaseRecyclerAdapter private constructor(builder: Builder) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "MultiBaseAdapter"
    }

    /**
     * [MutableList] object containing [Any] nullable type
     */
    var list: MutableList<Any?> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var onBindCallback: ((RecyclerView.ViewHolder, Int, MultiBaseRecyclerAdapter) -> Unit)?
    private var classTypeCallback: ((Int, MultiBaseRecyclerAdapter) -> Class<out RecyclerView.ViewHolder>?)?
    private var viewTypeCallback: ((Int, MultiBaseRecyclerAdapter) -> Int?)?
    private var onCreateCallback: ((RecyclerView.ViewHolder, MultiBaseRecyclerAdapter) -> Unit)?
    private var hasStable: Boolean = false

    init {
        this.list = builder.list ?: ArrayList()
        this.classTypeCallback = builder.classTypeCallback
        this.onBindCallback = builder.onBindCallback
        this.viewTypeCallback = builder.viewTypeCallback
        this.onCreateCallback = builder.onCreateCallback
        this.hasStable = builder.hasStable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        try {
            val classType = classTypeCallback?.invoke(viewType, this)
            if (classType != null) {
                val className: Class<*> = Class.forName(classType.name)
                val viewHolderConst = className.getConstructor(View::class.java)
                val holderObj = viewHolderConst.newInstance(
                    LayoutInflater.from(parent.context).inflate(
                        viewType,
                        parent,
                        false
                    )
                ) as RecyclerView.ViewHolder
                onCreateCallback?.let { it(holderObj, this) }
                return holderObj
            } else {
                throw NullPointerException(
                    "Class object not found for resource : ${parent.context?.resources?.getResourceName(
                        viewType
                    )}, Did you forgot to provide it?"
                )
            }
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }

    override fun getItemCount(): Int = if (list.isNotEmpty()) list.size else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (onBindCallback != null) {
            onBindCallback?.let { it(holder, holder.adapterPosition, this) }
        }
    }

    override fun getItemViewType(position: Int) =
        if (viewTypeCallback != null) {
            viewTypeCallback?.let { it(position, this) } ?: super.getItemViewType(position)
        } else super.getItemViewType(position)

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
     * Method to remove item at [position]
     *
     * @param position as [Int] indicating position of item
     */
    fun removeItemAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
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
     * [Builder] :
     *
     * Builder class for [MultiBaseRecyclerAdapter] to provide builder pattern so that api changes doesn't cause refactor problem.
     *
     * Use this class in congestion with [MultiBaseRecyclerAdapter] to create new object of [RecyclerView.Adapter]
     * and set it to your `RecyclerView` like usual.
     *
     * Check [MultiBaseRecyclerAdapter] for more details.
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/22/2019
     * @see [MultiBaseRecyclerAdapter]
     */
    open class Builder(list: MutableList<Any?>? = ArrayList()) {
        /**
         * [MutableList] variable of [Any] type to hold list of data for this adapter
         */
        var list: MutableList<Any?>? = list
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.onBindViewHolder]
         */
        lateinit var onBindCallback: (RecyclerView.ViewHolder, Int, MultiBaseRecyclerAdapter) -> Unit
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.getItemViewType]
         */
        lateinit var viewTypeCallback: (Int, MultiBaseRecyclerAdapter) -> Int?
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for positional view holder class types
         */
        lateinit var classTypeCallback: (Int, MultiBaseRecyclerAdapter) -> Class<out RecyclerView.ViewHolder>?
            private set
        /**
         * Callback variable of Method expression type to provide referencing callback for [RecyclerView.Adapter.onCreateViewHolder]
         */
        var onCreateCallback: ((RecyclerView.ViewHolder, MultiBaseRecyclerAdapter) -> Unit)? = null
            private set
        var hasStable: Boolean = false
            private set

        init {
            this.list = list
        }

        /**
         * Method to provide conditional view holder class based on position from [classTypeCallback]
         *
         * @param classTypeCallback as Callback parameter of method expression type to provide positional callback
         *
         * @return [Builder] to be used further for another setup method
         */
        fun provideHolderClass(classTypeCallback: (Int, MultiBaseRecyclerAdapter) -> Class<out RecyclerView.ViewHolder>?): Builder {
            this.classTypeCallback = classTypeCallback
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
        fun onCreateHolderCallback(onCreateCallback: (RecyclerView.ViewHolder, MultiBaseRecyclerAdapter) -> Unit): Builder {
            this.onCreateCallback = onCreateCallback
            return this
        }

        /**
         * Method to provide callback upon [RecyclerView.Adapter]'s [RecyclerView.Adapter.onBindViewHolder] method,
         * indicates that [RecyclerView.Adapter.onBindViewHolder] happened so that any implementation
         * should be resolved like setting up some views and binding data to it.
         *
         * @param onBindCallback as Method expression parameter for providing `onBindViewHolder` callback.
         *
         * @return [Builder] to be used further for another setup method
         */
        fun onBindViewHolderCallback(onBindCallback: (RecyclerView.ViewHolder, Int, MultiBaseRecyclerAdapter) -> Unit): Builder {
            this.onBindCallback = onBindCallback
            return this
        }

        /**
         * Method to provide callback upon [RecyclerView.Adapter]'s [RecyclerView.Adapter.getItemViewType] method,
         * indicates that [RecyclerView.Adapter.getItemViewType] happened so that any implementation
         * should be resolved like setting up multiple views positionally.
         *
         * @param viewTypeCallback as Method expression parameter for providing `getItemViewType` callback.
         *
         * @return [Builder] to be used further for another setup method
         */
        fun provideItemViewType(viewTypeCallback: (Int, MultiBaseRecyclerAdapter) -> Int?): Builder {
            this.viewTypeCallback = viewTypeCallback
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
         * Method finally builds new [MultiBaseRecyclerAdapter] object or throw exception if setup is improper.
         *
         * @return [MultiBaseRecyclerAdapter] newly created object when build is successful
         */
        fun build(): MultiBaseRecyclerAdapter {
            return MultiBaseRecyclerAdapter(this)
        }
    }
}