package com.epam.pipeline.elasticsearchagent.dao;

import com.epam.pipeline.elasticsearchagent.model.EventType;
import com.epam.pipeline.elasticsearchagent.model.PipelineEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class PipelineEventDaoUnitTest {

    private PipelineEvent expectedPipelineEvent;
    private List<PipelineEvent> expectedList;

    @Mock
    private PipelineEventDao pipelineEventDao;

    @Before
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

    @Test
    public void createPipelineEvent(){
        PipelineEvent actualPipelineEvent = new PipelineEvent();
        actualPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        actualPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        actualPipelineEvent.setEventType(EventType.INSERT);
        actualPipelineEvent.setObjectId(1L);
        actualPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            assertEquals(expectedPipelineEvent, arg0);
            return null;
        }).when(pipelineEventDao).createPipelineEvent(expectedPipelineEvent);
        pipelineEventDao.createPipelineEvent(actualPipelineEvent);
        verify(pipelineEventDao, atLeastOnce()).createPipelineEvent(expectedPipelineEvent);
    }

    @Test
    public void loadPipelineEventsByObjectType() {
        PipelineEvent actualPipelineEvent = new PipelineEvent();
        actualPipelineEvent.setObjectType(PipelineEvent.ObjectType.PIPELINE);
        actualPipelineEvent.setCreatedDate(LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        actualPipelineEvent.setEventType(EventType.INSERT);
        actualPipelineEvent.setObjectId(1L);
        actualPipelineEvent.setData("{\"tag\": {\"type\": \"string\", \"value\": \"admin\"}}");
        List<PipelineEvent> actualList = new ArrayList<>();
        actualList.add(actualPipelineEvent);
        when(pipelineEventDao.loadPipelineEventsByObjectType(expectedPipelineEvent.getObjectType(), expectedPipelineEvent.getCreatedDate()))
                .thenReturn(expectedList);
        pipelineEventDao.loadPipelineEventsByObjectType(actualPipelineEvent.getObjectType(), actualPipelineEvent.getCreatedDate());
        assertEquals(expectedList, actualList);
        verify(pipelineEventDao, atLeastOnce())
                .loadPipelineEventsByObjectType(expectedPipelineEvent.getObjectType(), expectedPipelineEvent.getCreatedDate());
    }

    @Test
    public void deleteEventByObjectId() {
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            Object arg2 = invocation.getArgument(2);
            assertEquals(expectedPipelineEvent.getObjectId(), arg0);
            assertEquals(expectedPipelineEvent.getObjectType(), arg1);
            assertEquals(expectedPipelineEvent.getCreatedDate(), arg2);
            return null;
        }).when(pipelineEventDao).deleteEventByObjectId(any(Long.class), any(PipelineEvent.ObjectType.class), any(LocalDateTime.class));
        pipelineEventDao
                .deleteEventByObjectId(1L, PipelineEvent
                        .ObjectType.PIPELINE, LocalDateTime.of(2019, Month.JUNE, 26, 11, 11, 0));
        verify(pipelineEventDao, atLeastOnce())
                .deleteEventByObjectId(expectedPipelineEvent
                        .getObjectId(), expectedPipelineEvent.getObjectType(), expectedPipelineEvent.getCreatedDate());
    }

    @Test
    public void setCreateEventQuery() {
        String expectedCreateEventQuery = "expectedCreateEventQuery";
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            assertEquals(expectedCreateEventQuery, arg0);
            return null;
        }).when(pipelineEventDao).setCreateEventQuery(any(String.class));
        pipelineEventDao.setCreateEventQuery("expectedCreateEventQuery");
        verify(pipelineEventDao, atLeastOnce()).setCreateEventQuery(expectedCreateEventQuery);
    }

    @Test
    public void setLoadAllEventsByObjectTypeQuery() {
        String expectedEventsByObjectTypeQuery = "allEventsByObjectTypeQuery";
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            assertEquals(expectedEventsByObjectTypeQuery, arg0);
            return null;
        }).when(pipelineEventDao).setLoadAllEventsByObjectTypeQuery(any(String.class));
        pipelineEventDao.setLoadAllEventsByObjectTypeQuery("allEventsByObjectTypeQuery");
        verify(pipelineEventDao, atLeastOnce()).setLoadAllEventsByObjectTypeQuery(expectedEventsByObjectTypeQuery);
    }

    @Test
    public void setDeleteEventQuery() {
        String expectedDeleteEventQuery = "deleteEventQuery";
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            assertEquals(expectedDeleteEventQuery, arg0);
            return null;
        }).when(pipelineEventDao).setDeleteEventQuery(any(String.class));
        pipelineEventDao.setDeleteEventQuery("deleteEventQuery");
        verify(pipelineEventDao, atLeastOnce()).setDeleteEventQuery(expectedDeleteEventQuery);
    }
}
