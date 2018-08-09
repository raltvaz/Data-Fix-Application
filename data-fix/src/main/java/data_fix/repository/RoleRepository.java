package data_fix.repository;

import org.springframework.data.repository.CrudRepository;

import data_fix.model.Role;

//@Repository("roleRepository")
public interface RoleRepository extends CrudRepository<Role, Integer> {
	Role findByRole(String role);

}
