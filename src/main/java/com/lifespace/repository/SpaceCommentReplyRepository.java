package com.lifespace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.entity.SpaceCommentReply;

public interface SpaceCommentReplyRepository extends JpaRepository<SpaceCommentReply, String>{

	Optional<SpaceCommentReply> findByOrdersOrderId(String orderId); 
}
