package data_fix.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import data_fix.model.User;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
	User findByToken(String Token);
}
