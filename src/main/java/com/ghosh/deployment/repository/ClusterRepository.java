package com.ghosh.deployment.repository;

import com.ghosh.deployment.model.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    Cluster findByName(String name);
}
