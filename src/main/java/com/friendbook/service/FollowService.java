package com.friendbook.service;

import com.friendbook.model.Follow;
import com.friendbook.model.User;
import com.friendbook.repository.FollowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowService {

    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void followUser(User follower, User following) {
        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            followRepository.save(follow);
        }
    }

    public void unfollowUser(User follower, User following) {
        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    public List<User> getFollowers(User user) {
        return followRepository.findByFollowing(user)
                .stream()
                .map(Follow::getFollower)
                .toList();
    }

    public List<User> getFollowing(User user) {
        return followRepository.findByFollower(user)
                .stream()
                .map(Follow::getFollowing)
                .toList();
    }

    public long countFollowers(User user) {
        return followRepository.countByFollowing(user);
    }

    public long countFollowing(User user) {
        return followRepository.countByFollower(user);
    }

    public boolean isFollowing(User follower, User following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
}
