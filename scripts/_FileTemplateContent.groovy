//includeTargets << grailsScript("_GrailsInit")
//
//target(main: "The description of the script goes here!") {
//
//}

defineAppResources = {
    return '''
modules = {
    \${lowcaseName} {
        dependsOn 'jquery-ui'
        resource id: 'css',
                url: [dir:'\${lowcaseName}/css', file:'\${lowcaseName}.css']
        resource id:'js',
                url: [dir:'\${lowcaseName}/js',file:'\${lowcaseName}.js']
    }
}'''
}

definePluginResources = {
    return '''
modules = {
    \${lowcaseName} {
        dependsOn 'jquery-ui'
        resource id: 'css',
                url: [plugin:"\${pluginName}",dir:'\${lowcaseName}/css', file:'\${lowcaseName}.css']
        resource id:'js',
                url: [plugin:"\${pluginName}",dir:'\${lowcaseName}/js',file:'\${lowcaseName}.js']
    }
}'''
}


defineController = {
    return '''
package metridoc.\${lowcaseName}\\n
import metridoc.ReportController\\n
class \${upcaseName}Controller extends ReportController{\\n
    def index() { }\\n
}'''
}

defineService = {
    return '''
package metridoc.\${lowcaseName}\\n
class \${upcaseName}Service {\\n
    def serviceMethod() {}\\n
}'''
}

defineChangeLog = {
    return '''
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd"/>'''
}


defineSchema = {
    return '''
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd">
    <include file="schemas/\${lowcaseName}/\${lowcaseName}.changelog-01.xml"/>
</databaseChangeLog>'''
}

//setDefaultTarget(main)
