package metridoc.core

import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class HomeService {

    static final HOME_DATA_FIELD = "homePage"
    def grailsApplication
    LinkGenerator grailsLinkGenerator

    static class CategoryFeatures {
        String appName
        String iconName
        boolean adminOnly


        CategoryFeatures(String n, String i, boolean a) {
            appName = n
            iconName = i
            adminOnly = a
        }
    }

    def testIfControllerIsValid(String url) {
        URL u = new URL(url)
        HttpURLConnection con = (HttpURLConnection) u.openConnection()
        //noinspection GroovyTrivialIf
        if (con.getResponseCode() != 200) {
            return false
        } else {
            return true
        }
    }

    def changeControllerValidity(ControllerData controller, boolean isValid) {
        if (!isValid) {
            controller.validity = "INVALID"
        } else {
            controller.validity = "VALID"
        }
        controller.save()
    }


    def getControllersByCategory() {
        def categoryMap = [:]
        def categoryList = AppCategory.list().sort() { it.adminOnly }
        def categoryControllers
        def badLinks = new ArrayList()
        for (cat in categoryList) {
            categoryControllers = ControllerData.where { category == cat }.list()
            for (controller in categoryControllers) {
                if (controller.validity.equals("INVALID")) {
                    badLinks.add(controller)
                    break;
                } else if (controller.validity..equals("UNSET")) {
                    def controllerName = controller.controllerPath.replace("/index", "")
                    def url = grailsLinkGenerator.link(controller: controllerName, action: 'index', absolute: true)
                    if (!testIfControllerIsValid(url)) {
                        badLinks.add(controller)
                        changeControllerValidity(controller, false)
                    } else {
                        changeControllerValidity(controller, true)
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

    def addCategories(CategoryFeatures... descriptions) {
        for (desc in descriptions) {
            def newCategory = AppCategory.findByName(desc.appName)
            if (!newCategory) {
                newCategory = new AppCategory(name: desc.appName, iconName: desc.iconName, adminOnly: desc.adminOnly)
                log.debug "Adding category ${newCategory.name}"
                newCategory.save()
            }
        }
    }

    def addControllerData(GrailsClass controller) {

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
                //CHANGE IF YOU EVER MAKE MORE CATEGORIES
                //Replace boolean adminOnly in dataField with a string with the category name
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
//Uses log.info to confirm what categories and applications are being added. feel free to comment out
    def bootStrapApplications() {

        //Must add categories by hand
        def adminOnly = new CategoryFeatures("Administration", "icon-cog", true)
        def availableApps = new CategoryFeatures("Available Applications", "icon-bar-chart", false)

        addCategories(adminOnly, availableApps)
        grailsApplication.controllerClasses.each { GrailsClass controller ->
            addControllerData(controller)
        }
        def dummy = new ControllerData(appName: "Dummy", appDescription: "Dummy", controllerPath: "Dummy/index", validity: ControllerData.IsValid.UNSET,
                category: AppCategory.findByName("Available Applications"), homePage: true)
        dummy.save()
    }
}
