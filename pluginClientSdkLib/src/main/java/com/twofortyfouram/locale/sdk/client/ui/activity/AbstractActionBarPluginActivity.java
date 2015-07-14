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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;

import com.twofortyfouram.annotation.VisibleForTesting;
import com.twofortyfouram.annotation.VisibleForTesting.Visibility;
import com.twofortyfouram.assertion.Assertions;
import com.twofortyfouram.assertion.BundleAssertions;
import com.twofortyfouram.log.Lumberjack;
import com.twofortyfouram.spackle.util.bundle.BundleComparer;
import com.twofortyfouram.spackle.util.bundle.BundleScrubber;

import net.jcip.annotations.NotThreadSafe;

/**
 * <p>NOTE: This class is for compatibility with Material Design via the appcompat-v7 library.  To use this
 * class, appcompat-v7 must be on the application's build path.  Typically, this would involve adding
 * appcompat-v7 to the dependencies section of the application's build.gradle script.  For example,
 * the dependency might look something like this
 * {@code compile group:'com.android.support', name:'appcompat-v7', version:'[21,)'}</p>
 * <p>
 * Implements the basic behaviors of a "Edit" activity for a
 * plug-in, handling the Intent protocol for storing and retrieving the plug-in's data.
 * Recall that a plug-in Activity more or less saves a Bundle and a String blurb via the Intent
 * extras {@link com.twofortyfouram.locale.api.Intent#EXTRA_BUNDLE EXTRA_BUNDLE} and {@link
 * com.twofortyfouram.locale.api.Intent#EXTRA_STRING_BLURB EXTRA_STRING_BLURB}.
 * Those extras represent the configured plug-in, so this Activity helps plug-ins store and
 * retrieve
 * those
 * extras while abstracting the actual Intent protocol.
 * </p>
 * <p>
 * The Activity can be started in one of two states:
 * <ul>
 * <li>New plug-in instance: The Activity's Intent will not contain
 * {@link com.twofortyfouram.locale.api.Intent#EXTRA_BUNDLE EXTRA_BUNDLE}.</li>
 * <li>Old plug-in instance: The Activity's Intent will contain
 * {@link com.twofortyfouram.locale.api.Intent#EXTRA_BUNDLE EXTRA_BUNDLE} and {@link
 * com.twofortyfouram.locale.api.Intent#EXTRA_STRING_BLURB EXTRA_STRING_BLURB} from a previously
 * saved plug-in instance that the user is editing. The previously saved Bundle
 * and blurb can be retrieved at any time via {@link #getPreviousBundle()} and
 * {@link #getPreviousBlurb()}. These will also be delivered via
 * {@link #onPostCreateWithPreviousResult(android.os.Bundle, String)} during the
 * Activity's {@link #onPostCreate(android.os.Bundle)} phase when the Activity is first
 * created.</li>
 * </ul>
 * <p>During
 * the Activity's {@link #finish()} lifecycle callback, this class will call {@link
 * #getResultBundle()} and {@link #getResultBlurb(android.os.Bundle)}, which should return the
 * Bundle and blurb data the Activity would like to save back to the host.
 * </p>
 * <p>
 * Note that all of these behaviors only apply if the Intent
 * starting the Activity is one of the plug-in "edit" Intent actions.
 * </p>
 *
 * @see com.twofortyfouram.locale.api.Intent#ACTION_EDIT_CONDITION ACTION_EDIT_CONDITION
 * @see com.twofortyfouram.locale.api.Intent#ACTION_EDIT_SETTING ACTION_EDIT_SETTING
 */
@NotThreadSafe
public abstract class AbstractActionBarPluginActivity extends ActionBarActivity {

    /**
     * Flag boolean that can be set prior to calling {@link #finish()} to control whether the
     * Activity
     * attempts to save a result back to the host.  Typically this is only set to true after an
     * explicit user interaction to abort editing the plug-in, such as tapping a "cancel" button.
     */
    /*
     * There is no need to save/restore this field's state.
     */
    protected boolean mIsCancelled = false;

