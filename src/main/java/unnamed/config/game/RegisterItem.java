package unnamed.config.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterItem {
    String DEFAULT = "[default]";
    String NONE = "[none]";

    String name();

    String unlocalizedName() default DEFAULT;

    boolean isEnabled() default true;

    boolean isConfigurable() default true;
}