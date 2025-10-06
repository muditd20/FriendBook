package com.friendbook.repository;

import com.friendbook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
 
	@Query("select u from User u where u.email = :email")
	public User getUserByUserName(@Param("email") String email);

	Optional<User> findByEmail(String email);

	List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

}
