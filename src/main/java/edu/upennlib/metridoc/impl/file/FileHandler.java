/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

import edu.upennlib.metridoc.FutureHandler;
import edu.upennlib.metridoc.MessageSender;
import edu.upennlib.metridoc.file.StreamHandler;
import edu.upennlib.metridoc.file.IteratorCreator;
import edu.upennlib.metridoc.utils.StateAssert;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tbarker
 */
public class FileHandler implements StreamHandler{

    private FileIteratorManager fileIteratorManager;
    private MessageSender messageSender;
    private FutureHandler futureHandler;
    private ReentrantLock lock = new ReentrantLock(true);
    private static Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    public FutureHandler getFutureHandler() {
        return futureHandler;
    }

    public void setFutureHandler(FutureHandler futureHandler) {
        this.futureHandler = futureHandler;
    }

    public FileIteratorManager getFileIteratorManager() {
        return fileIteratorManager;
    }

    public void setFileIteratorManager(FileIteratorManager fileIteratorManager) {
        this.fileIteratorManager = fileIteratorManager;
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    protected void validate() {

        StateAssert.notNull(fileIteratorManager,
                "a FileIteratorManager must be set on DefaultFileHandler before handle is called");
        StateAssert.notNull(messageSender,
                "a MessageSender must be set on DefaultFileHandler before handle is called");
        StateAssert.notNull(futureHandler,
                "a FutureHandler must be set on DefaultFileHandler before handle is called");
    }

    @Override
    public void handleStream(@Body InputStream inputStream, @Headers Map<String, Object> messageHeaders) {

        validate();
        try {
            lock.lock(); //TODO:maybe replace this with a memory check instead?
            String fileName = (String) messageHeaders.get(Exchange.FILE_NAME_ONLY);
            Validate.notEmpty(fileName,
                    "headers must contain the header " + Exchange.FILE_NAME_ONLY + " in order to process file");
            IteratorCreator iteratorCreator =
                    fileIteratorManager.getIteratorCreatorByFileExtension(getFileExtension(fileName));
            Iterator iterator = iteratorCreator.create(inputStream);

            while (iterator.hasNext()) {
                Object objectToSend = iterator.next();
                Future future = messageSender.asyncSendBodyAndHeadersToChannel(objectToSend, messageHeaders);
                futureHandler.submit(future, fileName);
            }

            futureHandler.finish(fileName);
        } catch (InterruptedException interruptedException) {
            LOGGER.warn(
                    "The container is likely shutting down but the file handler has not completed yet",
                    interruptedException);
        } finally {
            lock.unlock();
            IOUtils.closeQuietly(inputStream);
        }

    }

    protected Iterator getIterator(Map<String, Object> messageHeaders) {
        return null;
    }

    protected static String getFileExtension(String fileName){
        int index = fileName.lastIndexOf(".");
        Validate.isTrue(index > 0, fileName + " does not have a file extension");
        return fileName.substring(index + 1);
    }
}
