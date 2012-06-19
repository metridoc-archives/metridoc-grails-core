/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.workflows

import java.sql.BatchUpdateException
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource
import metridoc.camel.builder.ManagedRouteBuilder
import metridoc.camel.context.MetridocCamelContext
import metridoc.plugins.impl.iterators.DelimitedLineIterator
import metridoc.plugins.impl.iterators.IteratorFactory
import metridoc.plugins.iterators.IteratorCreator
import metridoc.plugins.sql.SqlPlus
import metridoc.utils.CamelUtils
import metridoc.utils.SystemUtils
import metridoc.workflows.mapper.MessageTransformer
import metridoc.workflows.mapper.impl.BasicListToMapTransformer
import metridoc.workflows.validation.ValidationErrorHandler
import metridoc.workflows.validation.Validator
import metridoc.workflows.validation.impl.FileIngestorValidationErrorHandler
import metridoc.workflows.validation.impl.HashSqlValidator
import org.apache.camel.CamelContext
import org.apache.camel.Endpoint
import org.apache.camel.Exchange
import org.apache.camel.component.file.GenericFile
import org.apache.camel.component.file.GenericFileEndpoint
import org.apache.camel.component.file.GenericFileFilter
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.model.language.ConstantExpression
import metridoc.camel.builder.ScheduledPollEndpointWrapper

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 3/8/12
 * Time: 12:17 AM
 * To change this template use File | Settings | File Templates.
 */
class LoadFileIntoTable extends ManagedRouteBuilder {
    public static final String DEFAULT_WORKING_DIRECTORY = "${SystemUtils.METRIDOC_HOME}${SystemUtils.FILE_SEPARATOR}files"
    public static final String DEFAULT_ROUTE_ID = "loadFileIntoTable"
    String routeId = "loadFileIntoTable"
    String delimiter
    String workingDirectory
    CamelContext camelContext
    IteratorCreator iteratorCreator
    String fileNameFilter
    Closure fileFilter
    Endpoint fromEndpoint
    /**
     * the uri used as a camel endpoint
     */
    String workingDirectoryUri
    int batchSize = 5000
    boolean parallel = true
    int maxNumberOfFiles = 1
    String loadingTable
    String loadingTableSql
    DataSource dataSource
    Integer recordWidth
    SqlPlus sqlPlus
    String projectName
    Integer previewLines = Integer.MAX_VALUE
    int previewOffset = 0
    long waitForLocalFilePoll = 3000L
    int updateAt = 10000
    int currentEndOfLine
    boolean deleteLoadingFileWhenDone = true
    String moveLoadingFileWhenDone
    String moveLoadingFileOnError
    boolean noop = false
    ValidationErrorHandler validationErrorHandler
    private List<String> fileList = []
    Map transformationMap
    MessageTransformer transformer
    Map validatorMap
    Validator validator
    int delimitTill = 0
    private int currentUpdate = updateAt
    private int currentSize = 0
    private String currentFile
    private Long startTime
    Endpoint endpoint
    private AtomicInteger lineCounter = new AtomicInteger(0)

    Validator getValidator() {
        if (validator) {
            return validator
        }

        validator = new HashSqlValidator(validationMap: validatorMap)
    }

    MessageTransformer getTransformer() {

        if (transformer) {
            return transformer
        }

        transformer = new BasicListToMapTransformer(parser: transformationMap)
    }

    ValidationErrorHandler getValidationErrorHandler() {
        if (validationErrorHandler) {
            return validationErrorHandler
        }

        validationErrorHandler = new FileIngestorValidationErrorHandler(dataSource: getDataSource())
    }

    Endpoint getNewEndpoint() {
        endpoint = null
        return getEndpoint()
    }

    Endpoint getEndpoint() {

        if (endpoint) {
            return endpoint
        }

        def camelContext = getCamelContext()
        Endpoint wrappedEndpoint = camelContext.getEndpoint(getWorkingDirectoryUri())

        if (wrappedEndpoint instanceof GenericFileEndpoint) {
            wrappedEndpoint.filter = new FileFilter(fileFilter: fileFilter, fileNameFilter: fileNameFilter)
            wrappedEndpoint.delete = deleteLoadingFileWhenDone
            if (moveLoadingFileWhenDone) {
                wrappedEndpoint.move = new ConstantExpression(moveLoadingFileWhenDone)
            }
            wrappedEndpoint.moveFailed = moveLoadingFileOnError
            wrappedEndpoint.noop = noop
        }

        endpoint = new ScheduledPollEndpointWrapper(scheduledPollEndpoint: wrappedEndpoint)
    }

