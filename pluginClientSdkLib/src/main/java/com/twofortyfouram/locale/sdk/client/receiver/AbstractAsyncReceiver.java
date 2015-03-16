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

package com.twofortyfouram.locale.sdk.client.receiver;

import com.twofortyfouram.assertion.Assertions;
import com.twofortyfouram.spackle.util.ThreadUtil;
import com.twofortyfouram.spackle.util.ThreadUtil.ThreadPriority;

import net.jcip.annotations.ThreadSafe;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Pair;

/**
 * Simplifies asynchronous broadcast handling. Subclasses call
 * {@link #goAsyncWithCallback(AsyncCallback)}, and the abstract class takes
 * care of executing the callback on a background thread.
 */
@ThreadSafe
/* package */ abstract class AbstractAsyncReceiver extends BroadcastReceiver {

    /*
     * This method is package visible rather than protected so that it will be
     * obfuscated by ProGuard.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB)
    /* package */ final void goAsyncWithCallback(@NonNull final AsyncCallback callback) {
        Assertions.assertNotNull(callback, "callback"); //$NON-NLS-1$

        final PendingResult pendingResult = goAsync();
        if (null == pendingResult) {
            throw new AssertionError(
                    "PendingResult was null.  Was goAsync() called previously?"); //$NON-NLS-1$
        }

        final Handler.Callback handlerCallback = new AsyncHandlerCallback();
        final HandlerThread thread = ThreadUtil.newHandlerThread(getClass().getName(),
                ThreadPriority.BACKGROUND);
        final Handler handler = new Handler(thread.getLooper(), handlerCallback);

        handler.sendMessage(handler.obtainMessage(AsyncHandlerCallback.MESSAGE_HANDLE_CALLBACK,
                new Pair<PendingResult, AsyncCallback>(pendingResult, callback)));
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private static final class AsyncHandlerCallback implements Handler.Callback {

        public static final int MESSAGE_HANDLE_CALLBACK = 0;

        @Override
        public boolean handleMessage(final Message msg) {
            Assertions.assertNotNull(msg, "msg"); //$NON-NLS-1$
            switch (msg.what) {
                case MESSAGE_HANDLE_CALLBACK: {
                    Assertions.assertNotNull(msg.obj, "msg.obj"); //$NON-NLS-1$

                    final Pair<PendingResult, AsyncCallback> pair
                            = (Pair<PendingResult, AsyncCallback>) msg.obj;

                    final PendingResult pendingResult = pair.first;
                    final AsyncCallback asyncCallback = pair.second;

                    try {
                        pendingResult.setResultCode(asyncCallback.runAsync());
                    } finally {
                        pendingResult.finish();
                    }

                    Looper.myLooper().quit();

                    break;
                }
            }
            return false;
        }
    }

    /* package */static interface AsyncCallback {

        /**
         * @return The result code to be set if this is an ordered broadcast.
         */
        public int runAsync();
    }
}
