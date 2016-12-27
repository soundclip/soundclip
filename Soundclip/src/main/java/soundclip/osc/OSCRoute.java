package soundclip.osc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a method to an OSC Listener using the specified route
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OSCRoute
{
    /** The pattern to create an {@link com.illposed.osc.messageselector.OSCPatternAddressMessageSelector} from*/
    String value();
}
