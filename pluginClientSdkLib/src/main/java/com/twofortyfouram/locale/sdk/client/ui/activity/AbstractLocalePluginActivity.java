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

package com.twofortyfouram.locale.sdk.client.ui.activity;

import com.twofortyfouram.locale.sdk.client.ui.util.BreadCrumber;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.util.AndroidSdkVersion;

import net.jcip.annotations.NotThreadSafe;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Extends {@link com.twofortyfouram.locale.sdk.client.ui.activity.AbstractPluginActivity} with UI
 * elements for a more consistent plug-in experience for users.  This Activity takes care of
 * initializing the ActionBar with the host application's icon, configuring the title and
 * breadcrumb, and configuring done and cancel
 * ActionBar buttons.
 */
@NotThreadSafe
public abstract class AbstractLocalePluginActivity extends AbstractPluginActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLocalePluginIntent(getIntent())) {
            if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
                setupTitleApi11OrLater();
            } else {
                setTitle(BreadCrumber.generateBreadcrumb(getApplicationContext(), getIntent(),
                        getActivityLabel()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (isLocalePluginIntent(getIntent())) {
            final int menuResId = getResources().getIdentifier(
                    com.twofortyfouram.locale.sdk.client.ui.util.UiResConstants.MENU_DEFAULT, "menu",
                    getPackageName()); //$NON-NLS-1$

            if (0 != menuResId) {
                getMenuInflater().inflate(menuResId, menu);

                if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.HONEYCOMB)) {
                    setupActionBarApi11OrLater();
                }

                if (AndroidSdkVersion.isAtLeastSdk(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
                    setupActionBarApi14OrLater();
                }
            }
        }

        return true;
    }

    @Override
    public final boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        if (isLocalePluginIntent(getIntent())) {
            final int id = item.getItemId();

            final int cancelResId = getResources().getIdentifier(
                    com.twofortyfouram.locale.sdk.client.ui.util.UiResConstants.ID_MENU_CANCEL, "id",
                    getPackageName()); //$NON-NLS-1$
            final int saveResId = getResources().getIdentifier(
                    com.twofortyfouram.locale.sdk.client.ui.util.UiResConstants.ID_MENU_DONE, "id",
                    getPackageName()); //$NON-NLS-1$

            if (android.R.id.home == id) {
                /*
                 * It is always important to know when to bend the rules!
                 * According to Android UI guidelines, this should be an "up"
                 * navigation action rather than a "back" navigation action.
                 * Plug-ins are supposed to look and feel as if there are a
                 * native part of the host however, so a back navigation action
                 * feels more natural here.
                 */
                finish();
                return true;
            } else if (cancelResId == id) {
                mIsCancelled = true;
                finish();
                return true;
            } else if (saveResId == id) {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private String getActivityLabel() {
        final PackageManager pm = getPackageManager();

        try {
            final ActivityInfo info = pm.getActivityInfo(getComponentName(), 0);

            return info.loadLabel(pm).toString();
        } catch (final NameNotFoundException e) {
            Lumberjack.e("My package couldn't be found%s", e); //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupTitleApi11OrLater() {
        CharSequence callingApplicationLabel = null;
        try {
            callingApplicationLabel =
                    getPackageManager().getApplicationLabel(
                            getPackageManager().getApplicationInfo(getCallingPackage(),
                                    0));
        } catch (final NameNotFoundException e) {
            Lumberjack.e("Calling package couldn't be found%s", e); //$NON-NLS-1$
        }
        if (null != callingApplicationLabel) {
            setTitle(callingApplicationLabel);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBarApi11OrLater() {
        getActionBar().setSubtitle(
                BreadCrumber.generateBreadcrumb(getApplicationContext(), getIntent(),
                        getActivityLabel()));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setupActionBarApi14OrLater() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        /*
         * Note: There is a small TOCTOU error here, in that the host could be
         * uninstalled right after launching the plug-in. That would cause
         * getApplicationIcon() to return the default application icon. It won't
         * fail, but it will return an incorrect icon. In practice, the chances
         * that the host will be uninstalled while the plug-in UI is running are
         * very slim.
         */
        try {
            getActionBar().setIcon(getPackageManager().getApplicationIcon(getCallingPackage()));
        } catch (final NameNotFoundException e) {
            Lumberjack.w("An error occurred loading the host's icon%s", e); //$NON-NLS-1$
        }
    }
}
