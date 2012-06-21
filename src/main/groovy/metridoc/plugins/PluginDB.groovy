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
package metridoc.plugins

import metridoc.utils.Assert

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/3/11
 * Time: 2:54 PM
 */
class PluginDB {

    /**
     * plugins by type
     */
    private Map<String, List<Class>> plugins = [:]
    private static PluginDB pluginDB

    /**
     * should not be implemented
     */
    protected PluginDB() {}

    static PluginDB getInstance() {
        if (pluginDB == null) {
            pluginDB = new PluginDB()
            def classLoader = Thread.currentThread().contextClassLoader
            def extensions = classLoader.getResources("MetridocExtension.groovy")
            while (extensions.hasMoreElements()) {
                URL next = extensions.nextElement()
                Script script = new GroovyShell(classLoader).parse(next.openStream(), "MetridocExtension.groovy")
                script.run()
                def plugins = script.binding.variables.plugins
                if (plugins) {
                    plugins.each {Class clazz ->
                        pluginDB.addPlugin(clazz)
                    }
                }
            }

        }

        return pluginDB
    }

    void addPlugin(Class clazz) {
        Plugin plugin = clazz.getAnnotation(Plugin.class)
        Assert.isTrue(plugin || Script.isAssignableFrom(clazz), "The class ${clazz} is not a plugin")
        def category = plugin ? plugin.category() : "job"
        def notThere = !plugins.containsKey(category)

        if (notThere) {
            plugins.put(category, [])
        }

        def name = getName(clazz)

        if (plugins.get(category).contains(clazz)) {
            return
        }

        if (name && plugins.get(category).find {name == getName(it)}) {
            throw new IllegalArgumentException("There already exists a plugin named ${name} in the category ${category}")
        }

        plugins.get(category).add(clazz)
    }

    List<Class> getPlugins(String category) {
        return plugins.get(category)
    }

    Class getPlugin(String category, String name) {
        def plugins = getPlugins(category)
        if (plugins) {
            def result
            plugins.each {
                if (name == getName(it)) {
                    result = it
                }
            }
            return result
        }

        return null
    }

    List<String> getCategories() {
        return plugins.keySet().toList().sort() + []
    }

    private static String getName(Class clazz) {
        Plugin plugin = clazz.getAnnotation(Plugin.class)
        return plugin ? plugin.name() : clazz.getName()
    }
}

