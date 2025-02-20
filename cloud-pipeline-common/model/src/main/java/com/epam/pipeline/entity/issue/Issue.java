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

package com.epam.pipeline.entity.issue;

import com.epam.pipeline.vo.EntityVO;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Issue {
    private Long id;
    private String name;
    private String text;
    private String author;
    private EntityVO entity;
    private Date createdDate;
    private Date updatedDate;
    private IssueStatus status;
    private List<String> labels;
    private List<IssueComment> comments;
    private List<Attachment> attachments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue)) return false;
        Issue issue = (Issue) o;
        return Objects.equals(getId(), issue.getId()) &&
                Objects.equals(getName(), issue.getName()) &&
                Objects.equals(getText(), issue.getText()) &&
                Objects.equals(getAuthor(), issue.getAuthor()) &&
                Objects.equals(getEntity(), issue.getEntity()) &&
                Objects.equals(getCreatedDate(), issue.getCreatedDate()) &&
                Objects.equals(getUpdatedDate(), issue.getUpdatedDate()) &&
                getStatus() == issue.getStatus() &&
                Objects.equals(getLabels(), issue.getLabels()) &&
                Objects.equals(getComments(), issue.getComments()) &&
                Objects.equals(getAttachments(), issue.getAttachments());
    }

}
