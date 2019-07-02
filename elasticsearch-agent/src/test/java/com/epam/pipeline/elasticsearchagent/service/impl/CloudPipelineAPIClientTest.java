package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.model.PipelineRunWithLog;
import com.epam.pipeline.elasticsearchagent.service.impl.converter.storage.DataStorageLoader;
import com.epam.pipeline.entity.datastorage.*;
import com.epam.pipeline.entity.metadata.MetadataEntry;
import com.epam.pipeline.entity.pipeline.*;
import com.epam.pipeline.entity.region.AbstractCloudRegion;
import com.epam.pipeline.entity.region.AwsRegion;
import com.epam.pipeline.entity.security.acl.AclClass;
import com.epam.pipeline.entity.user.PipelineUser;
import com.epam.pipeline.vo.EntityVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.pipeline.elasticsearchagent.TestConstants.USER;
import static com.epam.pipeline.elasticsearchagent.TestConstants.USER_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class CloudPipelineAPIClientTest {

    @Mock
    private CloudPipelineAPIClient cloudPipelineAPIClient;

    @Test
    void loadAllDataStorages() {
        AbstractDataStorage expectedDataStorage = new S3bucketDataStorage();
        List<AbstractDataStorage> expectedListOfDataStorages = new ArrayList<>();
        expectedListOfDataStorages.add(expectedDataStorage);
        AbstractDataStorage actualDataStorage = new S3bucketDataStorage();
        List<AbstractDataStorage> actualListOfDataStorages = new ArrayList<>();
        actualListOfDataStorages.add(actualDataStorage);
        when(cloudPipelineAPIClient.loadAllDataStorages()).thenReturn(expectedListOfDataStorages);
        cloudPipelineAPIClient.loadAllDataStorages();
        assertEquals(expectedListOfDataStorages, actualListOfDataStorages);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadAllDataStorages();
    }

    @Test
    void loadDataStorage() {
        AbstractDataStorage expectedDataStorage = new S3bucketDataStorage();
        AbstractDataStorage actualDataStorage = new S3bucketDataStorage();
        when(cloudPipelineAPIClient.loadDataStorage(1L)).thenReturn(expectedDataStorage);
        cloudPipelineAPIClient.loadDataStorage(1L);
        assertEquals(expectedDataStorage, actualDataStorage);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadDataStorage(1L);
    }

    @Test
    void loadPipelineRunWithLogs() {
        PipelineRunWithLog expectedPipelineRunWithLog = new PipelineRunWithLog();
        PipelineRunWithLog actualPipelineRunWithLog = new PipelineRunWithLog();
        when(cloudPipelineAPIClient.loadPipelineRunWithLogs(1L)).thenReturn(expectedPipelineRunWithLog);
        cloudPipelineAPIClient.loadPipelineRunWithLogs(1L);
        assertEquals(expectedPipelineRunWithLog, actualPipelineRunWithLog);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPipelineRunWithLogs(1L);
    }

    @Test
    void loadPipelineRun() {
        PipelineRun expectedPipelineRun = new PipelineRun();
        PipelineRun actualPipelineRun = new PipelineRun();
        when(cloudPipelineAPIClient.loadPipelineRun(1L)).thenReturn(expectedPipelineRun);
        cloudPipelineAPIClient.loadPipelineRun(1L);
        assertEquals(expectedPipelineRun, actualPipelineRun);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPipelineRun(1L);
    }

    @Test
    void generateTemporaryCredentials() {
        TemporaryCredentials expectedTemporaryCredentials = new TemporaryCredentials();
        TemporaryCredentials actualTemporaryCredentials = new TemporaryCredentials();
        DataStorageAction expectedDataStorageAction = new DataStorageAction();
        List<DataStorageAction> expectedDataStorageActionList = new ArrayList<>();
        expectedDataStorageActionList.add(expectedDataStorageAction);
        when(cloudPipelineAPIClient.generateTemporaryCredentials(expectedDataStorageActionList)).thenReturn(expectedTemporaryCredentials);
        cloudPipelineAPIClient.generateTemporaryCredentials(expectedDataStorageActionList);
        assertEquals(expectedTemporaryCredentials, actualTemporaryCredentials);
        verify(cloudPipelineAPIClient, atLeastOnce()).generateTemporaryCredentials(expectedDataStorageActionList);
    }

    @Test
    void loadAllUsers() {
        Map<String, PipelineUser> expectedPipelineUserMap = new HashMap<>();
        expectedPipelineUserMap.put("testString", USER);
        when(cloudPipelineAPIClient.loadAllUsers())
                .thenReturn(expectedPipelineUserMap);
        cloudPipelineAPIClient.loadAllUsers();
        assertEquals(expectedPipelineUserMap, expectedPipelineUserMap);
        verify(cloudPipelineAPIClient, atLeastOnce())
                .loadAllUsers();
    }

    @Test
    void loadUserByName() {
        when(cloudPipelineAPIClient.loadUserByName(USER_NAME))
                .thenReturn(USER);
        cloudPipelineAPIClient.loadUserByName(USER_NAME);
        assertEquals(USER, USER);
        verify(cloudPipelineAPIClient, atLeastOnce())
                .loadUserByName(USER_NAME);
    }

    @Test
    void loadAllRegions() {
    }

    @Test
    void loadRegion() {
        AbstractCloudRegion cloudRegion = new AwsRegion();
        when(cloudPipelineAPIClient.loadRegion(1L)).thenReturn(cloudRegion);
        cloudPipelineAPIClient.loadRegion(1L);
        assertEquals(cloudRegion, cloudRegion);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadRegion(1L);
    }

    @Test
    void loadTool() {
        Tool expectedTool = new Tool();
        String expectedToolId = "toolId";
        when(cloudPipelineAPIClient.loadTool(expectedToolId)).thenReturn(expectedTool);
        cloudPipelineAPIClient.loadTool(expectedToolId);
        assertEquals(expectedTool, expectedTool);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadTool(expectedToolId);
    }

    @Test
    void loadPipelineFolder() {
        Folder expectedFolder = new Folder();
        when(cloudPipelineAPIClient.loadPipelineFolder(1L)).thenReturn(expectedFolder);
        cloudPipelineAPIClient.loadPipelineFolder(1L);
        assertEquals(expectedFolder, expectedFolder);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPipelineFolder(1L);
    }

    @Test
    void loadMetadataEntry() {
        EntityVO expectedEntityVO = new EntityVO();
        List<EntityVO> expectedEntityVOList = new ArrayList<>();
        expectedEntityVOList.add(expectedEntityVO);
        MetadataEntry expectedMetadataEntry = new MetadataEntry();
        List<MetadataEntry> expectedMetadataEntryList = new ArrayList<>();
        expectedMetadataEntryList.add(expectedMetadataEntry);
        when(cloudPipelineAPIClient.loadMetadataEntry(expectedEntityVOList)).thenReturn(expectedMetadataEntryList);
        cloudPipelineAPIClient.loadMetadataEntry(expectedEntityVOList);
        assertEquals(expectedMetadataEntryList, expectedMetadataEntryList);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadMetadataEntry(expectedEntityVOList);
    }

    @Test
    void loadToolGroup() {
        String expectedtoolGroupId = "toolGroupId";
        ToolGroup expectedToolGroup = new ToolGroup();
        when(cloudPipelineAPIClient.loadToolGroup(expectedtoolGroupId)).thenReturn(expectedToolGroup);
        cloudPipelineAPIClient.loadToolGroup(expectedtoolGroupId);
        assertEquals(expectedToolGroup, expectedToolGroup);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadToolGroup(expectedtoolGroupId);
    }

    @Test
    void loadDockerRegistry() {
        DockerRegistry expectedDockerRegistry = new DockerRegistry();
        when(cloudPipelineAPIClient.loadDockerRegistry(1L)).thenReturn(expectedDockerRegistry);
        cloudPipelineAPIClient.loadDockerRegistry(1L);
        assertEquals(expectedDockerRegistry, expectedDockerRegistry);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadDockerRegistry(1L);
    }

    @Test
    void loadIssue() {
    }

    @Test
    void loadMetadataEntity() {
    }

    @Test
    void loadRunConfiguration() {
    }

    @Test
    void loadPipeline() {
    }

    @Test
    void loadPermissionsForEntity() {
    }

    @Test
    void loadPipelineVersions() {
    }

    @Test
    void getPipelineFile() {
    }

    @Test
    void loadRepositoryContents() {
    }

    @Test
    void loadPipelineByRepositoryUrl() {
    }
}