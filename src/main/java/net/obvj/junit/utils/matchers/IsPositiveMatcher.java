/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.junit.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

/**
 * A matcher that matches if a {@link Number} is positive.
 * <p>
 * This is particularly useful in situations where the exact value of an operation is
 * unpredictable.
 * <p>
 * For example:
 *
 * <pre>
 * assertThat(stopwatch.elapsedTime(TimeUnit.MILLISECONDS), isPositive());
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.3.0
 */
public class IsPositiveMatcher extends TypeSafeMatcher<Number>
{
    private static final String EXPECTED_SCENARIO = "a positive number";

    /**
     * Creates a matcher that matches if the examined object is a positive number.
     *
     * @return the matcher
     */
    @Factory
    public static IsPositiveMatcher isPositive()
    {
        return new IsPositiveMatcher();
    }

    /**
     * Execute the matcher business logic for the specified number.
     *
     * @param number the number to be checked
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(Number number)
    {
        return number.doubleValue() > 0;
    }

    /**
     * Describes the "expected" pat of the test description.
     *
     * @param description the {@link Description} to be appended to
     */
    @Override
    public void describeTo(Description description)
    {
        description.appendText(EXPECTED_SCENARIO);
    }

}
