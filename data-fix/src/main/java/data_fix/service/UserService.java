package data_fix.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import data_fix.model.User;

public interface UserService {

	public User findUserByToken(String token);

	public void saveUser(User user, List<String> role);

	public void saveUser(User user);

	public User findById(int id);

	public List<String> findAllRoles();

	Iterable<User> findAllUsers();

	void deleteUserById(Long id);

	UserDetails loadUserByUsername(String token) throws UsernameNotFoundException;
}
