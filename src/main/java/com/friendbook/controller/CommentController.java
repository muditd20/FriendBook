package com.friendbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	    public String addComment(@RequestParam("email") String email,
	                             @RequestParam("postId") Long postId,
	                             @RequestParam("text") String text,
	                             @RequestParam("redirectPage") String redirectPage) {

	        User user = userService.findByEmail(email);
	        Post post = postService.findById(postId);
	        commentService.addComment(user, post, text);

	        if ("feed".equalsIgnoreCase(redirectPage)) {
	            return "redirect:/user/feed?email=" + email;
	        } else {
	            return "redirect:/user/dashboard?email=" + email;
	        }
	    }
}
