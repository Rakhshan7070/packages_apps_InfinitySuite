/*
 * Copyright (C) 2023 AlphaDroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infinity.suite.utils;

import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.DimenRes;


public class ResourceUtils {
    public static Float getFloatDimensionDp(Resources resources, @DimenRes int dimenResId) {
        TypedValue value = new TypedValue();
        resources.getValue(dimenResId, value, true);
        return TypedValue.complexToFloat(value.data);
    }

    public static int getIntDimensionDp(Resources resources, @DimenRes int dimenResId) {
        return getFloatDimensionDp(resources, dimenResId).intValue();
    }
}
