package metridoc.core

import org.codehaus.groovy.grails.commons.GrailsClassUtils

class HomeService {

    static final HOME_DATA_FIELD = "homePage"
    def grailsApplication
    def grailsLinkGenerator




    def getControllersByCategory() {
        def categoryMap = [:]
        def categoryList = AppCategory.list().sort() { it.adminOnly }
        def categoryControllers
        def badLinks = new ArrayList()
        for (cat in categoryList) {
            categoryControllers = ControllerData.where { category == cat }.list()
            for (controller in categoryControllers) {
                if (controller.validity == ControllerData.IsValid.INVALID) {
                    badLinks.add(controller)
                    break;
                } else if (controller.validity == ControllerData.IsValid.UNSET) {
                    def controllerName = controller.controllerPath.replace("/index", "")
                    def url = grailsLinkGenerator.link(controller: controllerName, action: 'index', absolute: true)
                    URL u = new URL(url)
                    HttpURLConnection con = (HttpURLConnection) u.openConnection()
                    con.setRequestMethod("GET");
                    con.connect();
                    if (con.getResponseCode() != 200) {
                        badLinks.add(controller)
                        controller.validity = ControllerData.IsValid.INVALID
                        controller.save()
                    } else {
                        controller.validity = ControllerData.IsValid.VALID
                        controller.save()
                    }
                }
            }
            for (controller in badLinks) {
                categoryControllers.remove(controller)
            }
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
            log.debug "Adding category ${adminOnly.name}"
            adminOnly.save()
        }

        def availableApps = AppCategory.find {
            name == "Available Applications"
        }
        if (!availableApps) {
            availableApps = new AppCategory(name: "Available Applications", iconName: "icon-bar-chart", adminOnly: false)
            log.debug "Adding category ${availableApps.name}"
            availableApps.save()


        }
        grailsApplication.controllerClasses.each { controller ->
            def app = ControllerData.find {
                appName == "${controller.naturalName}"
            }
            log.debug "Couldn't find data for ${controller.naturalName}"
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
                    app = new ControllerData(appName: title, appDescription: description, controllerPath: path, validity: ControllerData.IsValid.UNSET,
                            category: AppCategory.findByName(category), homePage: true)
                    log.debug "Adding controller Data for ${controller.naturalName}, category ${category}"
                    app.save()
                } else {
                    log.debug "${controller.naturalName} doesn't have home page metadata"
                }
            }
        }
    }
}
