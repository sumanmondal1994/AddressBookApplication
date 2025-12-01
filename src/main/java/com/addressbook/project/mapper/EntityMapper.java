package com.addressbook.project.mapper;

/**
 * Generic mapper interface for entity to response mapping. * 
 * @param <E> Entity type
 * @param <R> Response DTO type
 */
public interface EntityMapper<E, R> {

    /**
     * Maps an entity to its response DTO.
     * 
     * @param entity the entity to map
     * @return the response DTO
     */
    R mapToResponse(E entity);
}
