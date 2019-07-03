package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.model.PipelineRunWithLog;
import com.epam.pipeline.entity.configuration.RunConfiguration;
import com.epam.pipeline.entity.datastorage.*;
import com.epam.pipeline.entity.git.GitRepositoryEntry;
import com.epam.pipeline.entity.issue.Issue;
import com.epam.pipeline.entity.metadata.MetadataEntity;
import com.epam.pipeline.entity.metadata.MetadataEntry;
import com.epam.pipeline.entity.pipeline.*;
import com.epam.pipeline.entity.region.AbstractCloudRegion;
import com.epam.pipeline.entity.region.AwsRegion;
import com.epam.pipeline.entity.security.acl.AclClass;
import com.epam.pipeline.entity.user.PipelineUser;
import com.epam.pipeline.vo.EntityPermissionVO;
import com.epam.pipeline.vo.EntityVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.pipeline.elasticsearchagent.ObjectCreationUtils.buildPipelineUser;
import static com.epam.pipeline.elasticsearchagent.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class CloudPipelineAPIClientTest {

    private AbstractDataStorage expectedDataStorage;
    private AbstractCloudRegion expectedCloudRegion;
    private Pipeline expectedPipeline;

    @BeforeEach
    public void setup() {
        expectedDataStorage = new S3bucketDataStorage();
        expectedCloudRegion = new AwsRegion();
        expectedPipeline = new Pipeline();
    }

        @Mock
    private CloudPipelineAPIClient cloudPipelineAPIClient;

    @Test
    void loadAllDataStorages() {
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
        Map<String, PipelineUser> actualPipelineUserMap = new HashMap<>();
        actualPipelineUserMap.put("testString", USER);
        when(cloudPipelineAPIClient.loadAllUsers()).thenReturn(expectedPipelineUserMap);
        cloudPipelineAPIClient.loadAllUsers();
        assertEquals(expectedPipelineUserMap, actualPipelineUserMap);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadAllUsers();
    }

    @Test
    void loadUserByName() {
        PipelineUser actualUser = buildPipelineUser(TEST_NAME, USER_NAME, USER_GROUPS);
        when(cloudPipelineAPIClient.loadUserByName(USER_NAME)).thenReturn(USER);
        cloudPipelineAPIClient.loadUserByName(USER_NAME);
        assertEquals(USER, actualUser);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadUserByName(USER_NAME);
    }

    @Test
    void loadAllRegions() {
        List<AbstractCloudRegion> expectedAbstractCloudRegionsList = new ArrayList<>();
        expectedAbstractCloudRegionsList.add(expectedCloudRegion);
        AbstractCloudRegion actualCloudRegion = new AwsRegion();
        List<AbstractCloudRegion> actualAbstractCloudRegionsList = new ArrayList<>();
        actualAbstractCloudRegionsList.add(actualCloudRegion);
        doReturn(expectedAbstractCloudRegionsList).when(cloudPipelineAPIClient).loadAllRegions();
        cloudPipelineAPIClient.loadAllRegions();
        assertEquals(expectedAbstractCloudRegionsList, actualAbstractCloudRegionsList);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadAllRegions();
    }

    @Test
    void loadRegion() {
        AbstractCloudRegion expectedCloudRegion = new AwsRegion();
        AbstractCloudRegion actualCloudRegion = new AwsRegion();
        when(cloudPipelineAPIClient.loadRegion(1L)).thenReturn(expectedCloudRegion);
        cloudPipelineAPIClient.loadRegion(1L);
        assertEquals(expectedCloudRegion, actualCloudRegion);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadRegion(1L);
    }

    @Test
    void loadTool() {
        Tool expectedTool = new Tool();
        Tool actualTool = new Tool();
        String expectedToolId = "toolId";
        when(cloudPipelineAPIClient.loadTool(expectedToolId)).thenReturn(expectedTool);
        cloudPipelineAPIClient.loadTool(expectedToolId);
        assertEquals(expectedTool, actualTool);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadTool(expectedToolId);
    }

    @Test
    void loadPipelineFolder() {
        Folder expectedFolder = new Folder();
        Folder actualFolder = new Folder();
        when(cloudPipelineAPIClient.loadPipelineFolder(1L)).thenReturn(expectedFolder);
        cloudPipelineAPIClient.loadPipelineFolder(1L);
        assertEquals(expectedFolder, actualFolder);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPipelineFolder(1L);
    }

    @Test
    void loadMetadataEntry() {
        EntityVO expectedEntityVO = new EntityVO();
        List<EntityVO> expectedEntityVOList = new ArrayList<>();
        expectedEntityVOList.add(expectedEntityVO);
        EntityVO actualEntityVO = new EntityVO();
        List<EntityVO> actualEntityVOList = new ArrayList<>();
        actualEntityVOList.add(actualEntityVO);

        MetadataEntry expectedMetadataEntry = new MetadataEntry();
        List<MetadataEntry> expectedMetadataEntryList = new ArrayList<>();
        expectedMetadataEntryList.add(expectedMetadataEntry);
        MetadataEntry actualMetadataEntry = new MetadataEntry();
        List<MetadataEntry> actualMetadataEntryList = new ArrayList<>();
        actualMetadataEntryList.add(actualMetadataEntry);

        when(cloudPipelineAPIClient.loadMetadataEntry(expectedEntityVOList)).thenReturn(expectedMetadataEntryList);
        cloudPipelineAPIClient.loadMetadataEntry(actualEntityVOList);
        assertEquals(expectedMetadataEntryList, actualMetadataEntryList);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadMetadataEntry(expectedEntityVOList);
    }

    @Test
    void loadToolGroup() {
        String expectedToolGroupId = "toolGroupId";
        ToolGroup expectedToolGroup = new ToolGroup();
        ToolGroup actualToolGroup = new ToolGroup();
        when(cloudPipelineAPIClient.loadToolGroup(expectedToolGroupId)).thenReturn(expectedToolGroup);
        cloudPipelineAPIClient.loadToolGroup("toolGroupId");
        assertEquals(expectedToolGroup, actualToolGroup);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadToolGroup(expectedToolGroupId);
    }

    @Test
    void loadDockerRegistry() {
        DockerRegistry expectedDockerRegistry = new DockerRegistry();
        DockerRegistry actualDockerRegistry = new DockerRegistry();
        when(cloudPipelineAPIClient.loadDockerRegistry(1L)).thenReturn(expectedDockerRegistry);
        cloudPipelineAPIClient.loadDockerRegistry(1L);
        assertEquals(expectedDockerRegistry, actualDockerRegistry);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadDockerRegistry(1L);
    }

    @Test
    void loadIssue() {
        Issue expectedIssue = new Issue();
        Issue actualIssue = new Issue();
        when(cloudPipelineAPIClient.loadIssue(1L)).thenReturn(expectedIssue);
        cloudPipelineAPIClient.loadIssue(1L);
        assertEquals(expectedIssue, actualIssue);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadIssue(1L);
    }

    @Test
    void loadMetadataEntity() {
        MetadataEntity expectedMetadataEntity = new MetadataEntity();
        MetadataEntity actualMetadataEntity = new MetadataEntity();
        when(cloudPipelineAPIClient.loadMetadataEntity(1L)).thenReturn(expectedMetadataEntity);
        cloudPipelineAPIClient.loadMetadataEntity(1L);
        assertEquals(expectedMetadataEntity, actualMetadataEntity);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadMetadataEntity(1L);
    }

    @Test
    void loadRunConfiguration() {
        RunConfiguration expectedRunConfiguration = new RunConfiguration();
        RunConfiguration actualRunConfiguration = new RunConfiguration();
        when(cloudPipelineAPIClient.loadRunConfiguration(1L)).thenReturn(expectedRunConfiguration);
        cloudPipelineAPIClient.loadRunConfiguration(1L);
        assertEquals(expectedRunConfiguration, actualRunConfiguration);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadRunConfiguration(1L);
    }

    @Test
    void loadPipeline() {
        String expectedIdentifier = "identifier";
        Pipeline actualPipeline = new Pipeline();
        when(cloudPipelineAPIClient.loadPipeline(expectedIdentifier)).thenReturn(expectedPipeline);
        cloudPipelineAPIClient.loadPipeline("identifier");
        assertEquals(expectedPipeline, actualPipeline);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPipeline(expectedIdentifier);
    }

    @Test
    void loadPermissionsForEntity() {
        AclClass expectedAclClass = AclClass.FOLDER;
        AclClass actualAclClass = AclClass.FOLDER;
        EntityPermissionVO expectedEntityPermissionVO = new EntityPermissionVO();
        EntityPermissionVO actualEntityPermissionVO = new EntityPermissionVO();
        when(cloudPipelineAPIClient.loadPermissionsForEntity(1L, expectedAclClass)).thenReturn(expectedEntityPermissionVO);
        cloudPipelineAPIClient.loadPermissionsForEntity(1L, actualAclClass);
        assertEquals(expectedEntityPermissionVO, actualEntityPermissionVO);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPermissionsForEntity(1L, expectedAclClass);
    }

    @Test
    void loadPipelineVersions() {
        Revision expectedRevision = new Revision();
        List<Revision> expectedRevisionList = new ArrayList<>();
        expectedRevisionList.add(expectedRevision);
        Revision actualRevision = new Revision();
        List<Revision> actualRevisionList = new ArrayList<>();
        actualRevisionList.add(actualRevision);
        when(cloudPipelineAPIClient.loadPipelineVersions(1L)).thenReturn(expectedRevisionList);
        cloudPipelineAPIClient.loadPipelineVersions(1L);
        assertEquals(expectedRevisionList, actualRevisionList);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPipelineVersions(1L);
    }

    @Test
    void getPipelineFile() {
        String actualTestVersion = "version";
        String actualTestPath = "test/path";
        String expectedFileContent = "fileContent";
        String actualFileContent = "fileContent";
        when(cloudPipelineAPIClient.getPipelineFile(1L, TEST_VERSION, TEST_PATH)).thenReturn(expectedFileContent);
        cloudPipelineAPIClient.getPipelineFile(1L, actualTestVersion, actualTestPath);
        assertEquals(expectedFileContent, actualFileContent);
        verify(cloudPipelineAPIClient, atLeastOnce()).getPipelineFile(1L, TEST_VERSION, TEST_PATH);
    }

    @Test
    void loadRepositoryContents() {
        String actualTestVersion = "version";
        String actualTestPath = "test/path";

        GitRepositoryEntry expectedGitRepositoryEntry = new GitRepositoryEntry();
        List<GitRepositoryEntry> expectedGitRepositoryEntryList = new ArrayList<>();
        expectedGitRepositoryEntryList.add(expectedGitRepositoryEntry);

        GitRepositoryEntry actualGitRepositoryEntry = new GitRepositoryEntry();
        List<GitRepositoryEntry> actualGitRepositoryEntryList = new ArrayList<>();
        actualGitRepositoryEntryList.add(actualGitRepositoryEntry);

        when(cloudPipelineAPIClient.loadRepositoryContents(1L, TEST_VERSION, TEST_PATH)).thenReturn(expectedGitRepositoryEntryList);
        cloudPipelineAPIClient.loadRepositoryContents(1L, actualTestVersion, actualTestPath);
        assertEquals(expectedGitRepositoryEntryList, actualGitRepositoryEntryList);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadRepositoryContents(1L, TEST_VERSION, TEST_PATH);
    }

    @Test
    void loadPipelineByRepositoryUrl() {
        String expectedRepositoryUrl = "String repositoryUrl";
        String actualRepositoryUrl = "String repositoryUrl";
        Pipeline actualPipeline = new Pipeline();
        when(cloudPipelineAPIClient.loadPipelineByRepositoryUrl(expectedRepositoryUrl)).thenReturn(expectedPipeline);
        cloudPipelineAPIClient.loadPipelineByRepositoryUrl(actualRepositoryUrl);
        assertEquals(expectedPipeline, actualPipeline);
        verify(cloudPipelineAPIClient, atLeastOnce()).loadPipelineByRepositoryUrl(expectedRepositoryUrl);
    }
}