package com.epam.pipeline.mapper.model;

import com.epam.pipeline.dto.model.PipelineDTO;
import com.epam.pipeline.entity.pipeline.Pipeline;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-06-26T17:37:39+0300",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_172 (Oracle Corporation)"
)
@Component
public class PipelineMapperImpl implements PipelineMapper {

    @Override
    public PipelineDTO modelToDTO(Pipeline pipeline) {
        if ( pipeline == null ) {
            return null;
        }

        PipelineDTO pipelineDTO = new PipelineDTO();

        pipelineDTO.setId( pipeline.getId() );
        pipelineDTO.setName( pipeline.getName() );

        return pipelineDTO;
    }
}
