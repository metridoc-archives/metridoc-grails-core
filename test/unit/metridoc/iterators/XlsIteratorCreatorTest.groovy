package metridoc.iterators

import org.junit.Test
import org.springframework.core.io.ClassPathResource

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/28/12
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
class XlsIteratorCreatorTest {

    @Test
    void "testing a location file we use in another program where we found errors"() {
        def file = new ClassPathResource("metridoc/iterators/locations.xls").file
        def iterator = new XlsIterator(inputStream: file.newInputStream())
        def row = iterator.next()

        assert "LOCATION_ID" == row.get(0)
    }
}
