package common.annotations;

import common.extension.SkipForBrokenImageExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SkipForBrokenImageExtension.class)
public @interface SkipForBrokenImage {
    String[] images() default {};
}
