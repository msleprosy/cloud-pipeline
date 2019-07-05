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


package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.exception.EntityNotFoundException;
import com.epam.pipeline.elasticsearchagent.model.*;
import com.epam.pipeline.elasticsearchagent.model.git.GitEventData;
import com.epam.pipeline.elasticsearchagent.model.git.GitEventDescription;
import com.epam.pipeline.elasticsearchagent.model.git.GitEventType;
import com.epam.pipeline.elasticsearchagent.service.impl.converter.pipeline.PipelineCodeMapper;
import com.epam.pipeline.elasticsearchagent.service.impl.converter.pipeline.PipelineLoader;
import com.epam.pipeline.entity.git.GitRepositoryEntry;
import com.epam.pipeline.entity.pipeline.Pipeline;
import com.epam.pipeline.entity.pipeline.Revision;
import com.epam.pipeline.entity.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.epam.pipeline.elasticsearchagent.ObjectCreationUtils.buildPermissions;
import static com.epam.pipeline.elasticsearchagent.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"PMD.TooManyStaticImports"})
class PipelineCodeHandlerTest {

    private static final String DOC_TYPE = "_doc";
    private static final String INDEX_PREFIX = "cp-";
    private static final String INDEX_NAME = "pipeline-code";
    private static final String FILE_INDEX_PATHS = "config.json";
    private static final String FOLDER_INDEX_PATHS = "docs/";
    private static final String VERSION = "1";
    private DocWriteRequest docWriteRequest;
    private List<DocWriteRequest> expectedListOfRequests;
    private Pipeline expectedPipeline;

    @Mock
    private PipelineLoader pipelineLoader;

    @Mock
    private CloudPipelineAPIClient apiClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PipelineCodeHandler pipelineCodeHandler;

    private PipelineEvent expectedPipelineEvent;
    private EntityContainer<PipelineDoc> container;
    private GitEventData gitPushEventData;
    private GitEventData gitTagEventData;
    private Pipeline pipeline;
    private GitRepositoryEntry gitRepositoryEntry;

    @BeforeEach
    void setup() {
        pipelineCodeHandler = new PipelineCodeHandler(INDEX_PREFIX, INDEX_NAME, apiClient,
                new ElasticIndexService(), FILE_INDEX_PATHS, objectMapper, pipelineLoader,
                new PipelineCodeMapper(), "master");

        expectedPipelineEvent = new PipelineEvent();
        expectedPipelineEvent.setEventType(EventType.INSERT);
        expectedPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        expectedPipelineEvent.setObjectId(1L);
        expectedPipelineEvent.setCreatedDate(LocalDateTime.now());
        expectedPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");

        pipeline = new Pipeline();
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

        gitPushEventData = new GitEventData();
        gitPushEventData.setGitEventType(GitEventType.push);
        gitPushEventData.setPaths(Collections.singletonList(FILE_INDEX_PATHS));
        gitPushEventData.setVersion(VERSION);

        gitTagEventData = new GitEventData();
        gitTagEventData.setGitEventType(GitEventType.tag_push);
        gitTagEventData.setPaths(Collections.singletonList(FILE_INDEX_PATHS));
        gitTagEventData.setVersion(VERSION);

        gitRepositoryEntry = new GitRepositoryEntry();
        gitRepositoryEntry.setId("1");
        gitRepositoryEntry.setName(TEST_NAME);
        gitRepositoryEntry.setPath(FOLDER_INDEX_PATHS);
        gitRepositoryEntry.setType("blob");

        container = EntityContainer.<PipelineDoc>builder()
                .entity(pipelineDoc)
                .owner(USER)
                .metadata(METADATA)
                .permissions(PERMISSIONS_CONTAINER)
                .build();

        docWriteRequest = new UpdateRequest();
        expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);

