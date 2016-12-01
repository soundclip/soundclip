// Copyright (C) 2016  Nathan Lowe
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
package soundclip.core.tests;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import soundclip.core.CueNumber;

/**
 * Tests for {@link soundclip.core.CueNumber}
 */
public class CueNumberTests
{

    @Test
    public void invalidForNoParams()
    {
        try
        {
            new CueNumber();
            fail();
        }
        catch (Exception ex)
        {
            assertThat(ex, instanceOf(IllegalArgumentException.class));
            assertThat(ex.getMessage(), is(equalTo("No number provided")));
        }
    }

    @Test
    public void invalidForEmptyString()
    {
        try
        {
            new CueNumber("");
            fail();
        }
        catch (Exception ex)
        {
            assertThat(ex, instanceOf(IllegalArgumentException.class));
            assertThat(ex.getMessage(), is(equalTo("No number provided")));
        }
    }

    @Test(expected = NumberFormatException.class)
    public void invalidForNonNumericString()
    {
        new CueNumber("foobar");
    }

    @Test(expected = NumberFormatException.class)
    public void invalidForPartiallyNumericString()
    {
        new CueNumber("1.foobar");
    }

    @Test
    public void invalidSingleNegativeInts()
    {
        try
        {
            new CueNumber(-1);
            fail();
        }
        catch (Exception ex)
        {
            assertThat(ex, instanceOf(IllegalArgumentException.class));
            assertThat(ex.getMessage(), is(equalTo("All parts must be positive")));
        }
    }

    @Test
    public void invalidNegativeInts()
    {
        try
        {
            new CueNumber(1, -2, 3);
            fail();
        }
        catch (Exception ex)
        {
            assertThat(ex, instanceOf(IllegalArgumentException.class));
            assertThat(ex.getMessage(), is(equalTo("All parts must be positive")));
        }
    }

    @Test
    public void invalidSingleNegativeString()
    {
        try
        {
            new CueNumber("-1");
            fail();
        }
        catch (Exception ex)
        {
            assertThat(ex, instanceOf(IllegalArgumentException.class));
            assertThat(ex.getMessage(), is(equalTo("All parts must be positive")));
        }
    }

    @Test
    public void invalidNegativeString()
    {
        try
        {
            new CueNumber("1.-2.3");
            fail();
        }
        catch (Exception ex)
        {
            assertThat(ex, instanceOf(IllegalArgumentException.class));
            assertThat(ex.getMessage(), is(equalTo("All parts must be positive")));
        }
    }

    @Test
    public void validSinglePartInt()
    {
        CueNumber num = new CueNumber(1);
        assertThat(num.toString(), is(equalTo("1")));
    }

    @Test
    public void validFromInts()
    {
        CueNumber num = new CueNumber(1, 2, 3);
        assertThat(num.toString(), is(equalTo("1.2.3")));
    }

    @Test
    public void validSinglePartString()
    {
        CueNumber num = new CueNumber("1");
        assertThat(num.toString(), is(equalTo("1")));
    }

    @Test
    public void validFromString()
    {
        CueNumber num = new CueNumber("1.2.3");
        assertThat(num.toString(), is(equalTo("1.2.3")));
    }

    @Test
    public void singlePartSelfEquals()
    {
        CueNumber num = new CueNumber(1);

        assertThat(num, is(equalTo(num)));
    }

    @Test
    public void singlePartEqual()
    {
        CueNumber a = new CueNumber(1);
        CueNumber b = new CueNumber(1);

        assertThat(a, is(equalTo(b)));
        assertThat(b, is(equalTo(a)));
    }

    @Test
    public void multiPartEqual()
    {
        CueNumber a = new CueNumber(1, 2, 3);
        CueNumber b = new CueNumber(1, 2, 3);

        assertThat(a, is(equalTo(b)));
        assertThat(b, is(equalTo(a)));
    }

