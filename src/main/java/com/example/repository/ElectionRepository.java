package com.example.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.Election;
import java.util.Optional;

public interface ElectionRepository extends JpaRepository<Election, Long> {
    Optional<Election> findTopByOrderByIdDesc(); // fetch latest election
}
