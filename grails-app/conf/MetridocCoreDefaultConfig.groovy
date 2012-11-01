import org.apache.commons.lang.SystemUtils

//TODO:what is this?  Maybe our first attempt at doing a file store?  Confusing... get rid fo this?
metridoc {
    files {
        directory = "${SystemUtils.USER_HOME}/.metridoc/files"
        foo {
            filter  = {
                it.name.contains("foo")
            }
            metaData = {File file ->
                fileName = file.name
                beginDate = new Date(file.lastModified())
                endDate = new Date(file.lastModified())
                bytes = file.getBytes()
            }
        }
    }
}

metridoc {
    security {
        dbdoc = [
            title : "Liquibase Migration Documentation",
            description: "Documentation on database migration from the grails database migration plugin",
            adminOnly: true
        ]
    }
}