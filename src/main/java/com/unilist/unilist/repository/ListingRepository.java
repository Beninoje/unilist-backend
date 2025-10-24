package com.unilist.unilist.repository;

import com.unilist.unilist.model.Listing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListingRepository extends CrudRepository<Listing,Long> {

}
