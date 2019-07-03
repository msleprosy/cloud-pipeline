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
package com.epam.pipeline.elasticsearchagent.model;

import com.epam.pipeline.entity.pipeline.PipelineRun;
import com.epam.pipeline.entity.pipeline.RunLog;
import com.epam.pipeline.entity.user.PipelineUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class PipelineRunWithLog {

    private PipelineRun pipelineRun;
    private PipelineUser runOwner;
    private List<RunLog> runLogs;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PipelineRunWithLog)) {
            return false;
        }
        PipelineRunWithLog that = (PipelineRunWithLog) o;
        return Objects.equals(getPipelineRun(), that.getPipelineRun()) &&
                Objects.equals(getRunOwner(), that.getRunOwner()) &&
                Objects.equals(getRunLogs(), that.getRunLogs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPipelineRun(), getRunOwner(), getRunLogs());
    }
}
