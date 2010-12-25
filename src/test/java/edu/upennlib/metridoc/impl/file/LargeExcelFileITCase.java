/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

import java.util.concurrent.Future;
import edu.upennlib.metridoc.FutureHandler;
import edu.upennlib.metridoc.MessageSender;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */
public class LargeExcelFileITCase extends Assert{

    /**
     * makes sure a sizable file can be processed
     * @throws Exception
     */
    @Test
    public void testLargeExcelFile() throws Exception{
        FileInputStream inpurStream
                = new FileInputStream(new File("src/test/resources/testFiles/excel/bigTestFile.xls"));
        FileHandler handler = new FileHandler();
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(Exchange.FILE_NAME_ONLY, "foo.xls");
        handler.setFileIteratorManager(new FileIteratorManager(new ExcelIteratorCreator()));
        FutureHandler futureHandler = createMock(FutureHandler.class);
        futureHandler.submit(anyObject(Future.class), eq("foo.xls"));
        expectLastCall().anyTimes();
        futureHandler.finish("foo.xls");
        MessageSender messageSender = createMock(MessageSender.class);
        expect(messageSender.asyncSendBodyAndHeadersToChannel(anyObject(), anyObject(Map.class)))
                .andReturn(createMock(Future.class)).times(30000, 60000);
        handler.setFutureHandler(futureHandler);
        handler.setMessageSender(messageSender);
        replay(futureHandler, messageSender);
        handler.handleStream(inpurStream, headers);
        verify(futureHandler, messageSender);
    }

}
