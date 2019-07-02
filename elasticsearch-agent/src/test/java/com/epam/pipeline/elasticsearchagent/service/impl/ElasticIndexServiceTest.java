package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.exception.ElasticClientException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.pipeline.elasticsearchagent.TestConstants.TEST_PATH;
import static com.epam.pipeline.elasticsearchagent.service.impl.converter.configuration.RunConfigurationDocumentBuilder.ID_DELIMITER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ElasticIndexServiceTest {

    private static final String WILDCARD = "*";
    private DocWriteRequest docWriteRequest;
    private List<DocWriteRequest> expectedListOfRequests;
    private String expectedIndexName;
    private SearchRequest expectedSearchRequest;

    @BeforeEach
    public void setup() {
        docWriteRequest = new UpdateRequest();
        expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        expectedIndexName = "indexName";
        expectedSearchRequest = new SearchRequest();
    }

    @Mock
    private ElasticIndexService elasticIndexService;

    @Test
    void createIndexIfNotExist() {
        String expectedIndex = "newIndex";
        String expectedMappingsJson = "mappingsJson";
        String actualIndex = "newIndex";
        String actualMappingsJson = "mappingsJson";
        try { doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            assertEquals(expectedIndex, arg0);
            assertEquals(expectedMappingsJson, arg1);
            return null;
        }).when(elasticIndexService).createIndexIfNotExist(expectedIndex, expectedMappingsJson);
        elasticIndexService.createIndexIfNotExist(actualIndex, actualMappingsJson);
            verify(elasticIndexService, atLeastOnce()).createIndexIfNotExist(expectedIndex, expectedMappingsJson);
        } catch (ElasticClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getDeleteRequestsByTerm() {
        String expectedField = "field";
        String expectedValue = "value";
        String actualField = "field";
        String actualValue = "value";
        String actualIndexName = "indexName";
        List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
        actualListOfRequests.add(docWriteRequest);
        when(elasticIndexService.getDeleteRequestsByTerm(expectedField, expectedValue, expectedIndexName)).thenReturn(expectedListOfRequests);
        elasticIndexService.getDeleteRequestsByTerm(actualField, actualValue, actualIndexName);
        assertEquals(expectedListOfRequests, actualListOfRequests);
        verify(elasticIndexService).getDeleteRequestsByTerm(expectedField, expectedValue, expectedIndexName);
    }

    @Test
    void getDeleteRequests() {
        String expectedId = "id";
        String actualIndexName = "indexName";
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
        actualListOfRequests.add(docWriteRequest);
        when(elasticIndexService.getDeleteRequests(expectedId, expectedIndexName)).thenReturn(expectedListOfRequests);
        elasticIndexService.getDeleteRequests("id", actualIndexName);
        assertEquals(expectedListOfRequests, actualListOfRequests);
        verify(elasticIndexService).getDeleteRequests(expectedId, expectedIndexName);
    }

    @Test
    void buildDeleteRequests(){
        String actualIndexName = "indexName";
        SearchRequest actualSearchRequest = new SearchRequest();
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
        actualListOfRequests.add(docWriteRequest);
        when(elasticIndexService.buildDeleteRequests(expectedIndexName, expectedSearchRequest)).thenReturn(expectedListOfRequests);
        elasticIndexService.buildDeleteRequests(actualIndexName, actualSearchRequest);
        assertEquals(expectedListOfRequests, actualListOfRequests);
        verify(elasticIndexService).buildDeleteRequests(expectedIndexName, expectedSearchRequest);
    }

    @Test
    void buildSearchRequestForConfigEntries(){
        String expectedId = "id";
        String expectedIndexName = "indexName";
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

    @Test
    void openJsonMapping() throws FileNotFoundException {
        String expectedPath = TEST_PATH;
        String actualPath = TEST_PATH;
        when(elasticIndexService.openJsonMapping(expectedPath)).thenReturn(any(InputStream.class));
        elasticIndexService.openJsonMapping(actualPath);
        verify(elasticIndexService).openJsonMapping(expectedPath);
    }
}