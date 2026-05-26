/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

/**
 * Translates code points to their Unicode escaped value.
 *
 * @since 3.0
 * @deprecated As of <a href="https://commons.apache.org/proper/commons-lang/changes-report.html#a3.6">3.6</a>, use Apache Commons Text
 * <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/translate/UnicodeEscaper.html">
 * UnicodeEscaper</a>.
 */
@Deprecated
public class UnicodeEscaper extends CodePointTranslator {

    /**
     * Constructs a {@link UnicodeEscaper} above the specified value (exclusive).
     *
     * @param codePoint above which to escape.
     * @return the newly created {@link UnicodeEscaper} instance.
     */
    public static UnicodeEscaper above(final int codePoint) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Constructs a {@link UnicodeEscaper} below the specified value (exclusive).
     *
     * @param codePoint below which to escape.
     * @return the newly created {@link UnicodeEscaper} instance.
     */
    public static UnicodeEscaper below(final int codePoint) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Constructs a {@link UnicodeEscaper} between the specified values (inclusive).
     *
     * @param codePointLow above which to escape.
     * @param codePointHigh below which to escape.
     * @return the newly created {@link UnicodeEscaper} instance.
     */
    public static UnicodeEscaper between(final int codePointLow, final int codePointHigh) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Constructs a {@link UnicodeEscaper} outside of the specified values (exclusive).
     *
     * @param codePointLow below which to escape.
     * @param codePointHigh above which to escape.
     * @return the newly created {@link UnicodeEscaper} instance.
     */
    public static UnicodeEscaper outsideOf(final int codePointLow, final int codePointHigh) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private final int below;

    private final int above;

    private final boolean between;

    /**
     * Constructs a {@link UnicodeEscaper} for all characters.
     */
    public UnicodeEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }

    /**
     * Constructs a {@link UnicodeEscaper} for the specified range. This is
     * the underlying method for the other constructors/builders. The {@code below}
     * and {@code above} boundaries are inclusive when {@code between} is
     * {@code true} and exclusive when it is {@code false}.
     *
     * @param below int value representing the lowest code point boundary.
     * @param above int value representing the highest code point boundary.
     * @param between whether to escape between the boundaries or outside them.
     */
    protected UnicodeEscaper(final int below, final int above, final boolean between) {
        this.below = below;
        this.above = above;
        this.between = between;
    }

    /**
     * Converts the given code point to a hexadecimal string of the form {@code "\\uXXXX"}
     *
     * @param codePoint
     *            a Unicode code point.
     * @return the hexadecimal string for the given code point.
     * @since 3.2
     */
    protected String toUtf16Escape(final int codePoint) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean translate(final int codePoint, final Writer out) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
