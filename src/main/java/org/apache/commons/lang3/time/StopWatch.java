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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableRunnable;
import org.apache.commons.lang3.function.FailableSupplier;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * {@link StopWatch} provides a convenient API for timings.
 *
 * <p>
 * To start the watch, call {@link #start()} or {@link StopWatch#createStarted()}. At this point you can:
 * </p>
 * <ul>
 * <li>{@link #split()} the watch to get the time whilst the watch continues in the background. {@link #unsplit()} will remove the effect of the split. At this
 * point, these three options are available again.</li>
 * <li>{@link #suspend()} the watch to pause it. {@link #resume()} allows the watch to continue. Any time between the suspend and resume will not be counted in
 * the total. At this point, these three options are available again.</li>
 * <li>{@link #stop()} the watch to complete the timing session.</li>
 * </ul>
 *
 * <p>
 * It is intended that the output methods {@link #toString()} and {@link #getTime()} should only be called after stop, split or suspend, however a suitable
 * result will be returned at other points.
 * </p>
 *
 * <p>
 * NOTE: As from v2.1, the methods protect against inappropriate calls. Thus you cannot now call stop before start, resume before suspend or unsplit before
 * split.
 * </p>
 *
 * <ol>
 * <li>{@link #split()}, {@link #suspend()}, or {@link #stop()} cannot be invoked twice</li>
 * <li>{@link #unsplit()} may only be called if the watch has been {@link #split()}</li>
 * <li>{@link #resume()} may only be called if the watch has been {@link #suspend()}</li>
 * <li>{@link #start()} cannot be called twice without calling {@link #reset()}</li>
 * </ol>
 *
 * <p>
 * This class is not thread-safe.
 * </p>
 *
 * @see DurationUtils#of(FailableRunnable)
 * @see DurationUtils#of(FailableConsumer)
 * @since 2.0
 */
public class StopWatch {

    /**
     * Stores a split as a label and duration.
     *
     * @since 3.20.0
     */
    public static final class Split extends ImmutablePair<String, Duration> {

        /**
         * Constructs a Split object with label and duration.
         *
         * @param label Label for this split.
         * @param duration Duration for this split.
         */
        public Split(String label, Duration duration) {
            super(label, duration);
        }

        /**
         * Gets the duration of this split.
         *
         * @return The duration of this split.
         */
        public Duration getDuration() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * Gets the label of this split.
         *
         * @return The label of this split.
         */
        public String getLabel() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        /**
         * Converts this instance to a string.
         *
         * @return this instance to a string.
         */
        @Override
        public String toString() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    /**
     * Enumerates the split states of a StopWatch.
     */
    private enum SplitState {

        SPLIT, UNSPLIT
    }

    /**
     * Enumerates type status of a StopWatch.
     */
    private enum State {

        RUNNING {

            @Override
            boolean isStarted() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isStopped() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isSuspended() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        }
        , STOPPED {

            @Override
            boolean isStarted() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isStopped() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isSuspended() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        }
        , SUSPENDED {

            @Override
            boolean isStarted() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isStopped() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isSuspended() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        }
        , UNSTARTED {

            @Override
            boolean isStarted() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isStopped() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }

            @Override
            boolean isSuspended() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        }
        ;

        /**
         * Tests whether this StopWatch is started. A suspended StopWatch is also started.
         *
         * @return boolean If this StopWatch is started.
         */
        abstract boolean isStarted();

        /**
         * Tests whether this StopWatch is stopped. A StopWatch which is not yet started and explicitly stopped is considered stopped.
         *
         * @return boolean If this StopWatch is stopped.
         */
        abstract boolean isStopped();

        /**
         * Tests whether this StopWatch is suspended.
         *
         * @return boolean If this StopWatch is suspended.
         */
        abstract boolean isSuspended();
    }

    private static final long NANO_2_MILLIS = 1_000_000L;

    /**
     * Creates a StopWatch.
     *
     * @return StopWatch a StopWatch.
     * @since 3.10
     */
    public static StopWatch create() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Creates and starts a StopWatch.
     *
     * @return StopWatch a started StopWatch.
     * @since 3.5
     */
    public static StopWatch createStarted() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * A message for string presentation.
     *
     * @since 3.10
     */
    private final String message;

    /**
     * The current running state of this StopWatch.
     */
    private State runningState = State.UNSTARTED;

    /**
     * Whether this StopWatch has a split time recorded.
     */
    private SplitState splitState = SplitState.UNSPLIT;

    /**
     * The start time in nanoseconds.
     *
     * This field can be removed once we move off of Java 8.
     */
    private long startTimeNanos;

    /**
     * The start Instant.
     * <p>
     * nanoTime is only for elapsed time so we need to also store the currentTimeMillis to maintain the old getStartTime API.
     * </p>
     * <p>
     * On Java 8, Instant has millisecond precision, later versions use nanoseconds.
     * </p>
     */
    private Instant startInstant;

    /**
     * The end Instant.
     * <p>
     * nanoTime is only for elapsed time so we need to also store the currentTimeMillis to maintain the old getStartTime API.
     * </p>
     * <p>
     * On Java 8, Instant has millisecond precision, later versions use nanoseconds.
     * </p>
     */
    private Instant stopInstant;

    /**
     * The stop time in nanoseconds.
     *
     * This field can be removed once we move off of Java 8.
     */
    private long stopTimeNanos;

    /**
     * The split list.
     */
    private final List<Split> splits = new ArrayList<>();

    /**
     * Constructs a new instance.
     */
    public StopWatch() {
        this(null);
    }

    /**
     * Constructs a new instance.
     *
     * @param message A message for string presentation.
     * @since 3.10
     */
    public StopWatch(final String message) {
        this.message = message;
    }

    /**
     * Formats the split time with {@link DurationFormatUtils#formatDurationHMS}.
     *
     * @return the split time formatted by {@link DurationFormatUtils#formatDurationHMS}.
     * @since 3.10
     */
    public String formatSplitTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Formats the time formatted with {@link DurationFormatUtils#formatDurationHMS}.
     *
     * @return the time formatted by {@link DurationFormatUtils#formatDurationHMS}.
     * @since 3.10
     */
    public String formatTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Delegates to {@link Supplier#get()} while recording the duration of the call.
     *
     * @param <T>      the type of results supplied by this supplier.
     * @param supplier The supplier to {@link Supplier#get()}.
     * @return a result from the given Supplier.
     * @since 3.18.0
     */
    public <T> T get(final Supplier<T> supplier) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the Duration on this StopWatch.
     *
     * <p>
     * This is either the Duration between the start and the moment this method is called, or the Duration between start and stop.
     * </p>
     *
     * @return the Duration.
     * @since 3.16.0
     */
    public Duration getDuration() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the message for string presentation.
     *
     * @return the message for string presentation.
     * @since 3.10
     */
    public String getMessage() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the <em>elapsed</em> time in nanoseconds.
     *
     * <p>
     * This is either the time between the start and the moment this method is called, or the amount of time between start and stop.
     * </p>
     *
     * @return the <em>elapsed</em> time in nanoseconds.
     * @see System#nanoTime()
     * @since 3.0
     */
    public long getNanoTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the split Duration on this StopWatch.
     *
     * <p>
     * This is the Duration between start and latest split.
     * </p>
     *
     * @return the split Duration.
     * @throws IllegalStateException if this StopWatch has not yet been split.
     * @since 3.16.0
     */
    public Duration getSplitDuration() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the split time in nanoseconds.
     *
     * <p>
     * This is the time between start and latest split.
     * </p>
     *
     * @return the split time in nanoseconds.
     * @throws IllegalStateException if this StopWatch has not yet been split.
     * @since 3.0
     */
    public long getSplitNanoTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the split list.
     *
     * @return the list of splits.
     * @since 3.20.0
     */
    public List<Split> getSplits() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the split time on this StopWatch.
     *
     * <p>
     * This is the time between start and latest split.
     * </p>
     *
     * @return the split time in milliseconds.
     * @throws IllegalStateException if this StopWatch has not yet been split.
     * @since 2.1
     * @deprecated Use {@link #getSplitDuration()}.
     */
    @Deprecated
    public long getSplitTime() {
        return nanosToMillis(getSplitNanoTime());
    }

    /**
     * Gets the Instant this StopWatch was started, between the current time and midnight, January 1, 1970 UTC.
     *
     * @return the Instant this StopWatch was started, between the current time and midnight, January 1, 1970 UTC.
     * @throws IllegalStateException if this StopWatch has not been started.
     * @since 3.16.0
     */
    public Instant getStartInstant() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the time this StopWatch was started in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     *
     * @return the time this StopWatch was started in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     * @throws IllegalStateException if this StopWatch has not been started.
     * @since 2.4
     * @deprecated Use {@link #getStartInstant()}.
     */
    @Deprecated
    public long getStartTime() {
        return getStartInstant().toEpochMilli();
    }

    /**
     * Gets the Instant this StopWatch was stopped, between the current time and midnight, January 1, 1970 UTC.
     *
     * @return the Instant this StopWatch was stopped in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     * @throws IllegalStateException if this StopWatch has not been started.
     * @since 3.16.0
     */
    public Instant getStopInstant() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the time this StopWatch was stopped in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     *
     * @return the time this StopWatch was stopped in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     * @throws IllegalStateException if this StopWatch has not been started.
     * @since 3.12.0
     * @deprecated Use {@link #getStopInstant()}.
     */
    @Deprecated
    public long getStopTime() {
        // stopTimeNanos stores System.nanoTime() for elapsed time
        final Instant stop = getStopInstant();
        return stop != null ? stop.toEpochMilli() : 0;
    }

    /**
     * Delegates to {@link FailableSupplier#get()} while recording the duration of the call.
     *
     * @param <T>      the type of results supplied by this supplier.
     * @param <E>      The kind of thrown exception or error.
     * @param supplier The supplier to {@link Supplier#get()}.
     * @return a result from the given Supplier.
     * @throws Throwable if the supplier fails.
     * @since 3.18.0
     */
    public <T, E extends Throwable> T getT(final FailableSupplier<T, E> supplier) throws Throwable {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the time on this StopWatch.
     *
     * <p>
     * This is either the time between the start and the moment this method is called, or the amount of time between start and stop.
     * </p>
     *
     * @return the time in milliseconds.
     * @see #getDuration()
     */
    public long getTime() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the time in the specified TimeUnit.
     *
     * <p>
     * This is either the time between the start and the moment this method is called, or the amount of time between start and stop. The resulting time will be
     * expressed in the desired TimeUnit with any remainder rounded down. For example, if the specified unit is {@code TimeUnit.HOURS} and this StopWatch time
     * is 59 minutes, then the result returned will be {@code 0}.
     * </p>
     *
     * @param timeUnit the unit of time, not null.
     * @return the time in the specified TimeUnit, rounded down.
     * @since 3.5
     */
    public long getTime(final TimeUnit timeUnit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Tests whether this StopWatch is started. A suspended StopWatch is also started watch.
     *
     * @return boolean If this StopWatch is started.
     * @since 3.2
     */
    public boolean isStarted() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Tests whether StopWatch is stopped. this StopWatch which's not yet started and explicitly stopped StopWatch is considered as stopped.
     *
     * @return boolean If this StopWatch is stopped.
     * @since 3.2
     */
    public boolean isStopped() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Tests whether this StopWatch is suspended.
     *
     * @return boolean If this StopWatch is suspended.
     * @since 3.2
     */
    public boolean isSuspended() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Converts nanoseconds to milliseconds.
     *
     * @param nanos nanoseconds to convert.
     * @return milliseconds conversion result.
     */
    private long nanosToMillis(final long nanos) {
        return nanos / NANO_2_MILLIS;
    }

    /**
     * Resets this StopWatch. Stops it if need be.
     *
     * <p>
     * This method clears the internal values to allow the object to be reused.
     * </p>
     */
    public void reset() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Resumes this StopWatch after a suspend.
     *
     * <p>
     * This method resumes the watch after it was suspended. The watch will not include time between the suspend and resume calls in the total time.
     * </p>
     *
     * @throws IllegalStateException if this StopWatch has not been suspended.
     */
    public void resume() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Delegates to {@link Runnable#run()} while recording the duration of the call.
     *
     * @param runnable The runnable to {@link Runnable#run()}.
     * @since 3.18.0
     */
    public void run(final Runnable runnable) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Delegates to {@link FailableRunnable#run()} while recording the duration of the call.
     *
     * @param <E>      The kind of {@link Throwable}.
     * @param runnable The runnable to {@link FailableRunnable#run()}.
     * @throws Throwable Thrown by {@link FailableRunnable#run()}.
     * @since 3.18.0
     */
    public <E extends Throwable> void runT(final FailableRunnable<E> runnable) throws Throwable {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Splits the time.
     *
     * <p>
     * This method sets the stop time of the watch to allow a time to be extracted. The start time is unaffected, enabling {@link #unsplit()} to continue the
     * timing from the original start point.
     * </p>
     *
     * @throws IllegalStateException if this StopWatch is not running.
     */
    public void split() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Splits the time with a label.
     *
     * <p>
     * This method sets the stop time of the watch to allow a time to be extracted. The start time is unaffected, enabling {@link #unsplit()} to continue the
     * timing from the original start point.
     * </p>
     *
     * @param label A message for string presentation.
     * @throws IllegalStateException if the StopWatch is not running.
     * @since 3.20.0
     */
    public void split(final String label) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Starts this StopWatch.
     *
     * <p>
     * This method starts a new timing session, clearing any previous values.
     * </p>
     *
     * @throws IllegalStateException if this StopWatch is already running.
     */
    public void start() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Starts or resumes this StopWatch.
     */
    private void startResume() {
        if (isStopped()) {
            start();
        } else if (isSuspended()) {
            resume();
        }
    }

    /**
     * Stops this StopWatch.
     *
     * <p>
     * This method ends a new timing session, allowing the time to be retrieved.
     * </p>
     *
     * @throws IllegalStateException if this StopWatch is not running.
     */
    public void stop() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private void stopSet() {
        stopTimeNanos = System.nanoTime();
        stopInstant = Instant.now();
    }

    /**
     * Suspends this StopWatch for later resumption.
     *
     * <p>
     * This method suspends the watch until it is resumed. The watch will not include time between the suspend and resume calls in the total time.
     * </p>
     *
     * @throws IllegalStateException if this StopWatch is not currently running.
     */
    public void suspend() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a summary of the last split time that this StopWatch recorded as a string.
     *
     * <p>
     * The format used is ISO 8601-like, [<em>message</em> ]<em>hours</em>:<em>minutes</em>:<em>seconds</em>.<em>milliseconds</em>.
     * </p>
     *
     * @return the split time as a String.
     * @since 2.1
     * @since 3.10 Returns the prefix {@code "message "} if the message is set.
     */
    public String toSplitString() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets a summary of the time that this StopWatch recorded as a string.
     *
     * <p>
     * The format used is ISO 8601-like, [<em>message</em> ]<em>hours</em>:<em>minutes</em>:<em>seconds</em>.<em>milliseconds</em>.
     * </p>
     *
     * @return the time as a String.
     * @since 3.10 Returns the prefix {@code "message "} if the message is set.
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Removes the split.
     *
     * <p>
     * This method clears the stop time. The start time is unaffected, enabling timing from the original start point to continue.
     * </p>
     *
     * @throws IllegalStateException if this StopWatch has not been split.
     */
    public void unsplit() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
