package metridoc.core

class ControllerData {

    static belongsTo = [category: AppCategory]
    String controllerPath
    String appName
    String appDescription
    Boolean homePage = false
    String validity
    static constraints = {
        controllerPath(nullable: false, blank: false, unique: true)
        appName(nullable: false, blank: false, unique: true)
        appDescription(nullable: false, blank: false)
        category(nullable: false)
    }
}
