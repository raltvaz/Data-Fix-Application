package data_fix;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import data_fix.model.User;
import data_fix.service.UserService;

@SuppressWarnings("unused")
@Controller
public class DataFixController {

	@Autowired
	private UserService userService;

	@RequestMapping("/")
	@ResponseBody
	public ModelAndView home() {

		System.out.println("I AM AT HOME");
		ModelAndView modelAndView = new ModelAndView();
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.findUserByToken(username);
		modelAndView.setViewName("homePage");
		modelAndView.addObject("user", user);
		return modelAndView;

		// FIND IF USER EXISTS IN DATABASE BY THEIR TOKEN
		// if (user != null) {

		// final String creditName = ((OpenIDConnectUserDetails)
		// SecurityContextHolder.getContext().getAuthentication()
		// .getPrincipal()).getName();
		// String message = "Welcome, " /* + creditName + " */ + "(" + user.getName() +
		// ")" + ", You are a "
		// + user.getRoles().toString();
		// modelAndView.addObject("welcome", message);
		// modelAndView.setViewName("homePage");
		// modelAndView.addObject("roleArray", user.getRoles());
		// return modelAndView;
		// }
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public ModelAndView showCreateNewUserForm() {
		System.out.println("I MAKE IN REGISTRATIONS");
		ModelAndView modelAndView = new ModelAndView();
		User newUser = new User();
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		newUser.setToken(username);
		modelAndView.addObject("user", newUser);
		modelAndView.addObject("roles", userService.findAllRoles());
		modelAndView.setViewName("addUser");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST, params = { "name" })
	public ModelAndView createNewUser(Principal p, @Valid User user,
			@RequestParam("roleValues") List<String> roleValues, BindingResult bindingResult) {
		System.out.println("I MAKE IN REGISTRATION");
		System.out.println("roleValues: " + roleValues.toString());
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByToken(user.getToken());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user",
					"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("addUser");
		} else {

			System.out.println("TOKEN: " + user.getToken());
			userService.saveUser(user, roleValues);

			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("redirect:/logout");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public ModelAndView welcome() {
		ModelAndView modelAndView = new ModelAndView();

		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.findUserByToken(username);

		modelAndView.addObject("userName", user.getName());
		modelAndView.addObject("roleArray", user.getRoles());
		modelAndView.setViewName("welcomePage");

		return modelAndView;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public ModelAndView admin() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.findUserByToken(username);
		List<User> users = (List<User>) userService.findAllUsers();

		modelAndView.addObject("users", users);

		modelAndView.addObject("userName", user.getName());
		modelAndView.addObject("roleArray", user.getRoles());
		modelAndView.setViewName("adminHome");

		return modelAndView;
	}

	@RequestMapping(value = { "/edit-user-{id}" }, method = RequestMethod.GET)
	public String editUser(@PathVariable int id, ModelAndView model) {
		System.out.println("ID: " + id);
		User user = userService.findById(id);
		model.addObject("user", user);
		model.addObject("edit", true);
		return "registration";
	}

	@PreAuthorize("hasAnyAuthority('USER','ADMIN')")
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ModelAndView user() {
		ModelAndView modelAndView = new ModelAndView();

		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.findUserByToken(username);

		modelAndView.addObject("userName", user.getName());
		modelAndView.addObject("roleArray", user.getRoles());
		modelAndView.setViewName("userhome");

		return modelAndView;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {

		String url = "";
		String url2 = "";
		int index = 0;

		HttpSession session = request.getSession(false);
		if (session != null && url.equals("")) {
			SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
			url = savedRequest.getRedirectUrl();
		}

		index = url.lastIndexOf("0");
		url2 = url.substring(index + 2);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/" + url2;
	}

	@RequestMapping(value = "/logoutButton", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/";
	}

	// https://www.djamware.com/post/5b2f000880aca77b083240b2/spring-boot-security-and-data-mongodb-authentication-example
	// https://spring.io/guides/tutorials/spring-boot-oauth2/
}
