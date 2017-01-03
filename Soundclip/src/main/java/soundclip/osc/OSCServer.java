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
package soundclip.osc;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.argument.OSCTimeStamp;
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector;
import com.illposed.osc.transport.udp.OSCPortIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.core.Project;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * The OSC Server for the specified project
 */
public class OSCServer implements AutoCloseable
{
    private static final Logger Log = LogManager.getLogger(OSCServer.class);

    private final Project project;
    private final OSCPortIn listener;

    private long lastOSCPanicAt = 0;

    public OSCServer(Project project) throws IOException
    {
        this.project = project;

        listener = new OSCPortIn();
        setupRoutes();

    }

    private void setupRoutes()
    {
        for(Method routeMethod : OSCServer.class.getDeclaredMethods())
        {
            Class[] pTypes = routeMethod.getParameterTypes();

            if(pTypes.length == 2 && pTypes[0] == OSCTimeStamp.class && pTypes[1] == OSCMessage.class)
            {
                if(routeMethod.isAnnotationPresent(OSCRoute.class))
                {
                    OSCRoute route = routeMethod.getDeclaredAnnotation(OSCRoute.class);

                    Log.debug("Registering {}#{} for route {}", OSCServer.class.getName(), routeMethod.getName(), route.value());
                    listener.getDispatcher().addListener(new OSCPatternAddressMessageSelector(route.value()), (t, m) -> {
                        try
                        {
                            routeMethod.invoke(this, t, m);
                        }
                        catch (IllegalAccessException | InvocationTargetException e)
                        {
                            Log.error("Unable to invoke OSC Route handler. Contact the developer.", e);
                        }
                    });
                }
            }
        }
    }

    public void listen()
    {
        if(listener.isListening())
        {
            Log.warn("OSC Server is already listening");
            return;
        }

        listener.startListening();
        Log.info("OSC Server listening on {}", listener.getLocalAddress());
    }

    public boolean isListening()
    {
        return listener.isListening();
    }

    @Override
    public void close() throws Exception
    {
        Log.debug("OSC Server Shutting Down");
        listener.stopListening();
        listener.close();

        Log.info("OSC Server shut down");
    }

    @OSCRoute("*")
    private void catchAllListener(OSCTimeStamp time, OSCMessage message)
    {
        StringBuilder arguments = new StringBuilder();
        message.getArguments().forEach((o) -> arguments.append(o.toString()).append(", "));

        Log.trace("Got a message at {} on route {} with arguments: {}", time, message.getAddress(), arguments);
    }

    @OSCRoute("/go")
    private void onGoNextCue(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got GO NEXT CUE message at {}", time.toDate());
    }

    @OSCRoute("/panic")
    private void onPanic(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got a PANIC message at {}", time.toDate());

        long now = System.currentTimeMillis();
        project.panic(now - lastOSCPanicAt <= project.getPanicHardStopBefore());
        lastOSCPanicAt = now;
    }

    @OSCRoute("/focus/previous/cue")
    private void onFocusPreviousCue(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got a FOCUS PREVIOUS CUE message at {}", time.toDate());
    }

    @OSCRoute("/focus/next/cue")
    private void onFocusNextCue(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got a FOCUS NEXT CUE message at {}", time.toDate());
    }

    @OSCRoute("/focus/previous/list")
    private void onFocusPreviousCueList(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got a FOCUS PREVIOUS LIST message at {}", time.toDate());
    }

    @OSCRoute("/focus/next/list")
    private void onFocusNextCueList(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got a FOCUS NEXT LIST message at {}", time.toDate());
    }

    @OSCRoute("/transport/pause")
    private void onTransportPause(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got a PAUSE TRANSPORT message at {}", time.toDate());
    }

    @OSCRoute("/transport/resume")
    private void onTransportResume(OSCTimeStamp time, OSCMessage message)
    {
        Log.debug("Got a RESUME TRANSPORT message at {}", time.toDate());
    }

    @OSCRoute("/transport/rewind")
    private void onRewind(OSCTimeStamp time, OSCMessage message)
    {
        double factor = -10;

        List<Object> args = message.getArguments();
        if(args.size() == 0) Log.warn("TRANSPORT REWIND without argument");
        try
        {
            factor = (double) args.get(0);
        }
        catch(ClassCastException ex)
        {
            Log.warn("TRANSPORT REWIND called with wrong argument type ({})", args.get(0).getClass().getName());
        }

        Log.debug("Got a TRANSPORT REWIND message at {} (TODO: Rewind {}s", time.toDate(), factor);
    }

    @OSCRoute("/transport/fastforward")
    private void onFastForward(OSCTimeStamp time, OSCMessage message)
    {
        double factor = 10;

        List<Object> args = message.getArguments();
        if(args.size() == 0) Log.warn("TRANSPORT FASTFORWARD without argument");
        try
        {
            factor = (double) args.get(0);
        }
        catch(ClassCastException ex)
        {
            Log.warn("TRANSPORT FASTFORWARD called with wrong argument type ({})", args.get(0).getClass().getName());
        }

        Log.debug("Got a TRANSPORT FASTFORWARD message at {} (TODO: Fastforward {}s", time.toDate(), factor);
    }
}
