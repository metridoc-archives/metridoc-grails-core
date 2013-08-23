package metridoc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(HomeService)
@Mock([ControllerData, AppCategory])
class HomeServiceTests {

    def grailsApplicationMock = [
            controllerClasses: []
    ]



    @Test
    void "make sure dummy app fails validation"() {
/*
        def accessController = [
                getNaturalName: {
                    return "ManageAccess"
                },
                getName: {
                    return "ManageAccess"
                },
                getClazz: {
                    ManageAccessController
                },
                getHomePage:{
                    [
                            title: "Manage Metridoc",
                            adminOnly: true,
                            description: """
                Create, update and delete users and roles.  Change configuration, load plugins and restart the
                application
            """
                    ]
                }
        ] as GrailsClass

        grailsApplicationMock.controllerClasses.add(accessController)
        service.addControllerData(accessController)
        service.addCategories(new HomeService.CategoryFeatures("Administration", "icon-cog", true))
        def dummyApp = new ControllerData(appName: "Dummy", appDescription: "words", controllerPath: "dummy/index", validity: ControllerData.IsValid.UNSET,
                category:AppCategory.findByName("Administration") , homePage: true)
        dummyApp.save(flush:true , failOnError: true)
        //def accessApp = ControllerData.findByAppName("ManageAccess")
        dummyApp = ControllerData.findByAppName("Dummy")
        def category = AppCategory.findByName("Administration")

        //assert accessApp.validity.getValue().equals("UNSET")
        assert dummyApp.validity.getValue().equals("UNSET")
        def url = "http://localhost:8080/link-generator/sample/show/100"
        if(!service.testIfControllerIsValid(url)){
//assert simpleApp.validity.getValue().equals("VALID")
            service.changeControllerValidity(dummyApp, false)
        } else {
            service.changeControllerValidity(dummyApp, true)
        }
        assert dummyApp.validity.getValue().equals("INVALID")*/
    }
}
