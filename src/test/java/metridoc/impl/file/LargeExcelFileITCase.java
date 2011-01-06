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

import metridoc.impl.file.FileHandler;
import metridoc.impl.file.FileIteratorManager;
import metridoc.impl.file.ExcelIteratorCreator;
import java.util.concurrent.Future;
import metridoc.FutureHandler;
import metridoc.MessageSender;
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
