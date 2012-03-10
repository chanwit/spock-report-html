package org.spockframework.extension;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.SpecInfo;

import spock.i18n.Thai;

public class ThaiExtension extends AbstractAnnotationDrivenExtension<Thai> {

    @Override
    public void visitSpecAnnotation(Thai annotation, SpecInfo spec) {
        spec.setMetadata(annotation);
    }

}
