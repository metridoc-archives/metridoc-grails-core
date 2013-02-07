package metridoc.core

import groovy.sql.Sql
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType

import java.sql.BatchUpdateException

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/16/12
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
class MetridocJobTest {

    def helper = new MetridocJobTestHelper()

    @Before
    void "add table to embedded data source"() {
        def sql = new Sql(helper.dataSource)
        sql.execute("create table foo (bar int unique)")
        sql.execute("create table bar (bar int)")
        sql.execute("create table foobar (bar int)")

        sql.execute("insert into bar values (10)")
        sql.execute("insert into bar values (10)")
    }

    @After
    void "shutdown embedded data source"() {
        try {
            helper.dataSource.shutdown()
            helper.refreshThreadLocalVariables()
        } catch (Exception e) {
            //do nothing
        }

    }

    @Test
    void "test routing failures"() {
        try {
            helper.runRoute helper.routeFailure
            assert false: "exception should have occurred"
        } catch (BatchUpdateException e) {

        }
    }

    @Test
    void "the job execution facade has a job key that is equal to the class"() {
        assert "metridoc.core.MetridocJobTestHelper" == helper.buildJobContextFacade().jobDetail.key.name

    }

    @Test
    void "the job execution facade has a trigger key equal to manual-cli"() {
        assert "manual-cli" == helper.buildJobContextFacade().trigger.key.name

    }

    @Test
    void "if target is not supplied, then target is null in job data map"() {
        assert null == helper.buildJobContextFacade().trigger.jobDataMap.target
    }

    @Test
    void "when profiling, the properties should still propogate to the routing"() {
        //just run the helper, no errors should occur
        helper.doExecute()
        assert helper.doExecuteRouteRan
    }

    @Test
    void "checking that depends within an imported target can run (see METCORE-188)"() {
        helper.includeTargets(MetridocJobTestTargetHelper)
        helper.depends("bar")
    }

    @Test
    void "checking that property propogation works properly to routes in imported targets"() {
        helper.includeTargets(MetridocJobTestTargetHelper)
        helper.depends("doRoute")
    }

    @Test
    void "profile throws exception if job was manually interupted"() {
        helper.interupt()
        try {
            helper.profile("do something") {

            }
            assert false : "exception should have occurred"
        } catch (JobInteruptionException e) {
        }
    }
}

class MetridocJobTestHelper extends MetridocJob {
    EmbeddedDatabase dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
    boolean doExecuteRouteRan = false
    /**
     * put the closure here to force the helper as the closure owner
     */
    def routeFailure = {
        from("sqlplus:bar?dataSource=dataSource").to("sqlplus:foo?dataSource=dataSource")
    }

    /**
     * we were having troubles when profiling a route.  The properties of the underlying class were not propogating to the
     * route
     */
    def doExecute() {
        profile("profiling the route") {
            runRoute {
                from("direct:start").process {
                    doExecuteRouteRan = true
                }
                from("sqlplus:bar?dataSource=dataSource").to("sqlplus:foobar?dataSource=dataSource")
            }
        }
    }
}

class MetridocJobTestTargetHelper extends Script {

    @Override
    Object run() {
        target(foo: "runs foo") {

        }

        target(bar: "runs bar") {
            depends("foo")
        }

        target(doRoute: "runs a basic route to test that property propogation works properly") {
            runRoute {
                from("sqlplus:bar?dataSource=dataSource").to("sqlplus:foobar?dataSource=dataSource")
            }
        }
    }
}