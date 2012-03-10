package spock.i18n;

import java.lang.annotation.*;

import org.spockframework.extension.ThaiExtension;
import org.spockframework.runtime.extension.ExtensionAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})

@ExtensionAnnotation(ThaiExtension.class)
public @interface Thai {

}
