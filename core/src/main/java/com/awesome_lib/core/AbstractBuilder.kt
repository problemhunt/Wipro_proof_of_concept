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

package com.awesome_lib.core

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * [AbstractBuilder] :
 *
 * Main **builder** class for Activity/Fragment that is used as *Model* to hold data used in abstract activity/fragment class.
 *
 * Check out variables:
 *   1. [AbstractBuilder.contentView]
 *   2. [AbstractBuilder.abstractBinding]
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 13-10-2019
 */
class AbstractBuilder {
    /**
     * Variable that hold layout `resource id` of Activity
     */
    @LayoutRes
    var contentView: Int? = null

    /**
     * Variable to be set if used **Data-Binding** to provide Generic logic for [AbstractBinding].
     */
    var abstractBinding: AbstractBinding<out ViewDataBinding>? = null

    var isCacheFragment: Boolean = false
}