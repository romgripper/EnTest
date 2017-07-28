package bin.xing.entest;

import javax.servlet.http.HttpSession;

public class BaseController
{
  // Session attributes
  static final String USER = "user";

  // Model attributes
  static final String ERROR = "error";

  // Pages
  static final String PAGE_ERROR = "error";

  protected static UserAccount getUserAccount(HttpSession session)
  {
    return (UserAccount) session.getAttribute(USER);
  }

  protected String redirect(String url)
  {
    return "redirect:" + url;
  }
}
