package com.api.stuv.domain.image.repository;

import com.api.stuv.domain.image.entity.EntityType;
import com.api.stuv.domain.image.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findAllByEntityIdAndEntityType(Long boardId, EntityType entityType);
    Optional<ImageFile> findByEntityIdAndEntityType(Long costumeId, EntityType entityType);
    void deleteByNewFilename(String filename);
}
