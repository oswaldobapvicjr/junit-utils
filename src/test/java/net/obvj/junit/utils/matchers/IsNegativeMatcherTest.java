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
