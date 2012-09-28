package metridoc.iterators

import org.junit.Test
import org.springframework.core.io.ClassPathResource
import org.junit.After

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/28/12
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
class XlsIteratorCreatorTest {

    def file = new ClassPathResource("metridoc/iterators/locations.xls").file
    def iterator = new XlsIterator(inputStream: file.newInputStream())

    @After
    void cleanup() {
        iterator.close()
    }

    @Test
    void "testing a location file we use in another program where we found errors"() {
        def row = iterator.next()
        assert "LOCATION_ID" == row.get(0)
    }

    @Test
    void "testing cell conversion issues we were seeing with formulas"() {
        iterator.next()
        def row = iterator.next()
        assert 1 == row.get(0)
    }
}
