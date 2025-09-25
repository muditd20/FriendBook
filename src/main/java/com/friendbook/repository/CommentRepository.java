package com.friendbook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.friendbook.model.Comment;
import com.friendbook.model.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPostOrderByCreatedAt(Post post);

}
