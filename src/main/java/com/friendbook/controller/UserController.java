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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.model.FollowRequest;
import com.friendbook.model.Notification;
import com.friendbook.service.CommentService;
import com.friendbook.service.LikeService;
import com.friendbook.service.NotificationService;
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
	private final NotificationService notificationService;

	public UserController(UserService userService, PostService postService, LikeService likeService,
			CommentService commentService, FollowRequestService followRequestService,
			NotificationService notificationService) {
		this.userService = userService;
		this.postService = postService;
		this.likeService = likeService;
		this.commentService = commentService;
		this.followRequestService = followRequestService;
		this.notificationService = notificationService;
	}

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
		List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);
		model.addAttribute("pendingRequests", pendingRequests);
		return "dashboard";
	}

	@PostMapping("/uploadPhoto")
	public String uploadPhoto(@RequestParam("photo") MultipartFile file, @RequestParam("email") String email,
			RedirectAttributes redirectAttributes) {
		try {
			User user = userService.findByEmail(email);
			if (user == null) {
				redirectAttributes.addFlashAttribute("uploadError", "User not found!");
				return "redirect:/user/dashboard?email=" + email;
			}

			// Upload directory
			String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
			File uploadFolder = new File(uploadDir);
			if (!uploadFolder.exists()) {
				uploadFolder.mkdirs();
			}

			// Generate unique filename
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			Path filePath = Paths.get(uploadDir, fileName);
			file.transferTo(filePath.toFile());

			// Save to user
			user.setProfilePhoto(fileName);
			userService.save(user);

			// 🔹 Success message removed to avoid showing "File uploaded successfully"
			// Optional: you can use redirectAttributes.addFlashAttribute("message", "Photo
			// updated!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("uploadError", "File upload failed: " + e.getMessage());
		}

		// Redirect to dashboard to avoid template parsing error
		return "redirect:/user/dashboard?email=" + email;
	}

	@GetMapping("/search")
	public String searchPage(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam("email") String email, Model model) {
		User currentUser = userService.findByEmail(email);
		if (currentUser == null) {
			return "redirect:/auth/login";
		}
		List<User> searchResults = (keyword == null || keyword.isEmpty()) ? List.of()
				: userService.searchUsers(keyword);

		// ✅ Set alreadyFollowing for each user
		searchResults.forEach(u -> u.setAlreadyFollowing(followRequestService.isFollowing(currentUser, u)));

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("searchResults", searchResults);
		model.addAttribute("keyword", keyword);
		return "search-users";
	}

	// view pending requests page
	@GetMapping("/requests")
	public String viewRequests(@RequestParam("email") String email, Model model) {
		User user = userService.findByEmail(email);
		if (user == null)
			return "redirect:/auth/login";
		List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);
		model.addAttribute("user", user);
		model.addAttribute("pendingRequests", pendingRequests);
		return "follow-requests";
	}

	@GetMapping("/notifications")
	public String notifications(@RequestParam("email") String email, Model model) {
		User user = userService.findByEmail(email);
		if (user == null)
			return "redirect:/auth/login";

		// Pending follow requests
		List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);

		// Follow-back notifications
		List<Notification> followBacks = notificationService.getNotifications(user);
		followBacks.forEach(n -> {
			if (n.getMessage().contains("::FOLLOW_BACK::")) {
				String[] parts = n.getMessage().split("::");
				n.setMessagePart1(parts[0]);
				n.setFollowBackUserId(Long.parseLong(parts[2]));
			}
		});

		model.addAttribute("user", user);
		model.addAttribute("notifications", pendingRequests);
		model.addAttribute("otherNotifications", followBacks);

		return "notification";
	}

	@PostMapping("/follow-requests/accept")
	public String acceptFollowRequest(@RequestParam("requestId") Long requestId, @RequestParam("email") String email) {
		User actingUser = userService.findByEmail(email);
		if (actingUser != null) {
			User sender = followRequestService.acceptRequest(requestId, actingUser);
			if (sender != null) {
				// ✅ Notification create for follow-back
				notificationService.createFollowBackNotification(actingUser, sender);
			}
		}
		return "redirect:/user/notifications?email=" + email;
	}

	@PostMapping("/follow-requests/reject")
	public String rejectFollowRequest(@RequestParam("requestId") Long requestId, @RequestParam("email") String email) {
		User actingUser = userService.findByEmail(email);
		if (actingUser != null) {
			followRequestService.rejectRequest(requestId, actingUser);
		}
		return "redirect:/user/notifications?email=" + email;
	}

	@PostMapping("/follow-toggle")
	public String toggleFollow(@RequestParam("email") String email, @RequestParam("targetId") Long targetId,
			@RequestParam(value = "isFollowBack", required = false) Boolean isFollowBack) {
		User currentUser = userService.findByEmail(email);
		User targetUser = userService.findById(targetId);

		if (currentUser != null && targetUser != null && !currentUser.getId().equals(targetUser.getId())) {
			if (Boolean.TRUE.equals(isFollowBack)) {
				// ✅ Direct follow-back
				followRequestService.followBack(currentUser, targetUser);
				// sirf iss target user ka follow-back notification remove karo
				notificationService.clearFollowBackNotification(currentUser, targetUser.getId());
			} else {
				// ✅ Normal request
				followRequestService.sendRequest(currentUser, targetUser);
			}
		}
		return "redirect:/user/notifications?email=" + email;
	}
}
