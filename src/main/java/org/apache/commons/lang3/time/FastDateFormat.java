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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * FastDateFormat is a fast and thread-safe version of {@link java.text.SimpleDateFormat}.
 *
 * <p>
 * To obtain an instance of FastDateFormat, use one of the static factory methods: {@link #getInstance(String, TimeZone, Locale)},
 * {@link #getDateInstance(int, TimeZone, Locale)}, {@link #getTimeInstance(int, TimeZone, Locale)}, or {@link #getDateTimeInstance(int, int, TimeZone, Locale)}
 * </p>
 *
 * <p>
 * Since FastDateFormat is thread safe, you can use a static member instance:
 * </p>
 * {@code
 *   private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.SHORT);
 * }
 *
 * <p>
 * This class can be used as a direct replacement to {@link SimpleDateFormat} in most formatting and parsing situations. This class is especially useful in
 * multi-threaded server environments. {@link SimpleDateFormat} is not thread-safe in any JDK version, nor will it be as Sun have closed the bug/RFE.
 * </p>
 *
 * <p>
 * All patterns are compatible with SimpleDateFormat (except time zones and some year patterns - see below).
 * </p>
 *
 * <p>
 * Since 3.2, FastDateFormat supports parsing as well as printing.
 * </p>
 *
 * <p>
 * Java 1.4 introduced a new pattern letter, {@code 'Z'}, to represent time zones in RFC822 format (for example, {@code +0800} or {@code -1100}). This pattern letter can
 * be used here (on all JDK versions).
 * </p>
 *
 * <p>
 * In addition, the pattern {@code 'ZZ'} has been made to represent ISO 8601 extended format time zones (for example, {@code +08:00} or {@code -11:00}). This introduces
 * a minor incompatibility with Java 1.4, but at a gain of useful functionality.
 * </p>
 *
 * <p>
 * Javadoc cites for the year pattern: <i>For formatting, if the number of pattern letters is 2, the year is truncated to 2 digits; otherwise it is interpreted
 * as a number.</i> Starting with Java 1.7 a pattern of 'Y' or 'YYY' will be formatted as '2003', while it was '03' in former Java versions. FastDateFormat
 * implements the behavior of Java 7.
 * </p>
 *
 * @since 2.0
 */
public class FastDateFormat extends Format implements DateParser, DatePrinter {

    /**
     * Required for serialization support.
     *
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 2L;

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

    private static final AbstractFormatCache<FastDateFormat> CACHE = new AbstractFormatCache<FastDateFormat>() {

        @Override
        protected FastDateFormat createInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    };

    /**
     * Clears the cache.
     */
    static void clear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date formatter instance using the specified style in the default time zone and locale.
     *
     * @param style date style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @return a localized standard date formatter.
     * @throws IllegalArgumentException if the Locale has no date pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getDateInstance(final int style) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date formatter instance using the specified style and locale in the default time zone.
     *
     * @param style  date style: {@link #FULL}, LO{@link #FULL},{@link #MEDIUM}, or {@link #SHORT}.
     * @param locale optional locale, overrides system locale.
     * @return a localized standard date formatter.
     * @throws IllegalArgumentException if the Locale has no date pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getDateInstance(final int style, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date formatter instance using the specified style and time zone in the default locale.
     *
     * @param style    date style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeZone optional time zone, overrides time zone of formatted date.
     * @return a localized standard date formatter.
     * @throws IllegalArgumentException if the Locale has no date pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date formatter instance using the specified style, time zone and locale.
     *
     * @param style    date style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeZone optional time zone, overrides time zone of formatted date.
     * @param locale   optional locale, overrides system locale.
     * @return a localized standard date formatter.
     * @throws IllegalArgumentException if the Locale has no date pattern defined.
     */
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date/time formatter instance using the specified style in the default time zone and locale.
     *
     * @param dateStyle date style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeStyle time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @return a localized standard date/time formatter.
     * @throws IllegalArgumentException if the Locale has no date/time pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date/time formatter instance using the specified style and locale in the default time zone.
     *
     * @param dateStyle date style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeStyle time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param locale    optional locale, overrides system locale.
     * @return a localized standard date/time formatter.
     * @throws IllegalArgumentException if the Locale has no date/time pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date/time formatter instance using the specified style and time zone in the default locale.
     *
     * @param dateStyle date style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeStyle time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeZone  optional time zone, overrides time zone of formatted date.
     * @return a localized standard date/time formatter.
     * @throws IllegalArgumentException if the Locale has no date/time pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a date/time formatter instance using the specified style, time zone and locale.
     *
     * @param dateStyle date style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeStyle time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeZone  optional time zone, overrides time zone of formatted date.
     * @param locale    optional locale, overrides system locale.
     * @return a localized standard date/time formatter.
     * @throws IllegalArgumentException if the Locale has no date/time pattern defined.
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a formatter instance using the default pattern in the default locale.
     *
     * @return a date/time formatter.
     */
    public static FastDateFormat getInstance() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a formatter instance using the specified pattern in the default locale and time zone.
     *
     * @param pattern {@link java.text.SimpleDateFormat} compatible pattern.
     * @return a pattern based date/time formatter.
     * @throws IllegalArgumentException if pattern is invalid.
     */
    public static FastDateFormat getInstance(final String pattern) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a formatter instance using the specified pattern and locale using the default time zone.
     *
     * @param pattern {@link java.text.SimpleDateFormat} compatible pattern.
     * @param locale  optional locale, overrides system locale.
     * @return a pattern based date/time formatter.
     * @throws IllegalArgumentException if pattern is invalid.
     */
    public static FastDateFormat getInstance(final String pattern, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a formatter instance using the specified pattern and time zone.
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern.
     * @param timeZone optional time zone, overrides time zone of formatted date.
     * @return a pattern based date/time formatter.
     * @throws IllegalArgumentException if pattern is invalid.
     */
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a formatter instance using the specified pattern, time zone and locale.
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern.
     * @param timeZone optional time zone, overrides time zone of formatted date.
     * @param locale   optional locale, overrides system locale.
     * @return a pattern based date/time formatter.
     * @throws IllegalArgumentException if pattern is invalid or {@code null}.
     */
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a time formatter instance using the specified style in the default time zone and locale.
     *
     * @param style time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @return a localized standard time formatter.
     * @throws IllegalArgumentException if the Locale has no time pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getTimeInstance(final int style) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a time formatter instance using the specified style and locale in the default time zone.
     *
     * @param style  time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param locale optional locale, overrides system locale.
     * @return a localized standard time formatter.
     * @throws IllegalArgumentException if the Locale has no time pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getTimeInstance(final int style, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a time formatter instance using the specified style and time zone in the default locale.
     *
     * @param style    time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeZone optional time zone, overrides time zone of formatted time.
     * @return a localized standard time formatter.
     * @throws IllegalArgumentException if the Locale has no time pattern defined.
     * @since 2.1
     */
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a time formatter instance using the specified style, time zone and locale.
     *
     * @param style    time style: {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, or {@link #SHORT}.
     * @param timeZone optional time zone, overrides time zone of formatted time.
     * @param locale   optional locale, overrides system locale.
     * @return a localized standard time formatter.
     * @throws IllegalArgumentException if the Locale has no time pattern defined.
     */
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone, final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Our fast printer.
     */
    private final FastDatePrinter printer;

    /**
     * Our fast parser.
     */
    private final FastDateParser parser;

    /**
     * Constructs a new FastDateFormat.
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern.
     * @param timeZone non-null time zone to use.
     * @param locale   non-null locale to use.
     * @throws NullPointerException if pattern, timeZone, or locale is null.
     */
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    /**
     * Constructs a new FastDateFormat.
     *
     * @param pattern      {@link java.text.SimpleDateFormat} compatible pattern.
     * @param timeZone     non-null time zone to use.
     * @param locale       non-null locale to use.
     * @param centuryStart The start of the 100-year period to use as the "default century" for 2 digit year parsing. If centuryStart is null, defaults to now -
     *                     80 years.
     * @throws NullPointerException if pattern, timeZone, or locale is null.
     */
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        printer = new FastDatePrinter(pattern, timeZone, locale);
        parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    /**
     * Performs the formatting by applying the rules to the specified calendar.
     *
     * @param calendar the calendar to format.
     * @param buf      the buffer to format into.
     * @return the specified string buffer.
     * @deprecated Use {@link #format(Calendar, Appendable)}
     */
    @Deprecated
    protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
        return printer.format(calendar, buf);
    }

    /**
     * Compares two objects for equality.
     *
     * @param obj the object to compare to.
     * @return {@code true} if equal.
     */
    @Override
    public boolean equals(final Object obj) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a {@link Calendar} object.
     *
     * @param calendar the calendar to format.
     * @return the formatted string.
     */
    @Override
    public String format(final Calendar calendar) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a {@link Calendar} object into the supplied {@link StringBuffer}.
     *
     * @param calendar the calendar to format.
     * @param buf      the buffer to format into.
     * @return the specified string buffer.
     * @since 3.5
     */
    @Override
    public <B extends Appendable> B format(final Calendar calendar, final B buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a {@link Calendar} object into the supplied {@link StringBuffer}.
     *
     * @param calendar the calendar to format.
     * @param buf      the buffer to format into.
     * @return the specified string buffer.
     * @deprecated Use {{@link #format(Calendar, Appendable)}.
     */
    @Deprecated
    @Override
    public StringBuffer format(final Calendar calendar, final StringBuffer buf) {
        return printer.format(calendar, buf);
    }

    /**
     * Formats a {@link Date} object using a {@link GregorianCalendar}.
     *
     * @param date the date to format.
     * @return the formatted string.
     */
    @Override
    public String format(final Date date) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a {@link Date} object into the supplied {@link StringBuffer} using a {@link GregorianCalendar}.
     *
     * @param date the date to format.
     * @param buf  the buffer to format into.
     * @return the specified string buffer.
     * @since 3.5
     */
    @Override
    public <B extends Appendable> B format(final Date date, final B buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a {@link Date} object into the supplied {@link StringBuffer} using a {@link GregorianCalendar}.
     *
     * @param date the date to format.
     * @param buf  the buffer to format into.
     * @return the specified string buffer.
     * @deprecated Use {{@link #format(Date, Appendable)}.
     */
    @Deprecated
    @Override
    public StringBuffer format(final Date date, final StringBuffer buf) {
        return printer.format(date, buf);
    }

    /**
     * Formats a millisecond {@code long} value.
     *
     * @param millis the millisecond value to format.
     * @return the formatted string.
     * @since 2.1
     */
    @Override
    public String format(final long millis) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a millisecond {@code long} value into the supplied {@link StringBuffer}.
     *
     * @param millis the millisecond value to format.
     * @param buf    the buffer to format into.
     * @return the specified string buffer.
     * @since 3.5
     */
    @Override
    public <B extends Appendable> B format(final long millis, final B buf) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats a millisecond {@code long} value into the supplied {@link StringBuffer}.
     *
     * @param millis the millisecond value to format.
     * @param buf    the buffer to format into.
     * @return the specified string buffer.
     * @since 2.1
     * @deprecated Use {{@link #format(long, Appendable)}.
     */
    @Deprecated
    @Override
    public StringBuffer format(final long millis, final StringBuffer buf) {
        return printer.format(millis, buf);
    }

    /**
     * Formats a {@link Date}, {@link Calendar} or {@link Long} (milliseconds) object. This method is an implementation of
     * {@link Format#format(Object, StringBuffer, FieldPosition)}
     *
     * @param obj        the object to format.
     * @param toAppendTo the buffer to append to.
     * @param pos        the position, ignored.
     * @return the given buffer.
     */
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the locale used by this formatter.
     *
     * @return the locale.
     */
    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets an estimate for the maximum string length that the formatter will produce.
     *
     * <p>
     * The actual formatted length will almost always be less than or equal to this amount.
     * </p>
     *
     * @return the maximum formatted length.
     */
    public int getMaxLengthEstimate() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the pattern used by this formatter.
     *
     * @return the pattern, {@link java.text.SimpleDateFormat} compatible.
     */
    @Override
    public String getPattern() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the time zone used by this formatter.
     *
     * <p>
     * This zone is always used for {@link Date} formatting.
     * </p>
     *
     * @return the time zone.
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

    /*
     * (non-Javadoc)
     *
     * @see DateParser#parse(String)
     */
    @Override
    public Date parse(final String source) throws ParseException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /*
     * (non-Javadoc)
     *
     * @see DateParser#parse(String, java.text.ParsePosition)
     */
    @Override
    public Date parse(final String source, final ParsePosition pos) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.lang3.time.DateParser#parse(String, java.text.ParsePosition, java.util.Calendar)
     */
    @Override
    public boolean parse(final String source, final ParsePosition pos, final Calendar calendar) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.text.Format#parseObject(String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a debugging string version of this formatter.
     *
     * @return a debug string.
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
