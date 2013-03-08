package metridoc.core

import metridoc.test.FooScriptJob
import org.junit.Test
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.quartz.Trigger

class ScriptJobTest {
    def foo = new FooScriptJob()
    ScriptJob scriptJob = new ScriptJob(foo)
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

    @Test
    void "make sure the config gets passed to the binding"() {
        scriptJob.execute(jobExecutionContext)
        assert jobExecutionContext == foo.jobExecutionContext
        assert "bar" == foo.config.foo
        assert "foo" == foo.config.bar
    }

    @Test
    void argumentsAreAnArraySplitOnSpace() {
        scriptJob.arguments = "foo bar"
        scriptJob.execute(jobExecutionContext)
        assert 2 == foo.args.size()
    }
}

class FooScript extends Script {

    @Override
    Object run() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}