package metridoc.admin



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(QuartzService)
class QuartzServiceTests {

    @Test
    void "test the minimum amx"() {
        assert 10 == service.getMax([:])
    }

    @Test
    void "if maximum is specified use that instead of minimum"() {
        assert 50 == service.getMax([max:50])
    }

    @Test
    void "if specified amount is larger than the max limit, return he limit"() {
        assert 100 == service.getMax([max: 1000])
    }

    @Test
    void "workflows are sorted by name" () {
        def workflows = [
            [
                name:"foo"
            ],
            [
                name:"bar"
            ],
            [
                name:"foobar"
            ]
        ]

        def ordered = service.listOrderedWorkflows([:], workflows)
        assert ordered == workflows

        ordered = service.listOrderedWorkflows([order:"asc"], workflows)
        assert ordered[0] == workflows[1]
        assert ordered[1] == workflows[0]
        assert ordered[2] == workflows[2]

        ordered = service.listOrderedWorkflows([order:"desc"], workflows)
        assert ordered[0] == workflows[2]
        assert ordered[1] == workflows[0]
        assert ordered[2] == workflows[1]
    }

}
