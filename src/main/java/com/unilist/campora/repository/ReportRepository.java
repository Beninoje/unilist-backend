package com.unilist.campora.repository;

import com.unilist.campora.model.Report;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ReportRepository extends CrudRepository<Report, UUID> {

}
