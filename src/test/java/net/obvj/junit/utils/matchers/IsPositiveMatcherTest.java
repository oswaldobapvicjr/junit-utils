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

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Unit tests for the {@link IsPositiveMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.3.0
 */
public class IsPositiveMatcherTest
{
    @Test(expected = AssertionError.class)
    public void isPositive_null_assertionError()
    {
        assertThat(null, isPositive());
    }

    @Test
    public void isPositive_positiveInt_success()
    {
        assertThat(5, isPositive());
    }

    @Test(expected = AssertionError.class)
    public void isPositive_negativeInt_assertionError()
    {
        assertThat(-5, isPositive());
    }

    @Test
    public void isPositive_positiveDouble_success()
    {
        assertThat(5d, isPositive());
    }

    @Test(expected = AssertionError.class)
    public void isPositive_negativeDouble_assertionError()
    {
        assertThat(-45.9, isPositive());
    }

    @Test
    public void isPositive_positiveBigDecimal_success()
    {
        assertThat(BigDecimal.ONE, isPositive());
    }

    @Test(expected = AssertionError.class)
    public void isPositive_negativeBigDecimal_assertionError()
    {
        assertThat(BigDecimal.valueOf(-5), isPositive());
    }

}
