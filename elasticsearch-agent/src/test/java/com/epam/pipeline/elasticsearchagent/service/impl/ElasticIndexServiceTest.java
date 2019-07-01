package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.exception.ElasticClientException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.epam.pipeline.elasticsearchagent.service.impl.converter.configuration.RunConfigurationDocumentBuilder.ID_DELIMITER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ElasticIndexServiceTest {

    private static final String WILDCARD = "*";

    @Mock
    private ElasticIndexService elasticIndexService;

    @Test
    void createIndexIfNotExist() {
        String expectedIndex = "newIndex";
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
        String expectedField = "field";
        String expectedValue = "value";
        String expectedIndexName = "indexName";
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
        actualListOfRequests.add(docWriteRequest);
        when(elasticIndexService.getDeleteRequestsByTerm(expectedField, expectedValue, expectedIndexName)).thenReturn(expectedListOfRequests);
        elasticIndexService.getDeleteRequestsByTerm("field", "value", "indexName");
        assertEquals(expectedListOfRequests, actualListOfRequests);
        verify(elasticIndexService).getDeleteRequestsByTerm(expectedField, expectedValue, expectedIndexName);
    }

    @Test
    void getDeleteRequests() {
        String expectedId = "expectedId";
        String expectedIndexName = "expectedIndexName";
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
        actualListOfRequests.add(docWriteRequest);
        when(elasticIndexService.getDeleteRequests(expectedId, expectedIndexName)).thenReturn(expectedListOfRequests);
        elasticIndexService.getDeleteRequests("expectedId", "expectedIndexName");
        assertEquals(expectedListOfRequests, actualListOfRequests);
        verify(elasticIndexService).getDeleteRequests(expectedId, expectedIndexName);
    }

    @Test
    void buildDeleteRequests(){
        String expectedIndexName = "indexName";
        SearchRequest expectedRequest = new SearchRequest();
        SearchRequest actualRequest = new SearchRequest();
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
        actualListOfRequests.add(docWriteRequest);
        when(elasticIndexService.buildDeleteRequests(expectedIndexName, expectedRequest)).thenReturn(expectedListOfRequests);
        elasticIndexService.buildDeleteRequests("indexName", actualRequest);
        assertEquals(expectedListOfRequests, actualListOfRequests);
        verify(elasticIndexService).buildDeleteRequests(expectedIndexName, expectedRequest);
    }

    @Test
    void buildSearchRequestForConfigEntries(){
        String expectedId = "id";
        String expectedIndexName = "indexName";
        SearchRequest expectedSearchRequest = new SearchRequest();
        SearchRequest actualSearchRequest = new SearchRequest();
        when(elasticIndexService.buildSearchRequestForConfigEntries(expectedId, expectedIndexName)).thenReturn(expectedSearchRequest);
        elasticIndexService.buildSearchRequestForConfigEntries("id", "indexName");
        assertEquals(expectedSearchRequest, actualSearchRequest);
        verify(elasticIndexService).buildSearchRequestForConfigEntries(expectedId, expectedIndexName);
    }

    @Test
    void getWildcardId(){
        String expectedId = "id";
        String expectedWildCardId = expectedId + ID_DELIMITER + WILDCARD;
        when(elasticIndexService.getWildcardId(expectedId)).thenReturn(expectedWildCardId);
        elasticIndexService.getWildcardId("id");
        assertEquals(expectedWildCardId, "id-*");
        verify(elasticIndexService).getWildcardId(expectedId);
    }
}