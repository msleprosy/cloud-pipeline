package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.model.PipelineRunWithLog;
import com.epam.pipeline.entity.datastorage.*;
import com.epam.pipeline.entity.pipeline.Folder;
import com.epam.pipeline.entity.pipeline.PipelineRun;
import com.epam.pipeline.entity.security.acl.AclClass;
import com.epam.pipeline.entity.user.PipelineUser;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class CloudPipelineAPIClientTest {

    @Mock
    private CloudPipelineAPIClient cloudPipelineAPIClient;

    @Test
    void loadAllDataStorages() {
        AbstractDataStorage abstractDataStorage = new AbstractDataStorage() {
            @Override
            public String getDelimiter() {
                return "test";
            }

            @Override
            public String getPathMask() {
                return "test";
            }

            @Override
            public boolean isPolicySupported() {
                return true;
            }
        };
        abstractDataStorage.setDescription("test");
        abstractDataStorage.setPath("test");
        abstractDataStorage.setType(DataStorageType.AZ);
        abstractDataStorage.setParentFolderId(1L);
        abstractDataStorage.setParent(new Folder());
        abstractDataStorage.setStoragePolicy(new StoragePolicy());
        abstractDataStorage.setAclClass(AclClass.DATA_STORAGE);
        abstractDataStorage.setHasMetadata(false);
        abstractDataStorage.setFileShareMountId(1L);
        List<AbstractDataStorage> expectedAbstractDataStorageList = new ArrayList<>();
        expectedAbstractDataStorageList.add(abstractDataStorage);

        when(cloudPipelineAPIClient.loadAllDataStorages()).thenReturn(expectedAbstractDataStorageList);
        cloudPipelineAPIClient.loadAllDataStorages();
        assertEquals(expectedAbstractDataStorageList, expectedAbstractDataStorageList);
        verify(cloudPipelineAPIClient, atLeastOnce())
                .loadAllDataStorages();
    }

    @Test
    void loadDataStorage() {
        AbstractDataStorage abstractDataStorage = new AbstractDataStorage() {
            @Override
            public String getDelimiter() {
                return "test";
            }

            @Override
            public String getPathMask() {
                return "test";
            }

            @Override
            public boolean isPolicySupported() {
                return true;
            }
        };
        abstractDataStorage.setDescription("test");
        abstractDataStorage.setPath("test");
        abstractDataStorage.setType(DataStorageType.AZ);
        abstractDataStorage.setParentFolderId(1L);
        abstractDataStorage.setParent(new Folder());
        abstractDataStorage.setStoragePolicy(new StoragePolicy());
        abstractDataStorage.setAclClass(AclClass.DATA_STORAGE);
        abstractDataStorage.setHasMetadata(false);
        abstractDataStorage.setFileShareMountId(1L);

        when(cloudPipelineAPIClient.loadDataStorage(1L))
                .thenReturn(abstractDataStorage);
        cloudPipelineAPIClient.loadDataStorage(1L);
        assertEquals(abstractDataStorage, abstractDataStorage);
        verify(cloudPipelineAPIClient, atLeastOnce())
                .loadDataStorage(1L);
    }

    @Test
    void loadPipelineRunWithLogs() {
        PipelineRunWithLog expectedPipelineRunWithLog = new PipelineRunWithLog();
        when(cloudPipelineAPIClient.loadPipelineRunWithLogs(1L))
                .thenReturn(expectedPipelineRunWithLog);
        cloudPipelineAPIClient.loadPipelineRunWithLogs(1L);
        assertEquals(expectedPipelineRunWithLog, expectedPipelineRunWithLog);
        verify(cloudPipelineAPIClient, atLeastOnce())
                .loadPipelineRunWithLogs(1L);
    }

    @Test
    void loadPipelineRun() {
        PipelineRun expectedPipelineRun = new PipelineRun();
        when(cloudPipelineAPIClient.loadPipelineRun(1L))
                .thenReturn(expectedPipelineRun);
        cloudPipelineAPIClient.loadPipelineRun(1L);
        assertEquals(expectedPipelineRun, expectedPipelineRun);
        verify(cloudPipelineAPIClient, atLeastOnce())
                .loadPipelineRun(1L);
    }

    @Test
    void generateTemporaryCredentials() {
        TemporaryCredentials expectedTemporaryCredentials = new TemporaryCredentials();
        DataStorageAction expectedDataStorageAction = new DataStorageAction();
        List<DataStorageAction> expectedDataStorageActionList = new ArrayList<>();
        expectedDataStorageActionList.add(expectedDataStorageAction);
        when(cloudPipelineAPIClient.generateTemporaryCredentials(expectedDataStorageActionList))
                .thenReturn(expectedTemporaryCredentials);
        cloudPipelineAPIClient.generateTemporaryCredentials(expectedDataStorageActionList);
        assertEquals(expectedTemporaryCredentials, expectedTemporaryCredentials);
        verify(cloudPipelineAPIClient, atLeastOnce())
                .generateTemporaryCredentials(expectedDataStorageActionList);
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
    }

    @Test
    void loadAllRegions() {
    }

    @Test
    void loadRegion() {
    }

    @Test
    void loadTool() {
    }

    @Test
    void loadPipelineFolder() {
    }

    @Test
    void loadMetadataEntry() {
    }

    @Test
    void loadToolGroup() {
    }

    @Test
    void loadDockerRegistry() {
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