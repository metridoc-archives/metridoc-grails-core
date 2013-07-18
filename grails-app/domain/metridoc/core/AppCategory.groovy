package metridoc.core

class AppCategory {

    static hasMany = [controllers: ControllerData]

    String name
    String iconName
    Boolean adminOnly = false
    //If true, overrides ControllerData.homepage   If false, doesn't override

    static constraints = {
        name(nullable: false, blank: false, unique: true)
        iconName(nullable: true, blank: true)
        adminOnly(nullable: false)
    }
}
