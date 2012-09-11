package metridoc.core

import org.apache.camel.component.file.GenericFileFilter
import org.apache.camel.component.file.GenericFile
import org.apache.shiro.crypto.hash.Sha256Hash

class FileIngestorFilterService implements GenericFileFilter {

    def grailsApplication
    private _filtersByName

    @Override
    boolean accept(GenericFile file) {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    private compareBytes(GenericFile file, sha256) {
        def bytes = file.file.bytes
        assert bytes: "file must contain at 1 byte"
        new Sha256Hash(bytes).toHex() == sha256
    }

    boolean acceptFromFilter(GenericFile file) {
        for(filter in filtersByName.values()) {
            if(filter.call(file)) {
                return true
            }
        }

        return false
    }

    def getFiltersByName() {
        if(_filtersByName) {
            return _filtersByName
        }

        _filtersByName = getFiltersByName(grailsApplication.config)
    }

    private static filterFile(file, closure) {
        return closure.call(file)
    }

    private static getFiltersByName(ConfigObject config) {
        def result = [:]
        config.metridoc.files.each {
            def filter = it.value.filter
            if(filter && filter instanceof Closure) {
                result[it.key] = filter
            }
        }
        return result
    }
}
