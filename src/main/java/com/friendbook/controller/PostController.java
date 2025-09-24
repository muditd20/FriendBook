package com.friendbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.User;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

@Controller
@RequestMapping("/posts")
public class PostController {

	private final PostService postService;
	private final UserService userService;

	public PostController(PostService postService, UserService userService) {
		this.postService = postService;
		this.userService = userService;
	}
	
	@PostMapping("/create")
	public String createPost(@RequestParam("email") String email,@RequestParam("caption") String caption,@RequestParam("image") MultipartFile image)
	{
		User user = userService.findByEmail(email);
		if(user==null)
		{
			return "redirect:/auth/login";
		}
		
		postService.createPost(user, caption, image);
		
		return "redirect:/user/dashboard?email=" + email;
	}

}
