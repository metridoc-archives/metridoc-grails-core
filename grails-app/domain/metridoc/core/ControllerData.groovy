package metridoc.core

class ControllerData {

    static belongsTo = [category: AppCategory]
    enum IsValid {
        UNSET("UNSET"), VALID("VALID"), INVALID("INVALID")
        final String value

        IsValid(String value) { this.value = value }

        String toString() { value }

        String getKey() { name() }
    }
    String controllerPath
    String appName
    String appDescription
    Boolean homePage = false
    IsValid validity
    static constraints = {
        controllerPath(nullable: false, blank: false, unique: true)
        appName(nullable: false, blank: false, unique: true)
        appDescription(nullable: false, blank: false)
        category(nullable: false)
    }
}
