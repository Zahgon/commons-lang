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
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import org.apache.commons.lang3.CharUtils;

/**
 * Translate XML numeric entities of the form &amp;#[xX]?\d+;? to the specific code point.
 *
 * Note that the semicolon is optional.
 *
 * @since 3.0
 * @deprecated As of <a href="https://commons.apache.org/proper/commons-lang/changes-report.html#a3.6">3.6</a>, use Apache Commons Text
 *             <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/translate/NumericEntityUnescaper.html">
 *             NumericEntityUnescaper</a>.
 */
@Deprecated
public class NumericEntityUnescaper extends CharSequenceTranslator {

    /**
     * Enumerates NumericEntityUnescaper options for unescaping.
     *
     * @deprecated As of 3.18.0, use Apache Commons Text <a href=
     *             "https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/translate/NumericEntityUnescaper.OPTION.html">
     *             NumericEntityUnescaper.OPTION</a>.
     */
    @Deprecated
    public enum OPTION {

        /**
         * Require a semicolon.
         */
        semiColonRequired,
        /**
         * Do not require a semicolon.
         */
        semiColonOptional,
        /**
         * Throw an exception if a semicolon is missing.
         */
        errorIfNoSemiColon
    }

    // TODO?: Create an OptionsSet class to hide some of the conditional logic below
    private final EnumSet<OPTION> options;

    /**
     * Create a UnicodeUnescaper.
     *
     * The constructor takes a list of options, only one type of which is currently
     * available (whether to allow, error or ignore the semicolon on the end of a
     * numeric entity to being missing).
     *
     * For example, to support numeric entities without a ';':
     *    new NumericEntityUnescaper(NumericEntityUnescaper.OPTION.semiColonOptional)
     * and to throw an IllegalArgumentException when they're missing:
     *    new NumericEntityUnescaper(NumericEntityUnescaper.OPTION.errorIfNoSemiColon)
     *
     * Note that the default behavior is to ignore them.
     *
     * @param options to apply to this unescaper.
     */
    public NumericEntityUnescaper(final OPTION... options) {
        if (options.length > 0) {
            this.options = EnumSet.copyOf(Arrays.asList(options));
        } else {
            this.options = EnumSet.copyOf(Collections.singletonList(OPTION.semiColonRequired));
        }
    }

    /**
     * Tests whether the passed in option is currently set.
     *
     * @param option to check state of.
     * @return whether the option is set.
     */
    public boolean isSet(final OPTION option) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
