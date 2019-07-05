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
import com.epam.pipeline.elasticsearchagent.service.impl.converter.pipeline.PipelineLoader;
import com.epam.pipeline.elasticsearchagent.utils.EventProcessorUtils;
import com.epam.pipeline.entity.pipeline.Pipeline;
import com.epam.pipeline.entity.pipeline.Revision;
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
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")

@ExtendWith(MockitoExtension.class)
class PipelineSynchronizerTest{

    private static final int YEAR = 2019;
    private static final int DAY_OF_MONTH = 26;
    private static final int HOUR = 11;
    private static final int MINUTE = 11;
    private static final int SECOND = 0;
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
    private List<DocWriteRequest> expectedListOfDocumentRequests;
    private List<PipelineEvent.ObjectType> expectedObjectTypes;
    private PipelineEvent.ObjectType expectedObjectType;
    private @Value("${sync.pipeline.index.mapping}") String pipelineIndexMappingFile;
    private List<Revision> expectedListOfRevisions;
    private Revision expectedRevision;
    private Pipeline expectedPipeline;
    private EntityContainer expectedEntityContainer;
    private String codeIndex;
    private List<String> expectedListOfPipelineFileIndexPaths;
    private @Value("${sync.pipeline-code.index.paths}") String pipelineFileIndexPaths;
    private String actualPipelineIndex;
    private String actualCodeIndex;
    private LocalDateTime actualSyncStart;
    private PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder expectedRequestsBuilder;

    @BeforeEach
    public void setup() {
        expectedPipelineEvent = new PipelineEvent();
        expectedPipelineEvent.setEventType(EventType.INSERT);
        expectedPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        expectedPipelineEvent.setObjectId(1L);

        expectedPipelineEvent.setCreatedDate(LocalDateTime
                .of(YEAR, Month.JUNE, DAY_OF_MONTH, HOUR, MINUTE, SECOND));
        expectedPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");

        expectedPipelineCodeEvent = new PipelineEvent();
        expectedPipelineCodeEvent.setEventType(EventType.INSERT);
        expectedPipelineCodeEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE_CODE);
        expectedPipelineCodeEvent.setObjectId(1L);
        expectedPipelineCodeEvent.setCreatedDate(LocalDateTime
                .of(YEAR, Month.JUNE, DAY_OF_MONTH, HOUR, MINUTE, SECOND));
        expectedPipelineCodeEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");

        expectedPipelineEventList = new ArrayList<>();
        expectedPipelineEventList.add(expectedPipelineEvent);
        expectedPipelineCodeEventList = new ArrayList<>();
        expectedPipelineCodeEventList.add(expectedPipelineCodeEvent);

        expectedSyncStart = LocalDateTime
                .of(YEAR, Month.JUNE, DAY_OF_MONTH, HOUR, MINUTE, SECOND);
        actualSyncStart = LocalDateTime
                .of(YEAR, Month.JUNE, DAY_OF_MONTH, HOUR, MINUTE, SECOND);
        expectedPipelineIndex = "pipelineIndex";
        expectedCodeIndex = "codeIndex";
        actualPipelineIndex = "pipelineIndex";
        actualCodeIndex = "codeIndex";

        docWriteRequest = new UpdateRequest();
        expectedListOfPipelineRequests = new ArrayList<>();
        expectedListOfPipelineRequests.add(docWriteRequest);
        expectedListOfCodeRequests = new ArrayList<>();
        expectedListOfCodeRequests.add(docWriteRequest);
        expectedListOfDocumentRequests = new ArrayList<>();
        expectedListOfDocumentRequests.add(docWriteRequest);
        expectedObjectTypes = Arrays.asList(PipelineEvent
                .ObjectType.PIPELINE, PipelineEvent
                .ObjectType.PIPELINE_CODE);
        expectedObjectType = PipelineEvent.ObjectType.PIPELINE_CODE;
        expectedPipelineDocRequests = PipelineSynchronizer.PipelineDocRequests.builder()
                .pipelineId(1L)
                .pipelineRequests(expectedListOfPipelineRequests)
                .codeRequests(expectedListOfCodeRequests)
                .build();

        expectedRevision = new Revision();
        expectedListOfRevisions = new ArrayList<>();
        expectedListOfRevisions.add(expectedRevision);

        expectedPipeline = new Pipeline();
        expectedPipeline.setId(1L);

