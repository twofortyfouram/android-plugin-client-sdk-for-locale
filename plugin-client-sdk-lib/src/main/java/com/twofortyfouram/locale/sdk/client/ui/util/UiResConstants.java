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

package com.twofortyfouram.locale.sdk.client.ui.util;



import net.jcip.annotations.ThreadSafe;

import android.support.annotation.NonNull;

/**
 * Resource constants to allow the UI to load resources dynamically at runtime.  This enables
 * backwards compatibility with Ant+JAR builds in addition to Gradle+AAR.
 *
 * This is NOT a public API.
 */
@ThreadSafe
public final class UiResConstants {

    /**
     * Resource constant for the default menu.
     */
    @NonNull
    public static final String MENU_DEFAULT = "com_twofortyfouram_locale_sdk_client_default_menu";  //$NON-NLS-1$

    @NonNull
    public static final String ID_MENU_DONE = "com_twofortyfouram_locale_sdk_client_menu_done";  //$NON-NLS-1$

    @NonNull
    public static final String ID_MENU_CANCEL =  "com_twofortyfouram_locale_sdk_client_menu_cancel";  //$NON-NLS-1$

    @NonNull
    public static final String STRING_BREADCRUMB_FORMAT = "com_twofortyfouram_locale_sdk_client_breadcrumb_format";  //$NON-NLS-1$

    @NonNull
    public static final String STRING_BREADCRUMB_SEPARATOR
            = "com_twofortyfouram_locale_sdk_client_breadcrumb_separator"; //$NON-NLS-1$

    @NonNull
    public static final String STRING_APP_STORE_DEEP_LINK = "com_twofortyfouram_locale_sdk_client_app_store_deep_link_format";  //$NON-NLS-1$

    /**
     * Private constructor prevents instantiation.
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private UiResConstants() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
