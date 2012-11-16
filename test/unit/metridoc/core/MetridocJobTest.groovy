package metridoc.core

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/16/12
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
class MetridocJobTest {

    def helper = new MetridocJobTestHelper()

    @Test
    void "the job execution facade has a job key that is equal to the class"() {
        assert "metridoc.core.MetridocJobTestHelper" == helper.buildJobContextFacade().jobDetail.key.name

    }

    @Test
    void "the job execution facade has a trigger key equal to manual-cli"() {
        assert "manual-cli" == helper.buildJobContextFacade().trigger.key.name

    }

    @Test
    void "without specifying the target, the target in the job data map is default"() {
        assert "default" == helper.buildJobContextFacade().trigger.jobDataMap.target
    }

}

class MetridocJobTestHelper extends MetridocJob {

}