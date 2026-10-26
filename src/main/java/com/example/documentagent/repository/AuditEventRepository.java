package com.example.documentagent.repository;
import com.example.documentagent.domain.AuditEvent; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditEventRepository extends JpaRepository<AuditEvent,Long>{List<AuditEvent> findAllByResourceTypeAndResourceIdOrderByCreatedAtAsc(String resourceType,String resourceId); List<AuditEvent> findAllByActorIdOrderByCreatedAtDesc(String actorId);}
