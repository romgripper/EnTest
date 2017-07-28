package bin.xing.entest;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;

public class UserAccount
{
  // private static final String AUTH_TOKEN =
  // "S=s1:U=92971:E=15c8beb6a46:C=155343a3d80:P=1cd:A=en-devtoken:V=2:H=b9528d0d88735ec64889c955756b1729";

  private final NoteStoreClient noteStore;

  private final String username;
  private final String urlPrefix;

  /**
   * Intialize UserStore and NoteStore clients. During this step, we
   * authenticate with the Evernote web service.
   */
  public UserAccount(String token) throws Exception
  {
    // Set up the UserStore client and check that we can speak to the server
    EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
    ClientFactory factory = new ClientFactory(evernoteAuth);
    UserStoreClient userStore = factory.createUserStoreClient();
    username = userStore.getUser().getUsername();
    urlPrefix = userStore.getPublicUserInfo(username).getWebApiUrlPrefix();
    System.out.println("UserAccount name: " + username);
    System.out.println("urlPrefix: " + urlPrefix);

    boolean versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
        com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
        com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
    if (!versionOk)
    {
      System.err.println("Incompatible Evernote client protocol version");
      System.exit(1);
    }

    // Set up the NoteStore client
    noteStore = factory.createNoteStoreClient();
  }

  // Something like a builder method for NoteManager
  public NoteManager createNoteManager()
  {
    return new NoteManager(noteStore, urlPrefix);
  }

  public String getUsername()
  {
    return username;
  }
}
