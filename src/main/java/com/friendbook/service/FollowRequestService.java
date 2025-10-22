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

	public FollowRequestService(FollowRequestRepository repo) {
		this.repo = repo;
	}

	public void sendRequest(User sender, User receiver) {
		if (sender.getId().equals(receiver.getId()))
			return;

		FollowRequest existing = repo.findBySenderAndReceiver(sender, receiver);
		if (existing != null) {
			if (existing.getStatus() == FollowRequest.Status.ACCEPTED
					|| existing.getStatus() == FollowRequest.Status.PENDING)
				return;
			existing.setStatus(FollowRequest.Status.PENDING);
			repo.save(existing);
			return;
		}

		FollowRequest req = new FollowRequest();
		req.setSender(sender);
		req.setReceiver(receiver);
		req.setStatus(FollowRequest.Status.PENDING);
		repo.save(req);
	}

	public void followBack(User sender, User receiver) {
		if (sender.getId().equals(receiver.getId()))
			return;

		FollowRequest existing = repo.findBySenderAndReceiver(sender, receiver);
		if (existing != null && existing.getStatus() == FollowRequest.Status.ACCEPTED)
			return;
		if (existing == null) {
			existing = new FollowRequest();
			existing.setSender(sender);
			existing.setReceiver(receiver);
		}
		existing.setStatus(FollowRequest.Status.ACCEPTED); // Direct accept
		repo.save(existing);
	}

	public User acceptRequest(Long requestId, User actingUser) {
		Optional<FollowRequest> opt = repo.findById(requestId);
		if (opt.isPresent()) {
			FollowRequest request = opt.get();
			if (!request.getReceiver().getId().equals(actingUser.getId()))
				return null;
			request.setStatus(FollowRequest.Status.ACCEPTED);
			repo.save(request);
			return request.getSender();
		}
		return null;
	}

	public boolean rejectRequest(Long requestId, User actingUser) {
		Optional<FollowRequest> opt = repo.findById(requestId);
		if (opt.isPresent()) {
			FollowRequest request = opt.get();
			if (!request.getReceiver().getId().equals(actingUser.getId()))
				return false;
			request.setStatus(FollowRequest.Status.REJECTED);
			repo.save(request);
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

	public boolean isRequested(User sender, User receiver) {
		FollowRequest fr = repo.findBySenderAndReceiver(sender, receiver);
		return fr != null && fr.getStatus() == FollowRequest.Status.PENDING;
	}

	public long countFollowers(User user) {
		return repo.countByReceiverAndStatus(user, FollowRequest.Status.ACCEPTED);
	}

	public long countFollowing(User user) {
		return repo.countBySenderAndStatus(user, FollowRequest.Status.ACCEPTED);
	}

	public FollowRequest findBySenderAndReceiver(User sender, User receiver) {
		return repo.findBySenderAndReceiver(sender, receiver);
	}

	public List<User> getFollowingUsers(User user) {
		return repo.findBySenderAndStatus(user, FollowRequest.Status.ACCEPTED).stream().map(FollowRequest::getReceiver)
				.toList();
	}

	public List<User> getFollowers(User user) {
		return repo.findByReceiverAndStatus(user, FollowRequest.Status.ACCEPTED).stream().map(FollowRequest::getSender)
				.toList();
	}

	public boolean unfollow(User sender, User receiver) {
		if (sender == null || receiver == null)
			return false;
		FollowRequest existing = repo.findBySenderAndReceiver(sender, receiver);
		if (existing != null && existing.getStatus() == FollowRequest.Status.ACCEPTED) {
			repo.delete(existing);
			return true;
		}
		return false;
	}

	public boolean withdrawRequest(User sender, User receiver) {
		FollowRequest existing = repo.findBySenderAndReceiver(sender, receiver);
		if (existing != null && existing.getStatus() == FollowRequest.Status.PENDING) {
			repo.delete(existing);
			return true;
		}
		return false;
	}
}
