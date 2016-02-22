/*
 * PasswordlessLoginEvent.java
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

package com.auth0.android.lock.events;

import com.auth0.android.lock.enums.PasswordlessMode;

public class PasswordlessLoginEvent {
    private final PasswordlessMode mode;
    private final String emailOrNumber;
    private final String code;

    public PasswordlessLoginEvent(PasswordlessMode mode, String emailOrNumber) {
        this.mode = mode;
        this.emailOrNumber = emailOrNumber;
        this.code = null;
    }

    public PasswordlessLoginEvent(PasswordlessMode mode, String emailOrNumber, String code) {
        this.mode = mode;
        this.emailOrNumber = emailOrNumber;
        this.code = code;
    }

    public PasswordlessMode getMode() {
        return mode;
    }

    public String getEmailOrNumber() {
        return emailOrNumber;
    }

    public String getCode() {
        return code;
    }
}
