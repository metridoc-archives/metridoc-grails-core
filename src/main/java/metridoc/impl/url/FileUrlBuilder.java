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

package metridoc.impl.url;

import metridoc.url.UrlBuilder;
import metridoc.url.UrlHelper;
import metridoc.url.UrlParam;

/**
 *
 * @author tbarker
 */
public class FileUrlBuilder implements UrlBuilder{

    public enum ReadLock{markerFile, changed, fileLock, rename, none} 
    
    @UrlParam(include=false)
    private String fromFolder;
    
    private Boolean autoCreate;
    private String bufferSize;
    private String fileName;
    private Boolean flatten;
    private String charset;
    private Long initialDelay;
    private Long delay;
    private Boolean useFixedDelay;
    private Boolean recursive;
    private Boolean delete;
    private Boolean noop;
    private String preMove;
    private String move;
    private String moveFailed;
    private String include;
    private String exclude;
    private Boolean idempotent;
    @UrlParam(serviceReference=true)
    private String idempotentRepository;
    @UrlParam(serviceReference=true)
    private String inProgressRepository;
    @UrlParam(serviceReference=true)
    private String filter;
    @UrlParam(serviceReference=true)
    private String sorter;
    private String sortBy;
    private ReadLock readLock;
    private Long readLockTimeout;
    @UrlParam(serviceReference=true)
    private String exclusiveReadLockStrategy;
    @UrlParam(serviceReference=true)
    private String processStrategy;
    private Integer maxMessagesPerPoll;
    private Boolean startingDirectoryMustExist;
    private Boolean directoryMustExist;

