/*
 * android-plugin-client-sdk-for-locale https://github.com/twofortyfouram/android-plugin-client-sdk-for-locale
 * Copyright 2014 two forty four a.m. LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twofortyfouram.locale.sdk.client.internal;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.twofortyfouram.test.assertion.MoarAsserts;

public final class BreadCrumberTest extends AndroidTestCase {

    @SmallTest
    public void testNonInstantiable() {
        MoarAsserts.assertNoninstantiable(BreadCrumber.class);
    }

    @SmallTest
    public void testGenerateBreadcrumb_null_intent() {
        assertEquals("baz", BreadCrumber
                .generateBreadcrumb(getContext(), null, "baz")); //$NON-NLS-1$//$NON-NLS-2$
    }

    @SmallTest
    public void testGenerateBreadcrumb_missing_extra() {
        final Intent i = new Intent();

        assertEquals("baz",
                BreadCrumber.generateBreadcrumb(getContext(), i, "baz")); //$NON-NLS-1$//$NON-NLS-2$
    }

    @SmallTest
    public void testGenerateBreadcrumb_normal() {
        final Intent i = new Intent();
        i.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BREADCRUMB,
                "foo > bar"); //$NON-NLS-1$

        assertEquals("foo > bar > baz",
                BreadCrumber.generateBreadcrumb(getContext(), i, "baz")); //$NON-NLS-1$//$NON-NLS-2$
    }
}
