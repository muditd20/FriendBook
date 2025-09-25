package com.friendbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.friendbook.model.Like;
import com.friendbook.model.Post;
import com.friendbook.model.User;

public interface LikeRepository extends JpaRepository<Like, Long>{

	Optional<Like> findByUserAndPost(User user,Post post);
	long countByPost(Post post);
	
}
