package com.twofortyfouram.locale.sdk.client.ui.util;

import com.twofortyfouram.locale.sdk.client.R;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public final class UiResConstantsTest extends AndroidTestCase {

    @SmallTest
    public static void testMenu_constant() {
        final String expected = "com_twofortyfouram_locale_sdk_client_default_menu"; //$NON-NLS-1$
        final String actual = UiResConstants.MENU_DEFAULT;

        assertEquals(expected, actual);
    }

    @SmallTest
    public void testMenu_resource() {
        final int expected = R.menu.com_twofortyfouram_locale_sdk_client_default_menu;
        final int actual = getContext().getResources()
                .getIdentifier(UiResConstants.MENU_DEFAULT, "menu", getContext().getPackageName());
        assertEquals(expected, actual);
    }

    @SmallTest
    public static void testIdDone_constant() {
        final String expected = "com_twofortyfouram_locale_sdk_client_menu_done"; //$NON-NLS-1$
        final String actual = UiResConstants.ID_MENU_DONE;

        assertEquals(expected, actual);
    }

    @SmallTest
    public void testIdDone_resource() {
        final int expected = R.id.com_twofortyfouram_locale_sdk_client_menu_done;
        final int actual = getContext().getResources()
                .getIdentifier(UiResConstants.ID_MENU_DONE, "id", getContext().getPackageName());
        assertEquals(expected, actual);
    }

    @SmallTest
    public static void testIdCancel_constant() {
        final String expected = "com_twofortyfouram_locale_sdk_client_menu_cancel"; //$NON-NLS-1$
        final String actual = UiResConstants.ID_MENU_CANCEL;

        assertEquals(expected, actual);
    }

    @SmallTest
    public void testIdCancel_resource() {
        final int expected = R.id.com_twofortyfouram_locale_sdk_client_menu_cancel;
        final int actual = getContext().getResources()
                .getIdentifier(UiResConstants.ID_MENU_CANCEL, "id", getContext().getPackageName());
        assertEquals(expected, actual);
    }

    @SmallTest
    public static void testStringBreadcrumbFormat_constant() {
        final String expected = "com_twofortyfouram_locale_sdk_client_breadcrumb_format"; //$NON-NLS-1$
        final String actual = UiResConstants.STRING_BREADCRUMB_FORMAT;

        assertEquals(expected, actual);
    }

    @SmallTest
    public void testStringBreadcrumbFormat_resource() {
        final int expected = R.string.com_twofortyfouram_locale_sdk_client_breadcrumb_format;
        final int actual = getContext().getResources()
                .getIdentifier(UiResConstants.STRING_BREADCRUMB_FORMAT, "string", getContext().getPackageName());
        assertEquals(expected, actual);
    }

    @SmallTest
    public static void testStringBreadcrumbSeparator_constant() {
        final String expected = "com_twofortyfouram_locale_sdk_client_breadcrumb_separator"; //$NON-NLS-1$
        final String actual = UiResConstants.STRING_BREADCRUMB_SEPARATOR;

        assertEquals(expected, actual);
    }

    @SmallTest
    public void testStringBreadcrumbSeparator_resource() {
        final int expected = R.string.com_twofortyfouram_locale_sdk_client_breadcrumb_separator;
        final int actual = getContext().getResources()
                .getIdentifier(UiResConstants.STRING_BREADCRUMB_SEPARATOR, "string", getContext().getPackageName());
        assertEquals(expected, actual);
    }

    @SmallTest
    public static void testStringAppStoreDeepLink_constant() {
        final String expected = "com_twofortyfouram_locale_sdk_client_app_store_deep_link_format";  //$NON-NLS-1$
        final String actual = UiResConstants.STRING_APP_STORE_DEEP_LINK;

        assertEquals(expected, actual);
    }

    @SmallTest
    public void testStringAppStoreDeepLink_resource() {
        final int expected = R.string.com_twofortyfouram_locale_sdk_client_app_store_deep_link_format;
        final int actual = getContext().getResources()
                .getIdentifier(UiResConstants.STRING_APP_STORE_DEEP_LINK, "string", getContext().getPackageName());
        assertEquals(expected, actual);
    }
}
