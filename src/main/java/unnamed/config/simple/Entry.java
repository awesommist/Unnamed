package unnamed.config.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Entry {
    String SAME_AS_FIELD = "";

    String name() default SAME_AS_FIELD;

    String[] comment() default {};

    int version() default 0;
}