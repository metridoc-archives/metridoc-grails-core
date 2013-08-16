package metridoc.core

import org.apache.commons.lang.SystemUtils

import java.lang.management.ManagementFactory

class GeneralSettingsService {

    def grailsApplication

    File getStartFile() {
        getMetridocHomeFile("start.sh")
    }

    File getWorkDirectoryFile() {
        getMetridocHomeFile("workDir.txt")
    }

    File getMetridocHomeFile(String fileName) {
        def metridocHome = grailsApplication.mergedConfig.metridoc.home
        return new File("$metridocHome${SystemUtils.FILE_SEPARATOR}${fileName}")
    }

    static boolean fileExists(File file) {
        file.exists() && file.text.trim()
    }

    String javaCommand() {

        def slash = SystemUtils.FILE_SEPARATOR
        System.getProperty("java.home") + "${slash}bin${slash}java"
    }

    String javaVmArguments() {
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

    String mainCommand() {
        System.getProperty("sun.java.command")
    }
}
