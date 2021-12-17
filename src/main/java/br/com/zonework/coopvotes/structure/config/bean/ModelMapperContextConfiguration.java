package br.com.zonework.coopvotes.structure.config.bean;

import br.com.zonework.coopvotes.application.dto.InputNewStaveDto;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperContextConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(InputNewStaveDto.class, StaveEntity.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        map().setState("CREATE");
                    }
                });

        return modelMapper;
    }
}
