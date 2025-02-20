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

package com.epam.pipeline.entity.datastorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporaryCredentials {

    @JsonProperty(value = "accessKey")
    private String accessKey;

    @JsonProperty(value = "keyID")
    private String keyId;

    @JsonProperty(value = "token")
    private String token;

    @JsonProperty(value = "expiration")
    private String expirationTime;

    private String region;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemporaryCredentials)) return false;
        TemporaryCredentials that = (TemporaryCredentials) o;
        return Objects.equals(getAccessKey(), that.getAccessKey()) &&
                Objects.equals(getKeyId(), that.getKeyId()) &&
                Objects.equals(getToken(), that.getToken()) &&
                Objects.equals(getExpirationTime(), that.getExpirationTime()) &&
                Objects.equals(getRegion(), that.getRegion());
    }
}
