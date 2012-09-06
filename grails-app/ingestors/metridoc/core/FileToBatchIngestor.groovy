package metridoc.core

abstract class FileToBatchIngestor {

    int batchSize = 500
    String directory
    Closure handleFile
    Closure fileFilter
    Closure handleBatch
    List currentBatch = []

    def execute() {
        def fileList = retrieveFileList(directory)
        handleFileList(fileList)
    }

    def retrieveFileList(directory) {
        def fileList = []

        new File(directory).eachFileRecurse {file ->
            if(fileFilter(file)) {
                fileList.add(file)
            }
        }

        return fileList
    }

    def handleFileList(fileList) {
        handleFile.delegate = this
        fileList.each {
            handleFile(it)
            finishFile()
        }
    }

    def addLine(Map line) {
        currentBatch.add(line)
        if(currentBatch.size() >= batchSize) {
            handleBatch(currentBatch)
        }
    }

    def finishFile() {
        handleBatch(currentBatch)
        currentBatch.clear()
    }
}