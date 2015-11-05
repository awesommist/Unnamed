package unnamed.network.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NetworkEventMeta {
    boolean compressed() default false;

    boolean chunked() default false;

    EventDirection direction() default EventDirection.ANY;
}