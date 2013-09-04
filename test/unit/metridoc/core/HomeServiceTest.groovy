package metridoc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Test

/**
 * @author Tommy Barker
 */
@Mock([ControllerData, AppCategory])
class HomeServiceTest {

    @Test
    void "test deleting bad links"() {
        def controllerNames = ["foo", "bar"]
        controllerNames.each {
            saveData(it)
        }

        saveData("baz")
        assert 3 == ControllerData.list().size()
        HomeService.deleteBadLinks(controllerNames)
        assert 2 == ControllerData.list().size()
    }

    static void saveData(String name) {
        new ControllerData(
                controllerPath: "$name/index.html",
                appName: name,
                appDescription: "blah",
                category: new AppCategory(name: "application"),
                validity: "VALID"
        ).save(failOnError: true)
    }
}
