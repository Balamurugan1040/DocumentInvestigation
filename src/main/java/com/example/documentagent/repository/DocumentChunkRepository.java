package com.example.documentagent.repository;

import com.example.documentagent.domain.DocumentChunk;
import com.example.documentagent.domain.DocumentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {
    List<DocumentChunk> findAllByDocumentIdOrderByChunkIndexAsc(Long documentId);
    List<DocumentChunk> findAllByDocumentOwnerIdAndDocumentStatusOrderByIdAsc(String ownerId, DocumentStatus status);
    @Query("select c from DocumentChunk c where c.document.ownerId = :ownerId and c.document.status = :status and c.document.id in :documentIds order by c.id asc")
    List<DocumentChunk> findAllByOwnerStatusAndDocumentIds(@Param("ownerId") String ownerId,
                                                            @Param("status") DocumentStatus status,
                                                            @Param("documentIds") List<Long> documentIds);
    long deleteAllByDocumentId(Long documentId);
    long countByDocumentId(Long documentId);
}
