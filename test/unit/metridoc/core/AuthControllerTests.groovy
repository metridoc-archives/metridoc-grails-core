package metridoc.core

import grails.test.mixin.TestFor
import org.junit.Test

@SuppressWarnings("GroovyAccessibility")
@TestFor(AuthController)
class AuthControllerTests {

    void loadParameters() {
        params << [username: "joe", targetUri: "http://foo.com"]
    }

    @Test
    void "testing the return values of getModel"() {
        loadParameters()
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
        loadParameters()
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
