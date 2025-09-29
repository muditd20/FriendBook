package com.friendbook.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.service.CommentService;
import com.friendbook.service.FollowService;
import com.friendbook.service.LikeService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final PostService postService;
	private final LikeService likeService;
	private final CommentService commentService;
	private final FollowService followService;
	

	public UserController(UserService userService, PostService postService, LikeService likeService,CommentService commentService,FollowService followService) {
		this.userService = userService;
		this.postService = postService;
		this.likeService = likeService;
		this.commentService=commentService;
		this.followService=followService;
	}

	@GetMapping("/dashboard")
	public String dashboard(@RequestParam("email") String email, Model model) {
		User user = userService.findByEmail(email);
		if (user == null) {
			return "redirect:/auth/login";
		}

		List<Post> posts = postService.getUserPosts(user);

		model.addAttribute("user", user);
		model.addAttribute("posts", posts); // 🟢 attribute ka naam fix kiya
		model.addAttribute("likeService", likeService); // so Thymeleaf can call countLikes
		model.addAttribute("commentService", commentService);
		
		model.addAttribute("followersCount", followService.countFollowers(user));
		model.addAttribute("followingCount", followService.countFollowing(user));
		   User target = userService.findById(2L); 
		    if (target != null) {
		        boolean isFollowing = followService.isFollowing(user, target);
		        model.addAttribute("target", target);
		        model.addAttribute("isFollowing", isFollowing);
		    }
		return "dashboard";
	}

	@PostMapping("/uploadPhoto")
	public String uploadPhoto(@RequestParam("photo") MultipartFile file, @RequestParam("email") String email,
			Model model) {
		try {
			User user = userService.findByEmail(email);
			if (user == null) {
				model.addAttribute("uploadError", "User not found!");
				return "dashboard";
			}

			String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
			File uploadFolder = new File(uploadDir);
			if (!uploadFolder.exists()) {
				uploadFolder.mkdirs();
			}

			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			Path filePath = Paths.get(uploadDir, fileName);

			file.transferTo(filePath.toFile());

			user.setProfilePhoto(fileName);
			userService.save(user);

			model.addAttribute("message", "File uploaded successfully!");
			model.addAttribute("user", user);
			model.addAttribute("posts", postService.getUserPosts(user)); // 🟢 posts wapas bhejne ke liye

		} catch (Exception e) {
			model.addAttribute("uploadError", "File upload failed: " + e.getMessage());
		}
		return "dashboard";
	}
	
	
	
}
