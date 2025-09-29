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

	public void createNotification(User receiver, String title, String message) {
		Notification n = new Notification();
		n.setReceiver(receiver);
		n.setTitle(title);
		n.setMessage(message);
		repo.save(n);
	}

	public List<Notification> getNotifications(User user) {
		return repo.findByReceiverOrderByCreatedAtDesc(user);
	}
}
