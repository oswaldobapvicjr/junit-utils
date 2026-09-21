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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AdvancedMatchers} numeric matchers.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.1
 */
class AdvancedMatchersTest
{
    private static final String STRING1 = "string1";
    private static final String STRING2 = "string2";

    @Test
    void isPositive_validNumbers_validatesAccordingly()
    {
        assertThat(Integer.MAX_VALUE, isPositive());
        assertThat(Integer.MIN_VALUE, not(isPositive()));
    }

    @Test
    void isNegative_validNumbers_validatesAccordingly()
    {
        assertThat(Integer.MIN_VALUE, isNegative());
        assertThat(Integer.MAX_VALUE, not(isNegative()));
    }

    @Test
    void isZero_validNumbers_validatesAccordingly()
    {
        assertThat(0, isZero());
        assertThat(1, not(isZero()));
        assertThat(-0.0d, isZero());
    }

    @Test
    void isNonZero_validNumbers_validatesAccordingly()
    {
        assertThat(1, isNonZero());
        assertThat(-1, isNonZero());
        assertThat(0, not(isNonZero()));
    }
}
