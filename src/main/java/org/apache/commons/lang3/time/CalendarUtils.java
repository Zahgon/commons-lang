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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;
import java.util.Objects;

/**
 * Helps use {@link Calendar}s.
 *
 * @since 3.10
 */
public class CalendarUtils {

    /**
     * The singleton instance for {@link Calendar#getInstance()}. The instance is created when the class is initialized and is based on the current time in the
     * default time zone with the default {@link Category#FORMAT} locale.
     *
     * @see CalendarUtils#getInstance()
     */
    public static final CalendarUtils INSTANCE = getInstance();

    /**
     * Creates a new instance based on the current time in the default time zone with the default {@link Category#FORMAT} locale.
     *
     * @return a new instance.
     * @since 3.14.0
     */
    public static CalendarUtils getInstance() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a CalendarUtils using the default time zone and specified locale. The {@code CalendarUtils} returned is based on the current time in the
     * default time zone with the given locale.
     *
     * @param locale the locale for the week data
     * @return a Calendar.
     */
    static CalendarUtils getInstance(final Locale locale) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts a Calendar to a LocalDateTime.
     *
     * @param calendar the Calendar to convert.
     * @return a LocalDateTime.
     * @since 3.17.0
     */
    public static LocalDateTime toLocalDateTime(final Calendar calendar) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts a Calendar to a OffsetDateTime.
     *
     * @param calendar the Calendar to convert.
     * @return a OffsetDateTime.
     * @since 3.17.0
     */
    public static OffsetDateTime toOffsetDateTime(final Calendar calendar) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts a Calendar to a ZonedDateTime.
     *
     * @param calendar the Calendar to convert.
     * @return a ZonedDateTime.
     * @since 3.17.0
     */
    public static ZonedDateTime toZonedDateTime(final Calendar calendar) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static ZoneId toZoneId(final Calendar calendar) {
        return calendar.getTimeZone().toZoneId();
    }

    private final Calendar calendar;

    private final Locale locale;

    /**
     * Creates an instance for the given Calendar.
     *
     * @param calendar A Calendar.
     */
    public CalendarUtils(final Calendar calendar) {
        this(calendar, Locale.getDefault());
    }

    /**
     * Creates an instance for the given Calendar.
     *
     * @param calendar A Calendar.
     * @param locale A Locale.
     */
    CalendarUtils(final Calendar calendar, final Locale locale) {
        this.calendar = Objects.requireNonNull(calendar, "calendar");
        this.locale = Objects.requireNonNull(locale, "locale");
    }

    /**
     * Gets the current day of month.
     *
     * @return the current day of month.
     */
    public int getDayOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the current day of year.
     *
     * @return the current day of year.
     * @since 3.13.0
     */
    public int getDayOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the current month.
     *
     * @return the current month.
     */
    public int getMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets month names in the requested style.
     *
     * @param style Must be a valid {@link Calendar#getDisplayNames(int, int, Locale)} month style.
     * @return Styled names of months
     */
    String[] getMonthDisplayNames(final int style) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets full standalone month names as used in "LLLL" date formatting.
     *
     * @return Long names of months
     */
    String[] getStandaloneLongMonthNames() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets short standalone month names as used in "LLLL" date formatting.
     *
     * @return Short names of months
     */
    String[] getStandaloneShortMonthNames() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the current year.
     *
     * @return the current year.
     */
    public int getYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts this instance to a {@link LocalDate}.
     *
     * @return a LocalDate.
     * @since 3.18.0
     */
    public LocalDate toLocalDate() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts this instance to a {@link LocalDateTime}.
     *
     * @return a LocalDateTime.
     * @since 3.17.0
     */
    public LocalDateTime toLocalDateTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts this instance to a {@link OffsetDateTime}.
     *
     * @return a OffsetDateTime.
     * @since 3.17.0
     */
    public OffsetDateTime toOffsetDateTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts this instance to a {@link ZonedDateTime}.
     *
     * @return a ZonedDateTime.
     * @since 3.17.0
     */
    public ZonedDateTime toZonedDateTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
