package data_fix.openid;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
@PropertySource("database.properties")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;

	@Autowired
	private DataSource dataSource;

	@Value("${spring.queries.users-query}")
	private String usersQuery;

	@Value("${spring.queries.roles-query}")
	private String rolesQuery;

	@Value("${orcid.loginPath}")
	private String loginPath;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().usersByUsernameQuery(usersQuery).authoritiesByUsernameQuery(rolesQuery)
				.dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);
	}

	/*
	 * @Override public void configure(WebSecurity web) throws Exception {
	 * web.ignoring().antMatchers("/resources/**"); }
	 */

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**");
		// web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**",
		// "/js/**", "/images/**");
	}

	@Bean
	public OpenIDConnectFilter myFilter() {
		final OpenIDConnectFilter filter = new OpenIDConnectFilter(loginPath);
		filter.setRestTemplate(restTemplate);
		filter.setAuthenticationSuccessHandler(customizeAuthenticationSuccessHandler);
		return filter;
	}

	@Bean
	public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
		return new MySimpleUrlAuthenticationSuccessHandler();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * http.addFilterAfter(new OAuth2ClientContextFilter(),
		 * AbstractPreAuthenticatedProcessingFilter.class) .addFilterAfter(myFilter(),
		 * OAuth2ClientContextFilter.class).httpBasic() .authenticationEntryPoint(new
		 * LoginUrlAuthenticationEntryPoint(loginPath)).and().authorizeRequests()
		 * .anyRequest().authenticated()/*
		 * .antMatchers("/admin").hasAuthority("ADMIN").anyRequest(). authenticated()
		 * .and().csrf().disable().formLogin().successHandler(
		 * customizeAuthenticationSuccessHandler) ;
		 */

		// http.authorizeRequests().antMatchers("/admin").access("hasRole('ROLE_USER')").anyRequest().permitAll();
		// http.csrf().disable().authorizeRequests();

		http.authorizeRequests().antMatchers("/welcome").permitAll();
		/*
		 * http.authorizeRequests().antMatchers("/admin").hasAuthority("ADMIN").
		 * anyRequest().authenticated()
		 * .antMatchers("/user").hasAuthority("USER").anyRequest().authenticated();
		 */

		http.addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
				.addFilterAfter(myFilter(), OAuth2ClientContextFilter.class).httpBasic()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(loginPath)).and().authorizeRequests()
				.anyRequest().authenticated();

	}
}

// http://www.baeldung.com/spring-security-redirect-login
