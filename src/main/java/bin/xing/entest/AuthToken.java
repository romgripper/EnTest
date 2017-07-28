package bin.xing.entest;

public class AuthToken
{
  String accessToken;
  String requestToken;
  String requestTokenSecret;
  String verifier;
  String noteStoreUrl;

  public String getAccessToken()
  {
    return accessToken;
  }

  public void setAccessToken(String accessToken)
  {
    this.accessToken = accessToken;
  }

  public String getRequestToken()
  {
    return requestToken;
  }

  public void setRequestToken(String requestToken)
  {
    this.requestToken = requestToken;
  }

  public String getRequestTokenSecret()
  {
    return requestTokenSecret;
  }

  public void setRequestTokenSecret(String requestTokenSecret)
  {
    this.requestTokenSecret = requestTokenSecret;
  }

  public String getVerifier()
  {
    return verifier;
  }

  public String getNoteStoreUrl()
  {
    return noteStoreUrl;
  }

  public void setNoteStoreUrl(String noteStoreUrl)
  {
    this.noteStoreUrl = noteStoreUrl;
  }

  public void setVerifier(String verifier)
  {
    this.verifier = verifier;
  }

  public String toString()
  {
    return "accessToken: " + accessToken + " requestToken: " + requestToken + " requestTokenSecret: " + requestTokenSecret
        + " verifier: " + verifier + " noteStoreUrl: " + noteStoreUrl;
  }
}
