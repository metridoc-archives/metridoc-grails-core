/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.utils

import org.slf4j.LoggerFactory


class PropertyUtils {

    private final static DEFAULT_METRIDOC_BASE = ".metridoc"
    private final static GROOVY = ".groovy"
    private static final PROPERTIES = ".properties"
    private static final EXTENSIONS_TO_SEARCH = [GROOVY, PROPERTIES]
    String environment
    String home
    String metridocHome
    ClassLoader classLoader
    def propertyFileOrder = [metridoc.utils.FileType.DEFAULT, metridoc.utils.FileType.HOME, metridoc.utils.FileType.METRIDOC_HOME, metridoc.utils.FileType.CLASSPATH, metridoc.utils.FileType.TEST_CLASSPATH]
    def log = LoggerFactory.getLogger(PropertyUtils.class)

    public ConfigObject getConfig(String configFile) {
        ConfigObject currentConfig = null

        propertyFileOrder.each {fileType ->
            switch (fileType) {
                case metridoc.utils.FileType.DEFAULT:
                    currentConfig = getConfigFileOnClassPath("default${configFile.capitalize()}", currentConfig)
                    break
                case metridoc.utils.FileType.HOME:
                    currentConfig = getConfigFileOnSystem(systemPath(getHome(), configFile), currentConfig)
                    break
                case metridoc.utils.FileType.METRIDOC_HOME:
                    currentConfig = getConfigFileOnSystem(systemPath(getMetridocHome(), configFile), currentConfig)
                    break
                case metridoc.utils.FileType.CLASSPATH:
                    currentConfig = getConfigFileOnClassPath(configFile, currentConfig)
                    break
                case metridoc.utils.FileType.TEST_CLASSPATH:
                    currentConfig = getConfigFileOnClassPath("${configFile}Test", currentConfig)
                    break
                default:
                    throw new IllegalArgumentException("Cannot support file type: ${fileType}")
            }
        }

        if (currentConfig == null) {
            throw new FileNotFoundException("Could not find the property file ${configFile} on the classpath or " +
                "on the system paths [${getHome()}, ${getMetridocHome()}]")
        }

        return currentConfig
    }

    void loadProperties(Binding binding, String... propertyFiles) {
        propertyFiles.each {file ->
            def config = getConfig(file)
            config.keySet().each {key ->
                binding[key] = config[key]
            }
        }
    }

    protected String systemPath(String base, String fileName) {
        base + SystemUtils.FILE_SEPARATOR + fileName
    }

    private ConfigObject getConfigFileOnSystem(String filePathNoExtension, ConfigObject currentConfig = null) {
        getConfigFile(filePathNoExtension, currentConfig) {String filePathWithExtension ->
            def file = new File(filePathWithExtension)
            if (file.exists()) {
                return new FileInputStream(file)
            }

            return null
        }
    }

    private ConfigObject getConfigFileOnClassPath(String filePathNoExtension, ConfigObject currentConfig = null) {
        getConfigFile(filePathNoExtension, currentConfig) {String filePathWithExtension ->
            def url = getClassLoader().getResource(filePathWithExtension)
            if (url) {
                return url.newInputStream()
            }

            def rootLoader = getClassLoader().rootLoader

            if (rootLoader) {
                url = rootLoader.getResource(filePathWithExtension)

            }

            if (url) {
                return url.newInputStream()
            }

            rootLoader = this.classLoader.rootLoader

            if (rootLoader) {
                url = rootLoader.getResource(filePathWithExtension)
            }

            if (url) {
                return url.newInputStream()
            }


            return null //not found
        }
    }

    private ConfigObject getConfigFile(String filePath, ConfigObject currentConfig = null, Closure getFile) {
        ConfigObject result = null
        EXTENSIONS_TO_SEARCH.each {String extension ->

            if (!result) { //if no result yet
                def fullPath

                if (filePath.endsWith(extension)) { //if the extension is already included
                    fullPath = "${filePath}"
                } else {
                    fullPath = "${filePath}${extension}"
                }


                log.debug "searching for file ${fullPath}"
                InputStream stream
                def slurper = new ConfigSlurper()
                if (environment) {
                    slurper = new ConfigSlurper(environment)
                }
                try {
                    stream = getFile(fullPath)
                    if (stream) {
                        log.debug "found file ${fullPath}"

                        if (extension == GROOVY) {
                            def fileName = convertPathToFileName(fullPath)
                            def script = new GroovyShell().parse(stream, fileName)
                            result = slurper.parse(script)
                        } else {
                            def properties = new Properties()
                            properties.load(stream)
                            result = slurper.parse(properties)
                        }
                    }

                } finally {
                    if (stream) {
                        IOUtils.closeQuietly(stream)
                    }
                }
            }
        }

        if (currentConfig != null) {
            currentConfig.merge(result)
            return currentConfig
        }

        return result
    }

    protected String convertPathToFileName(String path) {
        int lastFileSeparator = path.lastIndexOf(SystemUtils.FILE_SEPARATOR)
        path.substring(lastFileSeparator + 1)
    }

    ClassLoader getClassLoader() {
        if (classLoader) {
            return classLoader
        }

        classLoader = Thread.currentThread().contextClassLoader
    }

    String getHome() {
        if (home) {
            return home
        }

        home = SystemUtils.USER_HOME
    }

    String getMetridocHome() {
        if (metridocHome) {
            return metridocHome
        }

        metridocHome = systemPath(home, DEFAULT_METRIDOC_BASE)
    }
}
