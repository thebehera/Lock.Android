/*
 * Constants.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock;

public abstract class Constants {
    public static final String LIBRARY_NAME = "Lock.Android";

    public static final String OPTIONS_EXTRA = "com.auth0.android.lock.key.Options";

    public static final String AUTHENTICATION_ACTION = "com.auth0.android.lock.action.Authentication";
    public static final String SIGN_UP_ACTION = "com.auth0.android.lock.action.SignUp";
    public static final String CANCELED_ACTION = "com.auth0.android.lock.action.Canceled";
    public static final String INVALID_CONFIGURATION_ACTION = "com.auth0.android.lock.action.InvalidConfiguration";

    public static final String ERROR_EXTRA = "com.auth0.android.lock.extra.Error";
    public static final String ID_TOKEN_EXTRA = "com.auth0.android.lock.extra.IdToken";
    public static final String ACCESS_TOKEN_EXTRA = "com.auth0.android.lock.extra.AccessToken";
    public static final String TOKEN_TYPE_EXTRA = "com.auth0.android.lock.extra.TokenType";
    public static final String REFRESH_TOKEN_EXTRA = "com.auth0.android.lock.extra.RefreshToken";
    public static final String EMAIL_EXTRA = "com.auth0.android.lock.extra.Email";
    public static final String USERNAME_EXTRA = "com.auth0.android.lock.extra.Username";
}
