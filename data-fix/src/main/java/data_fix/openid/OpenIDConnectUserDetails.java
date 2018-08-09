package data_fix.openid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import data_fix.model.Role;
import data_fix.model.User;

public class OpenIDConnectUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private String name;
	private String username;
	private OAuth2AccessToken token;
	private User user;

	/**
	 * Takes userinfo from JWT token and turns into a User object Example JWT:
	 * {"aud":"APP-6LKIJ3I5B1C4YIQP","sub":"0000-0002-5062-2209","auth_time":1504616151,"iss":"https:\/\/orcid.org","name":"Mr
	 * Credit
	 * Name","exp":1504617454,"given_name":"Tom","iat":1504616854,"family_name":"Dem","jti":"3b2b662a-2429-4144-a986-06282b88d211"}
	 * 
	 * @param userInfo
	 * @param token
	 */
	public OpenIDConnectUserDetails(Map<String, String> jwtUserInfo, OAuth2AccessToken token, User user) {
		this.username = jwtUserInfo.get("sub");
		this.name = jwtUserInfo.get("name");
		this.token = token;
		this.user = user;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		System.out.println("I AM IN GETAUTHORITIES()");

		Collection<GrantedAuthority> retval = new ArrayList<GrantedAuthority>();
		if (null != user) {
			System.out.println("GETAUTHORITIES(): USER EXISTS");
			for (Role role : user.getRoles()) {
				retval.add(new SimpleGrantedAuthority(role.getRole()));
			}
		}

		// System.out.println("GRANTED AUTHORITY USER: " + retval.toArray().toString());

		return retval;

		// System.out.println("USER ROLES: " + user.getRoles().toString());

		// return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OAuth2AccessToken getToken() {
		return token;
	}

	public void setToken(OAuth2AccessToken token) {
		this.token = token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// https://medium.com/@bvulaj/mapping-your-users-and-roles-with-spring-boot-oauth2-a7ac3bbe8e7f
}
