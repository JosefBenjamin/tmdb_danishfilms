package app.services;

import app.DTO.*;
import app.entities.*;

public abstract class AbstractService<DTO> {

    public BaseEntity convertToEntity(DTO dto){
        BaseEntity result = null;

        if (dto == (ActorDTO.class) ){
            result = new Actor().builder()
                                    .name(((ActorDTO) dto).name())
                                    .build();
            return result;
        } else if (dto == (DirectorDTO.class)){
            result = new Director().builder()
                                    .name(((DirectorDTO) dto).name())
                                    .build();
            return result;
        } else if (dto == (MovieDTO.class)){
            result = new Movie().builder()
                                        .title(String.valueOf(dto))
                                        .releaseYear((((MovieDTO) dto).releaseYear()))
                                        .originalLanguage(((MovieDTO) dto).originalLanguage())
                                        .build();
            return result;
        } else if (dto == (GenreDTO.class)){
            result = new Genre().builder()
                                        .genreName(((GenreDTO) dto).genreName())
                                        .build();
            return result;
        } else return result;
    }
}
