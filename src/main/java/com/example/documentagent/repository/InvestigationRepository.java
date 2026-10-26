package com.example.documentagent.repository;
import com.example.documentagent.domain.Investigation; import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface InvestigationRepository extends JpaRepository<Investigation,Long>{List<Investigation> findAllByRequesterIdOrderByCreatedAtDesc(String requesterId); Optional<Investigation> findByIdAndRequesterId(Long id,String requesterId);}