        expectedPipeline = new Pipeline();
    }

 @Test
    void createRequestsForVersionEvents(){
        String expectedPath = "expectedPath";
        List<String> expectedPathList = new ArrayList<>();
        expectedPathList.add(expectedPath);
        GitEventData expectedGitEventData =  GitEventData.builder()
                .gitEventType(GitEventType.push)
                .paths(expectedPathList)
                .version(VERSION)
                .build();
        Pipeline expectedPipeline = new Pipeline();
        GitEventDescription expectedGitEventDescription = new GitEventDescription(expectedPipelineEvent, expectedGitEventData);
        List<GitEventDescription> expectedGitEventDescriptionList = new ArrayList<>();
        expectedGitEventDescriptionList.add(expectedGitEventDescription);
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        when(pipelineCodeHandler.createRequestsForVersionEvents(expectedGitEventDescriptionList, INDEX_NAME, expectedPipeline, PERMISSIONS_CONTAINER))
                .thenReturn(expectedListOfRequests);
        pipelineCodeHandler.createRequestsForVersionEvents(expectedGitEventDescriptionList, INDEX_NAME, expectedPipeline, PERMISSIONS_CONTAINER);
        assertEquals(expectedListOfRequests, expectedListOfRequests);
        verify(pipelineCodeHandler, atLeastOnce())
                .createRequestsForVersionEvents(expectedGitEventDescriptionList, INDEX_NAME, expectedPipeline, PERMISSIONS_CONTAINER);
    }


   @Test
   void getDocRequestsByType(){
       GitEventType expectedGitEventType = GitEventType.push;
       GitEventData expectedGitEventData = new GitEventData();
       List<String> paths = new ArrayList<>();
       paths.add(TEST_PATH);
       expectedGitEventData.setGitEventType(expectedGitEventType);
       expectedGitEventData.setPaths(paths);
       expectedGitEventData.setVersion(TEST_VERSION);
       GitEventDescription expectedGitEventDescription = new GitEventDescription(expectedPipelineEvent, expectedGitEventData);
       List<GitEventDescription> expectedGitEventDescriptionList = new ArrayList<>();
       GitEventDescription actualGitEventDescription = new GitEventDescription(expectedPipelineEvent, expectedGitEventData);
       expectedGitEventDescriptionList.add(expectedGitEventDescription);
       List<GitEventDescription> actualGitEventDescriptionList = new ArrayList<>();
       actualGitEventDescriptionList.add(actualGitEventDescription);
       GitEventType actualGitEventType = GitEventType.push;
       List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
       actualListOfRequests.add(docWriteRequest);
       String actualIndexName = "pipeline-code";
       PermissionsContainer actualPermissionContainer = buildPermissions(ALLOWED_USERS, DENIED_USERS, ALLOWED_GROUPS, DENIED_GROUPS);
       Pipeline actualPipeline = new Pipeline();
       actualPipeline.setId(1L);
       actualPipeline.setName(TEST_NAME);
       actualPipeline.setCreatedDate(DateUtils.now());
       actualPipeline.setParentFolderId(2L);
       actualPipeline.setDescription(TEST_DESCRIPTION);
       actualPipeline.setRepository(TEST_REPO);
       actualPipeline.setTemplateId(TEST_TEMPLATE);

       when(pipelineCodeHandler
               .getDocRequestsByType(INDEX_NAME, expectedPipeline, PERMISSIONS_CONTAINER, expectedGitEventType, expectedGitEventDescriptionList))
               .thenReturn(expectedListOfRequests);
       pipelineCodeHandler
               .getDocRequestsByType(actualIndexName, actualPipeline, actualPermissionContainer, actualGitEventType,actualGitEventDescriptionList);
       assertEquals(expectedListOfRequests, actualListOfRequests);
       verify(pipelineCodeHandler)
               .getDocRequestsByType(INDEX_NAME, expectedPipeline, PERMISSIONS_CONTAINER, expectedGitEventType, expectedGitEventDescriptionList);
   }

