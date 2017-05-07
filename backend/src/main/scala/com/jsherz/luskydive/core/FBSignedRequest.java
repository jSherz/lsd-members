/**
 * MIT License
 * <p>
 * Copyright (c) 2016 James Sherwood-Jones <james.sherwoodjones@gmail.com>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.jsherz.luskydive.core;

import com.restfb.Facebook;
import scala.Option;

import java.time.Instant;

/**
 * This class is written in Java for easy interoperability with the RestFB library.
 */
public class FBSignedRequest {

    @Facebook("user_id")
    public String userId;

    @Facebook("oauth_token")
    public String oauthToken;

    @Facebook("expires")
    public Long expires;

    @Facebook("issued_at")
    public Long issuedAt;

    public FBSignedRequest() {
    }

    public FBSignedRequest(final String userId, final String oauthToken, final Long expires, final Long issuedAt) {
        this.userId = userId;
        this.oauthToken = oauthToken;
        this.expires = expires;
        this.issuedAt = issuedAt;
    }


    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public void setOauthToken(final String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public void setExpires(final Long expires) {
        this.expires = expires;
    }

    public void setIssuedAt(final Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Option<Instant> getExpires() {
        return convertToDate(expires);
    }

    public Option<Instant> getIssuedAt() {
        return convertToDate(issuedAt);
    }

    /**
     * This *wonderful* specimen is a result of null.asInstanceOf[Long] always returning 0. We need a null value to test
     * this method and so this ~hack~ workaround produces one.
     *
     * @return IT'S ALWAYS NULL! ALWAYS!
     */
    public static Long nullLong() {
        return null;
    }

    private static Option<Instant> convertToDate(final Long rawSeconds) {
        if (rawSeconds == null) {
            return Option.empty();
        } else {
            return Option.apply(Instant.ofEpochSecond(rawSeconds));
        }
    }

}
