package org.nicholas.bookstorevaadin.mapper.simplicity;

import org.nicholas.bookstorevaadin.model.dto.DefaultDTO;

public interface DefaultMapper<D extends DefaultDTO, E> {
    E toEntity(D dto);

    D toDTO(E entity);
}
