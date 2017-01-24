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
package soundclip.controls;

import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

/**
 * A spinner value factory for longs
 */
public class LongSpinnerValueFactory extends SpinnerValueFactory<Long>
{
    private class LongStringConverter extends StringConverter<Long>
    {

        @Override
        public String toString(Long object)
        {
            return object == null ? "" : Long.toString(object);
        }

        @Override
        public Long fromString(String string)
        {
            if(string == null || string.trim().isEmpty()) return null;

            return Long.valueOf(string);
        }
    }

    private long min, max, step;

    public LongSpinnerValueFactory()
    {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public LongSpinnerValueFactory(long min, long max)
    {
        this(min, max, 0L);
    }

    public LongSpinnerValueFactory(long min, long max, long initialValue)
    {
        this(min, max, initialValue, 1L);
    }

    public LongSpinnerValueFactory(long min, long max, long initialValue, long step)
    {
        setConverter(new LongStringConverter());

        setMin(min);
        setMax(max);
        setStep(step);

        valueProperty().addListener((prop, oldVal, newVal) -> {
            if (newVal < getMin()) setValue(getMin());
            else if(newVal > getMax()) setValue(getMax());
        });

        setValue(initialValue);
    }

    @Override
    public void decrement(int steps)
    {
        long newVal = getValue() - step * steps;
        if(newVal < min) newVal = min;

        setValue(newVal);
    }

    @Override
    public void increment(int steps)
    {
        long newVal = getValue() + step * steps;
        if(newVal > max) newVal = max;

        setValue(newVal);
    }

    public long getMin()
    {
        return min;
    }

    public void setMin(long min)
    {
        this.min = min;
    }

    public long getMax()
    {
        return max;
    }

    public void setMax(long max)
    {
        this.max = max;
    }

    public long getStep()
    {
        return step;
    }

    public void setStep(long step)
    {
        this.step = step;
    }
}
