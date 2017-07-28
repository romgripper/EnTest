package bin.xing.entest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AuthInterceptor implements HandlerInterceptor
{

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception
  {

  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
      throws Exception
  {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
  {
    String contextPath = request.getContextPath();
    String url = request.getRequestURI();
    System.out.println("Intercept url: " + url);
    url = url.substring(contextPath.length());
    HttpSession session = request.getSession();
    if (!url.equals(AuthController.URL_WELCOME)
        && !url.equals(AuthController.URL_LOGIN)
        && !url.equals(AuthController.URL_LOGOUT)
        && !url.equals(AuthController.URL_CALLBACK)
        && !url.equals(BaseController.PAGE_ERROR + ".jsp"))
    {
      UserAccount userAccount = BaseController.getUserAccount(session);
      System.out.println("User account: " + userAccount);
      if (userAccount == null)
      {
        String query = request.getQueryString();
        if (query != null)
        {
          url += "?" + query;
        }
        session.setAttribute(AuthController.ORG_URL, url);
        System.out.println("Original url: " + url);
        response.sendRedirect(contextPath + AuthController.URL_LOGIN);
        return false;
      }
    }
    return true;
  }
}
