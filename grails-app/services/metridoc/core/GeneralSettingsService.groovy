package metridoc.core

import org.apache.commons.lang.SystemUtils

import java.lang.management.ManagementFactory

class GeneralSettingsService {

    static File getStartFile() {
        getMetridocHomeFile("start.sh")
    }

    static File getWorkDirectoryFile() {
        getMetridocHomeFile("workDir.txt")
    }

    static File getMetridocHomeFile(String fileName) {
        def metridocHome = grailsApplication.mergedConfig.metridoc.home
        return new File("$metridocHome${SystemUtils.FILE_SEPARATOR}${fileName}")
    }

    static boolean fileExists(File file) {
        file.exists() && file.text.trim()
    }

    static String javaCommand() {

        def slash = SystemUtils.FILE_SEPARATOR
        System.getProperty("java.home") + "${slash}bin${slash}java"
    }

    static String javaVmArguments() {
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

    static String mainCommand() {
        System.getProperty("sun.java.command")
    }
}
