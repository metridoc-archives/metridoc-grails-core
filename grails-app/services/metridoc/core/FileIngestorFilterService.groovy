package metridoc.core

import org.apache.camel.component.file.GenericFileFilter
import org.apache.camel.component.file.GenericFile

class FileIngestorFilterService implements GenericFileFilter {

    def grailsApplication
    private _filtersByName

    @Override
    boolean accept(GenericFile file) {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    boolean acceptFromFilter(GenericFile file) {

    }

    def getFiltersByName() {
        if(_filtersByName) {
            return _filtersByName
        }

        _filtersByName = getFiltersByName(grailsApplication.config)
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
