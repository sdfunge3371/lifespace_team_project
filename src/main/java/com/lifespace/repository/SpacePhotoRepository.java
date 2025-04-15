package com.lifespace.repository;

import com.lifespace.entity.Space;
import com.lifespace.entity.SpacePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpacePhotoRepository extends JpaRepository<SpacePhoto, Integer> {
    List<SpacePhoto> findBySpace(Space space);

    Optional<SpacePhoto> findBySpaceAndFilename(Space space, String filename);
}
