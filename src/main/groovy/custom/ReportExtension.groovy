package org.spockframework.extension

import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

import java.text.MessageFormat;

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.model.BlockKind
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.extension.IGlobalExtension

import spock.i18n.util.UnicodeUtils;

class ReportExtension implements IGlobalExtension {

    void visitSpec(SpecInfo spec) {

        spec.addListener(new AbstractRunListener() {

            def buffer = new StringBuilder()
            def iterBody
            def errBody
            def dataVars
            def enProp = new Properties()
            def prop   = new Properties()

            String fmt(String key, Object... args) {
                def val = prop.getProperty(key, enProp.getProperty(key))
                return MessageFormat.format(val, args)
            }

            @Override
            void beforeSpec(SpecInfo specInfo) {
                def cl = ReportExtension.class.getClassLoader()
                enProp.load(cl.getResourceAsStream("org/spockframework/extension/messages.properties"))
                if(specInfo.metadata) {
                    def name = specInfo.metadata.toString()
                    if(name == "@spock.i18n.Thai()") {
                        prop.load(cl.getResourceAsStream("org/spockframework/extension/messages_th.properties"))
                    }
                }

                def css = "  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />"
                def styleStream = cl.getResourceAsStream("spock-style.css")
                if(styleStream != null) {
                    if(new File("./target/spock-reports").exists()==false) {
                        new File("./target/spock-reports").mkdirs()
                    }
                    def outStyle = new File("./target/spock-reports/style.css").newPrintWriter("UTF-8")
                    styleStream.withReader { r ->
                        def lines = r.readLines()
                        lines.each { l ->
                            outStyle.write(l + "\n")
                        }
                    }
                    outStyle.close()
                } else {
                    css = '''\
  <style>
    table.whereBlock {
        background-color:#ffffff;
        border:1px solid #c3c3c3;
        border-collapse:collapse;
    }

    table.whereBlock th {
        background-color:#e3e3e3;
        border:1px solid #c3c3c3;
        padding:3px;
        vertical-align:top;
    }

    table.whereBlock td {
        border:1px solid #c3c3c3;
        padding:3px;
        vertical-align:top;
    }

    tr.error {
        background-color: #bb0000;
        color: white;
    }
    tr.error td {
        background-color: #bb0000;
        color: white;
    }
  </style>
'''
                }

                buffer << "<html>\n"
                buffer << """\
<head>
  <meta charset="utf-8" />
  <title>
    ${fmt('spock.specification', specInfo.description)}
  </title>
${css}
</head>

"""
                buffer << "<body>\n"
                buffer << "<h1>${fmt('spock.specification', specInfo.name-'Spec')}</h1>\n"
            }

            @Override
            void beforeFeature(FeatureInfo feature) {
                buffer << "<h2>${fmt('spock.feature', feature.name)}</h2>\n"
                iterBody = new StringBuilder()
                dataVars = feature.dataVariables
                iterBody << "<table class=\"whereBlock\">\n"
                iterBody << "<tbody>\n"
                iterBody << "<tr>\n"
                dataVars.each {
                    iterBody << "<th>${it}</th>\n"
                }
                iterBody << "</tr>\n"
            }

            @Override
            void beforeIteration(IterationInfo iteration) {
                errBody = null
            }

            @Override
            void afterIteration(IterationInfo iteration) {
                if(errBody) {
                    iterBody << "<tr class=\"error\">\n"
                }
                else {
                    iterBody << "<tr>\n"
                }
                iteration.dataValues.each {
                    if(it instanceof String) {
                        iterBody << "<td>\"${it}\"</td>\n"
                    } else {
                        iterBody << "<td>${it}</td>\n"
                    }
                }
                iterBody << "</tr>\n"
                if(errBody) {
                    iterBody << errBody
                }
            }

            @Override
            void afterFeature(FeatureInfo feature) {
                for (block in feature.blocks) {
                    def kindText = ""
                    switch(block.kind) {
                        case BlockKind.SETUP:  kindText = "spock.given" ; break
                        case BlockKind.WHEN:   kindText = "spock.when"  ; break
                        case BlockKind.THEN:   kindText = "spock.then"  ; break
                        case BlockKind.EXPECT: kindText = "spock.expect"; break
                        case BlockKind.WHERE:  kindText = "spock.where" ; break
                    }
                    buffer <<  "<div class=\"block-${block.kind}\">${fmt(kindText)} "
                    for (text in block.texts) {
                        buffer << "<span>${text}</span>\n"
                    }
                    buffer << "</div>\n"
                }
                iterBody << "</tbody>\n"
                iterBody << "</table>\n"
                buffer << iterBody.toString()
            }

            @Override
            void afterSpec(SpecInfo specInfo) {
                buffer << "</body>\n"
                buffer << "</html>"
                if(new File("./target/spock-reports").exists()==false) {
                    new File("./target/spock-reports").mkdirs()
                }
                def out = new File("./target/spock-reports/SPOCK-"+specInfo.description+".html")
                out.withWriter("UTF-8"){ w ->
                    w.write(buffer.toString())
                }
            }

            @Override
            void error(ErrorInfo error) {
                errBody = """\
            <tr>
                <td colspan=\"${dataVars.size()}\"><pre>${error.error.message}</pre></td>
            </tr>
"""
            }

        })
    }

}

