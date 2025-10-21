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
        if (user == null) return "redirect:/auth/login";

        List<Post> posts = postService.getUserPosts(user);

        long followersCount = followRequestService.countFollowers(user);
        long followingCount = followRequestService.countFollowing(user);

        // Get all followers/following independently
        List<User> followersList = followRequestService.getFollowers(user); // all users who follow this user
        List<User> followingList = followRequestService.getFollowingUsers(user); // all users this user follows

        List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("likeService", likeService);
        model.addAttribute("commentService", commentService);
        model.addAttribute("followersCount", followersCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("followersList", followersList);
        model.addAttribute("followingList", followingList);
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

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("uploadError", "File upload failed: " + e.getMessage());
        }

        return "redirect:/user/dashboard?email=" + email;
    }

    @GetMapping("/search")
    public String searchPage(@RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam("email") String email, Model model) {
        User currentUser = userService.findByEmail(email);
        if (currentUser == null)
            return "redirect:/auth/login";

        List<User> searchResults = (keyword == null || keyword.isEmpty()) ? List.of()
                : userService.searchUsers(keyword);

        searchResults.forEach(u -> {
            if (followRequestService.isFollowing(currentUser, u)) {
                u.setAlreadyFollowing(true);
                u.setRequested(false);
            } else if (followRequestService.isRequested(currentUser, u)) {
                u.setAlreadyFollowing(false);
                u.setRequested(true);
            } else {
                u.setAlreadyFollowing(false);
                u.setRequested(false);
            }
        });

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", keyword);

        return "search-users";
    }

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

        List<FollowRequest> pendingRequests = followRequestService.getPendingRequests(user);
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
                               @RequestParam(value = "isFollowBack", required = false) Boolean isFollowBack,
                               @RequestParam(value = "withdraw", required = false) Boolean withdraw) {
        User currentUser = userService.findByEmail(email);
        User targetUser = userService.findById(targetId);

        if (currentUser != null && targetUser != null && !currentUser.getId().equals(targetUser.getId())) {
            if (Boolean.TRUE.equals(isFollowBack)) {
                followRequestService.followBack(currentUser, targetUser);
                notificationService.clearFollowBackNotification(currentUser, targetUser.getId());
            } else if (Boolean.TRUE.equals(withdraw)) {
                boolean withdrawn = followRequestService.withdrawRequest(currentUser, targetUser);
                if (withdrawn)
                    notificationService.clearFollowBackNotification(targetUser, currentUser.getId());
            } else {
                followRequestService.sendRequest(currentUser, targetUser);
            }
        }

        return "redirect:/user/search?email=" + email;
    }

    @PostMapping("/unfollow")
    public String unfollowUser(@RequestParam("email") String email, @RequestParam("targetId") Long targetId) {
        User currentUser = userService.findByEmail(email);
        User targetUser = userService.findById(targetId);

        if (currentUser != null && targetUser != null) {
            boolean unfollowed = followRequestService.unfollow(currentUser, targetUser);
            if (unfollowed)
                notificationService.clearFollowBackNotification(currentUser, targetUser.getId());
        }

        return "redirect:/user/search?email=" + email;
    }

    @PostMapping("/saveFavorite")
    public String saveFavorite(@RequestParam("email") String email, @RequestParam("type") String type,
                               @RequestParam("value") String value) {
        User user = userService.findByEmail(email);
        if (user != null) {
            userService.saveFavorite(user, type, value);
        }
        return "redirect:/user/dashboard?email=" + email;
    }

}
