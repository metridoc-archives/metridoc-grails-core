package metridoc.core

class ControllerData {

    static belongsTo = [category: AppCategory]
    String controllerPath
    String appName
    String appDescription
    Boolean homePage = false
    static constraints = {
        controllerPath(nullable: false, blank: false, unique: true)
        appName(nullable: false, blank: false)
        appDescription(nullable: false, blank: false)
        category(nullable: false)
    }
}
