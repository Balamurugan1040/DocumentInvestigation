package com.example.documentagent.repository;
import com.example.documentagent.domain.Document; import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface DocumentRepository extends JpaRepository<Document,Long>{List<Document> findAllByOwnerIdOrderByCreatedAtDesc(String ownerId); List<Document> findAllByIdInAndOwnerId(List<Long> ids,String ownerId); Optional<Document> findByIdAndOwnerId(Long id,String ownerId); boolean existsByOwnerIdAndSha256(String ownerId,String sha256);}
