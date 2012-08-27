package metridoc.admin

import grails.test.mixin.TestFor
import org.junit.Test
import org.quartz.Trigger

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(QuartzService)
class QuartzServiceTests {

    def workflows = [
        [
            name: "foo"
        ],
        [
            name: "bar"
        ],
        [
            name: "foobar"
        ]
    ]

    @Test
    void "test the minimum max"() {
        assert 10 == service.getMax([:])
    }

    @Test
    void "if maximum is specified use that instead of minimum"() {
        assert 50 == service.getMax([max: 50])
    }

    @Test
    void "if specified amount is larger than the max limit, return he limit"() {
        assert 100 == service.getMax([max: 1000])
    }

    @Test
    void "workflows are sorted by name"() {


        def ordered = service.listOrderedWorkflows([:], workflows)
        assert ordered == workflows

        ordered = service.listOrderedWorkflows([order: "asc"], workflows)
        assert ordered[0] == workflows[1]
        assert ordered[1] == workflows[0]
        assert ordered[2] == workflows[2]

        ordered = service.listOrderedWorkflows([order: "desc"], workflows)
        assert ordered[0] == workflows[2]
        assert ordered[1] == workflows[0]
        assert ordered[2] == workflows[1]
    }

    @Test
    void "workflows can handle offsets with max"() {

        def params = [
            order: "asc",
            offset: 1
        ]

        def workflowsList = service.listWorkflowsWithOffsetAndMax(params, workflows)
        assert 2 == workflowsList.size()
        workflowsList [0] == workflows[0]
        workflowsList [1] == workflows[2]

        params.max = 1
        workflowsList = service.listWorkflowsWithOffsetAndMax(params, workflows)
        assert 1 == workflowsList.size()
        workflowsList [0] == workflows[0]
    }
}
