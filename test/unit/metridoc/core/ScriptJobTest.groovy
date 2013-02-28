package metridoc.core

import org.junit.Test
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.quartz.Trigger

class ScriptJobTest {

    @Test
    void "make sure the config gets passed to the binding"() {
        Trigger trigger = [
                getJobDataMap: {
                    def configObject = new ConfigObject()
                    configObject.put("foo", "bar")
                    configObject.put("bar", "foo")

                    new JobDataMap(config:configObject)
                }
        ] as Trigger

        def jobExecutionContext = [
                getTrigger: {
                    trigger
                }
        ] as JobExecutionContext

        def foo = new FooScript()
        def scriptJob = new ScriptJob(foo)
        scriptJob.execute(jobExecutionContext)
        assert jobExecutionContext == foo.jobExecutionContext
        assert "bar" == foo.foo
        assert "foo" == foo.bar
    }

}

class FooScript extends Script {

    @Override
    Object run() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}