@Test
   void mapPipelineEventToGitEvent(){
       GitEventType expectedGitEventType = GitEventType.push;
       GitEventData expectedGitEventData = new GitEventData();
       List<String> paths = new ArrayList<>();
       paths.add(TEST_PATH);
       expectedGitEventData.setGitEventType(expectedGitEventType);
       expectedGitEventData.setPaths(paths);
       expectedGitEventData.setVersion(TEST_VERSION);
       GitEventDescription expectedGitEventDescription = new GitEventDescription(expectedPipelineEvent, expectedGitEventData);

       when(pipelineCodeHandler.mapPipelineEventToGitEvent(expectedPipelineEvent)).thenReturn(expectedGitEventDescription);
       pipelineCodeHandler.mapPipelineEventToGitEvent(expectedPipelineEvent);
       assertEquals(expectedGitEventDescription, expectedGitEventDescription);
       verify(pipelineCodeHandler).mapPipelineEventToGitEvent(expectedPipelineEvent);
   }


    @Test
    void shouldProcessGitPushEventTest() throws EntityNotFoundException, IOException {
        when(pipelineLoader.loadEntity(anyLong())).thenReturn(Optional.ofNullable(container));
        when(objectMapper.readValue(anyString(), ArgumentMatchers.eq(GitEventData.class))).thenReturn(gitPushEventData);
        when(apiClient.getPipelineFile(anyLong(), anyString(), anyString())).thenReturn(TEST_VALUE);

        List<DocWriteRequest> docWriteRequests =
                pipelineCodeHandler.processGitEvents(1L, Collections.singletonList(expectedPipelineEvent));
        assertEquals(1, docWriteRequests.size());

        DocWriteRequest request = docWriteRequests.get(0);
        assertAll("request",
            () -> assertEquals(INDEX_PREFIX + INDEX_NAME + "-" + VERSION, request.index()),
            () -> assertEquals(VERSION + "-" + FILE_INDEX_PATHS, request.id()),
            () -> assertEquals(DOC_TYPE, request.type()));
    }

    @Test
    void shouldProcessGitTagEventTest() throws EntityNotFoundException, IOException {
        when(pipelineLoader.loadEntity(anyLong())).thenReturn(Optional.ofNullable(container));
        when(objectMapper.readValue(anyString(), ArgumentMatchers.eq(GitEventData.class))).thenReturn(gitTagEventData);
        when(apiClient.getPipelineFile(anyLong(), anyString(), anyString())).thenReturn(TEST_VALUE);

        List<DocWriteRequest> docWriteRequests =
                pipelineCodeHandler.processGitEvents(1L, Collections.singletonList(expectedPipelineEvent));
        assertEquals(1, docWriteRequests.size());

        DocWriteRequest request = docWriteRequests.get(0);
        assertAll("request",
            () -> assertEquals(INDEX_PREFIX + INDEX_NAME + "-" + VERSION, request.index()),
            () -> assertEquals(VERSION + "-" + FILE_INDEX_PATHS, request.id()),
            () -> assertEquals(DOC_TYPE, request.type()));
    }

    @Test
    void shouldCreatePipelineCodeDocumentsTest() {
        when(apiClient.loadRepositoryContents(anyLong(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(gitRepositoryEntry));
        when(apiClient.getPipelineFile(anyLong(), anyString(), anyString())).thenReturn(TEST_VALUE);
        List<DocWriteRequest> docWriteRequests = pipelineCodeHandler.createPipelineCodeDocuments(pipeline,
                PERMISSIONS_CONTAINER, TEST_VERSION, INDEX_NAME, Collections.singletonList(FOLDER_INDEX_PATHS));
        assertEquals(1, docWriteRequests.size());

        DocWriteRequest request = docWriteRequests.get(0);
        assertAll("request",
            () -> assertEquals(INDEX_NAME, request.index()),
            () -> assertEquals(TEST_VERSION + "-" + FOLDER_INDEX_PATHS, request.id()),
            () -> assertEquals(DOC_TYPE, request.type()));
    }
}

*/
