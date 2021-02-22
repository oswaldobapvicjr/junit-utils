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
