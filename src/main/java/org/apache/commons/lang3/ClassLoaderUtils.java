/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;

/**
 * Helps work with {@link ClassLoader}.
 *
 * @since 3.10
 */
public class ClassLoaderUtils {

    private static final URL[] EMPTY_URL_ARRAY = {};

    /**
     * Gets the system class loader's URLs, if any.
     *
     * @return the system class loader's URLs, if any.
     * @since 3.13.0
     */
    public static URL[] getSystemURLs() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the current thread's context class loader's URLs, if any.
     *
     * @return the current thread's context class loader's URLs, if any.
     * @since 3.13.0
     */
    public static URL[] getThreadURLs() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static URL[] getURLs(final ClassLoader cl) {
        return cl instanceof URLClassLoader ? ((URLClassLoader) cl).getURLs() : EMPTY_URL_ARRAY;
    }

    /**
     * Converts the given class loader to a String calling {@link #toString(URLClassLoader)}.
     *
     * @param classLoader to URLClassLoader to convert.
     * @return the formatted string.
     */
    public static String toString(final ClassLoader classLoader) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts the given URLClassLoader to a String in the format {@code "URLClassLoader.toString() + [URL1, URL2, ...]"}.
     *
     * @param classLoader to URLClassLoader to convert.
     * @return the formatted string.
     */
    public static String toString(final URLClassLoader classLoader) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Make private in 4.0.
     *
     * @deprecated TODO Make private in 4.0.
     */
    @Deprecated
    public ClassLoaderUtils() {
        // empty
    }
}
