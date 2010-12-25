/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

import edu.upennlib.metridoc.impl.file.FileIteratorManager.NoUniqueIteratorCreatorException;
import org.junit.Assert;
import edu.upennlib.metridoc.file.IteratorCreator;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */
public class FileIteratorManagerTest extends Assert{


    @Test
    public void cannotSendInEmptyIteratorCreators() {

        try {
            IteratorCreator[] iteratorCreators = new IteratorCreator[3];
            new FileIteratorManager(iteratorCreators);
            fail("exception should have occurred");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void canRetrieveIteratorCreatorByFileExtension() {
        IteratorCreator xls = createMock(IteratorCreator.class);
        IteratorCreator txt = createMock(IteratorCreator.class);
        expect(txt.supportsExtension("xls")).andReturn(Boolean.FALSE);
        expect(xls.supportsExtension("xls")).andReturn(Boolean.TRUE);
        expect(xls.getName()).andReturn("xls");
        expect(txt.getName()).andReturn("txt");
        replay(xls, txt);
        assertNotNull(manager(txt, xls).getIteratorCreatorByFileExtension("xls"));
        verify(xls, txt);
    }

    @Test
    public void canRetrieveIteratorCreatorByName() {
        IteratorCreator xls = createMock(IteratorCreator.class);
        expect(xls.getName()).andReturn("excel");
        replay(xls);
        assertEquals(manager(xls).getIteratorCreatorByName("excel"), xls);
        verify(xls);
    }

    @Test
    public void throwsUniquenessErrorIfRetrievalHasMultipleResults() {
        IteratorCreator xls1 = createMock(IteratorCreator.class);
        expect(xls1.supportsExtension("xls")).andReturn(Boolean.TRUE);
        expect(xls1.getName()).andReturn("xls1");
        IteratorCreator xls2 = createMock(IteratorCreator.class);
        expect(xls2.supportsExtension("xls")).andReturn(Boolean.TRUE);
        expect(xls2.getName()).andReturn("xls2");
        replay(xls1, xls2);
        
        try {
            manager(xls1, xls2).getIteratorCreatorByFileExtension("xls");
            fail("exception should have occurred");
        } catch (NoUniqueIteratorCreatorException e) {

        }

        verify(xls1, xls2);
    }

    @Test
    public void passingIteratorCreatorsWithSameNameThrowsUniquenessError() {
        IteratorCreator xls1 = createMock(IteratorCreator.class);
        expect(xls1.getName()).andReturn("xls");
        IteratorCreator xls2 = createMock(IteratorCreator.class);
        expect(xls2.getName()).andReturn("xls");
        replay(xls1, xls2);

        try {
            manager(xls1, xls2);
            fail("exception should have occurred");
        } catch (NoUniqueIteratorCreatorException e) {
            assertEquals("there are multiple IteratorCreators with the name xls", e.getMessage());
        }

        verify(xls1, xls2);
    }

    private FileIteratorManager manager(IteratorCreator... iteratorCreators) {

        FileIteratorManager result = new FileIteratorManager(iteratorCreators);
        return result;
    }


}