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

import static net.obvj.junit.utils.matchers.IsPositiveMatcher.isPositive;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link IsPositiveMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.3.0
 */
class IsPositiveMatcherTest
{
    @Test
    void isPositive_null_assertionError()
    {
        assertThrows(AssertionError.class, () -> assertThat(null, isPositive()));
    }

    @Test
    void isPositive_positiveInt_success()
    {
        assertThat(5, isPositive());
    }

    @Test
    void isPositive_negativeInt_assertionError()
    {
        assertThrows(AssertionError.class, () -> assertThat(-5, isPositive()));
    }

    @Test
    void isPositive_positiveDouble_success()
    {
        assertThat(5d, isPositive());
    }

    @Test
    void isPositive_negativeDouble_assertionError()
    {
        assertThrows(AssertionError.class, () -> assertThat(-45.9, isPositive()));
    }

    @Test
    void isPositive_positiveBigDecimal_success()
    {
        assertThat(BigDecimal.ONE, isPositive());
    }

    @Test
    void isPositive_negativeBigDecimal_assertionError()
    {
        assertThrows(AssertionError.class, () -> assertThat(BigDecimal.valueOf(-5), isPositive()));
    }

}
