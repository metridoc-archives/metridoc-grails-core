package metridoc.ingestors

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/5/12
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
class LogFileToGorm {
    def directory
    Closure fileFilter
    def gormClass
    def sessionFactory
    int batchSize
    def fileHandler
    int filesPerRun
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    def run(){

    }

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
