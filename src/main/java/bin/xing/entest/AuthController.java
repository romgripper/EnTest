package bin.xing.entest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController extends BaseController
{
  static final String URL_WELCOME = "/";
  static final String URL_LOGIN = "/login";
  static final String URL_LOGOUT = "/logout";
  static final String URL_CALLBACK = "/callback";

  // Session attributes
  static final String ORG_URL = "orgUrl";
  static final String AUTH_TOKEN = "authToken";
  static final String AUTH_SERVICE = "authService";

  // Pages
  static final String PAGE_WELCOME = "welcome";

  @RequestMapping(value = URL_WELCOME, method = RequestMethod.GET)
  public String welcome(HttpSession session)
  {
    System.out.println("Enter welcome");
    if (getUserAccount(session) != null) // User has logged in
    {
      return redirect(MainController.URL_HOME);
    }
    return PAGE_WELCOME;
  }

  @RequestMapping(value = URL_LOGIN, method = RequestMethod.GET)
  public String login(HttpServletRequest request, HttpSession session, Model model)
  {
    System.out.println("Enter login");

    UserAccount user = getUserAccount(session);
    System.out.println("user: " + user);
    if (user != null) // User has logged in
    {
      return redirect(getAndDeleteNextUrl(session));
    }

    AuthService authService = getAuthService(session);
    if (authService == null)
    {
      authService = createAuthService(session, request);
    }
    AuthToken authToken = getAuthToken(session);
    if (authToken == null)
    {
      authToken = createAuthToken(session);
    }
    return redirect(authService.getEvernoteAuthUrl(authToken));

  }

  @RequestMapping(value = URL_CALLBACK, method = RequestMethod.GET)
  public String authorizationCallback(HttpSession session, @RequestParam("oauth_token") String token,
      @RequestParam("oauth_verifier") String verifier, Model model)
  {
    System.out.println("Enter authorizationCallback");
    System.out.println("oauth_token: " + token);
    System.out.println("oauth_verifier: " + verifier);

    AuthToken authToken = getAuthToken(session);
    authToken.setRequestToken(token);
    authToken.setVerifier(verifier);
    AuthService authService = getAuthService(session);
    authService.retrieveAccessToken(authToken);

    try
    {
      createUserAccount(session, authToken);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      model.addAttribute(ERROR, e.getMessage());
      return PAGE_ERROR;
    }
    return redirect(getAndDeleteNextUrl(session));
  }

  @RequestMapping(value = URL_LOGOUT, method = RequestMethod.GET)
  public String logout(HttpSession session)
  {
    System.out.println("Enter logout");
    clearUserAccount(session);
    return redirect(URL_WELCOME);
  }

  private AuthService getAuthService(HttpSession session)
  {
    return (AuthService) session.getAttribute(AUTH_SERVICE);
  }

  private static AuthService createAuthService(HttpSession session, HttpServletRequest request)
  {
    String context = request.getContextPath();
    String thisUrl = request.getRequestURL().toString();
    System.out.println("This Url: " + thisUrl);
    int start = thisUrl.indexOf(context);
    String callbackUrl = thisUrl.substring(0, start) + context + URL_CALLBACK;
    System.out.println("Auth callback Url: " + callbackUrl);
    AuthService authService = new AuthService(callbackUrl);
    session.setAttribute(AUTH_SERVICE, authService);
    return authService;
  }

  private static AuthToken getAuthToken(HttpSession session)
  {
    return (AuthToken) session.getAttribute(AUTH_TOKEN);
  }

  private static AuthToken createAuthToken(HttpSession session)
  {
    AuthToken authToken = new AuthToken();
    session.setAttribute(AUTH_TOKEN, authToken);
    return authToken;
  }

  private static void createUserAccount(HttpSession session, AuthToken authToken) throws Exception
  {
    UserAccount userAccount = new UserAccount(authToken.getAccessToken());
    session.setAttribute(USER, userAccount);
  }

  private static void clearUserAccount(HttpSession session)
  {
    session.removeAttribute(USER);
  }

  private static String getAndDeleteNextUrl(HttpSession session)
  {
    String url = (String) session.getAttribute(ORG_URL);
    if (url == null)
    {
      url = MainController.URL_HOME;
    }
    else
    {
      session.removeAttribute(ORG_URL);
    }
    return url;
  }

}
