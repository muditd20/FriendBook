package com.friendbook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.friendbook.model.FollowRequest;
import com.friendbook.model.User;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FollowRequest.Status status);

    FollowRequest findBySenderAndReceiver(User sender, User receiver);

    List<FollowRequest> findByReceiverAndStatus(User receiver, FollowRequest.Status status);

    // ✅ Add these two methods for counts
    long countByReceiverAndStatus(User receiver, FollowRequest.Status status);

    long countBySenderAndStatus(User sender, FollowRequest.Status status);
}
