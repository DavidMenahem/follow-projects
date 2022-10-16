package com.dvmena.followprojects.repository;

import com.dvmena.followprojects.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectsRepository extends JpaRepository<Project,Long> {
}
