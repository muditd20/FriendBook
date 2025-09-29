package com.friendbook.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "follows")
public class Follow {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "follower_id",nullable = false)
	private User follower;
	
	@ManyToOne
	@JoinColumn(name = "following_id",nullable = false)
	private User following;
	
	private LocalDateTime followedAt=LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getFollower() {
		return follower;
	}

	public void setFollower(User follower) {
		this.follower = follower;
	}

	public User getFollowing() {
		return following;
	}

	public void setFollowing(User following) {
		this.following = following;
	}

	public LocalDateTime getFollowedAt() {
		return followedAt;
	}

	public void setFollowedAt(LocalDateTime followedAt) {
		this.followedAt = followedAt;
	}
	
}


