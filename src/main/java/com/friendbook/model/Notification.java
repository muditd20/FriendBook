package com.friendbook.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(length = 1000)
	private String message;

	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne
	private User receiver;

	@Transient
	private String messagePart1;
	@Transient
	private Long followBackUserId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getMessagePart1() {
		return messagePart1;
	}

	public void setMessagePart1(String messagePart1) {
		this.messagePart1 = messagePart1;
	}

	public Long getFollowBackUserId() {
		return followBackUserId;
	}

	public void setFollowBackUserId(Long followBackUserId) {
		this.followBackUserId = followBackUserId;
	}
}
