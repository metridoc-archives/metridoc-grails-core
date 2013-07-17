package metridoc.core

import org.codehaus.groovy.grails.commons.GrailsClassUtils

class HomeService {

    static final HOME_DATA_FIELD = "homePage"
    def grailsApplication

    //METADATA METHOD
    /**
     *
     * @return indication whether or not a controller is an application controller.  These include all controllers
     * that have no <code>homePage</code> variable, or have a <code>homePage</code> variable but do not have the
     * <code>adminOnly</code> variable set to true
     *
     */

    /*
    def getApplicationControllers() {
        return getControllerMetaData {
            def isNull = it == null
            def notAdmin = isNull ? true : !it.adminOnly
            def shouldInclude = isNull ? true : !it.exclude

            return isNull || (notAdmin && shouldInclude)
        }
    }
    */
    /**
     * returns all administrative controllers
     */

    /*
    def getAdminControllers() {
        return getControllerMetaData {
            return it != null && it.adminOnly
        }
    }
    */

    /**
     *
     * @param closure indicates whether the controller information should be collected or not
     */
    /*
    def getControllerMetaData(Closure closure) {
        def result = [:] as TreeMap
        def homePageConfig = grailsApplication.mergedConfig.metridoc.homePage
        grailsApplication.controllerClasses.each {controller ->
            def dataField = GrailsClassUtils.getStaticFieldValue(controller.clazz, HOME_DATA_FIELD)
            def homePageMetaData = dataField ?: [:]
            def uncapControllerName = StringUtils.uncapitalise(controller.name)
            def homePageInfoFromConfig = homePageConfig[uncapControllerName] ? homePageConfig[uncapControllerName] : [:]
            homePageMetaData.putAll(homePageInfoFromConfig)
            if (closure.call(homePageMetaData)) {
                if (homePageMetaData) {
                    if(!homePageMetaData.title) {
                        homePageMetaData.title = controller.naturalName
                    }
                    homePageMetaData.put("controllerName", controller.name)
                    def title = homePageMetaData.title
                    result.put(title, homePageMetaData)
                }
            }
        }

        return result.values()
    }
    */

    /**
     *
     * @return indication whether or not a controller is an application controller.  These include all controllers
     * that have no <code>homePage</code> variable, or have a <code>homePage</code> variable but do not have the
     * <code>adminOnly</code> variable set to true
     *
     */

    /*
    def getApplicationControllers() {
        return getControllerMetaData {
            def isNull = it == null
            def notAdmin = isNull ? true : !it.adminOnly
            def shouldInclude = isNull ? true : !it.exclude

            return isNull || (notAdmin && shouldInclude)
        }
    }
    */
    /**
     * returns all administrative controllers
     */

    /*
    def getAdminControllers() {
        return getControllerMetaData {
            return it != null && it.adminOnly
        }
    }
    */

    /**
     *
     * @return indication whether or not a controller is an application controller.  These include all controllers
     * that have no <code>homePage</code> variable, or have a <code>homePage</code> variable but do not have the
     * <code>adminOnly</code> variable set to true
     *
     */


    def getApplicationControllers() {
        def appControllers = ControllerData.where { homePage == true && category.adminOnly == false }.list()
        return appControllers
    }

    /**
     * returns all administrative controllers
     */


    def getAdminControllers() {
        def adminControllers = ControllerData.where { homePage == true && category.adminOnly == true }.list()
        return adminControllers
    }

    def getCategories() {
        def categories = AppCategory.where { adminOnly == false }.list()
        categories.addAll(AppCategory.where { adminOnly == true }.list())
        return categories
    }

//Uses log.info to confirm what categories and applications are being added. feel free to comment out
    def bootStrapApplications() {
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
