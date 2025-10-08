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

	// Send new follow request (allow resend if previously rejected)
	public void sendRequest(User sender, User receiver) {
		if (sender.getId().equals(receiver.getId()))
			return;

		FollowRequest existing = repo.findBySenderAndReceiver(sender, receiver);
		if (existing != null) {
			if (existing.getStatus() == FollowRequest.Status.ACCEPTED
					|| existing.getStatus() == FollowRequest.Status.PENDING)
				return;
			// Previously rejected, allow resend
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

	// Direct follow without pending (used for Follow Back)
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
		existing.setStatus(FollowRequest.Status.ACCEPTED); // ✅ Direct accept
		repo.save(existing);
	}

	// Accept follow request (return sender user instead of boolean)
	public User acceptRequest(Long requestId, User actingUser) {
		Optional<FollowRequest> opt = repo.findById(requestId);
		if (opt.isPresent()) {
			FollowRequest request = opt.get();
			if (!request.getReceiver().getId().equals(actingUser.getId()))
				return null;
			request.setStatus(FollowRequest.Status.ACCEPTED);
			repo.save(request);
			return request.getSender(); // ✅ Return the sender user
		}
		return null;
	}

	// Reject follow request
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

	// Get all pending requests for a user
	public List<FollowRequest> getPendingRequests(User receiver) {
		return repo.findByReceiverAndStatus(receiver, FollowRequest.Status.PENDING);
	}

	// Check if already following
	public boolean isFollowing(User sender, User receiver) {
		FollowRequest fr = repo.findBySenderAndReceiver(sender, receiver);
		return fr != null && fr.getStatus() == FollowRequest.Status.ACCEPTED;
	}

	// Count followers (who accepted currentUser’s request)
	public long countFollowers(User user) {
		return repo.countByReceiverAndStatus(user, FollowRequest.Status.ACCEPTED);
	}

	// Count following (requests sent by currentUser and accepted)
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
	
	public boolean unfollow(User sender,User receiver)
	{
		if(sender ==null || receiver==null)return false;
		FollowRequest existing = repo.findBySenderAndReceiver(sender, receiver);
		if(existing !=null && existing.getStatus()==FollowRequest.Status.ACCEPTED)
		{
			repo.delete(existing);
			return true;
		}
		return false;
	}
}
