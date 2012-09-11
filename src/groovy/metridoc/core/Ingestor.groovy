package metridoc.core

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/11/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class Ingestor {

    def ingest(Closure closure) {
        prepareClosure(closure)
        closure.call()
        this.doIngest()
    }

    def prepareClosure(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_FIRST
        }
    }

    abstract doIngest()
}