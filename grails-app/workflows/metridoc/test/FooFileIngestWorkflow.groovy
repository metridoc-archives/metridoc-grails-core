package metridoc.test

import metridoc.core.FileToBatchIngestor
import org.apache.commons.lang.SystemUtils

target(runFooFileIngest: "ingests foo files"){
    new FileToBatchIngestor().ingest {
        def directory = SystemUtils.USER_HOME + "/.metridoc/files/foo"
        files = new File(directory).listFiles()
        println files
        batchSize = 1

        //demonstrate filtering
        parseLine = {line, row ->
            if(row % 2) {
                return line.split()
            }

            return null
        }

        storeBatch = {
            println it
        }
    }
}




