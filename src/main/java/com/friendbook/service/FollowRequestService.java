package com.friendbook.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.friendbook.model.FollowRequest;
import com.friendbook.model.User;
import com.friendbook.repository.FollowRequestRepository;

@Service
public class FollowRequestService {
	private final FollowRequestRepository repo;
	private final NotificationService notificationService;

	public FollowRequestService(FollowRequestRepository repo, NotificationService notificationService) {
		this.repo = repo;
		this.notificationService = notificationService;
	}

	public void sendRequest(User sender, User receiver) {
		if (sender.getId().equals(receiver.getId()))
			return;
		if (repo.existsBySenderAndReceiverAndStatus(sender, receiver, FollowRequest.Status.PENDING))
			return;

		FollowRequest req = new FollowRequest();
		req.setSender(sender);
		req.setReceiver(receiver);
		req.setStatus(FollowRequest.Status.PENDING);
		repo.save(req);

		// Notification
		notificationService.createNotification(receiver, "Follow Request",
				sender.getName() + " has sent you a follow request.");
	}

	// ✅ Accept by id
	public void acceptRequest(Long requestId) {
		Optional<FollowRequest> opt = repo.findById(requestId);
		if (opt.isPresent()) {
			FollowRequest request = opt.get();
			request.setStatus(FollowRequest.Status.ACCEPTED);
			repo.save(request);
		}
	}

	// ✅ Reject by id
	public void rejectRequest(Long requestId) {
		Optional<FollowRequest> opt = repo.findById(requestId);
		if (opt.isPresent()) {
			FollowRequest request = opt.get();
			request.setStatus(FollowRequest.Status.REJECTED);
			repo.save(request);
		}
	}

	public List<FollowRequest> getPendingRequests(User receiver) {
		return repo.findByReceiverAndStatus(receiver, FollowRequest.Status.PENDING);
	}

	public boolean isFollowing(User sender, User receiver) {
		FollowRequest fr = repo.findBySenderAndReceiver(sender, receiver);
		return fr != null && fr.getStatus() == FollowRequest.Status.ACCEPTED;
	}

	// ✅ Count followers (who accepted following this user)
	public long countFollowers(User user) {
		return repo.countByReceiverAndStatus(user, FollowRequest.Status.ACCEPTED);
	}

	// ✅ Count following (how many this user follows)
	public long countFollowing(User user) {
		return repo.countBySenderAndStatus(user, FollowRequest.Status.ACCEPTED);
	}
}