    /**
     * @param intent Intent to check.
     * @return True if intent is a Locale plug-in edit Intent.
     */
    @VisibleForTesting(Visibility.PRIVATE)
    /* package */ static boolean isLocalePluginIntent(@NonNull final Intent intent) {
        Assertions.assertNotNull(intent, "intent"); //$NON-NLS-1$

        final String action = intent.getAction();

        return com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION.equals(action) || com.twofortyfouram.locale
                .api.Intent.ACTION_EDIT_SETTING.equals(action);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLocalePluginIntent(getIntent())) {
            BundleScrubber.scrub(getIntent());

            final Bundle previousBundle = getPreviousBundle();
            BundleScrubber.scrub(previousBundle);

            Lumberjack.v("Creating Activity with Intent=%s, savedInstanceState=%s, EXTRA_BUNDLE=%s", getIntent(),
                    savedInstanceState, previousBundle); //$NON-NLS-1$
        }
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (isLocalePluginIntent(getIntent())) {
            if (null == savedInstanceState) {
                final Bundle previousBundle = getPreviousBundle();
                final String previousBlurb = getPreviousBlurb();
                if (null != previousBundle && null != previousBlurb) {
                    onPostCreateWithPreviousResult(previousBundle, previousBlurb);
                }
            }
        }
    }

    @Override
    public void finish() {
        if (isLocalePluginIntent(getIntent())) {
            if (!mIsCancelled) {
                final Bundle resultBundle = getResultBundle();

                if (null != resultBundle) {
                    BundleAssertions.assertSerializable(resultBundle);

                    final String blurb = getResultBlurb(resultBundle);
                    Assertions.assertNotNull(blurb, "blurb"); //$NON-NLS-1$

                    if (!BundleComparer.areBundlesEqual(resultBundle, getPreviousBundle()) && !blurb.equals
                            (getPreviousBlurb())) {
                        final Intent resultIntent = new Intent();
                        resultIntent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE, resultBundle);
                        resultIntent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB, blurb);

                        setResult(RESULT_OK, resultIntent);
                    }
                }
            }
        }

        /*
         * Super call must come after the Activity result is set. If it comes
         * first, then the Activity result will be lost.
         */
        super.finish();
    }

    /**
     * @return The {@link com.twofortyfouram.locale.api.Intent#EXTRA_BUNDLE EXTRA_BUNDLE} that was
     * previously saved to the host and subsequently passed back to this Activity for further
     * editing.  Internally, this method relies on {@link #isBundleValid(android.os.Bundle)}.  If
     * the bundle exists but is not valid, this method will return null.
     */
    @Nullable
    public final Bundle getPreviousBundle() {
        final Bundle bundle = getIntent().getBundleExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE);

        if (null != bundle) {
            if (isBundleValid(bundle)) {
                return bundle;
            }
        }

        return null;
    }

    /**
     * @return The {@link com.twofortyfouram.locale.api.Intent#EXTRA_STRING_BLURB
     * EXTRA_STRING_BLURB} that was
     * previously saved to the host and subsequently passed back to this Activity for further
     * editing.
     */
    @Nullable
    public final String getPreviousBlurb() {
        final String blurb = getIntent().getStringExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB);

        return blurb;
    }

    /**
     * <p>Validates the Bundle, to ensure that a malicious application isn't attempting to pass
     * an invalid Bundle.</p>
     *
     * @param bundle The plug-in's Bundle previously returned by the edit
     *               Activity.  {@code bundle} should not be mutated by this method.
     * @return true if {@code bundle} is valid for the plug-in.
     */
    public abstract boolean isBundleValid(@NonNull final Bundle bundle);

    /**
     * Plug-in Activity lifecycle callback to allow the Activity to restore
     * state for editing a previously saved plug-in instance. This callback will
     * occur during the onPostCreate() phase of the Activity lifecycle.
     * <p>{@code bundle} will have been
     * validated by {@link #isBundleValid(android.os.Bundle)} prior to this
     * method being called.  If {@link #isBundleValid(android.os.Bundle)} returned false, then this
     * method will not be called.  This helps ensure that plug-in Activity subclasses only have to
     * worry about bundle validation once, in the {@link #isBundleValid(android.os.Bundle)}
     * method.</p>
     * <p>Note this callback only occurs the first time the Activity is created, so it will not be
     * called
     * when the Activity is recreated (e.g. {@code savedInstanceState != null}) such as after a
     * configuration change like a screen rotation.</p>
     *
     * @param previousBundle Previous bundle that the Activity saved.
     * @param previousBlurb  Previous blurb that the Activity saved
     */
    public abstract void onPostCreateWithPreviousResult(@NonNull final Bundle previousBundle,
                                                        @NonNull final String previousBlurb);

    /**
     * @return Bundle for the plug-in or {@code null} if a valid Bundle cannot
     * be generated.
     */
    @Nullable
    public abstract Bundle getResultBundle();

    /**
     * @param bundle Valid bundle for the component.
     * @return Blurb for {@code bundle}.
     */
    @NonNull
    public abstract String getResultBlurb(@NonNull final Bundle bundle);
}
