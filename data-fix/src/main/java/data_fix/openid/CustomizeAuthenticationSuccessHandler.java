package data_fix.openid;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomizeAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
		implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// set our response to OK status
		String url = "";

		HttpSession session = request.getSession(false);
		if (session != null && url.equals("")) {
			SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
			if (savedRequest != null) {
				url = savedRequest.getRedirectUrl();
			}
			System.out.println("URL: " + url);
		}

		response.setStatus(HttpServletResponse.SC_OK);

		// for (GrantedAuthority auth : authentication.getAuthorities()) {
		// if ("ADMIN".equals(auth.getAuthority())) {
		// response.sendRedirect("/admin");
		// }

		System.out.println("I ENTER CUSTOMIZEAUTHENTICATION SUCCESS HANDLER");

		if (authentication.getAuthorities().isEmpty()) {
			System.out.println("IF");
			response.sendRedirect("/registration");
		} else {
			System.out.println("ELSE: ");
			response.sendRedirect(url);
		}

	}

}
