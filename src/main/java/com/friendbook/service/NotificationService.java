package com.friendbook.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.friendbook.model.Notification;
import com.friendbook.model.User;
import com.friendbook.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    // Follow-back notification create karega
    public void createFollowBackNotification(User receiver, User targetUser) {
        // Check agar already notification exist hai toh dobara na banao
        List<Notification> existing = repo.findByReceiverOrderByCreatedAtDesc(receiver);
        boolean alreadyExists = existing.stream().anyMatch(
            n -> n.getMessage().contains("::FOLLOW_BACK::" + targetUser.getId())
        );
        if (alreadyExists) return;

        Notification n = new Notification();
        n.setReceiver(receiver);
        n.setTitle("Follow Back");
        n.setMessage("Do you also follow " + targetUser.getName() + "?::FOLLOW_BACK::" + targetUser.getId());
        repo.save(n);
    }

    public List<Notification> getNotifications(User user) {
        return repo.findByReceiverOrderByCreatedAtDesc(user);
    }

    public void deleteNotification(Long id) {
        repo.deleteById(id);
    }

    public void clearUserNotifications(User user) {
        List<Notification> all = repo.findByReceiverOrderByCreatedAtDesc(user);
        repo.deleteAll(all);
    }
    public void clearFollowBackNotification(User user, Long targetUserId) {
        List<Notification> all = repo.findByReceiverOrderByCreatedAtDesc(user);
        all.stream()
            .filter(n -> n.getMessage().contains("::FOLLOW_BACK::" + targetUserId))
            .findFirst()
            .ifPresent(repo::delete);
    }

}
