package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.model.PipelineEvent;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
class BulkResponsePostProcessorImplTest {

    @Mock
    private BulkResponsePostProcessorImpl bulkResponsePostProcessor;

    @Test
    void postProcessResponse() {
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
        }).when(bulkResponsePostProcessor).postProcessResponse(anyList(), anyList(), anyLong(), any(LocalDateTime.class));
        bulkResponsePostProcessor
                .postProcessResponse(anyList(), anyList(), anyLong(), any(LocalDateTime.class));
        verify(bulkResponsePostProcessor, atLeastOnce())
                .postProcessResponse(anyList(), anyList(), anyLong(), any(LocalDateTime.class));
    }
}