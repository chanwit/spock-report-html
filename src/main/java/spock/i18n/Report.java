package spock.i18n;

import org.spockframework.extension.ThaiExtension;
import org.spockframework.runtime.extension.ExtensionAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ExtensionAnnotation(ThaiExtension.class)
public @interface Report {
    Language language() default Language.EN;
}
