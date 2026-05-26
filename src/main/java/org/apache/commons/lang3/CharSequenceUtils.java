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
package org.apache.commons.lang3;

/**
 * Operations on {@link CharSequence} that are
 * {@code null} safe.
 *
 * @see CharSequence
 * @since 3.0
 */
public class CharSequenceUtils {

    private static final int NOT_FOUND = -1;

    static final int TO_STRING_LIMIT = 16;

    private static boolean checkLaterThan1(final CharSequence cs, final CharSequence searchChar, final int len2, final int start1) {
        for (int i = 1, j = len2 - 1; i <= j; i++, j--) {
            if (cs.charAt(start1 + i) != searchChar.charAt(i) || cs.charAt(start1 + j) != searchChar.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Used by the indexOf(CharSequence methods) as a green implementation of indexOf.
     *
     * @param cs         the {@link CharSequence} to be processed.
     * @param searchChar the {@link CharSequence} to be searched for.
     * @param start      the start index.
     * @return the index where the search sequence was found, or {@code -1} if there is no such occurrence.
     */
    static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the index within {@code cs} of the first occurrence of the specified character, starting the search at the specified index.
     * <p>
     * If a character with value {@code searchChar} occurs in the character sequence represented by the {@code cs} object at an index no smaller than
     * {@code start}, then the index of the first such occurrence is returned. For values of {@code searchChar} in the range from 0 to 0xFFFF (inclusive), this
     * is the smallest value <em>k</em> such that:
     * </p>
     *
     * <pre>
     * (this.charAt(<em>k</em>) == searchChar) &amp;&amp; (<em>k</em> &gt;= start)
     * </pre>
     * <p>
     * is true. For other values of {@code searchChar}, it is the smallest value <em>k</em> such that:
     * </p>
     *
     * <pre>
     * (this.codePointAt(<em>k</em>) == searchChar) &amp;&amp; (<em>k</em> &gt;= start)
     * </pre>
     * <p>
     * is true. In either case, if no such character occurs inm {@code cs} at or after position {@code start}, then {@code -1} is returned.
     * </p>
     * <p>
     * There is no restriction on the value of {@code start}. If it is negative, it has the same effect as if it were zero: the entire {@link CharSequence} may
     * be searched. If it is greater than the length of {@code cs}, it has the same effect as if it were equal to the length of {@code cs}: {@code -1} is
     * returned.
     * </p>
     * <p>
     * All indices are specified in {@code char} values (Unicode code units).
     * </p>
     *
     * @param cs         the {@link CharSequence} to be processed, not null.
     * @param searchChar the char to be searched for.
     * @param start      the start index, negative starts at the string start.
     * @return the index where the search char was found, -1 if not found.
     * @since 3.6 updated to behave more like {@link String}.
     */
    static int indexOf(final CharSequence cs, final int searchChar, int start) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Used by the lastIndexOf(CharSequence methods) as a green implementation of lastIndexOf
     *
     * @param cs the {@link CharSequence} to be processed.
     * @param searchChar the {@link CharSequence} to find.
     * @param start the start index.
     * @return the index where the search sequence was found.
     */
    static int lastIndexOf(final CharSequence cs, final CharSequence searchChar, int start) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the index within {@code cs} of the last occurrence of the specified character, searching backward starting at the specified index. For values of
     * {@code searchChar} in the range from 0 to 0xFFFF (inclusive), the index returned is the largest value <em>k</em> such that:
     *
     * <pre>
     * (this.charAt(<em>k</em>) == searchChar) &amp;&amp; (<em>k</em> &lt;= start)
     * </pre>
     * <p>
     * is true. For other values of {@code searchChar}, it is the largest value <em>k</em> such that:
     * <p>
     *
     * <pre>
     * (this.codePointAt(<em>k</em>) == searchChar) &amp;&amp; (<em>k</em> &lt;= start)
     * </pre>
     * <p>
     * is true. In either case, if no such character occurs in {@code cs} at or before position {@code start}, then {@code -1} is returned.
     * </p>
     * <p>
     * All indices are specified in {@code char} values (Unicode code units).
     * </p>
     *
     * @param cs         the {@link CharSequence} to be processed.
     * @param searchChar the char to be searched for.
     * @param start      the start index, negative returns -1, beyond length starts at end.
     * @return the index where the search char was found, -1 if not found.
     * @since 3.6 updated to behave more like {@link String}.
     */
    static int lastIndexOf(final CharSequence cs, final int searchChar, int start) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Tests if two string regions are equal.
     *
     * @param cs the {@link CharSequence} to be processed.
     * @param ignoreCase whether or not to be case-insensitive.
     * @param thisStart the index to start on the {@code cs} CharSequence.
     * @param substring the {@link CharSequence} to be looked for.
     * @param start the index to start on the {@code substring} CharSequence.
     * @param length character length of the region.
     * @return whether the region matched.
     * @see String#regionMatches(boolean, int, String, int, int)
     */
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns a new {@link CharSequence} that is a subsequence of this
     * sequence starting with the {@code char} value at the specified index.
     *
     * <p>This provides the {@link CharSequence} equivalent to {@link String#substring(int)}.
     * The length (in {@code char}) of the returned sequence is {@code length() - start},
     * so if {@code start == end} then an empty sequence is returned.</p>
     *
     * @param cs  the specified subsequence, null returns null.
     * @param start  the start index, inclusive, valid.
     * @return a new subsequence, may be null.
     * @throws IndexOutOfBoundsException if {@code start} is negative or if
     *  {@code start} is greater than {@code length()}.
     */
    public static CharSequence subSequence(final CharSequence cs, final int start) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts the given CharSequence to a char[].
     *
     * @param source the {@link CharSequence} to be processed.
     * @return the resulting char array, never null.
     * @since 3.11
     */
    public static char[] toCharArray(final CharSequence source) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * {@link CharSequenceUtils} instances should NOT be constructed in
     * standard programming.
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     *
     * @deprecated TODO Make private in 4.0.
     */
    @Deprecated
    public CharSequenceUtils() {
        // empty
    }
}