    IteratorCreator getIteratorCreator() {
        if (iteratorCreator != null) {
            return iteratorCreator
        }

        if (delimiter) {
            iteratorCreator = new DelimitedLineIterator(delimiter: delimiter, delimitTill: Math.max(0, recordWidth))
            return iteratorCreator
        }

        iteratorCreator = new IteratorFactory()
    }

    CamelContext getCamelContext() {
        if (camelContext) {
            return camelContext
        }

        camelContext = MetridocCamelContext.instance()
    }

    String getWorkingDirectoryUri() {
        if (workingDirectoryUri) {
            return workingDirectoryUri
        }
        workingDirectoryUri = "file://${getWorkingDirectory()}?maxMessagesPerPoll=${maxNumberOfFiles}"
    }

    String getWorkingDirectory() {
        if (workingDirectory) {
            return workingDirectory
        }

        def metridocHome = SystemUtils.METRIDOC_HOME
        def slash = SystemUtils.FILE_SEPARATOR

        if (projectName) {
            workingDirectory = "${metridocHome}${slash}${projectName}${slash}files"
        } else {
            workingDirectory = DEFAULT_WORKING_DIRECTORY
        }

        return workingDirectory
    }

    SqlPlus getSqlPlus() {
        if (sqlPlus) {
            return sqlPlus
        }

        sqlPlus = new SqlPlus(dataSource)
    }

    String getLoadingTable() {
        if (loadingTable) {
            return loadingTable
        }

        loadingTable = "${projectName}_loading"
    }

    String getLoadingTableSql() {
        if (loadingTableSql) {
            return loadingTableSql
        }

        loadingTableSql = getLoadingTable()
    }

    Closure aggregateRecords = {ProcessorDefinition definition ->
        definition.aggregateBody(batchSize).forceCompletionOnStop()
    }

    Closure splitContents = {ProcessorDefinition definition ->

        def currentDefinition = definition.process {Exchange exchange ->
            def iterator = getIteratorCreator().create(exchange.in.body, exchange.in.headers)
            exchange.in.body = new IteratorWrapper(
                iterator: iterator,
                limit: previewLines,
                offset: previewOffset
            )
        }.split(body()).streaming()

        if (parallel) {
            currentDefinition = currentDefinition.parallelProcessing()
        }

        currentDefinition.process {
            lineCounter.incrementAndGet()
        }

        def loggedWarning = false
        currentDefinition = currentDefinition.filter {
            if (routeException) {
                if (!loggedWarning) {
                    log.warn("splitter is ignoring the rest of the messages since there was an exception")
                    loggedWarning = true
                }
                return false
            }
            return true
        }

        return currentDefinition
    }

    Closure loadData = {ProcessorDefinition definition ->
        definition.process {Exchange exchange ->
            def records = exchange.in.body

            def sqlPlus = getSqlPlus()
            try {
                sqlPlus.runBatch(loadingTableSql, records)
            } catch (BatchUpdateException ex) {
                log.warn("exception occurred while trying to insert a batch, will process the records line by line to find the culprit")

                if (records instanceof List) {
                    Map record
                    try {
                        records.each {
                            record = it
                            sqlPlus.runBatch(loadingTableSql, record)
                        }
                    } catch (SQLException sqlException) {
                        log.error("could not insert record with line {} from file {} because {}", record.line_num + previewOffset, record.source_file, sqlException.message)
                        throw sqlException
                    }
                } else {
                    throw ex
                }
            }
        }
    }

    Closure checkArrayWidth = {

        def isValid = true

        if (recordWidth) {
            try {
                def body = it.in.body
                assert recordWidth == body.size()
            } catch (AssertionError e) {
                log.warn(e.message)
                getValidationErrorHandler().handle(e, it)
                isValid = false
            }
        }

        return isValid
    }

    Closure parseData = {Exchange exchange ->
        transformer.transform(exchange)
    }

    Closure validateData = {Exchange record ->
        try {
            validator.validate(record.in.getBody(Map.class))
            return true
        } catch (AssertionError error) {
            log.warn("VALIDATION ERROR: {}", error.message)
            validationErrorHandler.handle(error, record)
            return false
        }
    }

    /*
            TODO: consider using a poll enrich call to handle this instead?  This is just a mess
     */

