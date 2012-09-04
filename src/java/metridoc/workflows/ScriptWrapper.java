package metridoc.workflows;

import groovy.lang.Script;
import metridoc.dsl.JobBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/3/12
 * Time: 10:50 AM
 */
public class ScriptWrapper extends Script {

    private Script wrappedScript;

    public void setWrappedScript(Script wrappedScript) {
        this.wrappedScript = wrappedScript;
    }

    @Override
    public Object run() {
        JobBuilder.isJob(wrappedScript);
        return wrappedScript.run();
    }
}
