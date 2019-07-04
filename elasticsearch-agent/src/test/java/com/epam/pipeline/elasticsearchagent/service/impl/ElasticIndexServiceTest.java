package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.exception.ElasticClientException;
import com.epam.pipeline.elasticsearchagent.service.ElasticsearchServiceClient;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
    private String expectedId;

    @BeforeEach
    public void setup() {
        docWriteRequest = new UpdateRequest();
        expectedListOfRequests = new ArrayList<>();
        //expectedListOfRequests.add(docWriteRequest);
        expectedIndexName = "indexName";
        expectedSearchRequest = new SearchRequest();
        expectedId = "id";
    }

    @Mock
    private ElasticsearchServiceClient elasticsearchServiceClient;

    @Mock
    private ElasticIndexService elasticIndexService;

    @Test
    void shouldCreateIndexIfNotExist() throws ElasticClientException {
        String expectedIndex = "newIndex";
        String expectedMappingsJson = "mappingsJson";
        String actualIndex = "newIndex";
        String actualMappingsJson = "mappingsJson";
         doAnswer(invocationOnElasticIndexService -> {
            Object index = invocationOnElasticIndexService.getArgument(0);
            Object mappingsJson = invocationOnElasticIndexService.getArgument(1);
            assertEquals(expectedIndex, index);
            assertEquals(expectedMappingsJson, mappingsJson);
            return null;
        }).when(elasticsearchServiceClient).createIndex(expectedIndex, expectedMappingsJson);
        elasticsearchServiceClient.createIndex(actualIndex, actualMappingsJson);
        verify(elasticsearchServiceClient, atLeastOnce()).createIndex(expectedIndex, expectedMappingsJson);
    }

    @Test
    void shouldReturnDeleteRequestsByTerm() {
        String expectedField = "field";
        String expectedValue = "value";
        List<DocWriteRequest> actualListOfRequests;
        when(elasticIndexService.buildDeleteRequests(eq(expectedIndexName), any(SearchRequest.class)))
                .thenReturn(expectedListOfRequests);
        SearchSourceBuilder searchSource = new SearchSourceBuilder()
                .query(QueryBuilders.termQuery(expectedField, expectedValue));
        SearchRequest actualRequest = new SearchRequest(expectedIndexName).source(searchSource);
        elasticIndexService.buildDeleteRequests("indexName", actualRequest);
        actualListOfRequests = elasticIndexService
                .getDeleteRequestsByTerm(expectedField, expectedValue, expectedIndexName);
        assertEquals(expectedListOfRequests, actualListOfRequests);
    }

    @Test
    void getDeleteRequests() {
        String actualId = "id";
        String actualIndexName = "indexName";
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(docWriteRequest);
        List<DocWriteRequest> actualListOfRequests = new ArrayList<>();
        actualListOfRequests.add(docWriteRequest);
        when(elasticIndexService.getDeleteRequests(expectedId, expectedIndexName)).thenReturn(expectedListOfRequests);
        elasticIndexService.getDeleteRequests(actualId, actualIndexName);
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
        String actualId = "id";
        String actualIndexName = "indexName";
        SearchRequest actualSearchRequest = new SearchRequest();
        when(elasticIndexService.buildSearchRequestForConfigEntries(expectedId, expectedIndexName)).thenReturn(expectedSearchRequest);
        elasticIndexService.buildSearchRequestForConfigEntries(actualId, actualIndexName);
        assertEquals(expectedSearchRequest, actualSearchRequest);
        verify(elasticIndexService).buildSearchRequestForConfigEntries(expectedId, expectedIndexName);
    }

    @Test
    void getWildcardId(){
        String expectedWildCardId = expectedId + ID_DELIMITER + WILDCARD;
        String actualId = "id";
        String actualWildCardId = actualId + ID_DELIMITER + WILDCARD;
        when(elasticIndexService.getWildcardId(expectedId)).thenReturn(expectedWildCardId);
        elasticIndexService.getWildcardId(actualId);
        assertEquals(expectedWildCardId, actualWildCardId);
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