    void run() {
        lineCounter = new AtomicInteger(0)
        deleteCamelDirectory()
        def camelContext = getCamelContext()
        camelContext.addRoutes(this)
        CamelUtils.waitTillDone(camelContext)
        camelContext.stopRoute(routeId)
        camelContext.removeEndpoints(getEndpoint().getEndpointUri())
        if (this.routeException) {
            throw routeException
        }
        this.currentSize = 0
        this.currentUpdate = updateAt
        this.startTime = null
        deleteCamelDirectory()
        validateLineCount()
    }

    void validateLineCount() {
        String validationErrorTable = FileIngestorValidationErrorHandler.DEFAULT_VALIDATION_TABLE
        def loadingTable = getLoadingTable()
        int loadingTableTotal = getSqlPlus().firstRow("select count(*) as total from ${loadingTable}" as String).total
        int validationErrorTotal = getSqlPlus().firstRow("select count(*) as total from ${FileIngestorValidationErrorHandler.DEFAULT_VALIDATION_TABLE} where file_name = '${currentFile}'" as String).total
        int totalLinesProcessed = loadingTableTotal + validationErrorTotal

        try {
            assert totalLinesProcessed == lineCounter.get(): "Total lines processed was ${totalLinesProcessed} but should have been ${lineCounter.get()}"
        } catch (AssertionError error) {
            log.error("rolling back loading table and validation table due to line mismatch")
            getSqlPlus().execute("truncate ${loadingTable}" as String)
            getSqlPlus().execute("delete from ${validationErrorTable} where file_name = '${currentFile}'" as String)
            throw error
        }

        log.info("${loadingTableTotal} lines were inserted into the loading table ${loadingTable} for file ${currentFile}")
        log.info("${validationErrorTotal} lines were inserted into the validation error table ${validationErrorTable} for file ${currentFile}")
    }

    private void deleteCamelDirectory() {
        if (deleteLoadingFileWhenDone) {
            def camel = new File(workingDirectory + "/.camel")
            if (camel.exists()) {
                camel.eachFile {
                    it.deleteOnExit()
                }
            }
        }
    }

    @Override
    void doConfigure() {
        log.info("adding load file to loading table route")
        ProcessorDefinition currentDefinition = from(getNewEndpoint()).routeId(routeId).process {
            def fileName = it.in.getHeader(Exchange.FILE_NAME_ONLY, String.class)
            log.info "processing file {}", fileName
            fileList.add(fileName)
            currentFile = fileName
        }

        currentDefinition = splitContents.call(currentDefinition)
        currentDefinition = currentDefinition.filter(checkArrayWidth).process(parseData).filter(validateData)
        currentDefinition = aggregateRecords.call(currentDefinition)
        currentDefinition = loadData.call(currentDefinition)

        currentDefinition.process {
            updateOperations(it)
        }
    }

    private synchronized void updateOperations(Exchange exchange) {
        def body = exchange.in.getBody(List)
        def fileName = exchange.in.getHeader(Exchange.FILE_NAME_ONLY)
        def now = new Date().time

        if (startTime == null) {
            startTime = now - 1 //just to make sure now is always greater than startTime, ie no divide by zero
        }
        currentSize += body.size()

        if (currentSize > currentUpdate) {
            def elapsedTimeInSeconds = (now - startTime) / 1000
            int operationsPerSecond = currentSize / elapsedTimeInSeconds
            log.info("processed {} operations since start [operations per second: {}, currentFile: {}] ", currentSize, operationsPerSecond, fileName)
            currentUpdate += updateAt
        }
    }
}

class FileFilter implements GenericFileFilter {
    String fileNameFilter
    Closure fileFilter

    boolean accept(GenericFile file) {
        boolean nameOk = true
        boolean fileOk = true

        if (fileNameFilter != null) {
            nameOk = file.fileNameOnly =~ fileNameFilter
        }

        if (fileFilter != null) {
            fileOk = fileFilter.call(file)
        }

        return nameOk && fileOk
    }
}

class IteratorWrapper implements Iterator {

    Iterator iterator
    int offset = 0
    int limit = Integer.MAX_VALUE
    int line = 0


    boolean hasNext() {
        if (line < offset) {
            getToOffset()
        }

        if (line >= limit + offset) {
            return false
        }
        return iterator.hasNext()
    }

    Object next() {
        if (line < offset) {
            getToOffset()
        }
        if (line >= limit + offset) {
            throw new NoSuchElementException()
        }
        line++
        iterator.next()
    }

    void remove() {
        throw new UnsupportedOperationException("not supported")
    }

    private void getToOffset() {
        if (offset > 1) {
            (1..offset).each {
                if (iterator.hasNext()) {
                    iterator.next()
                }
                line++
            }
        }
    }
}


