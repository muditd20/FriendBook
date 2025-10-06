package com.friendbook.service;

import com.friendbook.dto.LoginRequest;
import com.friendbook.dto.RegisterRequest;
import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User register(RegisterRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists!");
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		return userRepository.save(user);
	}

	public User login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid credentials!"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentials!");
		}

		return user;
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public void save(User user) {
		userRepository.save(user);
	}

	public List<User> searchUsers(String keyword) {
		return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
	}

	public String uploadProfilePhoto(User user, MultipartFile file) {
	    if (file.isEmpty()) {
	        throw new RuntimeException("Please select photo to upload!");
	    }

	    // ✅ File type validation
	    String contentType = file.getContentType();
	    if (contentType == null || 
	       !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
	        throw new RuntimeException("Only JPG and PNG images are allowed!");
	    }

	    try {
	        String uploadDir = "uploads/";
	        File dir = new File(uploadDir);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }

	        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	        String filePath = uploadDir + fileName;

	        file.transferTo(new File(filePath));

	        user.setProfilePhoto(fileName);
	        userRepository.save(user);

	        return fileName;

	    } catch (IOException e) {
	        throw new RuntimeException("File upload failed: " + e.getMessage());
	    }
	}

}
