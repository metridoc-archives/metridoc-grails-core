package metridoc.core

import grails.test.mixin.TestFor
import org.junit.Before
import org.junit.Test

@TestFor(AuthController)
class AuthControllerTests {

    @Before
    void loadParameters() {
        params << [username: "joe", targetUri:"http://foo.com"]
    }

    @Test
    void "testing the return values of getModel"() {
        def model = controller.getModel()
        assert !model.rememberMe
        assert "joe" == model.username
        assert "http://foo.com" == model.targetUri
        controller.params << [rememberMe: true]
        model = controller.getModel()
        assert model.rememberMe
    }

    @Test
    void "test that login returns the same values as model"() {
        assert controller.getModel() == controller.login()
    }

    @Test
    void "test that index chains to login"() {
        controller.index()
        assert response.redirectedUrl == '/auth/login'
    }

    @Test
    void "unauthorized returns nothing"() {
        assert null == controller.unauthorized()
    }
}
