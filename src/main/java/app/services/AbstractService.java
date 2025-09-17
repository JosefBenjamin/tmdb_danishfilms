package app.services;

import app.DTO.*;
import app.entities.*;

public abstract class AbstractService<DTO> {

    public BaseEntity convertToEntity(DTO dto, Class targetDTO){
        BaseEntity result = null;

        if (targetDTO == (ActorDTO.class) ){
            result = new Actor().builder()
                                    .name(((ActorDTO) dto).name())
                                    .build();
            return result;
        } else if (targetDTO == (DirectorDTO.class)){
            result = new Director().builder()
                                    .name(((DirectorDTO) dto).name())
                                    .build();
            return result;
        } else if (targetDTO == (MovieDTO.class)){
            result = new Movie().builder()
                                        .title(String.valueOf(dto))
                                        .releaseYear((((MovieDTO) dto).releaseYear()))
                                        .originalLanguage(((MovieDTO) dto).originalLanguage())
                                        .build();
            return result;
        } else if (targetDTO == (GenreDTO.class)){
            result = new Genre().builder()
                                        .genreName(((GenreDTO) dto).genreName())
                                        .build();
            return result;
        } else return result;

    }
}
