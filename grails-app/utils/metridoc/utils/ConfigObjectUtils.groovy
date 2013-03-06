package metridoc.utils

import org.apache.commons.lang.SerializationException
import org.apache.commons.lang.SerializationUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/6/13
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
class ConfigObjectUtils {

    /**
     * Best effort to deep clone a config object.
     * @param configObject
     * @return
     */
    static ConfigObject clone(ConfigObject configObject) {
        def copy = new ConfigObject()
        cloneHelper(configObject, copy)
        return copy
    }

    private static cloneHelper(Map original, Map copy) {
        original.each {key, value ->
            if (value instanceof Map) {
                copy[key] = new LinkedHashMap()
                cloneHelper(value, copy[key] as Map)
            } else if (value instanceof Serializable) {
                try {
                    copy[key] = SerializationUtils.clone(value)
                } catch (SerializationException e) {
                    copy[key] = value
                }
            } else if (value instanceof Cloneable) {
                copy[key] = value.clone()
            } else {
                copy[key] = value
            }
        }
    }
}
