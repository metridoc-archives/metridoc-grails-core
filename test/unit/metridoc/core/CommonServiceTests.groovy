package metridoc.core



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CommonService)
class CommonServiceTests {

    def configMockWithUsername = [
        config: [
                grails: [
                        mail: [
                                username: "foo"
                        ]

                ]
        ]
    ]

    def configMockWith_NO_Username = [
        config: [

        ]
    ]

    @Test
    void "if the property grails.mail.username is not set, then email service is NOT configured"() {
        assert !service.doEmailIsConfigured(configMockWith_NO_Username)
        assert !service.doEmailIsConfigured(configMockWithUsername)
    }
}
