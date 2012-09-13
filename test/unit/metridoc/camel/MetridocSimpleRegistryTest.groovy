package metridoc.camel

import org.junit.Test
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.spi.Registry

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/13/12
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
class MetridocSimpleRegistryTest {

    @Test
    void "can set a backup registry for the registry"() {
        Binding binding = new Binding()
        Registry registry = new SimpleRegistry()
        binding.appCtx = [
            registry: registry
        ]
        registry.put("foo", "bar")
        Registry metridocRegistry = new MetridocSimpleRegistry(binding:binding)
        assert "bar" == metridocRegistry.lookup("foo")
        assert "bar" == metridocRegistry.lookupByType(String)["foo"]
    }
}
