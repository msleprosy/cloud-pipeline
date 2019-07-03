/*
 * Copyright 2017-2019 EPAM Systems, Inc. (https://www.epam.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.epam.pipeline.elasticsearchagent.service.impl;

import com.epam.pipeline.elasticsearchagent.exception.ElasticClientException;
import com.epam.pipeline.elasticsearchagent.service.ElasticsearchServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.pipeline.elasticsearchagent.service.EventToRequestConverter.INDEX_TYPE;
import static com.epam.pipeline.elasticsearchagent.service.impl.converter.configuration.RunConfigurationDocumentBuilder.ID_DELIMITER;

@Service
@Slf4j
public class ElasticIndexService {

    private static final String WILDCARD = "*";

    @Autowired
    private ElasticsearchServiceClient elasticsearchServiceClient;

    public void createIndexIfNotExist(final String indexName, final String settingsFilePath)
            throws ElasticClientException {
        try {
            final String mappingsJson = IOUtils.toString(openJsonMapping(settingsFilePath),
                    Charset.defaultCharset());
            elasticsearchServiceClient.createIndex(indexName, mappingsJson);
        } catch (IOException e) {
            throw new ElasticClientException("Failed to create elasticsearch index with name " + indexName, e);
        }
    }

    public List<DocWriteRequest> getDeleteRequestsByTerm(final String field,
                                                         final String value,
                                                         final String indexName) {

        final SearchSourceBuilder searchSource = new SearchSourceBuilder()
                .query(QueryBuilders.termQuery(field, value));
        final SearchRequest request = new SearchRequest(indexName).source(searchSource);
        log.debug("Search request: {}", request);
        try {
            return buildDeleteRequests(indexName, request);
        } catch (ElasticsearchException e) {
            return Collections.emptyList();
        }
    }

    public List<DocWriteRequest> getDeleteRequests(final String id,
                                                   final String indexName) {
        SearchRequest request = buildSearchRequestForConfigEntries(id, indexName);
        log.debug("Search request: {}", request);
        try {
            final List<DocWriteRequest> requests = buildDeleteRequests(indexName, request);
            // return dummy doc, since we need it to clear events DB
            if (CollectionUtils.isEmpty(requests)) {
                return Collections.singletonList(new DeleteRequest(indexName, INDEX_TYPE,
                        getWildcardId(id)));
            }
            return requests;
        } catch (ElasticsearchException e) {
            return Collections.emptyList();
        }
    }

     List<DocWriteRequest> buildDeleteRequests(final String indexName,
                                                      final SearchRequest request) {
        SearchResponse search = elasticsearchServiceClient.search(request);
        if (search.getHits().getTotalHits() == 0) {
            log.debug("No documents found for {} {}", indexName, request);
            return Collections.emptyList();
        }
        return Arrays.stream(search.getHits().getHits())
                .map(hit -> {
                    log.debug("Found {} entry doc: {}", indexName, hit.getId());
                    return new DeleteRequest(indexName, INDEX_TYPE, hit.getId());
                })
                .collect(Collectors.toList());
    }

     SearchRequest buildSearchRequestForConfigEntries(final String id,
                                                             final String indexName) {
        final SearchSourceBuilder searchSource = new SearchSourceBuilder()
                .query(QueryBuilders.wildcardQuery("id", getWildcardId(id)));
        return new SearchRequest(indexName).source(searchSource);
    }

     String getWildcardId(final String id) {
        return id + ID_DELIMITER + WILDCARD;
    }

     InputStream openJsonMapping(final String path) throws FileNotFoundException {
        if (path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            final InputStream classPathResource = getClass().getResourceAsStream(path
                    .substring(ResourceUtils.CLASSPATH_URL_PREFIX.length()));
            Assert.notNull(classPathResource, String.format("Failed to resolve path: %s", path));
            return classPathResource;
        }
        if (path.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            return new FileInputStream(path.substring(ResourceUtils.FILE_URL_PREFIX.length()));
        }
        throw new IllegalArgumentException("Unsupported mapping file: " + path);
    }


}
