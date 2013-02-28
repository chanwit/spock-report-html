package org.spockframework.extension;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.SpecInfo;
import spock.i18n.Report;

public class ReportAnnotationExtension extends AbstractAnnotationDrivenExtension<Report> {

    @Override
    public void visitSpecAnnotation(Report annotation, SpecInfo spec) {
        spec.setMetadata(annotation);
    }
}
