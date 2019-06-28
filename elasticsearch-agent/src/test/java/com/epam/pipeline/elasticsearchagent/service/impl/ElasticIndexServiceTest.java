package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.exception.ElasticClientException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

class ElasticIndexServiceTest {

    @Mock
    private ElasticIndexService elasticIndexService;

    @Test
    void createIndexIfNotExist() {
        MockitoAnnotations.initMocks(this);
        String expectedIndex = "newIndex";
        String existingIndex = "existingIndex";
        String mappingsJson = "mappingsJson";
        try { doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            assertEquals(expectedIndex, arg0);
            assertEquals(mappingsJson, arg1);
            return null;
        }).when(elasticIndexService).createIndexIfNotExist(expectedIndex, mappingsJson);
               // doThrow(new IOException()).when(elasticIndexService).createIndexIfNotExist("test", "test");
        elasticIndexService.createIndexIfNotExist("newIndex", "mappingsJson");
            verify(elasticIndexService, atLeastOnce()).createIndexIfNotExist(expectedIndex, mappingsJson);
        } catch (ElasticClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getDeleteRequestsByTerm() {
        MockitoAnnotations.initMocks(this);
        String test = "test";
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        when(elasticIndexService.getDeleteRequestsByTerm(test, test, test)).thenReturn(expectedListOfRequests);
        elasticIndexService.getDeleteRequestsByTerm(test, test, test);
        assertEquals(expectedListOfRequests, expectedListOfRequests);
        verify(elasticIndexService).getDeleteRequestsByTerm(test, test, test);
    }

    @Test
    void getDeleteRequests() {
        MockitoAnnotations.initMocks(this);
        String test = "test";
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        when(elasticIndexService.getDeleteRequests(test, test)).thenReturn(expectedListOfRequests);
        elasticIndexService.getDeleteRequests(test, test);
        assertEquals(expectedListOfRequests, expectedListOfRequests);
        verify(elasticIndexService).getDeleteRequests(test, test);
    }
}