//package com.friendbook.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.friendbook.model.User;
//import com.friendbook.service.FollowRequestService;
//import com.friendbook.service.NotificationService;
//import com.friendbook.service.UserService;
//
//@Controller
//@RequestMapping("/follow")
//public class FollowController {
//
//	private final UserService userService;
//	private final FollowRequestService followRequestService;
//	private final NotificationService notificationService;
//
//	public FollowController(UserService userService, FollowRequestService followRequestService,
//			NotificationService notificationService) {
//		this.userService = userService;
//		this.followRequestService = followRequestService;
//		this.notificationService = notificationService;
//	}
//
//	@PostMapping("/follow-requests/accept")
//	public String acceptFollowRequest(@RequestParam("requestId") Long requestId, @RequestParam("email") String email) {
//		User actingUser = userService.findByEmail(email);
//		if (actingUser != null) {
//			User sender = followRequestService.acceptRequest(requestId, actingUser);
//			if (sender != null) {
//				// ✅ Notification create for follow-back
//				notificationService.createFollowBackNotification(actingUser, sender);
//			}
//		}
//		return "redirect:/user/notifications?email=" + email;
//	}
//
//	@PostMapping("/follow-requests/reject")
//	public String rejectFollowRequest(@RequestParam("requestId") Long requestId, @RequestParam("email") String email) {
//		User actingUser = userService.findByEmail(email);
//		if (actingUser != null) {
//			followRequestService.rejectRequest(requestId, actingUser);
//		}
//		return "redirect:/user/notifications?email=" + email;
//	}
//
//	@PostMapping("/follow-toggle")
//	public String toggleFollow(@RequestParam("email") String email, @RequestParam("targetId") Long targetId,
//			@RequestParam(value = "isFollowBack", required = false) Boolean isFollowBack) {
//		User currentUser = userService.findByEmail(email);
//		User targetUser = userService.findById(targetId);
//
//		if (currentUser != null && targetUser != null && !currentUser.getId().equals(targetUser.getId())) {
//			if (Boolean.TRUE.equals(isFollowBack)) {
//				// ✅ Direct follow-back
//				followRequestService.followBack(currentUser, targetUser);
//				// sirf iss target user ka follow-back notification remove karo
//				notificationService.clearFollowBackNotification(currentUser, targetUser.getId());
//			} else {
//				// ✅ Normal request
//				followRequestService.sendRequest(currentUser, targetUser);
//			}
//		}
//		return "redirect:/user/notifications?email=" + email;
//	}
//
//}
