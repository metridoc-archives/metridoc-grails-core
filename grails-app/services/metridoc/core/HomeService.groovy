package metridoc.core

import org.codehaus.groovy.grails.commons.GrailsClassUtils

class HomeService {

    static final HOME_DATA_FIELD = "homePage"
    def grailsApplication





    def getControllersByCategory() {
        def categoryMap = [:]
        def categoryList = AppCategory.list().sort() { it.adminOnly == true }
        def categoryControllers
        for (cat in categoryList) {
            categoryControllers = ControllerData.where { category == cat }.list()
            categoryMap.put(cat, categoryControllers)
        }
        return categoryMap

    }

//Uses log.info to confirm what categories and applications are being added. feel free to comment out
    def bootStrapApplications() {

        //Must add categories by hand
        def adminOnly = AppCategory.find {
            name == "Administration"
        }
        if (!adminOnly) {
            adminOnly = new AppCategory(name: "Administration", iconName: "icon-cog", adminOnly: true)
            log.info "Adding category ${adminOnly.name}"
            adminOnly.save()
        }

        def availableApps = AppCategory.find {
            name == "Available Applications"
        }
        if (!availableApps) {
            availableApps = new AppCategory(name: "Available Applications", iconName: "icon-bar-chart", adminOnly: false)
            log.info "Adding category ${availableApps.name}"
            availableApps.save()


        }
        grailsApplication.controllerClasses.each { controller ->
            def app = ControllerData.find {
                appName == "${controller.naturalName}"
            }
            log.info "Couldn't find data for ${controller.naturalName}"
            if (!app) {
                def dataField = GrailsClassUtils.getStaticFieldValue(controller.clazz, HOME_DATA_FIELD)
                if (dataField != null) {
                    def title = dataField.getAt("title") ?: "${controller.naturalName}"
                    def description = dataField.getAt("description") ?: """No Description"""
                    def path = "${controller.name}/index"
                    def category;
                    if (dataField["adminOnly"] != null) {
                        category = "Administration"
                    } else {
                        category = "Available Applications"
                    }
                    app = new ControllerData(appName: title, appDescription: description, controllerPath: path, category: AppCategory.findByName(category), homePage: true)
                    log.info "Adding controller Data for ${controller.naturalName}, category ${category}"
                    app.save()
                } else {
                    log.info "${controller.naturalName} doesn't have home page metadata"
                }
            }
        }
    }
}
