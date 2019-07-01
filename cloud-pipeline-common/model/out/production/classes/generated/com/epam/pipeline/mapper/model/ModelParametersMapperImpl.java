package com.epam.pipeline.mapper.model;

import com.epam.pipeline.dto.model.ModelParametersDTO;
import com.epam.pipeline.entity.model.AbstractStepParameters;
import com.epam.pipeline.entity.model.ModelParameters;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-07-01T16:58:53+0300",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_172 (Oracle Corporation)"
)
@Component
public class ModelParametersMapperImpl implements ModelParametersMapper {

    @Override
    public ModelParametersDTO modelToDTO(ModelParameters modelParameters) {
        if ( modelParameters == null ) {
            return null;
        }

        ModelParametersDTO modelParametersDTO = new ModelParametersDTO();

        modelParametersDTO.setPathToModel( modelParameters.getPathToModel() );
        Map<String, AbstractStepParameters> map = modelParameters.getSteps();
        if ( map != null ) {
            modelParametersDTO.setSteps( new HashMap<String, AbstractStepParameters>( map ) );
        }
        else {
            modelParametersDTO.setSteps( null );
        }

        return modelParametersDTO;
    }
}
