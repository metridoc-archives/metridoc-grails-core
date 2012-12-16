package metridoc.core

import static org.junit.Assert.*
import org.junit.*
import grails.util.Holders

class MetridocJobIntTests {



    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testSomething() {
        def applicationContext = Holders.applicationContext
        def job = applicationContext."metridoc.test.BarJob"
        //making sure di actually happened
        assert job.dataSource
        //making sure that new instances are different
        assert job != applicationContext."metridoc.test.BarJob"
        //testing that di works properly
        assert job.dataSource == applicationContext."metridoc.test.BarJob".dataSource
    }
}
