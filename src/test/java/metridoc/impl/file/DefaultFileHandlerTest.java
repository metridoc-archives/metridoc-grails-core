/**
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metridoc.impl.file;

import metridoc.impl.file.FileIteratorManager;
import metridoc.impl.file.FileHandler;
import metridoc.FutureHandler;
import java.io.IOException;
import org.apache.camel.Exchange;
import org.junit.Ignore;
import org.junit.Assert;
import java.util.concurrent.Future;
import java.util.Map;
import metridoc.MessageSender;
import java.util.HashMap;
import java.util.Iterator;
import java.io.InputStream;
import metridoc.file.IteratorCreator;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */
public class DefaultFileHandlerTest extends Assert{

    private FileIteratorManager fileIteratorManager;
    private IteratorCreator iteratorCreator;
    private Iterator iterator;
    private InputStream inputStream;
    private MessageSender messageSender;
    private FutureHandler futureHandler;

    @Test
    public void ifFileNameNotInHeaderThrowException() {
        FileHandler fileHandler = new FileHandler();
        fileHandler.setFileIteratorManager(createMock(FileIteratorManager.class));
        fileHandler.setFutureHandler(createMock(FutureHandler.class));
        fileHandler.setMessageSender(createMock(MessageSender.class));

        try {
            fileHandler.handleStream(createMock(InputStream.class), new HashMap<String, Object>());
            fail("exception should have occurred");
        } catch (IllegalArgumentException e) {
            assertEquals("headers must contain the header " + Exchange.FILE_NAME_ONLY + " in order to process file",
                    e.getMessage());
        }
    }

    private FileHandler fileHandler() throws Exception {
        iterator = createMock(Iterator.class);
        inputStream = createMock(InputStream.class);
        fileIteratorManager = createMock(FileIteratorManager.class);
        iteratorCreator = createMock(IteratorCreator.class);
        messageSender = createMock(MessageSender.class);
        futureHandler = createMock(FutureHandler.class);
        expect(fileIteratorManager.getIteratorCreatorByFileExtension("xls")).andReturn(iteratorCreator);
        expect(iteratorCreator.create(inputStream)).andReturn(iterator);
        inputStream.close();
        expectLastCall().once();
        FileHandler fileHandler = new FileHandler();
        fileHandler.setFileIteratorManager(fileIteratorManager);
        fileHandler.setMessageSender(messageSender);
        fileHandler.setFutureHandler(futureHandler);

        return fileHandler;
    }

    @Test
    public void testStateBeforeFileHandler() {
        FileHandler handler = new FileHandler();
        try {
            handler.validate();
            fail("exception should have occurred");
        } catch (IllegalStateException e) {
            assertEquals("a FileIteratorManager must be set on DefaultFileHandler before handle is called",
                    e.getMessage());
        }

        handler.setFileIteratorManager(createMock(FileIteratorManager.class));
        try {
            handler.validate();
            fail("exception should have occurred");
        } catch (IllegalStateException e) {
            assertEquals("a MessageSender must be set on DefaultFileHandler before handle is called", e.getMessage());
        }

        handler.setMessageSender(createMock(MessageSender.class));
        try {
            handler.validate();
            fail("exception should have occurred");
        } catch (IllegalStateException e) {
            assertEquals("a FutureHandler must be set on DefaultFileHandler before handle is called", e.getMessage());
        }

        handler.setFutureHandler(createMock(FutureHandler.class));
        handler.validate();
    }

    @Test
    public void willIterateAndSendTillNothingLeft() throws Exception {
        FileHandler fileHandler = fileHandler();
        expect(iterator.hasNext()).andReturn(true).times(2);
        Object obj1 = new Object();
        Object obj2 = new Object();
        expect(iterator.next()).andReturn(obj1);
        expect(iterator.next()).andReturn(obj2);
        expect(iterator.hasNext()).andReturn(false);
        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put(Exchange.FILE_NAME_ONLY, "foo.xls");

        Future future1 = createMock(Future.class);
        Future future2 = createMock(Future.class);
        expect(messageSender.asyncSendBodyAndHeadersToChannel(obj1, headers)).andReturn(future1);
        expect(messageSender.asyncSendBodyAndHeadersToChannel(obj2, headers)).andReturn(future2);
        futureHandler.submit(future1, "foo.xls");
        futureHandler.submit(future2, "foo.xls");
        futureHandler.finish("foo.xls");

        replay(iterator, inputStream, fileIteratorManager, iteratorCreator, messageSender, futureHandler);
        fileHandler.handleStream(inputStream, headers);
        verify(iterator, inputStream, fileIteratorManager, iteratorCreator, messageSender, futureHandler);
    }

    @Test
    public void canGetTheFileExtensionFromAFileName() {
        assertEquals("txt", FileHandler.getFileExtension("foo.txt"));

        try {
            FileHandler.getFileExtension(".txt");
            fail("exception should have occurred");
        } catch (IllegalArgumentException e) {
            assertEquals(".txt does not have a file extension", e.getMessage());
        }

        try {
            FileHandler.getFileExtension("txt");
            fail("exception should have occurred");
        } catch (IllegalArgumentException e) {
            assertEquals("txt does not have a file extension", e.getMessage());
        }
        
    }

    @Test
    public void canGetIteratorFromFileName() {
        FileHandler fileHandler = new FileHandler();
        FileIteratorManager iteratorManager = createMock(FileIteratorManager.class);
        Iterator iterator = createMock(Iterator.class);
        IteratorCreator iteratorCreator = createMock(IteratorCreator.class);
        expect(iteratorManager.getIteratorCreatorByFileExtension("txt")).andReturn(iteratorCreator);
        expect(iteratorCreator.create(anyObject(InputStream.class))).andReturn(iterator);

        Map<String, Object> headers = new HashMap<String, Object>();
        fileHandler.getIterator(null);
    }

    private Map<String, Object> headers() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(Exchange.FILE_NAME_ONLY, "foo.xls");

        return headers;
    }




}