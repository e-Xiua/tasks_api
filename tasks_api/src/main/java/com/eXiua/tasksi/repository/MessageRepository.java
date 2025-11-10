package com.eXiua.tasksi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eXiua.tasksi.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTaskIdOrderByTimestampAsc(Long taskId);
    List<Message> findByReceiverIdOrderByTimestampDesc(String receiverId);
    Page<Message> findByReceiverIdOrderByTimestampDesc(String receiverId, Pageable pageable);
    Page<Message> findByReceiverIdAndReadFlagFalseOrderByTimestampDesc(String receiverId, Pageable pageable);
}
