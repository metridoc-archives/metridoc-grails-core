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
package metridoc.console

/**
 *
 * When building a comprehensive jar, this allows for abstracting out a console implementation, and allows a user
 * to add new console commands.  One such implementation is {@link RunJob}.  To map a console command to a class
 * that will handle the call simply add an entry into a MetridocExtension.groovy file at the base of your project
 * that maps a command to the class.  Please see <a href="http://code.google.com/p/metridoc/source/browse/trunk/job-api/core/metridoc-core/src/main/resources/MetridocExtension.groovy">MetridocExtension</a>
 * for a reference on how "runJob" is mapped to {@link metridoc.console.RunJob}
 *
 * @see metridoc.console.RunJob
 */
class MetridocConsole {

    private static final DEFAULT_CLASS_LOADER = Thread.currentThread().contextClassLoader
    ClassLoader classLoader = DEFAULT_CLASS_LOADER
    private Map _consoleMapping = [:]

    void runConsoleCommand(String[] args) {
        Thread.currentThread().contextClassLoader.loadClass("groovy.lang.Script")
        def runner = getConsoleMapping().get(args[0]).newInstance()
        runner.args = args
        runner.run()
    }

    Map getConsoleMapping() {
        if (_consoleMapping) return _consoleMapping

        def extensions = classLoader.getResources("MetridocExtension.groovy")
        while (extensions.hasMoreElements()) {
            URL next = extensions.nextElement()
            Script script = new GroovyShell(classLoader).parse(next.openStream(), "MetridocExtension.groovy")
            script.run()
            def consoleMapping = script.binding.variables.consoleMapping
            if (consoleMapping && consoleMapping instanceof Map) {
                _consoleMapping.putAll(consoleMapping)
            }
        }

        return _consoleMapping
    }
}
