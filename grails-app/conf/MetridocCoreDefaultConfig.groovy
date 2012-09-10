import org.apache.commons.lang.SystemUtils

metridoc {
    schemas {
        admin {
            dataSource = "admin"
            schema = "schemas/admin/adminSchema.xml"
        }
        user {
            dataSource = "admin"
            schema = "schemas/user/userSchema.xml"
        }
    }
}


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