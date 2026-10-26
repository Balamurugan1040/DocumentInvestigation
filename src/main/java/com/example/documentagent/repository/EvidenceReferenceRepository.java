package com.example.documentagent.repository;
import com.example.documentagent.domain.EvidenceReference; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface EvidenceReferenceRepository extends JpaRepository<EvidenceReference,Long>{List<EvidenceReference> findAllByInvestigationIdOrderByIdAsc(Long investigationId); long deleteAllByInvestigationId(Long investigationId);}
