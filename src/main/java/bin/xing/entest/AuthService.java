package bin.xing.entest;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class AuthService
{

  static final String CONSUMER_KEY = "xb1101101";
  static final String CONSUMER_SECRET = "6b137d13da8c6aca";
  static final EvernoteService EVERNOTE_SERVICE = EvernoteService.SANDBOX;

  OAuthService service;

  public AuthService(String callbackUrl)
  {
    Class<? extends EvernoteApi> providerClass = EvernoteApi.Sandbox.class;
    if (EVERNOTE_SERVICE == EvernoteService.PRODUCTION)
    {
      providerClass = org.scribe.builder.api.EvernoteApi.class;
    }
    service = new ServiceBuilder()
        .provider(providerClass)
        .apiKey(CONSUMER_KEY)
        .apiSecret(CONSUMER_SECRET)
        .callback(callbackUrl)
        .build();
  }

  private void retrieveRequestToken(AuthToken token)
  {
    Token scribeRequestToken = service.getRequestToken();
    String requestToken = scribeRequestToken.getToken();
    String requestTokenSecret = scribeRequestToken.getSecret();
    System.out.println("requestToken: " + requestToken);
    System.out.println("requestTokenSecret: " + requestTokenSecret);
    token.setRequestToken(requestToken);
    token.setRequestTokenSecret(requestTokenSecret);
  }

  private String getAuthorizationUrl(AuthToken token)
  {
    String url = EVERNOTE_SERVICE.getAuthorizationUrl(token.getRequestToken());
    System.out.println("authorizationUrl: " + url);
    return url;
  }

  String getEvernoteAuthUrl(AuthToken token)
  {
    retrieveRequestToken(token);
    return getAuthorizationUrl(token);
  }

  void retrieveAccessToken(AuthToken token)
  {
    Token scribeRequestToken = new Token(token.getRequestToken(), token.getRequestTokenSecret());
    Verifier scribeVerifier = new Verifier(token.getVerifier());
    Token scribeAccessToken = service.getAccessToken(scribeRequestToken, scribeVerifier);
    EvernoteAuth evernoteAuth = EvernoteAuth.parseOAuthResponse(EVERNOTE_SERVICE, scribeAccessToken.getRawResponse());
    String accessToken = evernoteAuth.getToken();
    String noteStoreUrl = evernoteAuth.getNoteStoreUrl();
    token.setAccessToken(accessToken);
    token.setNoteStoreUrl(noteStoreUrl);
    System.out.println("accessToken: " + accessToken);
    System.out.println("noteStoreUrl: " + noteStoreUrl);
  }
}
