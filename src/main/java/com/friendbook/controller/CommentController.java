package com.friendbook.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.friendbook.model.Comment;
import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.service.CommentService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

@Controller
@RequestMapping("/comments")
public class CommentController {

	private final CommentService commentService;
	private final UserService userService;
	private final PostService postService;

	public CommentController(CommentService commentService, UserService userService, PostService postService) {
		this.commentService = commentService;
		this.userService = userService;
		this.postService = postService;
	}

	@PostMapping("/add")
	public String addComment(@RequestParam("email") String email, @RequestParam("postId") Long postId,
			@RequestParam("text") String text, @RequestParam("redirectPage") String redirectPage) {

		User user = userService.findByEmail(email);
		Post post = postService.findById(postId);
		commentService.addComment(user, post, text);

		if ("feed".equalsIgnoreCase(redirectPage)) {
			return "redirect:/user/feed?email=" + email;
		} else {
			return "redirect:/user/dashboard?email=" + email;
		}
	}

	@PostMapping("/delete")
	public String deleteCommnt(@RequestParam("email") String email, @RequestParam("commentId") Long commentId,
			@RequestParam("redirectPage") String redirectPage) {
		User currentUser = userService.findByEmail(email);
		Optional<Comment> optionalComment = commentService.findById(commentId);
		if (optionalComment.isPresent()) {
			Comment comment = optionalComment.get();
			Post post = comment.getPost();

			if (post.getUser().getId().equals(currentUser.getId())) {
				commentService.deleteComment(comment);
			}
		}

		if ("feed".equalsIgnoreCase(redirectPage)) {
			return "redirect:/user/feed?email=" + email;
		} else {
			return "redirect:/user/dashboard?email=" + email;
		}
	}

}
