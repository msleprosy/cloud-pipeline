/*
 * Copyright 2017-2019 EPAM Systems, Inc. (https://www.epam.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.dao.PipelineEventDao;
import com.epam.pipeline.elasticsearchagent.exception.ElasticClientException;
import com.epam.pipeline.elasticsearchagent.exception.EntityNotFoundException;
import com.epam.pipeline.elasticsearchagent.model.EntityContainer;
import com.epam.pipeline.elasticsearchagent.model.EventType;
import com.epam.pipeline.elasticsearchagent.model.PipelineDoc;
import com.epam.pipeline.elasticsearchagent.model.PipelineEvent;
import com.epam.pipeline.elasticsearchagent.utils.EventProcessorUtils;
import com.epam.pipeline.entity.pipeline.Pipeline;
import com.epam.pipeline.entity.pipeline.Revision;
import com.epam.pipeline.entity.utils.DateUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.pipeline.elasticsearchagent.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")

@ExtendWith(MockitoExtension.class)

class PipelineSynchronizerTest{

    private PipelineEvent expectedPipelineEvent;
    private PipelineEvent expectedPipelineCodeEvent;
    private List<PipelineEvent> expectedPipelineEventList;
    private List<PipelineEvent> expectedPipelineCodeEventList;
    private List<PipelineEvent> commonPipelineEventList;
    private LocalDateTime expectedSyncStart;
    private String expectedPipelineIndex;
    private String expectedCodeIndex;
    private PipelineSynchronizer.PipelineDocRequests expectedPipelineDocRequests;
    private DocWriteRequest docWriteRequest;
    private List<DocWriteRequest> expectedListOfPipelineRequests;
    private List<DocWriteRequest> expectedListOfCodeRequests;
    private List<DocWriteRequest> expectedDocumentRequests;
    private List<PipelineEvent.ObjectType> expectedObjectTypes;
    private @Value("${sync.pipeline.index.mapping}") String pipelineIndexMappingFile;

    @BeforeEach
    public void setup() {
        expectedPipelineEvent = new PipelineEvent();
        expectedPipelineEvent.setEventType(EventType.INSERT);
        expectedPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        expectedPipelineEvent.setObjectId(1L);
        expectedPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        expectedPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");

        expectedPipelineCodeEvent = new PipelineEvent();
        expectedPipelineCodeEvent.setEventType(EventType.INSERT);
        expectedPipelineCodeEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE_CODE);
        expectedPipelineCodeEvent.setObjectId(1L);
        expectedPipelineCodeEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        expectedPipelineCodeEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");

        expectedPipelineEventList = new ArrayList<>();
        expectedPipelineEventList.add(expectedPipelineEvent);
        expectedPipelineCodeEventList = new ArrayList<>();
        expectedPipelineCodeEventList.add(expectedPipelineCodeEvent);

        expectedSyncStart = LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0);
        expectedPipelineIndex = "pipelineIndex";
        expectedCodeIndex = "codeIndex";

        docWriteRequest = new UpdateRequest();
        expectedListOfPipelineRequests = new ArrayList<>();
        expectedListOfPipelineRequests.add(docWriteRequest);
        expectedListOfCodeRequests = new ArrayList<>();
        expectedListOfCodeRequests.add(docWriteRequest);
        expectedDocumentRequests = new ArrayList<>();
        expectedDocumentRequests.add(docWriteRequest);
        expectedObjectTypes = Arrays.asList(PipelineEvent.ObjectType.PIPELINE, PipelineEvent.ObjectType.PIPELINE_CODE);
        expectedPipelineDocRequests = PipelineSynchronizer.PipelineDocRequests.builder()
                .pipelineId(1L)
                .pipelineRequests(expectedListOfPipelineRequests)
                .codeRequests(expectedListOfCodeRequests)
                .build();
    }

    @Mock
    private PipelineEventDao pipelineEventDao;

    @Mock
    private PipelineSynchronizer pipelineSynchronizer;

    @Mock
    private ElasticIndexService elasticIndexService;

    @Mock
    private BulkRequestSender requestSender;

    @Test
    void synchronize() {
        PipelineEvent.ObjectType expectedPipelineCodeEventObjectType = PipelineEvent.ObjectType.PIPELINE_CODE;
        PipelineEvent.ObjectType expectedPipelineEventObjectType = PipelineEvent.ObjectType.PIPELINE;
        when(pipelineEventDao.loadPipelineEventsByObjectType(expectedPipelineCodeEventObjectType, expectedSyncStart))
                .thenReturn(expectedPipelineCodeEventList);
        List<PipelineEvent> actualPipelineCodeEventList = pipelineEventDao
                .loadPipelineEventsByObjectType(PipelineEvent
                        .ObjectType.PIPELINE_CODE, LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        assertEquals(expectedPipelineCodeEventList, actualPipelineCodeEventList);
        when(pipelineEventDao.loadPipelineEventsByObjectType(expectedPipelineEventObjectType, expectedSyncStart))
                .thenReturn(expectedPipelineEventList);
        List<PipelineEvent> actualPipelineEventList = EventProcessorUtils.mergeEvents(
                pipelineEventDao
                        .loadPipelineEventsByObjectType(PipelineEvent
                                .ObjectType.PIPELINE, LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0)));
        assertEquals(expectedPipelineEventList, actualPipelineEventList);

        commonPipelineEventList = new ArrayList<>(expectedPipelineEventList);
        commonPipelineEventList.addAll(expectedPipelineCodeEventList);

        doAnswer(invocationOnPipelineSynchronizer -> {
            Object pipelineEvent = invocationOnPipelineSynchronizer.getArgument(0);
            Object pipeLineEventList = invocationOnPipelineSynchronizer.getArgument(1);
            Object syncStart = invocationOnPipelineSynchronizer.getArgument(2);
            assertEquals(expectedPipelineCodeEvent.getObjectId(), pipelineEvent);
            assertEquals(commonPipelineEventList, pipeLineEventList);
            assertEquals(expectedSyncStart, syncStart);
            return null;
        }).when(pipelineSynchronizer).synchronizePipelineEvents(expectedPipelineCodeEvent.getObjectId(), commonPipelineEventList, expectedSyncStart);
        Stream.of(actualPipelineEventList, actualPipelineCodeEventList)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(PipelineEvent::getObjectId))
                .forEach((id, events) -> pipelineSynchronizer
                        .synchronizePipelineEvents(id, events, LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0)));
        verify(pipelineSynchronizer, atLeast(1))
                .synchronizePipelineEvents(expectedPipelineCodeEvent.getObjectId(), commonPipelineEventList, expectedSyncStart);
    }

    @Test
    void synchronizePipelineEvents() throws ElasticClientException {
        when(pipelineEventDao.loadPipelineEventsByObjectType(PipelineEvent.ObjectType.PIPELINE, expectedSyncStart))
                .thenReturn(expectedPipelineEventList);
        List<PipelineEvent> actualPipelineEventList = EventProcessorUtils.mergeEvents(
                pipelineEventDao
                        .loadPipelineEventsByObjectType(PipelineEvent
                                .ObjectType.PIPELINE, LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0)));
        when(pipelineSynchronizer
                .buildDocRequests(expectedPipelineEvent.getObjectId(), expectedPipelineEventList, expectedPipelineIndex, expectedCodeIndex))
                .thenReturn(expectedPipelineDocRequests);
        PipelineSynchronizer.PipelineDocRequests actualPipelineRequests = pipelineSynchronizer
                .buildDocRequests(1L, actualPipelineEventList, "pipelineIndex", "codeIndex");
        PipelineSynchronizer.PipelineDocRequests pipelineRequests = new PipelineSynchronizer
                .PipelineDocRequests(expectedListOfPipelineRequests, expectedListOfCodeRequests, 1L);
        assertEquals(pipelineRequests, actualPipelineRequests);

        doAnswer(invocationOnElasticIndexService -> {
            Object pipelineIndex = invocationOnElasticIndexService.getArgument(0);
            Object pipelineIndexMappingFile = invocationOnElasticIndexService.getArgument(1);
            assertEquals(expectedPipelineIndex, pipelineIndex);
            assertEquals(pipelineIndexMappingFile, pipelineIndexMappingFile);
            return null;
        }).when(elasticIndexService).createIndexIfNotExist(expectedPipelineIndex, pipelineIndexMappingFile);
        elasticIndexService.createIndexIfNotExist("pipelineIndex", pipelineIndexMappingFile);

        doAnswer(invocationOnRequestSender -> {
            Object pipelineIndex = invocationOnRequestSender.getArgument(0);
            Object pipelineIndexMappingFile = invocationOnRequestSender.getArgument(1);
            assertEquals(expectedPipelineIndex, pipelineIndex);
            assertEquals(pipelineIndexMappingFile, pipelineIndexMappingFile);
            return null;
        }).when(requestSender)
                .indexDocuments(expectedPipelineIndex, expectedObjectTypes, expectedPipelineDocRequests
                        .getPipelineRequests(), LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        requestSender
                .indexDocuments("pipelineIndex", Arrays
                        .asList(PipelineEvent.ObjectType.PIPELINE, PipelineEvent.ObjectType.PIPELINE_CODE), actualPipelineRequests
                        .getPipelineRequests(), LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
    }

    @Test
    void createIndexDocuments(){
        String actualPipelineIndex = "pipelineIndex";
        String actualCodeIndex = "codeIndex";
        PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder expectedPipelineDocRequestsBuilder =  null;
        PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder actualPipelineDocRequestsBuilder = null;
        PipelineEvent actualPipelineEvent = new PipelineEvent();
        actualPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        actualPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        actualPipelineEvent.setEventType(EventType.INSERT);
        actualPipelineEvent.setObjectId(1L);
        actualPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");

        Pipeline pipeline = new Pipeline();
        pipeline.setId(1L);
        pipeline.setName(TEST_NAME);
        pipeline.setCreatedDate(DateUtils.now());
        pipeline.setParentFolderId(2L);
        pipeline.setDescription(TEST_DESCRIPTION);
        pipeline.setRepository(TEST_REPO);
        pipeline.setTemplateId(TEST_TEMPLATE);

        Revision revision = new Revision();
        revision.setName(TEST_VERSION);

        PipelineDoc pipelineDoc = PipelineDoc.builder()
                .pipeline(pipeline)
                .revisions(Collections.singletonList(revision)).build();
        EntityContainer expectedEntityContainer = EntityContainer.<PipelineDoc>builder()
                .entity(pipelineDoc)
                .owner(USER)
                .metadata(METADATA)
                .permissions(PERMISSIONS_CONTAINER)
                .build();
        EntityContainer actualEntityContainer = EntityContainer.<PipelineDoc>builder()
                .entity(pipelineDoc)
                .owner(USER)
                .metadata(METADATA)
                .permissions(PERMISSIONS_CONTAINER)
                .build();

        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            Object arg2 = invocation.getArgument(2);
            Object arg3 = invocation.getArgument(3);
            Object arg4 = invocation.getArgument(4);
            assertEquals(expectedPipelineEvent, arg0);
            assertEquals(expectedPipelineIndex, arg1);
            assertEquals(expectedCodeIndex, arg2);
            assertEquals(expectedPipelineDocRequestsBuilder, arg3);
            assertEquals(expectedEntityContainer, arg4);
            return null;
        }).when(pipelineSynchronizer)
                .createIndexDocuments(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex, expectedPipelineDocRequestsBuilder, expectedEntityContainer);
        pipelineSynchronizer
                .createIndexDocuments(actualPipelineEvent, actualPipelineIndex, actualCodeIndex, actualPipelineDocRequestsBuilder, actualEntityContainer);
        verify(pipelineSynchronizer, atLeast(1))
                .createIndexDocuments(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex, expectedPipelineDocRequestsBuilder, expectedEntityContainer);
    }

    @Test
    void buildDocRequests() {
        PipelineEvent actualPipelineEvent = new PipelineEvent();
        actualPipelineEvent.setEventType(EventType.INSERT);
        actualPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        actualPipelineEvent.setObjectId(1L);
        actualPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        actualPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");
        List<PipelineEvent> actualList;
        actualList = new ArrayList<>();
        actualList.add(expectedPipelineEvent);
        String actualPipelineIndex = "pipelineIndex";
        String actualCodeIndex = "codeIndex";
        PipelineSynchronizer.PipelineDocRequests actualPipelineDocRequests = PipelineSynchronizer.PipelineDocRequests.builder()
                .pipelineId(1L)
                .pipelineRequests(expectedListOfPipelineRequests)
                .codeRequests(expectedListOfCodeRequests)
                .build();

        when(pipelineSynchronizer.buildDocRequests(expectedPipelineEvent.getObjectId(), expectedPipelineEventList, expectedPipelineIndex, expectedCodeIndex))
                .thenReturn(expectedPipelineDocRequests);
        pipelineSynchronizer.buildDocRequests(actualPipelineEvent.getObjectId(), actualList, actualPipelineIndex, actualCodeIndex);
        assertEquals(expectedPipelineDocRequests, actualPipelineDocRequests);
        verify(pipelineSynchronizer, atLeastOnce())
                .buildDocRequests(expectedPipelineEvent.getObjectId(), expectedPipelineEventList, expectedPipelineIndex, expectedCodeIndex);
    }

    @Test
    void processPipelineEvent() throws EntityNotFoundException {
        PipelineEvent actualPipelineEvent = new PipelineEvent();
        actualPipelineEvent.setEventType(EventType.INSERT);
        actualPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        actualPipelineEvent.setObjectId(1L);
        actualPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        actualPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");
        String expectedPipelineIndex = "expectedPipelineIndex";
        String expectedCodeIndex = "expectedCodeIndex";
        String actualPipelineIndex = "expectedPipelineIndex";
        String actualCodeIndex = "expectedCodeIndex";
        List<DocWriteRequest> pipelineRequests = new ArrayList<>();
        pipelineRequests.add(docWriteRequest);
        List<DocWriteRequest> codeRequests = new ArrayList<>();
        codeRequests.add(docWriteRequest);
        PipelineSynchronizer.PipelineDocRequests actualPipelineDocRequests = new PipelineSynchronizer
                .PipelineDocRequests(pipelineRequests, codeRequests, 1L);
        when(pipelineSynchronizer.processPipelineEvent(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex))
                .thenReturn(expectedPipelineDocRequests);
        pipelineSynchronizer.processPipelineEvent(actualPipelineEvent, actualPipelineIndex, actualCodeIndex);
        assertEquals(expectedPipelineDocRequests, actualPipelineDocRequests);
        verify(pipelineSynchronizer, atLeastOnce())
                .processPipelineEvent(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex);
    }

    @Test
    void cleanCodeIndexAndCreateDeleteRequest(){
        List<DocWriteRequest> pipelineRequests = new ArrayList<>();
        pipelineRequests.add(docWriteRequest);
        List<DocWriteRequest> codeRequests = new ArrayList<>();
        codeRequests.add(docWriteRequest);
        PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder expectedPipelineDocRequestsBuilder =  null;
        PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder actualPipelineDocRequestsBuilder = null;
        PipelineSynchronizer.PipelineDocRequests actualPipelineDocRequests = new PipelineSynchronizer
                .PipelineDocRequests(pipelineRequests, codeRequests, 1L);
        String actualPipelineIndex = "pipelineIndex";
        String actualCodeIndex = "codeIndex";
        when(pipelineSynchronizer.cleanCodeIndexAndCreateDeleteRequest(1L, expectedPipelineIndex, expectedCodeIndex, expectedPipelineDocRequestsBuilder))
                .thenReturn(expectedPipelineDocRequests);
        pipelineSynchronizer.cleanCodeIndexAndCreateDeleteRequest(1L, actualPipelineIndex, actualCodeIndex, actualPipelineDocRequestsBuilder);
        assertEquals(expectedPipelineDocRequests, actualPipelineDocRequests);
        verify(pipelineSynchronizer, atLeastOnce())
                .cleanCodeIndexAndCreateDeleteRequest(1L, expectedPipelineIndex, expectedCodeIndex, expectedPipelineDocRequestsBuilder);
    }
}