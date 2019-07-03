package com.epam.pipeline.elasticsearchagent.service.impl;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.tasks.TaskId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeast;

@ExtendWith(MockitoExtension.class)

class ElasticsearchServiceClientImplTest {

    private RestHighLevelClient client;
    private String expectedIndexName;
    private String expectedAlias;

    @BeforeEach
    public void setup() {

        expectedIndexName = "indexName";
        expectedAlias = "alias";
    }

    @Mock
    private ElasticsearchServiceClientImpl elasticsearchServiceClientImpl;

    @Test
    void createIndex() {
        String expectedIndexName = "indexName";
        String expectedSource = "source";
        String actualIndexName = "indexName";
        String actualSource = "source";
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            assertEquals(expectedIndexName, arg0);
            assertEquals(expectedSource, arg1);
            return null;
        }).when(elasticsearchServiceClientImpl).createIndex(expectedIndexName, expectedSource);
        elasticsearchServiceClientImpl.createIndex(actualIndexName, actualSource);
        verify(elasticsearchServiceClientImpl, atLeastOnce()).createIndex(expectedIndexName, expectedSource);
    }

/*    @Test
    void sendRequests() throws IOException {
        DocWriteRequest expectedDocWriteRequest = new UpdateRequest();
        List<DocWriteRequest> expectedListOfRequests = new ArrayList<>();
        expectedListOfRequests.add(expectedDocWriteRequest);
        String expectedIndexName = "String indexName";
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(expectedDocWriteRequest);
        BulkResponse expectedBulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);;
        when(elasticsearchServiceClientImpl.sendRequests(expectedIndexName, expectedListOfRequests)).thenReturn(expectedBulkResponse);
        elasticsearchServiceClientImpl.sendRequests(expectedIndexName, expectedListOfRequests);
        assertEquals(expectedBulkResponse, expectedBulkResponse);
        verify(elasticsearchServiceClientImpl).sendRequests(expectedIndexName, expectedListOfRequests);
    }*/

    @Test
    void deleteIndex() {
        String actualIndexName = "indexName";
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            assertEquals(expectedIndexName, arg0);
            return null;
        }).when(elasticsearchServiceClientImpl).deleteIndex(expectedIndexName);
        elasticsearchServiceClientImpl.deleteIndex(actualIndexName);
        verify(elasticsearchServiceClientImpl, atLeast(1)).deleteIndex(expectedIndexName);
    }

    @Test
    void isIndexExists() {
        String actualIndexName = "indexName";
        boolean isExists = false;
        when(elasticsearchServiceClientImpl.isIndexExists(expectedIndexName)).thenReturn(isExists);
        elasticsearchServiceClientImpl.isIndexExists(actualIndexName);
        assertEquals(isExists, false);
        verify(elasticsearchServiceClientImpl).isIndexExists(expectedIndexName);
    }

    @Test
    void createIndexAlias() {
        String actualAlias = "alias";
        String actualIndexName = "indexName";
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            assertEquals(expectedIndexName, arg0);
            assertEquals(expectedAlias, arg1);
            return null;
        }).when(elasticsearchServiceClientImpl).createIndexAlias(expectedIndexName, expectedAlias);
        elasticsearchServiceClientImpl.createIndexAlias(actualIndexName, actualAlias);
        verify(elasticsearchServiceClientImpl, atLeast(1)).createIndexAlias(expectedIndexName, expectedAlias);
    }

    @Test
    void getIndexNameByAlias() {
        String actualAlias = "alias";
        String actualIndexName = "indexName";
        when(elasticsearchServiceClientImpl.getIndexNameByAlias(expectedAlias)).thenReturn(expectedIndexName);
        elasticsearchServiceClientImpl.getIndexNameByAlias(actualAlias);
        assertEquals(expectedIndexName, actualIndexName);
        verify(elasticsearchServiceClientImpl).getIndexNameByAlias(expectedAlias);
    }

    @Test
    void search() {
        SearchRequest expectedSearchRequest = new SearchRequest();
        TaskId taskId = new TaskId("nodeId", 1l);
        expectedSearchRequest.setBatchedReduceSize(5);
        expectedSearchRequest.setMaxConcurrentShardRequests(5);
        expectedSearchRequest.setPreFilterShardSize(5);
        expectedSearchRequest.setParentTask(taskId);
        SearchRequest actualSearchRequest = new SearchRequest();
        actualSearchRequest.setBatchedReduceSize(5);
        actualSearchRequest.setMaxConcurrentShardRequests(5);
        actualSearchRequest.setPreFilterShardSize(5);
        actualSearchRequest.setParentTask(taskId);
        when(elasticsearchServiceClientImpl.search(expectedSearchRequest)).thenReturn(any(SearchResponse.class));
        elasticsearchServiceClientImpl.search(expectedSearchRequest);
        verify(elasticsearchServiceClientImpl).search(expectedSearchRequest);
    }
}