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
package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * FastDatePrinter is a fast and thread-safe version of
 * {@link java.text.SimpleDateFormat}.
 *
 * <p>To obtain a FastDatePrinter, use {@link FastDateFormat#getInstance(String, TimeZone, Locale)}
 * or another variation of the factory methods of {@link FastDateFormat}.</p>
 *
 * <p>Since FastDatePrinter is thread safe, you can use a static member instance:</p>
 * {@code
 *     private static final DatePrinter DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd");
 * }
 *
 * <p>This class can be used as a direct replacement to
 * {@link SimpleDateFormat} in most formatting situations.
 * This class is especially useful in multi-threaded server environments.
 * {@link SimpleDateFormat} is not thread-safe in any JDK version,
 * nor will it be as Sun have closed the bug/RFE.
 * </p>
 *
 * <p>Only formatting is supported by this class, but all patterns are compatible with
 * SimpleDateFormat (except time zones and some year patterns - see below).</p>
 *
 * <p>Java 1.4 introduced a new pattern letter, {@code 'Z'}, to represent
 * time zones in RFC822 format (for example, {@code +0800} or {@code -1100}).
 * This pattern letter can be used here (on all JDK versions).</p>
 *
 * <p>In addition, the pattern {@code 'ZZ'} has been made to represent
 * ISO 8601 extended format time zones (for example, {@code +08:00} or {@code -11:00}).
 * This introduces a minor incompatibility with Java 1.4, but at a gain of
 * useful functionality.</p>
 *
 * <p>Starting with JDK7, ISO 8601 support was added using the pattern {@code 'X'}.
 * To maintain compatibility, {@code 'ZZ'} will continue to be supported, but using
 * one of the {@code 'X'} formats is recommended.
 *
 * <p>Javadoc cites for the year pattern: <i>For formatting, if the number of
 * pattern letters is 2, the year is truncated to 2 digits; otherwise it is
 * interpreted as a number.</i> Starting with Java 1.7 a pattern of 'Y' or
 * 'YYY' will be formatted as '2003', while it was '03' in former Java
 * versions. FastDatePrinter implements the behavior of Java 7.</p>
 *
 * @since 3.2
 * @see FastDateParser
 */
public class FastDatePrinter implements DatePrinter, Serializable {

    // A lot of the speed in this class comes from caching, but some comes
    // from the special int to StringBuffer conversion.
    //
    // The following produces a padded 2-digit number:
    //   buffer.append((char)(value / 10 + '0'));
    //   buffer.append((char)(value % 10 + '0'));
    //
    // Note that the fastest append to StringBuffer is a single char (used here).
    // Note that Integer.toString() is not called, the conversion is simply
    // taking the value and adding (mathematically) the ASCII value for '0'.
    // So, don't change this code! It works and is very fast.
    /**
     * Inner class to output a constant single character.
     */
    private static final class CharacterLiteral implements Rule {

        private final char value;

        /**
         * Constructs a new instance of {@link CharacterLiteral}
         * to hold the specified value.
         *
         * @param value the character literal
         */
        CharacterLiteral(final char value) {
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output the numeric day in week.
     */
    private static final class DayInWeekField implements NumberRule {

        private final NumberRule rule;

        DayInWeekField(final NumberRule rule) {
            this.rule = rule;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output a time zone as a number {@code +/-HHMM}
     * or {@code +/-HH:MM}.
     */
    private static final class Iso8601_Rule implements Rule {

        // Sign TwoDigitHours or Z
        static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);

        // Sign TwoDigitHours Minutes or Z
        static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);

        // Sign TwoDigitHours : Minutes or Z
        static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);

        /**
         * Factory method for Iso8601_Rules.
         *
         * @param tokenLen a token indicating the length of the TimeZone String to be formatted.
         * @return an Iso8601_Rule that can format TimeZone String of length {@code tokenLen}. If no such
         *          rule exists, an IllegalArgumentException will be thrown.
         */
        static Iso8601_Rule getRule(final int tokenLen) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        private final int length;

        /**
         * Constructs an instance of {@code Iso8601_Rule} with the specified properties.
         *
         * @param length The number of characters in output (unless Z is output).
         */
        Iso8601_Rule(final int length) {
            this.length = length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class defining a numeric rule.
     */
    private interface NumberRule extends Rule {

        /**
         * Appends the specified value to the output buffer based on the rule implementation.
         *
         * @param buffer the output buffer.
         * @param value the value to be appended.
         * @throws IOException if an I/O error occurs.
         */
        void appendTo(Appendable buffer, int value) throws IOException;
    }

    /**
     * Inner class to output a padded number.
     */
    private static final class PaddedNumberField implements NumberRule {

        // Note: This is final to avoid Spotbugs CT_CONSTRUCTOR_THROW
        private final int field;

        private final int size;

        /**
         * Constructs an instance of {@link PaddedNumberField}.
         *
         * @param field the field.
         * @param size size of the output field.
         */
        PaddedNumberField(final int field, final int size) {
            if (size < 3) {
                // Should use UnpaddedNumberField or TwoDigitNumberField.
                throw new IllegalArgumentException();
            }
            this.field = field;
            this.size = size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public /* final */
        void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class defining a rule.
     */
    private interface Rule {

        /**
         * Appends the value of the specified calendar to the output buffer based on the rule implementation.
         *
         * @param buf the output buffer.
         * @param calendar calendar to be appended.
         * @throws IOException if an I/O error occurs.
         */
        void appendTo(Appendable buf, Calendar calendar) throws IOException;

        /**
         * Returns the estimated length of the result.
         *
         * @return the estimated length of the result.
         */
        int estimateLength();
    }

    /**
     * Inner class to output a constant string.
     */
    private static final class StringLiteral implements Rule {

        private final String value;

        /**
         * Constructs a new instance of {@link StringLiteral}
         * to hold the specified value.
         *
         * @param value the string literal.
         */
        StringLiteral(final String value) {
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output one of a set of values.
     */
    private static final class TextField implements Rule {

        private final int field;

        private final String[] values;

        /**
         * Constructs an instance of {@link TextField}
         * with the specified field and values.
         *
         * @param field the field.
         * @param values the field values.
         */
        TextField(final int field, final String[] values) {
            this.field = field;
            this.values = values;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class that acts as a compound key for time zone names.
     */
    private static final class TimeZoneDisplayKey {

        private final TimeZone timeZone;

        private final int style;

        private final Locale locale;

        /**
         * Constructs an instance of {@link TimeZoneDisplayKey} with the specified properties.
         *
         * @param timeZone the time zone.
         * @param daylight adjust the style for daylight saving time if {@code true}.
         * @param style the time zone style.
         * @param locale the time zone locale.
         */
        TimeZoneDisplayKey(final TimeZone timeZone, final boolean daylight, final int style, final Locale locale) {
            this.timeZone = timeZone;
            if (daylight) {
                this.style = style | 0x80000000;
            } else {
                this.style = style;
            }
            this.locale = LocaleUtils.toLocale(locale);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output a time zone name.
     */
    private static final class TimeZoneNameRule implements Rule {

        private final Locale locale;

        private final int style;

        private final String standard;

        private final String daylight;

        /**
         * Constructs an instance of {@link TimeZoneNameRule} with the specified properties.
         *
         * @param timeZone the time zone.
         * @param locale the locale.
         * @param style the style.
         */
        TimeZoneNameRule(final TimeZone timeZone, final Locale locale, final int style) {
            this.locale = LocaleUtils.toLocale(locale);
            this.style = style;
            this.standard = getTimeZoneDisplay(timeZone, false, style, locale);
            this.daylight = getTimeZoneDisplay(timeZone, true, style, locale);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output a time zone as a number {@code +/-HHMM}
     * or {@code +/-HH:MM}.
     */
    private static final class TimeZoneNumberRule implements Rule {

        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);

        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);

        private final boolean colon;

        /**
         * Constructs an instance of {@link TimeZoneNumberRule} with the specified properties.
         *
         * @param colon add colon between HH and MM in the output if {@code true}.
         */
        TimeZoneNumberRule(final boolean colon) {
            this.colon = colon;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output the twelve hour field.
     */
    private static final class TwelveHourField implements NumberRule {

        private final NumberRule rule;

        /**
         * Constructs an instance of {@link TwelveHourField} with the specified
         * {@link NumberRule}.
         *
         * @param rule the rule.
         */
        TwelveHourField(final NumberRule rule) {
            this.rule = rule;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output the twenty four hour field.
     */
    private static final class TwentyFourHourField implements NumberRule {

        private final NumberRule rule;

        /**
         * Constructs an instance of {@link TwentyFourHourField} with the specified
         * {@link NumberRule}.
         *
         * @param rule the rule.
         */
        TwentyFourHourField(final NumberRule rule) {
            this.rule = rule;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output a two digit month.
     */
    private static final class TwoDigitMonthField implements NumberRule {

        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        /**
         * Constructs an instance of {@link TwoDigitMonthField}.
         */
        TwoDigitMonthField() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output a two digit number.
     */
    private static final class TwoDigitNumberField implements NumberRule {

        private final int field;

        /**
         * Constructs an instance of {@link TwoDigitNumberField} with the specified field.
         *
         * @param field the field
         */
        TwoDigitNumberField(final int field) {
            this.field = field;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output a two digit year.
     */
    private static final class TwoDigitYearField implements NumberRule {

        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        /**
         * Constructs an instance of {@link TwoDigitYearField}.
         */
        TwoDigitYearField() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output an unpadded month.
     */
    private static final class UnpaddedMonthField implements NumberRule {

        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        /**
         * Constructs an instance of {@link UnpaddedMonthField}.
         */
        UnpaddedMonthField() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output an unpadded number.
     */
    private static final class UnpaddedNumberField implements NumberRule {

        private final int field;

        /**
         * Constructs an instance of {@link UnpaddedNumberField} with the specified field.
         *
         * @param field the field.
         */
        UnpaddedNumberField(final int field) {
            this.field = field;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Inner class to output the numeric day in week.
     */
    private static final class WeekYear implements NumberRule {

        private final NumberRule rule;

        WeekYear(final NumberRule rule) {
            this.rule = rule;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public int estimateLength() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Empty array.
     */
    private static final Rule[] EMPTY_RULE_ARRAY = {};

    /**
     * Required for serialization support.
     *
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * FULL locale dependent date or time style.
     */
    public static final int FULL = DateFormat.FULL;

    /**
     * LONG locale dependent date or time style.
     */
    public static final int LONG = DateFormat.LONG;

    /**
     * MEDIUM locale dependent date or time style.
     */
    public static final int MEDIUM = DateFormat.MEDIUM;

    /**
     * SHORT locale dependent date or time style.
     */
    public static final int SHORT = DateFormat.SHORT;

    // log10(Integer.MAX_VALUE) ~= 9.3
    private static final int MAX_DIGITS = 10;

    private static final ConcurrentMap<TimeZoneDisplayKey, String> timeZoneDisplayCache = new ConcurrentHashMap<>(7);

    /**
     * Appends two digits to the given buffer.
     *
     * @param buffer the buffer to append to.
     * @param value the value to append digits from.
     * @throws IOException If an I/O error occurs.
     */
    private static void appendDigits(final Appendable buffer, final int value) throws IOException {
        buffer.append((char) (value / 10 + '0'));
        buffer.append((char) (value % 10 + '0'));
    }

    /**
     * Appends all digits to the given buffer.
     *
     * @param buffer the buffer to append to.
     * @param value the value to append digits from.
     * @param minFieldWidth Minimum field width.
     * @throws IOException If an I/O error occurs.
     */
    private static void appendFullDigits(final Appendable buffer, int value, int minFieldWidth) throws IOException {
        // specialized paths for 1 to 4 digits -> avoid the memory allocation from the temporary work array
        // see LANG-1248
        if (value < 10000) {
            // less memory allocation path works for four digits or less
            int nDigits = 4;
            if (value < 1000) {
                --nDigits;
                if (value < 100) {
                    --nDigits;
                    if (value < 10) {
                        --nDigits;
                    }
                }
            }
            // left zero pad
            for (int i = minFieldWidth - nDigits; i > 0; --i) {
                buffer.append('0');
            }
            switch(nDigits) {
                case 4:
                    buffer.append((char) (value / 1000 + '0'));
                    value %= 1000;
                // falls-through
                case 3:
                    if (value >= 100) {
                        buffer.append((char) (value / 100 + '0'));
                        value %= 100;
                    } else {
                        buffer.append('0');
                    }
                // falls-through
                case 2:
                    if (value >= 10) {
                        buffer.append((char) (value / 10 + '0'));
                        value %= 10;
                    } else {
                        buffer.append('0');
                    }
                // falls-through
                case 1:
                    buffer.append((char) (value + '0'));
            }
        } else {
            // more memory allocation path works for any digits
            // build up decimal representation in reverse
            final char[] work = new char[MAX_DIGITS];
            int digit = 0;
            while (value != 0) {
                work[digit++] = (char) (value % 10 + '0');
                value /= 10;
            }
            // pad with zeros
            while (digit < minFieldWidth) {
                buffer.append('0');
                --minFieldWidth;
            }
            // reverse
            while (--digit >= 0) {
                buffer.append(work[digit]);
            }
        }
    }

    static void clear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the time zone display name, using a cache for performance.
     *
     * @param tz  the zone to query.
     * @param daylight  true if daylight savings.
     * @param style  the style to use {@link TimeZone#LONG} or {@link TimeZone#SHORT}.
     * @param locale  the locale to use.
     * @return the textual name of the time zone.
     */
    static String getTimeZoneDisplay(final TimeZone tz, final boolean daylight, final int style, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * The pattern.
     */
    private final String pattern;

    /**
     * The time zone.
     */
    private final TimeZone timeZone;

    /**
     * The locale.
     */
    private final Locale locale;

    /**
     * The parsed rules.
     */
    private transient Rule[] rules;

    /**
     * The estimated maximum length.
     */
    private transient int maxLengthEstimate;

    /**
     * Constructs a new FastDatePrinter.
     * Use {@link FastDateFormat#getInstance(String, TimeZone, Locale)}  or another variation of the
     * factory methods of {@link FastDateFormat} to get a cached FastDatePrinter instance.
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern.
     * @param timeZone  non-null time zone to use.
     * @param locale  non-null locale to use.
     * @throws NullPointerException if pattern, timeZone, or locale is null.
     */
    protected FastDatePrinter(final String pattern, final TimeZone timeZone, final Locale locale) {
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = LocaleUtils.toLocale(locale);
        init();
    }

    /**
     * Performs the formatting by applying the rules to the
     * specified calendar.
     *
     * @param calendar  the calendar to format.
     * @param buf  the buffer to format into.
     * @param <B> the Appendable class type, usually StringBuilder or StringBuffer.
     * @return the specified string buffer.
     */
    private <B extends Appendable> B applyRules(final Calendar calendar, final B buf) {
        try {
            for (final Rule rule : rules) {
                rule.appendTo(buf, calendar);
            }
        } catch (final IOException ioe) {
            ExceptionUtils.asRuntimeException(ioe);
        }
        return buf;
    }

    /**
     * Performs the formatting by applying the rules to the
     * specified calendar.
     *
     * @param calendar the calendar to format.
     * @param buf the buffer to format into.
     * @return the specified string buffer.
     * @deprecated Use {@link #format(Calendar)} or {@link #format(Calendar, Appendable)}
     */
    @Deprecated
    protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
        return (StringBuffer) applyRules(calendar, (Appendable) buf);
    }

    /**
     * Creates a String representation of the given Calendar by applying the rules of this printer to it.
     *
     * @param c the Calendar to apply the rules to.
     * @return a String representation of the given Calendar.
     */
    private String applyRulesToString(final Calendar c) {
        return applyRules(c, new StringBuilder(maxLengthEstimate)).toString();
    }

    /**
     * Compares two objects for equality.
     *
     * @param obj  the object to compare to.
     * @return {@code true} if equal.
     */
    @Override
    public boolean equals(final Object obj) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar)
     */
    @Override
    public String format(final Calendar calendar) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar, Appendable)
     */
    @Override
    public <B extends Appendable> B format(final Calendar calendar, final B buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar, StringBuffer)
     */
    @Override
    public StringBuffer format(final Calendar calendar, final StringBuffer buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date)
     */
    @Override
    public String format(final Date date) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date, Appendable)
     */
    @Override
    public <B extends Appendable> B format(final Date date, final B buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date, StringBuffer)
     */
    @Override
    public StringBuffer format(final Date date, final StringBuffer buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(long)
     */
    @Override
    public String format(final long millis) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(long, Appendable)
     */
    @Override
    public <B extends Appendable> B format(final long millis, final B buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(long, StringBuffer)
     */
    @Override
    public StringBuffer format(final long millis, final StringBuffer buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a {@link Date}, {@link Calendar} or
     * {@link Long} (milliseconds) object.
     *
     * @param obj  the object to format.
     * @return The formatted value.
     * @since 3.5
     */
    String format(final Object obj) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a {@link Date}, {@link Calendar} or
     * {@link Long} (milliseconds) object.
     *
     * @param obj  the object to format.
     * @param toAppendTo  the buffer to append to.
     * @param pos  the position; ignored.
     * @return the buffer passed in.
     * @deprecated Use {{@link #format(Date)}, {{@link #format(Calendar)}, {{@link #format(long)}.
     */
    @Deprecated
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        if (obj instanceof Date) {
            return format((Date) obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, toAppendTo);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), toAppendTo);
        }
        throw new IllegalArgumentException("Unknown class: " + ClassUtils.getName(obj, "<null>"));
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#getLocale()
     */
    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets an estimate for the maximum string length that the
     * formatter will produce.
     *
     * <p>The actual formatted length will almost always be less than or
     * equal to this amount.</p>
     *
     * @return the maximum formatted length.
     */
    public int getMaxLengthEstimate() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#getPattern()
     */
    @Override
    public String getPattern() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#getTimeZone()
     */
    @Override
    public TimeZone getTimeZone() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns a hash code compatible with equals.
     *
     * @return a hash code compatible with equals.
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Initializes the instance for first use.
     */
    private void init() {
        final List<Rule> rulesList = parsePattern();
        rules = rulesList.toArray(EMPTY_RULE_ARRAY);
        int len = 0;
        for (int i = rules.length; --i >= 0; ) {
            len += rules[i].estimateLength();
        }
        maxLengthEstimate = len;
    }

    /**
     * Creates a new Calendar instance.
     *
     * @return a new Calendar instance.
     */
    private Calendar newCalendar() {
        return Calendar.getInstance(timeZone, locale);
    }

    /**
     * Returns a list of Rules given a pattern.
     *
     * @return a {@link List} of Rule objects.
     * @throws IllegalArgumentException if pattern is invalid.
     */
    protected List<Rule> parsePattern() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Performs the parsing of tokens.
     *
     * @param pattern  the pattern.
     * @param indexRef  index references.
     * @return parsed token.
     */
    protected String parseToken(final String pattern, final int[] indexRef) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Create the object after serialization. This implementation reinitializes the
     * transient properties.
     *
     * @param in ObjectInputStream from which the object is being deserialized.
     * @throws IOException if there is an IO issue.
     * @throws ClassNotFoundException if a class cannot be found.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    /**
     * Gets an appropriate rule for the padding required.
     *
     * @param field  the field to get a rule for.
     * @param padding  the padding required.
     * @return a new rule with the correct padding.
     */
    protected NumberRule selectNumberRule(final int field, final int padding) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a debugging string version of this formatter.
     *
     * @return a debugging string.
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
