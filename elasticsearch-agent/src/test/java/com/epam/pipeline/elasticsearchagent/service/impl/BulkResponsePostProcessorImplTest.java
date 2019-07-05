/*
package com.epam.pipeline.elasticsearchagent.service.impl;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

class BulkResponsePostProcessorImplTest {

    RestHighLevelClient client;

    @Autowired
    public BulkResponsePostProcessorImplTest(RestHighLevelClient client) {
        this.client = client;
    }


    @Mock
    private BulkResponsePostProcessorImpl bulkResponsePostProcessor;

    @Test
    void postProcessResponse() throws IOException {
        DocWriteRequest docWriteRequest = new UpdateRequest();
        List<DocWriteRequest> docWriteRequests = new ArrayList<>();
        docWriteRequests.add(docWriteRequest);

        BulkRequest bulkRequest = new BulkRequest();
        docWriteRequests.forEach(bulkRequest::add);
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        List<BulkItemResponse> bulkItemResponses = new ArrayList<>();
        bulkItemResponses.add(bulkResponse);

        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            Object arg2 = invocation.getArgument(2);
            Object arg3 = invocation.getArgument(3);
            assertEquals(anyList(), arg0);
            assertEquals(anyList(), arg1);
            assertEquals(anyLong(), arg2);
            assertEquals(any(LocalDateTime.class), arg3);
            return null;
        }).when(bulkResponsePostProcessor)
        .postProcessResponse(anyList(), anyList(), anyLong(), any(LocalDateTime.class));
        bulkResponsePostProcessor
                .postProcessResponse(anyList(), anyList(), anyLong(), any(LocalDateTime.class));
        verify(bulkResponsePostProcessor, atLeastOnce())
                .postProcessResponse(anyList(), anyList(), anyLong(), any(LocalDateTime.class));
    }
}*/
