package metridoc.workflows

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/22/12
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
class WorkflowArtefactHandlerTest {

    @Test
    void "test that a script can be a workflow artifact"() {
        assert new WorkflowArtefactHandler().isArtefactClass(FooWorkflow.class)
    }

    @Test
    void "test that any class must be a script to be a workflow"() {
        assert !new WorkflowArtefactHandler().isArtefactClass(BarWorkflow.class)
    }

}

class FooWorkflow extends Script {

    @Override
    Object run() {
        //do nothing
    }
}

class BarWorkflow {
    def run() {

    }
}
