package metridoc.camel

import org.junit.Before
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/28/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
class CamelScriptRegistryTest {

    def registry = new CamelScriptRegistry()

    @Before
    void setupRegistry() {
        def c = new SampleClosure(new SampleOwner())
        c.delegate = new SampleDelegate()
        registry.closure = c
    }


    @Test
    void "propertiesMap is built on owner properties"() {
        assert "bar" == registry.propertiesMap.foo
    }

    @Test
    void "propertieMap is built on delegate properties"() {
        assert "foobar" == registry.propertiesMap.bar
    }

    @Test
    void "by default owner properties override delegate properties"() {
        assert "bar" == registry.propertiesMap.foo
    }

    @Test
    void "if set to delegate first, the delegate properties override owner properties"() {
        registry.closure.resolveStrategy = Closure.DELEGATE_FIRST
        assert "foo" == registry.propertiesMap.foo
    }

    @Test
    void "null is returned if the value is not the right type"() {
        assert "bar" == registry.lookup("foo", String)
        assert null == registry.lookup("foo", Integer)
    }

    @Test
    void "owner only has no delegate properties"() {
        registry.closure.resolveStrategy = Closure.OWNER_ONLY
        assert null == registry.lookup("delegateOnlyProperty")
    }

    @Test
    void "check that property map returns correct values based on required type"() {
        assert 4 == registry.lookupByType(String).size()
        assert 0 == registry.lookupByType(Integer).size()
    }

    @Test
    void "closure must not be null"() {
        registry.closure = null
        try {
            registry.propertiesMap
        } catch (AssertionError error) {
        }
    }
}

class SampleOwner {
    def foo = "bar"
    def ownerOnlyProperty = "owner"
}

class SampleDelegate {
    def foo = "foo"
    def bar = "foobar"
    def delegateOnlyProperty = "delegate"
}

class SampleClosure extends Closure {

    /**
     *  Constructor used when the "this" object for the Closure is null.
     *  This is rarely the case in normal Groovy usage.
     *
     * @param owner the Closure owner
     */

    SampleClosure(Object owner) {
        super(owner)
    }
}