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
package soundclip.core.interop;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A simple collection of event listeners. Each listener is identified by a unique
 * 64-bit integer.
 */
public class Signal<T>
{
    private volatile long nextListenerId = 0;
    private final HashMap<Long, Consumer<T>> listeners = new HashMap<>();

    public long whenTriggered(Consumer<T> listener)
    {
        synchronized (listeners)
        {
            listeners.put(nextListenerId, listener);
            return nextListenerId++;
        }
    }

    public void unsubscribe(long id)
    {
        synchronized (listeners)
        {
            listeners.remove(id);
        }
    }

    public void post(T event)
    {
        synchronized (listeners)
        {
            listeners.values().parallelStream().forEach(l -> l.accept(event));
        }
    }
}
