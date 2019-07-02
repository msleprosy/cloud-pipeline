package com.epam.pipeline.elasticsearchagent.service.impl;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class ElasticsearchServiceClientImplTest {

    private RestHighLevelClient client;

    @Mock
    private ElasticsearchServiceClientImpl elasticsearchServiceClientImpl;

    @Test
    void createIndex() {
    }

    @Test
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
    }

    @Test
    void deleteIndex() {
    }

    @Test
    void isIndexExists() {
    }

    @Test
    void createIndexAlias() {
    }

    @Test
    void getIndexNameByAlias() {
    }

    @Test
    void search() {
    }
}