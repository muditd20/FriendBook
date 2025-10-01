package com.friendbook.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.service.CommentService;
import com.friendbook.service.FollowRequestService;
import com.friendbook.service.LikeService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

@Controller
@RequestMapping("/user")
public class FeedController {

	private final UserService userService;
	private final PostService postService;
	private final FollowRequestService followRequestService;
	private final LikeService likeService;
	private final CommentService commentService;

	public FeedController(UserService userService, PostService postService, FollowRequestService followRequestService,
			LikeService likeService, CommentService commentService) {
		this.userService = userService;
		this.postService = postService;
		this.followRequestService = followRequestService;
		this.likeService = likeService;
		this.commentService = commentService;
	}

	@GetMapping("/feed")
	public String feedPage(@RequestParam("email") String email, Model model) {
		User currentUser = userService.findByEmail(email);
		if (currentUser == null) {
			return "redirect:/auth/login";
		}

		// ✅ Get feed posts from PostService
		List<Post> feedPosts = postService.getFeedPosts(currentUser, followRequestService);

		model.addAttribute("user", currentUser);
		model.addAttribute("posts", feedPosts); // this is what feed.html uses
		model.addAttribute("likeService", likeService);
		model.addAttribute("commentService", commentService);

		return "feed";
	}
}
