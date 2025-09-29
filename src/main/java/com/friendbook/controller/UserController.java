package com.friendbook.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.model.FollowRequest;
import com.friendbook.service.CommentService;
import com.friendbook.service.LikeService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;
import com.friendbook.service.FollowRequestService;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final PostService postService;
	private final LikeService likeService;
	private final CommentService commentService;
	private final FollowRequestService followRequestService;

	public UserController(UserService userService, PostService postService,
			LikeService likeService, CommentService commentService,
			FollowRequestService followRequestService) {
		this.userService = userService;
		this.postService = postService;
		this.likeService = likeService;
		this.commentService = commentService;
		this.followRequestService = followRequestService;
	}

	// ✅ Dashboard
	@GetMapping("/dashboard")
	public String dashboard(@RequestParam("email") String email, Model model) {
		User user = userService.findByEmail(email);
		if (user == null) {
			return "redirect:/auth/login";
		}

		List<Post> posts = postService.getUserPosts(user);

		model.addAttribute("user", user);
		model.addAttribute("posts", posts);
		model.addAttribute("likeService", likeService);
		model.addAttribute("commentService", commentService);

		model.addAttribute("followersCount", followRequestService.countFollowers(user));
		model.addAttribute("followingCount", followRequestService.countFollowing(user));

		// ✅ Pending requests for this user
		List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);
		model.addAttribute("pendingRequests", pendingRequests);

		return "dashboard";
	}

	// ✅ Upload Profile Photo
	@PostMapping("/uploadPhoto")
	public String uploadPhoto(@RequestParam("photo") MultipartFile file,
			@RequestParam("email") String email, Model model) {
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
			model.addAttribute("posts", postService.getUserPosts(user));

		} catch (Exception e) {
			model.addAttribute("uploadError", "File upload failed: " + e.getMessage());
		}
		return "dashboard";
	}

	// ✅ Search Users
	@GetMapping("/search")
	public String searchPage(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam("email") String email, Model model) {

		User currentUser = userService.findByEmail(email);
		if (currentUser == null) {
			return "redirect:/auth/login";
		}

		List<User> searchResults = (keyword == null || keyword.isEmpty()) ? List.of()
				: userService.searchUsers(keyword);

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("searchResults", searchResults);
		model.addAttribute("keyword", keyword);

		return "search-users";
	}

	// ✅ View pending requests page
	@GetMapping("/requests")
	public String viewRequests(@RequestParam("email") String email, Model model) {
		User user = userService.findByEmail(email);
		if (user == null)
			return "redirect:/auth/login";

		List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);
		model.addAttribute("user", user);
		model.addAttribute("pendingRequests", pendingRequests);

		return "follow-requests"; // new Thymeleaf page
	}

	// ✅ Accept request
	@PostMapping("/requests/accept")
	public String acceptFollowRequest(@RequestParam("requestId") Long requestId,
			@RequestParam("email") String email) {
		followRequestService.acceptRequest(requestId);
		return "redirect:/user/requests?email=" + email;
	}

	// ✅ Reject request
	@PostMapping("/requests/reject")
	public String rejectFollowRequest(@RequestParam("requestId") Long requestId,
			@RequestParam("email") String email) {
		followRequestService.rejectRequest(requestId);
		return "redirect:/user/requests?email=" + email;
	}

	// ✅ Send follow request
	@PostMapping("/follow-toggle")
	public String toggleFollow(@RequestParam("email") String email,
			@RequestParam("targetId") Long targetId) {
		User currentUser = userService.findByEmail(email);
		User targetUser = userService.findById(targetId);
		if (currentUser != null && targetUser != null && !currentUser.getId().equals(targetUser.getId())) {
			followRequestService.sendRequest(currentUser, targetUser);
		}
		return "redirect:/user/search?keyword=&email=" + email;
	}
	
	// ✅ Notifications Page
	@GetMapping("/notifications")
	public String notifications(@RequestParam("email") String email, Model model) {
	    User user = userService.findByEmail(email);
	    if (user == null) return "redirect:/auth/login";

	    List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);
	    model.addAttribute("user", user);
	    model.addAttribute("notifications", pendingRequests);

	    return "notification"; // this will load notification.html
	}

}
