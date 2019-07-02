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

import com.epam.pipeline.elasticsearchagent.model.EntityContainer;
import com.epam.pipeline.elasticsearchagent.model.EventType;
import com.epam.pipeline.elasticsearchagent.model.PipelineEvent;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")

@ExtendWith(MockitoExtension.class)

class PipelineSynchronizerTest{

    private PipelineEvent expectedPipelineEvent;
    private List<PipelineEvent> expectedList;

    @BeforeEach
    public void setup() {
        expectedPipelineEvent = new PipelineEvent();
        expectedPipelineEvent.setEventType(EventType.INSERT);
        expectedPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        expectedPipelineEvent.setObjectId(1L);
        expectedPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        expectedPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");

        expectedList = new ArrayList<>();
        expectedList.add(expectedPipelineEvent);
    }

    @Mock
    private PipelineSynchronizer pipelineSynchronizer;

    @Test
    void shouldStartSynchronizeTest() throws InterruptedException {
        MockitoAnnotations.initMocks(this);
        TimeUnit.SECONDS.sleep(2);
        LocalDateTime expectedSyncStart = LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0);
        LocalDateTime expectedLastSyncTime = LocalDateTime.of(2019, Month.JUNE, 25, 11, 11, 0);
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            assertEquals(expectedSyncStart, arg0);
            assertEquals(expectedLastSyncTime, arg1);
            return null;
        }).when(pipelineSynchronizer).synchronize(any(LocalDateTime.class), any(LocalDateTime.class));
        pipelineSynchronizer
                .synchronize(LocalDateTime
                        .of(2019, Month.JUNE, 26, 11, 11, 0), LocalDateTime
                        .of(2019, Month.JUNE, 25, 11, 11, 0));
        verify(pipelineSynchronizer, atLeast(1)).synchronize(expectedSyncStart, expectedLastSyncTime);
    }

    @Test
    void synchronizePipelineEvents(){
        LocalDateTime expectedSyncStart = LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0);
        PipelineEvent actualPipelineEvent = new PipelineEvent();
        actualPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        actualPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        actualPipelineEvent.setEventType(EventType.INSERT);
        actualPipelineEvent.setObjectId(1L);
        actualPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");
        List<PipelineEvent> actualList = new ArrayList<>();
        actualList.add(actualPipelineEvent);
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            Object arg2 = invocation.getArgument(2);
            assertEquals(expectedPipelineEvent.getObjectId(), arg0);
            assertEquals(expectedList, arg1);
            assertEquals(expectedSyncStart, arg2);
            return null;
        }).when(pipelineSynchronizer).synchronizePipelineEvents(any(Long.class), anyList(), any(LocalDateTime.class));
        pipelineSynchronizer.synchronizePipelineEvents(1L, actualList, LocalDateTime
                        .of(2019, Month.JUNE, 26, 11, 11, 0));
        verify(pipelineSynchronizer, atLeast(1)).synchronizePipelineEvents(1L, expectedList, expectedSyncStart);
    }

    @Test
    void createIndexDocuments(){
        String expectedPipelineIndex = "expectedPipelineIndex";
        String expectedCodeIndex = "expectedCodeIndex";
        PipelineSynchronizer.PipelineDocRequests.PipelineDocRequestsBuilder pipelineDocRequestsBuilder = new PipelineSynchronizer
                .PipelineDocRequests.PipelineDocRequestsBuilder();
        EntityContainer entityContainer = new EntityContainer();
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            Object arg2 = invocation.getArgument(2);
            Object arg3 = invocation.getArgument(3);
            Object arg4 = invocation.getArgument(4);
            assertEquals(expectedPipelineEvent, arg0);
            assertEquals(expectedPipelineIndex, arg1);
            assertEquals(expectedCodeIndex, arg2);
            assertEquals(pipelineDocRequestsBuilder, arg3);
            assertEquals(entityContainer, arg4);
            return null;
        }).when(pipelineSynchronizer)
                .createIndexDocuments(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex, pipelineDocRequestsBuilder, entityContainer);
        pipelineSynchronizer
                .createIndexDocuments(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex, pipelineDocRequestsBuilder, entityContainer);
        verify(pipelineSynchronizer, atLeast(1))
                .createIndexDocuments(expectedPipelineEvent, expectedPipelineIndex, expectedCodeIndex, pipelineDocRequestsBuilder, entityContainer);
    }

/*    @Test
    void buildDocRequests() {
        MockitoAnnotations.initMocks(this);
        String expectedPipelineIndex = "expectedPipelineIndex";
        String expectedCodeIndex = "expectedCodeIndex";
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        PipelineSynchronizer.PipelineDocRequests expectedPipelineDocRequests = PipelineSynchronizer.PipelineDocRequests.builder()
                .pipelineId(1L)
                .pipelineRequests(expectedListOfRequests)
                .codeRequests(expectedListOfRequests)
                .build();
        when(pipelineSynchronizer.buildDocRequests(expectedPipelineEvent.getObjectId(), expectedList, expectedPipelineIndex, expectedCodeIndex))
                .thenReturn(expectedPipelineDocRequests);
        pipelineSynchronizer.buildDocRequests(expectedPipelineEvent.getObjectId(), expectedList, expectedPipelineIndex, expectedCodeIndex);
        assertEquals(expectedPipelineDocRequests, expectedPipelineDocRequests);
        verify(pipelineSynchronizer, atLeastOnce())
                .buildDocRequests(expectedPipelineEvent.getObjectId(), expectedList, expectedPipelineIndex, expectedCodeIndex);
    }*/
}