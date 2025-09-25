package com.friendbook.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.friendbook.model.Like;
import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.LikeRepository;

@Service
public class LikeService {

	private final LikeRepository likeRepository;

	public LikeService(LikeRepository likeRepository) {
		this.likeRepository = likeRepository;
	}

	public boolean toogleLike(User user, Post post) {
		Optional<Like> existing = likeRepository.findByUserAndPost(user, post);
		if (existing.isPresent()) {
			likeRepository.delete(existing.get());
			return false; // unlike
		} else {
			Like like = new Like();
			like.setUser(user);
			like.setPost(post);
			likeRepository.save(like);
			return true;
		}
	}
	
	public long countLikes(Post post)
	{
		return likeRepository.countByPost(post);
	}
	

}
