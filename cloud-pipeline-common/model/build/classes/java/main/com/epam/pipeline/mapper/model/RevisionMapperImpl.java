package com.epam.pipeline.mapper.model;

import com.epam.pipeline.dto.model.RevisionDTO;
import com.epam.pipeline.entity.pipeline.Revision;
import java.text.SimpleDateFormat;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-07-03T20:03:26+0300",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_172 (Oracle Corporation)"
)
@Component
public class RevisionMapperImpl implements RevisionMapper {

    @Override
    public RevisionDTO modelToDTO(Revision revision) {
        if ( revision == null ) {
            return null;
        }

        RevisionDTO revisionDTO = new RevisionDTO();

        revisionDTO.setId( revision.getId() );
        revisionDTO.setName( revision.getName() );
        revisionDTO.setMessage( revision.getMessage() );
        if ( revision.getCreatedDate() != null ) {
            revisionDTO.setCreatedDate( new SimpleDateFormat().format( revision.getCreatedDate() ) );
        }
        revisionDTO.setDraft( revision.getDraft() );
        revisionDTO.setCommitId( revision.getCommitId() );

        return revisionDTO;
    }
}