    @Test
    public void singlePartNotEqual()
    {
        CueNumber a = new CueNumber(1);
        CueNumber b = new CueNumber(2);

        assertThat(a, is(not(equalTo(b))));
        assertThat(b, is(not(equalTo(a))));
    }

    @Test
    public void multiPartNotEqual()
    {
        CueNumber a = new CueNumber(1, 2);
        CueNumber b = new CueNumber(1, 3);
        CueNumber c = new CueNumber(3, 4);
        CueNumber d = new CueNumber(4, 4);

        assertThat(a, is(not(anyOf(equalTo(b), equalTo(c), equalTo(d)))));
        assertThat(b, is(not(anyOf(equalTo(a), equalTo(c), equalTo(d)))));
        assertThat(c, is(not(anyOf(equalTo(b), equalTo(a), equalTo(d)))));
        assertThat(d, is(not(anyOf(equalTo(b), equalTo(c), equalTo(a)))));
    }

    @Test
    public void multiNotEvenPartNotEqual()
    {
        CueNumber a = new CueNumber(1);
        CueNumber b = new CueNumber(1,1);
        CueNumber c = new CueNumber(1,1,1);
        CueNumber d = new CueNumber(1,1,1,1);

        assertThat(a, is(not(anyOf(equalTo(b), equalTo(c), equalTo(d)))));
        assertThat(b, is(not(anyOf(equalTo(a), equalTo(c), equalTo(d)))));
        assertThat(c, is(not(anyOf(equalTo(b), equalTo(a), equalTo(d)))));
        assertThat(d, is(not(anyOf(equalTo(b), equalTo(c), equalTo(a)))));
    }

    @Test
    public void compareDoesNotAcceptNull()
    {
        try
        {
            CueNumber num = new CueNumber(1);
            num.compareTo(null);

            fail();
        }
        catch(Exception ex)
        {
            assertThat(ex, is(instanceOf(NullPointerException.class)));
            assertThat(ex.getMessage(), is(equalTo("The other cue is null")));
        }
    }

    @Test
    public void compareEven()
    {
        CueNumber a = new CueNumber(1);
        assertThat(a.compareTo(a), is(equalTo(0)));

        CueNumber b = new CueNumber(1);

        assertThat(a.compareTo(b), is(equalTo(0)));
        assertThat(b.compareTo(b), is(equalTo(0)));

        CueNumber c = new CueNumber(2);
        assertThat(a.compareTo(c), is(lessThan(0)));
        assertThat(c.compareTo(a), is(greaterThan(0)));


        CueNumber d = new CueNumber(1,1);
        assertThat(d.compareTo(d), is(equalTo(0)));

        CueNumber e = new CueNumber(1,1);

        assertThat(d.compareTo(e), is(equalTo(0)));
        assertThat(e.compareTo(d), is(equalTo(0)));

        CueNumber f = new CueNumber(2,2);

        assertThat(d.compareTo(f), is(lessThan(0)));
        assertThat(f.compareTo(d), is(greaterThan(0)));

        CueNumber g = new CueNumber(1,2);

        assertThat(d.compareTo(g), is(lessThan(0)));
        assertThat(g.compareTo(d), is(greaterThan(0)));

        assertThat(g.compareTo(f), is(lessThan(0)));
        assertThat(f.compareTo(g), is(greaterThan(0)));
    }

    @Test
    public void compareNotEven()
    {
        CueNumber a = new CueNumber(1);
        CueNumber b = new CueNumber(1,1);

        assertThat(a.compareTo(b), is(lessThan(0)));
        assertThat(b.compareTo(a), is(greaterThan(0)));

        CueNumber c = new CueNumber(1,0,1);

        assertThat(b.compareTo(c), is(greaterThan(0)));
        assertThat(c.compareTo(b), is(lessThan(0)));

        assertThat(a.compareTo(c), is(lessThan(0)));
        assertThat(c.compareTo(a), is(greaterThan(0)));
    }
}
