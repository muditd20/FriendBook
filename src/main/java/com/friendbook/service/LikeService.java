package com.friendbook.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.friendbook.model.Like;
import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.LikeRepository;

import jakarta.transaction.Transactional;

@Service
public class LikeService {

	private final LikeRepository likeRepository;

	public LikeService(LikeRepository likeRepository) {
		this.likeRepository = likeRepository;
	}

	   // Toggle like (like/unlike)
    @Transactional
    public void toggleLike(User user, Post post) {
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            // Already liked → remove
            likeRepository.delete(existingLike.get());
        } else {
            // Not liked → add
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
        }
    }	
	public long countLikes(Post post)
	{
		return likeRepository.countByPost(post);
	}
	

}
