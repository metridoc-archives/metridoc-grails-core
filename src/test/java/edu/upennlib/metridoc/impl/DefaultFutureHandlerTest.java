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

package edu.upennlib.metridoc.impl;

import edu.upennlib.metridoc.FutureHandler;
import edu.upennlib.metridoc.ExceptionHandler;
import edu.upennlib.metridoc.impl.DefaultFutureHandler.FutureHolder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */ 
public class DefaultFutureHandlerTest extends Assert{

    private CountDownLatch latch = new CountDownLatch(1);
    private ExceptionHandler exceptionHandler;
    private RuntimeException exception;
    private ExecutorService executorService;
    private FutureHandler futureHandler;
    private FutureHandler noQueueFutureHandler;
    private BlockingQueue<FutureHolder> blockingQueue;

    private BlockingQueue blockingQueue() throws Exception {
        if (blockingQueue == null) {
            blockingQueue = createMock(BlockingQueue.class);
            Future future1 = future();
            Future future2 = future();
            expect(blockingQueue.contains(new FutureHolder("foo"))).andReturn(true).times(2);
            expect(blockingQueue.poll(500, TimeUnit.MILLISECONDS)).andReturn(new FutureHolder("bar", future1));
            expect(blockingQueue.poll(500, TimeUnit.MILLISECONDS)).andReturn(new FutureHolder("foo", future2));
            expect(blockingQueue.contains(new FutureHolder("foo"))).andReturn(false);
        }

        return blockingQueue;
        
    }

    private ExceptionHandler exceptionHandler() {
        if (exceptionHandler == null) {
            exceptionHandler = createMock(ExceptionHandler.class);
            exceptionHandler.handleException("exception occurred processing future", exception());
        }

        return exceptionHandler;
    }

    private ExecutorService executorService(FutureHandler futureHandler) throws Exception {
        if (executorService == null) {
            executorService = createMock(ExecutorService.class);
            expect(executorService.submit(futureHandler)).andReturn(createMock(Future.class));
        }

        return executorService;
    }

    private DefaultFutureHandler defaultFutureHandler() throws Exception {
        return (DefaultFutureHandler) futureHandler();
    }

    private FutureHandler noMockedQueueFutureHandler() throws Exception {
        if (noQueueFutureHandler == null) {
            DefaultFutureHandler defaultFutureHandler = new DefaultFutureHandler();
            defaultFutureHandler.setExecutorService(executorService(defaultFutureHandler));
            noQueueFutureHandler = defaultFutureHandler;
        }

        return noQueueFutureHandler;
    }

    private FutureHandler futureHandler() throws Exception {
        if (futureHandler == null) {
            DefaultFutureHandler defaultFutureHandler = new DefaultFutureHandler();
            defaultFutureHandler.setExecutorService(executorService(defaultFutureHandler));
            defaultFutureHandler.setBlockingQueue(blockingQueue());
            defaultFutureHandler.setExceptionHandler(exceptionHandler());
            futureHandler = defaultFutureHandler;
        }

        return futureHandler;
    }

    private RuntimeException exception() {
        if (exception == null) {
            exception = createMock(RuntimeException.class);
        }

        return exception;
    }

    @Test
    public void whenStartIsCalledFutureHandlerIsSubmittedToExecutorService() throws Exception {
        replay(executorService(futureHandler()));
        futureHandler().start();
        verify(executorService(futureHandler()));
    }

    @Test(timeout=2000)
    public void afterStartingAndSubmitingFutureRunCallsFutureDotGet() throws Exception {
        replay(executorService(noMockedQueueFutureHandler()));
        noMockedQueueFutureHandler().start();
        new Thread(noMockedQueueFutureHandler()).start();
        noMockedQueueFutureHandler().submit(new MockedFuture(), "foo");
        latch.await();
        noMockedQueueFutureHandler().stop();
        verify(executorService(noMockedQueueFutureHandler()));
    }

    @Test
    public void exceptionHandlerCalledOnFutureException() throws Exception {
        defaultFutureHandler().setBlockingQueue(new ArrayBlockingQueue<FutureHolder>(1));
        defaultFutureHandler().getBlockingQueue().add(new FutureHolder("foo", new ErrorFuture()));
        replay(exceptionHandler());
        DefaultFutureHandler.processFuture(defaultFutureHandler());
        verify(exceptionHandler());

    }

    @Test
    public void cantSetCapacityOnceQueueIsCreated() throws Exception {
        defaultFutureHandler().getBlockingQueue();

        try {
            defaultFutureHandler().setCapacity(5);
            fail("exception should have occurred");
        } catch (IllegalStateException e) {
            assertEquals("cannot set capacity when the queue has already been constructed", e.getMessage());
        }
    }

    @Test
    public void getQueueIsNeverNull() throws Exception {
        assertNotNull(defaultFutureHandler().getBlockingQueue());
    }

    @Test
    public void getQueueAlwaysReturnsTheSameThingForTheSameObject() throws Exception {
        assertEquals(defaultFutureHandler().getBlockingQueue(), defaultFutureHandler().getBlockingQueue());
    }

    @Test
    public void validationFailsWithoutExecutorAndExceptionHandlerSet() {
        try {
            new DefaultFutureHandler().validate();
            fail("exception should have occurred");
        } catch (IllegalArgumentException e) {
            assertEquals("an ExecutorService has not been set in FutureHandler instance",
                    e.getMessage());
        }
    }

    @Test
    public void finishRunsProcessingUntilAllBatchReferencesRemoved() throws InterruptedException, Exception {
        DefaultFutureHandler handler = new DefaultFutureHandler();
        BlockingQueue<FutureHolder> queue = createMock(BlockingQueue.class);
        handler.setBlockingQueue(queue);
        Future future1 = future();
        Future future2 = future();
        expect(queue.contains(new FutureHolder("foo"))).andReturn(true).times(2);
        expect(queue.poll(500, TimeUnit.MILLISECONDS)).andReturn(new FutureHolder("bar", future1));
        expect(queue.poll(500, TimeUnit.MILLISECONDS)).andReturn(new FutureHolder("foo", future2));
        expect(queue.contains(new FutureHolder("foo"))).andReturn(false);
        replay(queue, future2, future1);
        handler.finish("foo");
        verify(queue, future2, future1);
    }

    @Test
    public void capacityHasToBeGreaterThanZero() {
        DefaultFutureHandler handler = new DefaultFutureHandler();
        handler.setCapacity(5);

        try {
            handler.setCapacity(0);
            fail("Exception should have occurred");
        } catch (IllegalArgumentException e) {
            assertEquals("capacity must be greater than zero", e.getMessage());
        }

        try {
            handler.setCapacity(-5);
            fail("Exception should have occurred");
        } catch (IllegalArgumentException e) {
            assertEquals("capacity must be greater than zero", e.getMessage());
        }
    }

    public Future future() throws Exception {
        Future mock = createMock(Future.class);
        expect(mock.get(DefaultFutureHandler.FIFTEEN_MINUTES, TimeUnit.MILLISECONDS)).andReturn(new Object());
        return mock;
    }

    public class ErrorFuture implements Future {

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDone() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            latch.countDown();
            throw exception();
        }
        
    }

    public class MockedFuture implements Future {

        @Override
        public boolean cancel(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDone() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
            latch.countDown();
            return new Object();
        }

    }

}