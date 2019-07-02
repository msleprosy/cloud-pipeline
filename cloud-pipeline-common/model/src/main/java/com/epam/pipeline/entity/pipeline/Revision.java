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

package com.epam.pipeline.entity.pipeline;

import java.util.Date;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Revision {

    private Long id;
    private String name;
    private String message;
    private Date createdDate;
    private Boolean draft;
    private String commitId;

    public Revision() {
        this.id = 1L;
        this.draft = Boolean.FALSE;
    }

    public Revision(String name, String message, Date createdDate, String commitId) {
        this();
        this.name = name;
        this.message = message;
        this.createdDate = createdDate;
        this.commitId = commitId;
    }

    public Revision(String name, String message, Date createdDate, String commitId, Boolean draft) {
        this(name, message, createdDate, commitId);
        this.draft = draft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Revision)) return false;
        Revision revision = (Revision) o;
        return Objects.equals(getId(), revision.getId()) &&
                Objects.equals(getName(), revision.getName()) &&
                Objects.equals(getMessage(), revision.getMessage()) &&
                Objects.equals(getCreatedDate(), revision.getCreatedDate()) &&
                Objects.equals(getDraft(), revision.getDraft()) &&
                Objects.equals(getCommitId(), revision.getCommitId());
    }
}
