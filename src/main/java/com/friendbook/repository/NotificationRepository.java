package com.friendbook.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.friendbook.model.Notification;
import com.friendbook.model.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);
}
