import metridoc.utils.ClassUtils
import metridoc.admin.ReportsConfiguration
import org.apache.shiro.SecurityUtils
import org.apache.commons.lang.StringUtils

class HomeController {

    def index() {
        def adminLinks = []
        def reportLinks = []

        grailsApplication.controllerClasses.each {
            def clazz = it.clazz
            def artifactName = StringUtils.uncapitalize(it.name)
            def config = ReportsConfiguration.find() {
                name == artifactName
            }

            def role = config ? config.role : null

            def hasRole = SecurityUtils.subject.hasRole(role) || SecurityUtils.subject.hasRole("ROLE_ADMIN")

            if(hasRole) {

                def reportName = ClassUtils.getStaticVariable(clazz, "reportName")
                def isAdmin = "ROLE_ADMIN" == role

                if (isAdmin && reportName) {
                    adminLinks << [
                        reportName: reportName,
                        controllerName: it.name
                    ]
                } else if (reportName) {
                    reportLinks << [
                        reportName: reportName,
                        controllerName: it.name
                    ]
                }
            }
        }


        return [
            reportLinks: reportLinks,
            adminLinks: adminLinks,
            hasLinks: reportLinks.size() + adminLinks.size(),
        ]
    }

}
