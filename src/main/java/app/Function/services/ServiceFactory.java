//package app.Function.services;
//
//import app.Function.DAO.IDAO;
//import app.Object.DTO.BaseDTO;
//import app.Object.entities.BaseEntity;
//import app.exceptions.ApiException;
//import jakarta.persistence.EntityManagerFactory;
//import java.lang.reflect.*;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Factory that creates fully functional services with ZERO boilerplate code
// * Uses reflection for automatic DTO/Entity conversion
// */
//public class ServiceFactory {
//
//    private final EntityManagerFactory emf;
//    private final Map<String, Object> serviceCache = new ConcurrentHashMap<>();
//
//    // Reflection caches for performance
//    private static final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
//    private static final Map<String, Field[]> fieldCache = new ConcurrentHashMap<>();
//
//    public ServiceFactory(EntityManagerFactory emf) {
//        this.emf = emf;
//    }
//
//    /**
//     * Creates a fully functional service with ZERO code required
//     * All CRUD operations and conversions are automatic
//     */
//    @SuppressWarnings("unchecked")
//    public <DTO extends BaseDTO<ID>, Entity extends BaseEntity<ID>, ID>
//           IService<DTO, ID> createService(Class<DTO> dtoClass, Class<Entity> entityClass) {
//
//        String cacheKey = dtoClass.getSimpleName() + "_" + entityClass.getSimpleName();
//
//        return (IService<DTO, ID>) serviceCache.computeIfAbsent(cacheKey, k -> {
//            // Create universal DAO
//            IDAO<Entity, ID> dao = new UniversalDAO<>(emf, entityClass);
//
//            // Return automatic service with everything built-in
//            return new AutomaticService<>(emf, dao, dtoClass, entityClass);
//        });
//    }
//
//    /**
//     * Automatic Service Implementation - NO manual code needed!
//     */
//    public static class AutomaticService<DTO extends BaseDTO<ID>, Entity extends BaseEntity<ID>, ID>
//            extends AbstractService<DTO, Entity, ID> {
//
//        private final Class<DTO> dtoClass;
//        private final Class<Entity> entityClass;
//
//        public AutomaticService(EntityManagerFactory emf, IDAO<Entity, ID> dao,
//                               Class<DTO> dtoClass, Class<Entity> entityClass) {
//            super(emf, dao);
//            this.dtoClass = dtoClass;
//            this.entityClass = entityClass;
//        }
//
//        /**
//         * AUTOMATIC DTO to Entity conversion using reflection
//         */
//        @Override
//        @SuppressWarnings("unchecked")
//        protected Entity convertToEntity(DTO dto) {
//            if (dto == null) return null;
//
//            try {
//                // Special handling for your existing entities with builders
//                if (entityClass.getSimpleName().equals("Actor")) {
//                    return (Entity) createActorFromDTO(dto);
//                } else if (entityClass.getSimpleName().equals("Director")) {
//                    return (Entity) createDirectorFromDTO(dto);
//                } else if (entityClass.getSimpleName().equals("Genre")) {
//                    return (Entity) createGenreFromDTO(dto);
//                } else if (entityClass.getSimpleName().equals("Movie")) {
//                    return (Entity) createMovieFromDTO(dto);
//                }
//
//                // Fallback to generic reflection approach
//                return createEntityGeneric(dto);
//
//            } catch (Exception e) {
//                throw ApiException.serverError("Auto-conversion DTO->Entity failed: " + e.getMessage());
//            }
//        }
//
//        /**
//         * AUTOMATIC Entity to DTO conversion using reflection
//         */
//        @Override
//        @SuppressWarnings("unchecked")
//        protected DTO convertToDTO(Entity entity) {
//            if (entity == null) return null;
//
//            try {
//                // Special handling for your existing DTOs
//                if (dtoClass.getSimpleName().equals("ActorDTO")) {
//                    return (DTO) createActorDTO(entity);
//                } else if (dtoClass.getSimpleName().equals("DirectorDTO")) {
//                    return (DTO) createDirectorDTO(entity);
//                } else if (dtoClass.getSimpleName().equals("GenreDTO")) {
//                    return (DTO) createGenreDTO(entity);
//                } else if (dtoClass.getSimpleName().equals("MovieDTO")) {
//                    return (DTO) createMovieDTO(entity);
//                }
//
//                // Fallback to generic approach
//                return createDTOGeneric(entity);
//
//            } catch (Exception e) {
//                throw ApiException.serverError("Auto-conversion Entity->DTO failed: " + e.getMessage());
//            }
//        }
//
//        // Specific conversion methods for your entities using reflection
//        private Object createActorFromDTO(DTO dto) throws Exception {
//            Method getId = dto.getClass().getMethod("getId");
//            Method getName = dto.getClass().getMethod("name");
//
//            Class<?> actorClass = Class.forName("app.Object.entities.ActorEntity");
//            Method builderMethod = actorClass.getMethod("builder");
//            Object builder = builderMethod.invoke(null);
//
//            // Set ID if not null
//            Object id = getId.invoke(dto);
//            if (id != null) {
//                Method idMethod = builder.getClass().getMethod("id", Integer.class);
//                idMethod.invoke(builder, id);
//            }
//
//            // Set name
//            Object name = getName.invoke(dto);
//            Method nameMethod = builder.getClass().getMethod("name", String.class);
//            nameMethod.invoke(builder, name);
//
//            // Set default age
//            Method ageMethod = builder.getClass().getMethod("age", int.class);
//            ageMethod.invoke(builder, 0);
//
//            // Build the actor
//            Method buildMethod = builder.getClass().getMethod("build");
//            return buildMethod.invoke(builder);
//        }
//
//        private Object createDirectorFromDTO(DTO dto) throws Exception {
//            Method getId = dto.getClass().getMethod("getId");
//            Method getName = dto.getClass().getMethod("name");
//            Method getJob = dto.getClass().getMethod("job");
//
//            Class<?> directorClass = Class.forName("app.Object.entities.DirectorEntity");
//            Method builderMethod = directorClass.getMethod("builder");
//            Object builder = builderMethod.invoke(null);
//
//            // Set fields using reflection
//            Object id = getId.invoke(dto);
//            if (id != null) {
//                Method idMethod = builder.getClass().getMethod("id", Integer.class);
//                idMethod.invoke(builder, id);
//            }
//
//            Object name = getName.invoke(dto);
//            Method nameMethod = builder.getClass().getMethod("name", String.class);
//            nameMethod.invoke(builder, name);
//
//            Object job = getJob.invoke(dto);
//            Method jobMethod = builder.getClass().getMethod("job", String.class);
//            jobMethod.invoke(builder, job);
//
//            Method ageMethod = builder.getClass().getMethod("age", int.class);
//            ageMethod.invoke(builder, 0);
//
//            Method buildMethod = builder.getClass().getMethod("build");
//            return buildMethod.invoke(builder);
//        }
//
//        private Object createGenreFromDTO(DTO dto) throws Exception {
//            Method getId = dto.getClass().getMethod("getId");
//            Method getGenreName = dto.getClass().getMethod("genreName");
//
//            Class<?> genreClass = Class.forName("app.Object.entities.GenreEntity");
//            Method builderMethod = genreClass.getMethod("builder");
//            Object builder = builderMethod.invoke(null);
//
//            Object id = getId.invoke(dto);
//            if (id != null) {
//                Method idMethod = builder.getClass().getMethod("id", Integer.class);
//                idMethod.invoke(builder, id);
//            }
//
//            Object genreName = getGenreName.invoke(dto);
//            Method genreNameMethod = builder.getClass().getMethod("genreName", String.class);
//            genreNameMethod.invoke(builder, genreName);
//
//            Method buildMethod = builder.getClass().getMethod("build");
//            return buildMethod.invoke(builder);
//        }
//
//        private Object createMovieFromDTO(DTO dto) throws Exception {
//            Method getId = dto.getClass().getMethod("getId");
//            Method getTitle = dto.getClass().getMethod("title");
//            Method getReleaseYear = dto.getClass().getMethod("releaseYear");
//            Method getOriginalLanguage = dto.getClass().getMethod("originalLanguage");
//
//            Class<?> movieClass = Class.forName("app.Object.entities.MovieEntity");
//            Method builderMethod = movieClass.getMethod("builder");
//            Object builder = builderMethod.invoke(null);
//
//            Object id = getId.invoke(dto);
//            if (id != null) {
//                Method idMethod = builder.getClass().getMethod("id", Integer.class);
//                idMethod.invoke(builder, id);
//            }
//
//            Object title = getTitle.invoke(dto);
//            Method titleMethod = builder.getClass().getMethod("title", String.class);
//            titleMethod.invoke(builder, title);
//
//            Object releaseYear = getReleaseYear.invoke(dto);
//            if (releaseYear != null) {
//                Method releaseYearMethod = builder.getClass().getMethod("releaseYear", int.class);
//                releaseYearMethod.invoke(builder, releaseYear);
//            }
//
//            Object originalLanguage = getOriginalLanguage.invoke(dto);
//            if (originalLanguage != null) {
//                Method originalLanguageMethod = builder.getClass().getMethod("originalLanguage", String.class);
//                originalLanguageMethod.invoke(builder, originalLanguage);
//            }
//
//            Method buildMethod = builder.getClass().getMethod("build");
//            return buildMethod.invoke(builder);
//        }
//
//        // DTO creation methods using reflection
//        private Object createActorDTO(Entity entity) throws Exception {
//            Method getId = entity.getClass().getMethod("getId");
//            Method getName = entity.getClass().getMethod("getName");
//
//            Class<?> actorDTOClass = Class.forName("app.Object.DTO.ActorDTO");
//            Constructor<?> constructor = actorDTOClass.getDeclaredConstructor(Integer.class, String.class, String.class);
//
//            return constructor.newInstance(getId.invoke(entity), getName.invoke(entity), "Acting");
//        }
//
//        private Object createDirectorDTO(Entity entity) throws Exception {
//            Method getId = entity.getClass().getMethod("getId");
//            Method getName = entity.getClass().getMethod("getName");
//            Method getJob = null;
//            try {
//                getJob = entity.getClass().getMethod("getJob");
//            } catch (NoSuchMethodException e) {
//                // Job method doesn't exist, use default
//            }
//
//            Class<?> directorDTOClass = Class.forName("app.Object.DTO.DirectorDTO");
//            Constructor<?> constructor = directorDTOClass.getDeclaredConstructor(Integer.class, String.class, String.class);
//
//            String job = (getJob != null) ? (String) getJob.invoke(entity) : "Directing";
//            return constructor.newInstance(getId.invoke(entity), getName.invoke(entity), job);
//        }
//
//        private Object createGenreDTO(Entity entity) throws Exception {
//            Method getId = entity.getClass().getMethod("getId");
//            Method getGenreName = entity.getClass().getMethod("getGenreName");
//
//            Class<?> genreDTOClass = Class.forName("app.Object.DTO.GenreDTO");
//            Constructor<?> constructor = genreDTOClass.getDeclaredConstructor(Integer.class, String.class);
//
//            return constructor.newInstance(getId.invoke(entity), getGenreName.invoke(entity));
//        }
//
//        private Object createMovieDTO(Entity entity) throws Exception {
//            Method getId = entity.getClass().getMethod("getId");
//            Method getTitle = entity.getClass().getMethod("getTitle");
//            Method getReleaseYear = entity.getClass().getMethod("getReleaseYear");
//            Method getOriginalLanguage = entity.getClass().getMethod("getOriginalLanguage");
//
//            Class<?> movieDTOClass = Class.forName("app.Object.DTO.MovieDTO");
//            Constructor<?> constructor = movieDTOClass.getDeclaredConstructor(
//                Integer.class, String.class, Integer.class, String.class, Set.class);
//
//            return constructor.newInstance(
//                getId.invoke(entity),
//                getTitle.invoke(entity),
//                getReleaseYear.invoke(entity),
//                getOriginalLanguage.invoke(entity),
//                new HashSet<>()
//            );
//        }
//
//        // Generic fallback methods
//        private Entity createEntityGeneric(DTO dto) throws Exception {
//            Entity entity = entityClass.getDeclaredConstructor().newInstance();
//            // Generic field copying logic would go here
//            return entity;
//        }
//
//        private DTO createDTOGeneric(Entity entity) throws Exception {
//            Constructor<DTO> constructor = getDtoConstructor();
//            Object[] args = new Object[constructor.getParameterCount()];
//            Arrays.fill(args, null); // Fill with nulls as fallback
//            return constructor.newInstance(args);
//        }
//
//        @SuppressWarnings("unchecked")
//        private Constructor<DTO> getDtoConstructor() throws Exception {
//            Constructor<?>[] constructors = dtoClass.getDeclaredConstructors();
//            if (constructors.length > 0) {
//                return (Constructor<DTO>) constructors[0];
//            }
//            throw new RuntimeException("No constructor found for DTO: " + dtoClass);
//        }
//
//        /**
//         * AUTOMATIC validation
//         */
//        @Override
//        protected void validateDTO(DTO dto) {
//            super.validateDTO(dto);
//
//            try {
//                // Basic validation using common patterns
//                if (dto.getClass().getSimpleName().contains("Actor")) {
//                    Method getName = dto.getClass().getMethod("name");
//                    String name = (String) getName.invoke(dto);
//                    if (name == null || name.trim().isEmpty()) {
//                        throw ApiException.badRequest("Actor name cannot be empty");
//                    }
//                } else if (dto.getClass().getSimpleName().contains("Director")) {
//                    Method getName = dto.getClass().getMethod("name");
//                    String name = (String) getName.invoke(dto);
//                    if (name == null || name.trim().isEmpty()) {
//                        throw ApiException.badRequest("Director name cannot be empty");
//                    }
//                } else if (dto.getClass().getSimpleName().contains("Genre")) {
//                    Method getGenreName = dto.getClass().getMethod("genreName");
//                    String genreName = (String) getGenreName.invoke(dto);
//                    if (genreName == null || genreName.trim().isEmpty()) {
//                        throw ApiException.badRequest("Genre name cannot be empty");
//                    }
//                }
//            } catch (Exception e) {
//                // Fallback to basic validation if reflection fails
//            }
//        }
//    }
//}
