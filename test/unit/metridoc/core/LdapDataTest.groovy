package metridoc.core

import grails.test.mixin.Mock
import org.junit.Test

/**
 * Created with IntelliJ IDEA on 9/4/13
 * @author Tommy Barker
 */
@Mock(LdapData)
class LdapDataTest {

    @Test
    void "make sure that LdapData is valid when groupSearch is not specified"() {
        new LdapData(
                userSearchBase: "foo",
                rootDN: "foo",
                userSearchFilter: "foo",
                server: "foo",
                managerDN: "foo"
        ).save(failOnError: true)
    }
}
