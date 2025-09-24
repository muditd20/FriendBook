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
import java.util.UUID;

@Service
public class PostService {
	
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(User user, String caption, MultipartFile imageFile) {
        try {
            String uploadDir = new File("src/main/resources/static/uploads/posts").getAbsolutePath();
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

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
}
