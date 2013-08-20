package metridoc.core

import org.apache.commons.lang.SystemUtils

import java.lang.management.ManagementFactory

class StatusController {

    def dataSource
    def grailsApplication

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def index() {
        redirect(controller: "manageConfig", action: "index")
    }

    private String javaCommand() {
        def slash = SystemUtils.FILE_SEPARATOR
        System.getProperty("java.home") + "${slash}bin${slash}java"
    }

    private String javaVmArguments() {
        def vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments()
        def vmArgsOneLine = new StringBuffer();
        vmArguments.each {
            if (!it.contains("-agentlib")) {
                vmArgsOneLine.append(it);
                vmArgsOneLine.append(" ");
            }
        }
        return vmArgsOneLine
    }

    private String mainCommand() {
        System.getProperty("sun.java.command")
    }
}
