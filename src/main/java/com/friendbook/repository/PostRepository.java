package com.friendbook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.friendbook.model.Post;
import com.friendbook.model.User;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByUserOrderByCreatedAtDesc(User user);

}
