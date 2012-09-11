package metridoc.core

import org.slf4j.LoggerFactory
import org.slf4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/11/12
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
class FileToBatchIngestor extends Ingestor{

    public static final Logger log = LoggerFactory.getLogger(FileToBatchIngestor)
    Closure retrieveFiles
    int previewLines = 0
    int maxFiles = 5

    def iterate = {file ->
        try {
            file.withInputStream {InputStream stream ->
                def batch = []
                stream.eachLine("utf-8") {row, line ->
                    def parsedLine = parseLine.call(row, line)
                    if (parsedLine) {
                        batch.add(parsedLine)
                    }

                    if(batch.size() >= batchSize && !previewLines) {
                        def clonedBatch = []
                        clonedBatch.addAll(batch)
                        storeBatch.call(clonedBatch)
                        batch.clear()
                    }

                    if(previewLines){
                        if(row < previewLines) {
                            previewLine.call(line)
                        }
                    }
                }

                if(batch) {
                    storeBatch.call(batch)
                }
            }
        } catch (Throwable throwable) {
            rollbackFile.call(file, throwable)
        }
    }

    int batchSize = 50
    Closure rollbackFile
    Closure parseLine
    Closure storeBatch
    Closure previewLine = {
        log.info(it)
    }

    def doIngest() {
        prepareClosures()
        def files = retrieveFiles.call()
        def maxFiles = maxFilesToProcess
        (1..maxFiles).each {index ->
            iterate(files[index])
        }
    }

    def prepareClosures() {
        prepareClosure(this.previewLine)
        prepareClosure(this.retrieveFiles)
        prepareClosure(this.parseLine)
        prepareClosure(this.rollbackFile)
        prepareClosure(this.storeBatch)
    }

    def getMaxFilesToProcess() {
        def files = retrieveFiles ? retrieveFiles.call() : null
        if(files) {
            def size = files.size()
            if(size) {
                return Math.min(maxFiles, size)
            } else {
                return 1
            }
        } else {
            return 0;
        }
    }
}
