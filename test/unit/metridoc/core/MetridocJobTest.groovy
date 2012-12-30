package metridoc.core

import org.junit.Test
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.junit.Before
import org.junit.After
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import groovy.sql.Sql
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
        def routeBuilder
        try {

            routeBuilder = helper.runRoute helper.routeFailure

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
}

class MetridocJobTestHelper extends MetridocJob {
    EmbeddedDatabase dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()

    /**
     * put the closure here to force the helper as the closure owner
     */
    def routeFailure = {
        from("sqlplus:bar?dataSource=dataSource").to("sqlplus:foo?dataSource=dataSource")
    }
}