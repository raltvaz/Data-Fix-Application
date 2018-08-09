package data_fix.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import data_fix.model.Role;
import data_fix.model.User;
import data_fix.repository.RoleRepository;
import data_fix.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User findUserByToken(String Token) {
		return userRepository.findByToken(Token);
	}

	@Override
	public void saveUser(User user, List<String> role) {
		// user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);

		Set<Role> userRoles = new HashSet<Role>();
		Role userRole = null;

		for (String r : role) {
			userRole = roleRepository.findByRole(r);
			userRoles.add(userRole);
		}

		System.out.println("THE ROLE ID IS: " + userRole.getId() + ", THE ROLE IS: " + userRole.getRole());
		user.setRoles(userRoles);
		userRepository.save(user);
	}

	@Override
	public void saveUser(User user) {
		// TODO Auto-generated method stub
		user.setActive(1);
		userRepository.save(user);
	}

	@Override
	public User findById(int id) {
		for (User u : findAllUsers()) {
			if (u.getId() == id) {
				return u;
			}
		}
		return null;
	}

	@Override
	public List<String> findAllRoles() {
		// TODO Auto-generated method stub

		List<String> roles = new ArrayList<String>();

		for (Role r : roleRepository.findAll()) {
			System.out.println(r.getRole());
			roles.add(r.getRole());

		}

		return roles;
	}

	@Override
	public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {

		User user = userRepository.findByToken(token);
		if (user != null) {
			List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
			return buildUserForAuthentication(user, authorities);
		} else {
			throw new UsernameNotFoundException("username not found");
		}
	}

	private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
		Set<GrantedAuthority> roles = new HashSet<>();
		userRoles.forEach((role) -> {
			roles.add(new SimpleGrantedAuthority(role.getRole()));
		});

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
		return grantedAuthorities;
	}

	private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}

	@Override
	public Iterable<User> findAllUsers() {

		return userRepository.findAll();

	}

	@Override
	public void deleteUserById(Long id) {
		userRepository.delete(id);
	}

}
