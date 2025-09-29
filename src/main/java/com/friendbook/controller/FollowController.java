package com.friendbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.friendbook.model.User;
import com.friendbook.service.FollowService;
import com.friendbook.service.UserService;

@Controller
@RequestMapping("/follow")
public class FollowController {

	private final FollowService followService;
	private final UserService userService;

	public FollowController(FollowService followService, UserService userService) {
		this.followService = followService;
		this.userService = userService;
	}

	@PostMapping("/do")
	public String followUser(@RequestParam("email") String email, @RequestParam("targetID") Long targetID) {
		User follower = userService.findByEmail(email);
		User following = userService.findById(targetID);

		if (follower != null && following != null && !follower.getId().equals(following.getId())) {
			followService.followUser(follower, following);
		}
		return "redirect:/user/dashboard?email=" + email;
	}

	public String unfollowUser(@RequestParam("email") String email, @RequestParam("targetId") Long targetId) {
		
		User follower = userService.findByEmail(email);
		User following = userService.findById(targetId);
		
		if(follower !=null && following !=null && !follower.getId().equals(following.getId()))
		{
			followService.unfollowUser(follower, following);
		}
		return "redirect:/user/dashboard?email=" + email;
	}

}
