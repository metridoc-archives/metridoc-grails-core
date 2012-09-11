package metridoc.core

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/11/12
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
class FileToBatchIngestorTest {

    @Test
    void """max files to process should be the minimum of file list size and maxFiles, if max files less than
        or equal to zero then file list size"""() {

        def ingestor = new FileToBatchIngestor()
        assert 0 == ingestor.maxFilesToProcess

        //no files available
        ingestor.maxFiles = 2
        assert 0 == ingestor.maxFilesToProcess

        ingestor.files = [1,2]
        ingestor.maxFiles = 3
        assert 2 == ingestor.maxFilesToProcess
    }
}