        PipelineDoc pipelineDoc = PipelineDoc.builder()
                .pipeline(expectedPipeline)
                .revisions(Collections.singletonList(expectedRevision)).build();
        expectedEntityContainer = EntityContainer.<PipelineDoc>builder()
                .entity(pipelineDoc)
                .owner(USER)
                .metadata(METADATA)
                .permissions(PERMISSIONS_CONTAINER)
                .build();
        expectedListOfPipelineFileIndexPaths = new ArrayList<>();
        expectedListOfPipelineFileIndexPaths.add(TEST_PATH);

        expectedRequestsBuilder = PipelineSynchronizer.PipelineDocRequests.builder()
                .pipelineId(expectedPipeline.getId());
    }

    @Mock
    private PipelineEventDao pipelineEventDao;

    @Mock
    private PipelineSynchronizer pipelineSynchronizer;

    @Mock
    private ElasticIndexService elasticIndexService;

    @Mock
    private BulkRequestSender requestSender;

    @Mock
    private CloudPipelineAPIClient cloudPipelineAPIClient;

    @Mock
    private PipelineCodeHandler pipelineCodeHandler;

    @Mock
    private PipelineLoader pipelineLoader;

    @Test
    void shouldSynchronize() {
        PipelineEvent.ObjectType expectedPipelineCodeEventObjectType = PipelineEvent
                .ObjectType.PIPELINE_CODE;
        PipelineEvent.ObjectType expectedPipelineEventObjectType = PipelineEvent
                .ObjectType.PIPELINE;
        when(pipelineEventDao
                .loadPipelineEventsByObjectType(expectedPipelineCodeEventObjectType, expectedSyncStart))
                .thenReturn(expectedPipelineCodeEventList);
        List<PipelineEvent> actualPipelineCodeEventList = pipelineEventDao
                .loadPipelineEventsByObjectType(PipelineEvent
                        .ObjectType.PIPELINE_CODE, actualSyncStart);
        assertEquals(expectedPipelineCodeEventList, actualPipelineCodeEventList);
        when(pipelineEventDao
                .loadPipelineEventsByObjectType(expectedPipelineEventObjectType, expectedSyncStart))
                .thenReturn(expectedPipelineEventList);
        List<PipelineEvent> actualPipelineEventList = EventProcessorUtils.mergeEvents(
                pipelineEventDao
                        .loadPipelineEventsByObjectType(PipelineEvent
                                .ObjectType.PIPELINE, actualSyncStart));
        assertEquals(expectedPipelineEventList, actualPipelineEventList);

        commonPipelineEventList = new ArrayList<>(expectedPipelineEventList);
        commonPipelineEventList.addAll(expectedPipelineCodeEventList);

        doAnswer(invocationOnPipelineSynchronizer -> {
            Object pipelineEvent = invocationOnPipelineSynchronizer
                    .getArgument(0);
            Object pipeLineEventList = invocationOnPipelineSynchronizer
                    .getArgument(1);
            Object syncStart = invocationOnPipelineSynchronizer.getArgument(2);
            assertEquals(expectedPipelineCodeEvent.getObjectId(), pipelineEvent);
            assertEquals(commonPipelineEventList, pipeLineEventList);
            assertEquals(expectedSyncStart, syncStart);
            return null;
        }).when(pipelineSynchronizer)
                .synchronizePipelineEvents(expectedPipelineCodeEvent
                        .getObjectId(), commonPipelineEventList, expectedSyncStart);
        Stream.of(actualPipelineEventList, actualPipelineCodeEventList)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(PipelineEvent::getObjectId))
                .forEach((id, events) -> pipelineSynchronizer
                        .synchronizePipelineEvents(id, events, actualSyncStart));
        verify(pipelineSynchronizer, atLeast(1))
                .synchronizePipelineEvents(expectedPipelineCodeEvent
                        .getObjectId(), commonPipelineEventList, expectedSyncStart);
    }

    @Test
    void shouldSynchronizePipelineEvents() throws ElasticClientException {
        when(pipelineEventDao.loadPipelineEventsByObjectType(PipelineEvent
                .ObjectType.PIPELINE, expectedSyncStart))
                .thenReturn(expectedPipelineEventList);
        List<PipelineEvent> actualPipelineEventList = EventProcessorUtils.mergeEvents(
                pipelineEventDao
                        .loadPipelineEventsByObjectType(PipelineEvent
                                .ObjectType.PIPELINE, actualSyncStart));
        when(pipelineSynchronizer
                .buildDocRequests(expectedPipelineEvent
                        .getObjectId(), expectedPipelineEventList, expectedPipelineIndex, expectedCodeIndex))
                .thenReturn(expectedPipelineDocRequests);
        PipelineSynchronizer.PipelineDocRequests actualPipelineRequests = pipelineSynchronizer
                .buildDocRequests(1L, actualPipelineEventList, actualPipelineIndex, actualCodeIndex);
        PipelineSynchronizer.PipelineDocRequests expectedPipelineRequests = new PipelineSynchronizer
                .PipelineDocRequests(expectedListOfPipelineRequests, expectedListOfCodeRequests, 1L);
        assertEquals(expectedPipelineRequests, actualPipelineRequests);

        doAnswer(invocationOnElasticIndexService -> {
            Object pipelineIndex = invocationOnElasticIndexService.getArgument(0);
            Object pipelineIndexMappingFileArg = invocationOnElasticIndexService.getArgument(1);
            assertEquals(expectedPipelineIndex, pipelineIndex);
            assertEquals(pipelineIndexMappingFile, pipelineIndexMappingFileArg);
            return null;
        }).when(elasticIndexService).createIndexIfNotExist(expectedPipelineIndex, pipelineIndexMappingFile);
        elasticIndexService.createIndexIfNotExist(actualPipelineIndex, pipelineIndexMappingFile);
        verify(elasticIndexService, atLeast(1))
                .createIndexIfNotExist(expectedPipelineIndex, pipelineIndexMappingFile);

        doAnswer(invocationOnRequestSenderWithListOfObjectTypes -> {
            Object pipelineIndex = invocationOnRequestSenderWithListOfObjectTypes.getArgument(0);
            Object objectTypes = invocationOnRequestSenderWithListOfObjectTypes.getArgument(1);
            Object pipeLineDocRequests = invocationOnRequestSenderWithListOfObjectTypes.getArgument(2);
            Object syncStart = invocationOnRequestSenderWithListOfObjectTypes.getArgument(3);
            assertEquals(expectedPipelineIndex, pipelineIndex);
            assertEquals(expectedObjectTypes, objectTypes);
            assertEquals(expectedPipelineDocRequests.getPipelineRequests(), pipeLineDocRequests);
            assertEquals(expectedSyncStart, syncStart);
            return null;
        }).when(requestSender)
                .indexDocuments(expectedPipelineIndex, expectedObjectTypes, expectedPipelineDocRequests
                        .getPipelineRequests(), expectedSyncStart);
        requestSender
                .indexDocuments(actualPipelineIndex, Arrays
                        .asList(PipelineEvent
                                .ObjectType.PIPELINE, PipelineEvent
                                .ObjectType.PIPELINE_CODE), actualPipelineRequests
                        .getPipelineRequests(), actualSyncStart);
        verify(requestSender, atLeast(1))
                .indexDocuments(expectedPipelineIndex, expectedObjectTypes, expectedPipelineDocRequests
                .getPipelineRequests(), expectedSyncStart);

        doAnswer(invocationOnRequestSenderWithObjectType -> {
            Object pipelineIndex = invocationOnRequestSenderWithObjectType.getArgument(0);
            Object objectType = invocationOnRequestSenderWithObjectType.getArgument(1);
            Object pipeLineDocRequests = invocationOnRequestSenderWithObjectType.getArgument(2);
            Object syncStart = invocationOnRequestSenderWithObjectType.getArgument(3);
            assertEquals(expectedPipelineIndex, pipelineIndex);
            assertEquals(expectedObjectType, objectType);
            assertEquals(expectedPipelineDocRequests.getPipelineRequests(), pipeLineDocRequests);
            assertEquals(expectedSyncStart, syncStart);
            return null;
        }).when(requestSender)
                .indexDocuments(expectedPipelineIndex, expectedObjectType, expectedPipelineDocRequests
                        .getPipelineRequests(), expectedSyncStart);
        requestSender
                .indexDocuments(actualPipelineIndex, PipelineEvent
                        .ObjectType.PIPELINE_CODE, actualPipelineRequests
                        .getPipelineRequests(), actualSyncStart);
        verify(requestSender, atLeast(1))
                .indexDocuments(expectedPipelineIndex, expectedObjectType, expectedPipelineDocRequests
                        .getPipelineRequests(), expectedSyncStart);
    }

    @Test
    void shouldCreateIndexDocuments() throws EntityNotFoundException {
        Optional<EntityContainer<PipelineDoc>> actualPipelineEntity = pipelineLoader.loadEntity(1L);
        if(actualPipelineEntity.isPresent()) {
            Pipeline actualPipeline = actualPipelineEntity.get().getEntity().getPipeline();
            List<String> actualListOfPipelineFileIndexPaths = EventProcessorUtils
                    .splitOnPaths(pipelineFileIndexPaths);
            when(cloudPipelineAPIClient.loadPipelineVersions(expectedPipeline.getId()))
                    .thenReturn(expectedListOfRevisions);
            List<Revision> actualListOfRevisions = cloudPipelineAPIClient.loadPipelineVersions(1L);
            assertEquals(expectedListOfRevisions, actualListOfRevisions);
            verify(cloudPipelineAPIClient, atLeast(1))
                    .loadPipelineVersions(expectedPipeline.getId());

            when((pipelineCodeHandler
                    .createPipelineCodeDocuments(expectedPipeline, expectedEntityContainer
                            .getPermissions(), expectedRevision
                            .getName(), codeIndex, expectedListOfPipelineFileIndexPaths)))
                    .thenReturn(expectedListOfDocumentRequests);
            List<DocWriteRequest> actualListOfDocRequests = pipelineCodeHandler
                    .createPipelineCodeDocuments(actualPipeline, actualPipelineEntity.get()
                            .getPermissions(), actualListOfRevisions
                            .get(0).getName(), actualCodeIndex, actualListOfPipelineFileIndexPaths);
            assertEquals(expectedListOfDocumentRequests, actualListOfDocRequests);
            verify(pipelineCodeHandler, atLeast(1))
                    .createPipelineCodeDocuments(expectedPipeline, expectedEntityContainer
                    .getPermissions(), expectedRevision
                            .getName(), codeIndex, expectedListOfPipelineFileIndexPaths);
        }
    }

    @Test
    void shouldBuildDocRequests() throws EntityNotFoundException {
        Optional<EntityContainer<PipelineDoc>> actualPipelineEntity = pipelineLoader.loadEntity(1L);
         List<PipelineEvent> actualPipelineEventList = EventProcessorUtils.mergeEvents(
                pipelineEventDao.loadPipelineEventsByObjectType(PipelineEvent
                        .ObjectType.PIPELINE, actualSyncStart));
        if(actualPipelineEntity.isPresent()) {
            Pipeline actualPipeline = actualPipelineEntity.get().getEntity().getPipeline();
            when((pipelineCodeHandler
                    .processGitEvents(expectedPipeline.getId(), expectedPipelineEventList)))
                    .thenReturn(expectedListOfDocumentRequests);
            List<DocWriteRequest> actualListOfDocumentRequests = pipelineCodeHandler
                    .processGitEvents(actualPipeline.getId(), actualPipelineEventList);
            assertEquals(expectedListOfDocumentRequests, actualListOfDocumentRequests);
            verify(pipelineCodeHandler, atLeast(1))
                    .processGitEvents(expectedPipeline.getId(), expectedPipelineEventList);

            when(pipelineSynchronizer
                    .processPipelineEvent
                            (expectedPipelineEventList.get(0), expectedPipelineIndex, expectedCodeIndex))
                    .thenReturn(expectedPipelineDocRequests);
            PipelineSynchronizer.PipelineDocRequests actualPipelineDocRequests  = pipelineSynchronizer
                    .processPipelineEvent(actualPipelineEventList.get(0), actualPipelineIndex, actualCodeIndex);
            assertEquals(expectedPipelineDocRequests, actualPipelineDocRequests);
            verify(pipelineSynchronizer, atLeast(1))
                    .processPipelineEvent(expectedPipelineEventList.get(0), expectedPipelineIndex, expectedCodeIndex);

            when(pipelineSynchronizer
                    .cleanCodeIndexAndCreateDeleteRequest
                            (expectedPipeline
                                    .getId(), expectedPipelineIndex, expectedCodeIndex, expectedRequestsBuilder))
                    .thenReturn(expectedPipelineDocRequests);
            PipelineSynchronizer.PipelineDocRequests
                    .PipelineDocRequestsBuilder actualRequestsBuilder = PipelineSynchronizer
                    .PipelineDocRequests.builder()
                    .pipelineId(actualPipeline.getId());
            actualPipelineDocRequests = pipelineSynchronizer
                    .cleanCodeIndexAndCreateDeleteRequest
                            (actualPipeline.getId(),actualPipelineIndex, actualCodeIndex, actualRequestsBuilder);
            assertEquals(expectedPipelineDocRequests, actualPipelineDocRequests);
            verify(pipelineSynchronizer, atLeast(1))
                    .cleanCodeIndexAndCreateDeleteRequest(expectedPipeline
                            .getId(), expectedPipelineIndex, expectedCodeIndex, expectedRequestsBuilder);
        }
    }

    @Test
    void processPipelineEvent() throws EntityNotFoundException {
        List<PipelineEvent> actualPipelineEventList = EventProcessorUtils.mergeEvents(
                pipelineEventDao.loadPipelineEventsByObjectType(PipelineEvent
                        .ObjectType.PIPELINE, actualSyncStart));
        Optional<EntityContainer<PipelineDoc>> actualPipelineEntity = pipelineLoader.loadEntity(1L);

        if(actualPipelineEntity.isPresent()) {
            Pipeline actualPipeline = actualPipelineEntity.get().getEntity().getPipeline();

            PipelineSynchronizer.PipelineDocRequests
                    .PipelineDocRequestsBuilder actualRequestsBuilder = PipelineSynchronizer
                    .PipelineDocRequests.builder()
                    .pipelineId(actualPipeline.getId());
            doAnswer(invocationOnPipelineSynchronizer -> {
                Object pipelineEvent = invocationOnPipelineSynchronizer
                        .getArgument(0);
                Object pipeLineIndex = invocationOnPipelineSynchronizer
                        .getArgument(1);
                Object codeIndex = invocationOnPipelineSynchronizer
                        .getArgument(2);
                Object requestsBuilder = invocationOnPipelineSynchronizer
                        .getArgument(3);
                Object entityContainer = invocationOnPipelineSynchronizer
                        .getArgument(4);
                assertEquals(expectedPipelineEvent.getObjectId(), pipelineEvent);
                assertEquals(expectedPipelineIndex, pipeLineIndex);
                assertEquals(expectedCodeIndex, codeIndex);
                assertEquals(expectedRequestsBuilder, requestsBuilder);
                assertEquals(expectedEntityContainer, entityContainer);
                return null;
            }).when(pipelineSynchronizer)
                    .createIndexDocuments(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex,
                            expectedRequestsBuilder, expectedEntityContainer);
            pipelineSynchronizer.createIndexDocuments
                    (actualPipelineEventList.get(0), actualPipelineIndex, actualCodeIndex,
                            actualRequestsBuilder, actualPipelineEntity.get());
            verify(pipelineSynchronizer, atLeast(1))
                    .createIndexDocuments(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex,
                            expectedRequestsBuilder, expectedEntityContainer);
        }
    }

    @Test
    void cleanCodeIndexAndCreateDeleteRequest(){
        /*List<DocWriteRequest> pipelineRequests = new ArrayList<>();
        pipelineRequests.add(docWriteRequest);
        List<DocWriteRequest> codeRequests = new ArrayList<>();
        codeRequests.add(docWriteRequest);
        PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder expectedPipelineDocRequestsBuilder =  null;
        PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder actualPipelineDocRequestsBuilder = null;
        PipelineSynchronizer.PipelineDocRequests actualPipelineDocRequests = new PipelineSynchronizer
                .PipelineDocRequests(pipelineRequests, codeRequests, 1L);
        String actualPipelineIndex = actualPipelineIndex;
        String actualCodeIndex = actualCodeIndex;
        when(pipelineSynchronizer
        .cleanCodeIndexAndCreateDeleteRequest
        (1L, expectedPipelineIndex, expectedCodeIndex, expectedPipelineDocRequestsBuilder))
                .thenReturn(expectedPipelineDocRequests);
        pipelineSynchronizer
        .cleanCodeIndexAndCreateDeleteRequest
        (1L, actualPipelineIndex, actualCodeIndex, actualPipelineDocRequestsBuilder);
        assertEquals(expectedPipelineDocRequests, actualPipelineDocRequests);
        verify(pipelineSynchronizer, atLeastOnce())
                .cleanCodeIndexAndCreateDeleteRequest
                (1L, expectedPipelineIndex, expectedCodeIndex, expectedPipelineDocRequestsBuilder);*/
    }
}
