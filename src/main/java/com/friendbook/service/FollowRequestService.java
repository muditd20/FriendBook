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

		// create notification for receiver
		notificationService.createNotification(receiver, "Follow Request",
				sender.getName() + " has sent you a follow request.");
	}

	// Accept by id — now creates notification to sender
	public boolean acceptRequest(Long requestId, User actingUser) {
		Optional<FollowRequest> opt = repo.findById(requestId);
		if (opt.isPresent()) {
			FollowRequest request = opt.get();
			// security: only receiver can accept
			if (!request.getReceiver().getId().equals(actingUser.getId())) {
				return false;
			}
			request.setStatus(FollowRequest.Status.ACCEPTED);
			repo.save(request);

			// notify sender
			notificationService.createNotification(request.getSender(), "Follow Request Accepted",
					request.getReceiver().getName() + " accepted your follow request.");
			return true;
		}
		return false;
	}

	// Reject by id — now creates notification to sender
	public boolean rejectRequest(Long requestId, User actingUser) {
		Optional<FollowRequest> opt = repo.findById(requestId);
		if (opt.isPresent()) {
			FollowRequest request = opt.get();
			// security: only receiver can reject
			if (!request.getReceiver().getId().equals(actingUser.getId())) {
				return false;
			}
			request.setStatus(FollowRequest.Status.REJECTED);
			repo.save(request);

			// notify sender
			notificationService.createNotification(request.getSender(), "Follow Request Rejected",
					request.getReceiver().getName() + " rejected your follow request.");
			return true;
		}
		return false;
	}

	public List<FollowRequest> getPendingRequests(User receiver) {
		return repo.findByReceiverAndStatus(receiver, FollowRequest.Status.PENDING);
	}

	public boolean isFollowing(User sender, User receiver) {
		FollowRequest fr = repo.findBySenderAndReceiver(sender, receiver);
		return fr != null && fr.getStatus() == FollowRequest.Status.ACCEPTED;
	}

	public long countFollowers(User user) {
		return repo.countByReceiverAndStatus(user, FollowRequest.Status.ACCEPTED);
	}

	public long countFollowing(User user) {
		return repo.countBySenderAndStatus(user, FollowRequest.Status.ACCEPTED);
	}
}
