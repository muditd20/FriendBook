package com.friendbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.service.LikeService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

@Controller
@RequestMapping("/likes")
public class LikeController {

	private final UserService userService;
	private final PostService postService;
	private final LikeService likeService;

	public LikeController(UserService userService, PostService postService, LikeService likeService) {
		this.userService = userService;
		this.postService = postService;
		this.likeService = likeService;
	}
	
	@PostMapping("/toggle")
	public String toogleLike(@RequestParam("email") String email, @RequestParam("postId")Long postId)
	{
		User user = userService.findByEmail(email);
		Post post=postService.findById(postId);
		likeService.toogleLike(user, post);
		return "redirect:/user/dashboard?email=" + email;
	}

}
