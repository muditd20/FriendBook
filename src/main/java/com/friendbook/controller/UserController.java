package com.friendbook.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.User;
import com.friendbook.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🟢 Dashboard
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "dashboard";
    }

    // 🟢 Upload Profile Photo
    @PostMapping("/uploadPhoto")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file,
                              @RequestParam("email") String email,
                              Model model) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                model.addAttribute("uploadError", "User not found!");
                return "dashboard";
            }

            // ✅ uploads folder (static) ka absolute path
            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // ✅ unique filename
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // ✅ save file
            file.transferTo(filePath.toFile());

            // ✅ update user in DB
            user.setProfilePhoto(fileName);
            userService.save(user);

            model.addAttribute("message", "File uploaded successfully!");
            model.addAttribute("user", user);

        } catch (Exception e) {
            model.addAttribute("uploadError", "File upload failed: " + e.getMessage());
        }
        return "dashboard";
    }
}