    public FileUrlBuilder autoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
        return this;
    }
    
    public Boolean getAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(Boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public FileUrlBuilder bufferSize(String bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }
    
    public String getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(String bufferSize) {
        this.bufferSize = bufferSize;
    }

    public FileUrlBuilder charSet(String charset) {
        this.charset = charset;
        return this;
    }
    
    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public FileUrlBuilder delay(long delay) {
        this.delay = delay;
        return this;
    }
    
    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public FileUrlBuilder delete(boolean delete) {
        this.delete = delete;
        return this;
    }
    
    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public FileUrlBuilder directoryMustExist(boolean directoryMustExist) {
        this.directoryMustExist = directoryMustExist;
        return this;
    }
    
    public Boolean getDirectoryMustExist() {
        return directoryMustExist;
    }

    public void setDirectoryMustExist(Boolean directoryMustExist) {
        this.directoryMustExist = directoryMustExist;
    }

    public FileUrlBuilder exclude(String exclude) {
        this.exclude = exclude;
        return this;
    }
    
    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public FileUrlBuilder exclusiveReadLockStrategy(String exclusiveReadLockStrategy) {
        this.exclusiveReadLockStrategy = exclusiveReadLockStrategy;
        return this;
    }
    
    public String getExclusiveReadLockStrategy() {
        return exclusiveReadLockStrategy;
    }

    public void setExclusiveReadLockStrategy(String exclusiveReadLockStrategy) {
        this.exclusiveReadLockStrategy = exclusiveReadLockStrategy;
    }

    public FileUrlBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileUrlBuilder filter(String filter) {
        this.filter = filter;
        return this;
    }
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public FileUrlBuilder flatten(boolean flatten) {
        this.flatten = flatten;
        return this;
    }
    public Boolean getFlatten() {
        return flatten;
    }

    public void setFlatten(Boolean flatten) {
        this.flatten = flatten;
    }

    public FileUrlBuilder fromFolder(String fromFolder) {
        this.fromFolder = fromFolder;
        return this;
    }
    
    public String getFromFolder() {
        return fromFolder;
    }

    public void setFromFolder(String fromFolder) {
        this.fromFolder = fromFolder;
    }

    public FileUrlBuilder idempotent(boolean idempotent) {
        this.idempotent = idempotent;
        return this;
    }
    
    public Boolean getIdempotent() {
        return idempotent;
    }

    public void setIdempotent(Boolean idempotent) {
        this.idempotent = idempotent;
    }

    public FileUrlBuilder idempotentRepository(String idempotentRepository) {
        this.idempotentRepository = idempotentRepository;
        return this;
    }
    
    public String getIdempotentRepository() {
        return idempotentRepository;
    }

    public void setIdempotentRepository(String idempotentRepository) {
        this.idempotentRepository = idempotentRepository;
    }

    public FileUrlBuilder inProgressRepository(String inProgressRepository) {
        this.inProgressRepository = inProgressRepository;
        return this;
    }
    
    public String getInProgressRepository() {
        return inProgressRepository;
    }

    public void setInProgressRepository(String inProgressRepository) {
        this.inProgressRepository = inProgressRepository;
    }

    public FileUrlBuilder include(String include) {
        this.include = include;
        return this;
    }
    
    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public FileUrlBuilder initialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }
    
    public Long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(Long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public FileUrlBuilder maxMessagesPerPoll(int maxMessagesPerPoll) {
        this.maxMessagesPerPoll(maxMessagesPerPoll);
        return this;
    }
    
    public Integer getMaxMessagesPerPoll() {
        return maxMessagesPerPoll;
    }

    public void setMaxMessagesPerPoll(Integer maxMessagesPerPoll) {
        this.maxMessagesPerPoll = maxMessagesPerPoll;
    }

    public FileUrlBuilder move(String move) {
        this.move = move;
        return this;
    }
    
    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public FileUrlBuilder moveFailed(String moveFailed) {
        this.moveFailed = moveFailed;
        return this;
    }
    
    public String getMoveFailed() {
        return moveFailed;
    }

    public void setMoveFailed(String moveFailed) {
        this.moveFailed = moveFailed;
    }

    public FileUrlBuilder noop(boolean noop){
        this.noop = noop;
        return this;
    }
            
    public Boolean getNoop() {
        return noop;
    }

    public void setNoop(Boolean noop) {
        this.noop = noop;
    }

    public FileUrlBuilder preMove(String preMove) {
        this.preMove = preMove;
        return this;
    }
    
    public String getPreMove() {
        return preMove;
    }

    public void setPreMove(String preMove) {
        this.preMove = preMove;
    }

    public FileUrlBuilder processStrategy(String processStrategy) {
        this.processStrategy = processStrategy;
        return this;
    }
    
    public String getProcessStrategy() {
        return processStrategy;
    }

    public void setProcessStrategy(String processStrategy) {
        this.processStrategy = processStrategy;
    }

    public FileUrlBuilder readLock(ReadLock readLock) {
        this.readLock = readLock;
        return this;
    }
    
    public ReadLock getReadLock() {
        return readLock;
    }

    public void setReadLock(ReadLock readLock) {
        this.readLock = readLock;
    }

    public FileUrlBuilder readLockTimeout(long readLockTimeout) {
        this.readLockTimeout = readLockTimeout;
        return this;
    }
    
    public Long getReadLockTimeout() {
        return readLockTimeout;
    }

    public void setReadLockTimeout(Long readLockTimeout) {
        this.readLockTimeout = readLockTimeout;
    }

    public FileUrlBuilder recursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }
    
    public Boolean getRecursive() {
        return recursive;
    }

    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    public FileUrlBuilder sortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }
    
    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public FileUrlBuilder sorter(String sorter) {
        this.sorter = sorter;
        return this;
    }
    
    public String getSorter() {
        return sorter;
    }

    public void setSorter(String sorter) {
        this.sorter = sorter;
    }

    public FileUrlBuilder startingDirectoryMustExist(boolean startingDirectoryMustExist) {
        this.startingDirectoryMustExist = startingDirectoryMustExist;
        return this;
    }
    
    public Boolean getStartingDirectoryMustExist() {
        return startingDirectoryMustExist;
    }

    public void setStartingDirectoryMustExist(Boolean startingDirectoryMustExist) {
        this.startingDirectoryMustExist = startingDirectoryMustExist;
    }

    public FileUrlBuilder useFixedDelay(boolean useFixedDelay) {
        this.useFixedDelay = useFixedDelay;
        return this;
    }
    
    public Boolean getUseFixedDelay() {
        return useFixedDelay;
    }

    public void setUseFixedDelay(Boolean useFixedDelay) {
        this.useFixedDelay = useFixedDelay;
    }
    
    @Override
    public String url() {
        return UrlHelper.buildUrl("file", fromFolder, this);
    }
}
