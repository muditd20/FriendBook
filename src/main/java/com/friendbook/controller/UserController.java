package com.friendbook.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

	private UserService userService;

    
	public UserController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@GetMapping("/dashboard")
	public String dashboard(@RequestParam("email") String email, Model model) {
		User user = userService.findByEmail(email);
		model.addAttribute("user", user);
		return "dashboard";
	}

	@PostMapping("/upload-profile")
	public String uploadProfile(@RequestParam("file") MultipartFile file, @RequestParam("email") String email) throws Exception
	{
			User user = userService.findByEmail(email);
			if(user !=null && !file.isEmpty())
			{
				user.setProfilePicture(file.getBytes());
				userService.save(user);
			}
			return "redirect:/user/dashboard?email=" + email;
	}
	
	@GetMapping("/profile-picture/{id}")
	public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id)
	{
		User user = userService.findById(id);
		byte[] image = user.getProfilePicture();
		if(image !=null)
		{
			return ResponseEntity.ok()
					.contentType(MediaType.ALL)
					.body(image);
		}
		
		return ResponseEntity.notFound().build();
	}

	
}
