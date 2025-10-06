package com.friendbook.service;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.PostRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

	private final PostRepository postRepository;

	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	public Post createPost(User user, String caption, MultipartFile imageFile) {
		try {
			if (imageFile == null || imageFile.isEmpty()) {
				throw new RuntimeException("Please upload an image!");
			}

			// ✅ File type validation
			String contentType = imageFile.getContentType();
			if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
				throw new RuntimeException("Only JPG and PNG images are allowed!");
			}

			String uploadDir = new File("src/main/resources/static/uploads/posts").getAbsolutePath();
			File folder = new File(uploadDir);
			if (!folder.exists())
				folder.mkdirs();

			String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
			Path filePath = Paths.get(uploadDir, fileName);
			imageFile.transferTo(filePath.toFile());

			Post post = new Post();
			post.setCaption(caption);
			post.setImagePath("posts/" + fileName);
			post.setUser(user);
			post.setCreatedAt(LocalDateTime.now());

			return postRepository.save(post);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create post: " + e.getMessage());
		}
	}

	public List<Post> getUserPosts(User user) {
		return postRepository.findByUserOrderByCreatedAtDesc(user);
	}

	public Post findById(Long id) {
		return postRepository.findById(id).orElse(null);
	}

	public Post updatePost(Long postId, String caption, MultipartFile imageFile) {
		Post post = findById(postId);
		if (post == null) {
			throw new RuntimeException("Post not Found");
		}

		post.setCaption(caption);

		try {
			if (imageFile != null && !imageFile.isEmpty()) {
				String uploadDir = new File("src/main/resources/static/uploads/posts").getAbsolutePath();
				File folder = new File(uploadDir);
				if (!folder.exists())
					folder.mkdir();

				String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
				Path filePath = Paths.get(uploadDir, fileName);
				imageFile.transferTo(filePath.toFile());

				post.setImagePath("posts/" + fileName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to update post: " + e.getMessage());
		}
		return postRepository.save(post);
	}

	public List<Post> getFeedPosts(User currentUser, FollowRequestService followRequestService) {
		List<User> followingUsers = followRequestService.getFollowingUsers(currentUser);
		if (followingUsers.isEmpty()) {
			return List.of(); // No following, empty feed
		}
		return postRepository.findByUserInOrderByCreatedAtDesc(followingUsers);
	}

	public void deletePost(Long postId) {
		Optional<Post> optionalPost = postRepository.findById(postId);
		if (optionalPost.isPresent()) {
			postRepository.delete(optionalPost.get());
		}
	}

}
