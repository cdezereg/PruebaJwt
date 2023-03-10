package cl.dp.signup.repository;

import cl.dp.signup.models.User;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository  extends CrudRepository<User, String> {
	  Optional<User> findByEmail(String email);

	  Optional<User> findByUserId(UUID userId);

	  Boolean existsByEmail(String email);

	  Boolean existsByName(String name);
}
