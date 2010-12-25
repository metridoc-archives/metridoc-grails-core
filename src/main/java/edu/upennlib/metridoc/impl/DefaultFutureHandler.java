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

import edu.upennlib.metridoc.ExceptionHandler;
import edu.upennlib.metridoc.FutureHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tbarker
 */
public class DefaultFutureHandler implements FutureHandler {

    private BlockingQueue<FutureHolder> blockingQueue;
    private int capacity = 10;
    protected final static long FIFTEEN_MINUTES = 15 * 60 * 60;
    private long maxFutureWaitTime = FIFTEEN_MINUTES;
    private boolean stopped = true;
    private ExecutorService executorService;
    private static Logger LOGGER = LoggerFactory.getLogger(DefaultFutureHandler.class);
    private ExceptionHandler exceptionHandler;

    public long getMaxFutureWaitTime() {
        return maxFutureWaitTime;
    }

    public void setMaxFutureWaitTime(long maxFutureWaitTime) {
        this.maxFutureWaitTime = maxFutureWaitTime;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        Validate.isTrue(capacity > 0, "capacity must be greater than zero");
        if (blockingQueue != null) {
            throw new IllegalStateException("cannot set capacity when the queue has already been constructed");
        }
        this.capacity = capacity;
    }

    @Override
    public void submit(Future future, String batchLabel) throws InterruptedException {
        if (!stopped) {
            getBlockingQueue().put(new FutureHolder(batchLabel, future));
        } else {
            throw new IllegalStateException("cannot add batch items since system is shutting down");
        }
    }

    @Override
    public void run() {
        validate();
        while (!stopped || !getBlockingQueue().isEmpty()) {
            processFuture(this);
        }
    }

    protected void setBlockingQueue(BlockingQueue<FutureHolder> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    protected BlockingQueue<FutureHolder> getBlockingQueue() {
        if (blockingQueue == null) {
            threadSafeInitializeBlockingQueue();
        }

        return blockingQueue;
    }

    private synchronized void threadSafeInitializeBlockingQueue() {
        if (blockingQueue == null) {
            blockingQueue = new LinkedBlockingQueue<FutureHolder>();
        }
    }

    @Override
    public void finish(String batchLabel) {
        while(getBlockingQueue().contains(new FutureHolder(batchLabel))) {
            processFuture(this);
        }
    }

    @Override
    public void stop() {
        stopped = true;

        processFuture(this);
    }

    @Override
    public void start() {
        stopped = false;
        executorService.submit(this);
    }

    protected static void processFuture(DefaultFutureHandler handler) {
        try {
            FutureHolder futureHolder = handler.getBlockingQueue().poll(500, TimeUnit.MILLISECONDS);
            if (futureHolder != null) {
                Future future = futureHolder.getFuture();
                future.get(handler.maxFutureWaitTime, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException interruptedException) {
            LOGGER.warn(
                    "The container is likely shutting down but the future handler has not completed yet",
                    interruptedException);
        } catch (Exception ex) {
            handler.exceptionHandler.handleException("exception occurred processing future", ex);
        }
    }

    protected void validate() {
        String message = "an ExecutorService has not been set in FutureHandler instance";
        Validate.notNull(executorService, message);
    }

    protected static class FutureHolder {
        private final String id;
        private final Future future;

        public FutureHolder(String id, Future future) {
            this.id = id;
            this.future = future;
        }

        public FutureHolder(String id) {
            this.id = id;
            future = null;
        }

        public Future getFuture() {
            return future;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FutureHolder other = (FutureHolder) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }
}
