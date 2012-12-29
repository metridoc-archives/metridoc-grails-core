package metridoc.camel

import groovy.util.logging.Slf4j
import org.apache.camel.spi.Registry

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/28/12
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class CamelScriptRegistry implements Registry {
    int resolutionStrategy = Closure.OWNER_FIRST
    def owner
    def delegate
    private Map<String, Object> _propertiesMap = [:]

    Map<String, Object> getPropertiesMap() {
        if (_propertiesMap) return _propertiesMap

        switch (resolutionStrategy) {
            case Closure.DELEGATE_FIRST:
                loadPropertyMap(owner, delegate)
                break
            case Closure.DELEGATE_ONLY:
                loadPropertyMap(delegate)
                break
            case Closure.OWNER_FIRST:
                loadPropertyMap(delegate, owner)
                break
            case Closure.OWNER_ONLY:
                loadPropertyMap([owner] as Object[])
                break
            default:
                loadPropertyMap([owner] as Object[])
        }

        return _propertiesMap
    }

    private loadPropertyMap(Object... objects) {
        objects.each {
            if (it) {
                _propertiesMap.putAll(it.properties)
            }
        }
    }

    Object lookup(String name) {
        propertiesMap[name]
    }

    def <T> T lookup(String name, Class<T> type) {
        def o = lookup(name);

        try {
            if (o) {
                return type.cast(o);
            }
        } catch (ClassCastException ex) {
            log.debug "Could not convert object $o to ${type.name}, lookup will return null instead of the object value", ex
        }

        return null
    }

    def <T> Map<String, T> lookupByType(Class<T> type) {
        propertiesMap.findAll {
            lookup(it.key, type) //if it is null, it will be skipped
        }
    }
}
