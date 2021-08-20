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

import static net.obvj.junit.utils.matchers.IsNegativeMatcher.isNegative;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Unit tests for the {@link IsNegativeMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.3.0
 */
public class IsNegativeMatcherTest
{
    @Test(expected = AssertionError.class)
    public void isNegative_null_assertionError()
    {
        assertThat(null, isNegative());
    }

    @Test
    public void isNegative_negativeInt_success()
    {
        assertThat(-5, isNegative());
    }

    @Test(expected = AssertionError.class)
    public void isNegative_positiveInt_assertionError()
    {
        assertThat(5, isNegative());
    }

    @Test
    public void isNegative_negativeDouble_success()
    {
        assertThat(-9999d, isNegative());
    }

    @Test(expected = AssertionError.class)
    public void isNegative_positiveDouble_assertionError()
    {
        assertThat(45.9, isNegative());
    }

    @Test
    public void isNegative_negativeBigDecimal_success()
    {
        assertThat(BigDecimal.valueOf(-1), isNegative());
    }

    @Test(expected = AssertionError.class)
    public void isNegative_positiveBigDecimal_assertionError()
    {
        assertThat(BigDecimal.valueOf(Double.MAX_VALUE), isNegative());
    }

}
