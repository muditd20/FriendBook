package com.friendbook.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.friendbook.model.Comment;
import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.CommentRepository;

@Service
public class CommentService {

	private final CommentRepository commentRepository;

	public CommentService(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}

	public Comment addComment(User user, Post post, String text) {
		Comment comment = new Comment();
		comment.setUser(user);
		comment.setPost(post);
		comment.setText(text);
		comment.setCreatedAt(LocalDateTime.now());
		return commentRepository.save(comment);
	}

	public List<Comment> getComments(Post post) {
		return commentRepository.findByPostOrderByCreatedAt(post);
	}

	public Optional<Comment> findById(Long id) {
		return commentRepository.findById(id);
	}

	public void deleteComment(Comment comment) {
		commentRepository.delete(comment);
	}
	

}
