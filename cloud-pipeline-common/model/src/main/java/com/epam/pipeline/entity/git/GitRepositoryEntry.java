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

package com.epam.pipeline.entity.git;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents Gitlab repository browsing request result
 */
@Getter
@Setter
public class GitRepositoryEntry {
    private String id;
    private String name;
    private String type;
    private String path;
    private String mode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GitRepositoryEntry)) return false;
        GitRepositoryEntry that = (GitRepositoryEntry) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getType(), that.getType()) &&
                Objects.equals(getPath(), that.getPath()) &&
                Objects.equals(getMode(), that.getMode());
    }
}
