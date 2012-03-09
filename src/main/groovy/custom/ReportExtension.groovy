package extension.custom

import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.extension.IGlobalExtension

class ReportExtension implements IGlobalExtension {

    void visitSpec(SpecInfo spec) {
        spec.addListener(new AbstractRunListener() {

            StringBuilder buffer = new StringBuilder()
            def dataVars

            @Override
            void beforeSpec(SpecInfo specInfo) {
                buffer << "<html>\n"
                buffer << """\
<head>
  <title>
    Specification ${specInfo.description}
  </title>
</head>

"""
                buffer << "<body>\n"
            }

            @Override
            void beforeFeature(FeatureInfo feature) {
                buffer << "<h2>Feature ${feature.name}</h2>"
                dataVars = feature.dataVariables
            }

            @Override
            void beforeIteration(IterationInfo iteration) {
                buffer << "<div>${iteration.dataValues}</div>"
            }

            @Override
            void afterFeature(FeatureInfo feature) {
                for (block in feature.blocks) {
                    buffer <<  "<div class=\"block-${block.kind}\">${block.kind}"
                    for (text in block.texts) {
                        buffer << "<span>${text}</span>"
                    }
                    buffer << "</div>"
                }
            }

            @Override
            void afterSpec(SpecInfo specInfo) {

                buffer << "</body>\n"
                buffer << "</html>"

                def out = new File("./target/SPEC-"+specInfo.description+".html")
                out.withWriter { w ->
                    w.write(buffer.toString())
                }
            }

            @Override
            void error(ErrorInfo error) {
                buffer << "<div><pre>${error.error.message}</pre></div>"
            }

        })
    }

}

