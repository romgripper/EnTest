package bin.xing.entest;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.thrift.TException;

import java.util.List;

public class NoteManager
{
  private final NoteStoreClient noteStore;
  private final String urlPrefix;

  NoteManager(NoteStoreClient noteStore, String urlPrefix)
  {
    this.noteStore = noteStore;
    this.urlPrefix = urlPrefix;
  }

  public List<Notebook> getNotebooks() throws EDAMUserException, EDAMSystemException, TException
  {
    return noteStore.listNotebooks();
  }

  public Notebook getNotebookByGuid(String guid) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException,
      TException
  {
    return noteStore.getNotebook(guid);
  }

  public Note getNoteByGuid(String guid) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException
  {
    Note note = noteStore.getNote(guid, true, false, false, false);
    return note;
  }

  public String composeNoteContentHtml(Note note) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException
  {
    String content = note.getContent();
    int start = content.indexOf("<en-note>") + "<en-note>".length();
    int end = content.indexOf("</en-note>");
    content = content.substring(start, end);
    StringBuilder sb = new StringBuilder();
    for (String line : content.split("\n"))
    {
      System.out.println(line);
      // Suppose consider every en-media tags does cross lines and no corrupted tags
      if (!line.contains("<en-media"))
      {
        sb.append(line).append("\n");
      }
      else
      {
        // Locate en-media tag
        start = line.indexOf("<en-media");
        end = line.indexOf("</en-media>") + "</en-media>".length();
        String mediaTag = line.substring(start, end);

        // Parse hash
        start = mediaTag.indexOf("hash=\"") + "hash=\"".length();
        end = mediaTag.indexOf("\"", start);
        String hash = mediaTag.substring(start, end);
        System.out.println("Hash in hex: " + hash);
        byte[] hashBytes = hexStringToByteArray(hash);

        // Get resource url
        Resource res = noteStore.getResourceByHash(note.getGuid(), hashBytes, false, false, false);
        String resGuid = res.getGuid();
        String resUrl = urlPrefix + "/res/" + resGuid;
        // TODO check mime type
        String mimeType = res.getMime();
        System.out.println("resUrl: " + resUrl);
        System.out.println("mimeType: " + mimeType);
        String imageTag = "<img src=\"" + resUrl + "\"/>";
        line = line.replace(mediaTag, imageTag);
        System.out.println("Processed line: " + line);
        sb.append(line);
      }
    }

    return sb.toString();
  }

  public List<Note> getNotes(Notebook notebook) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException
  {
    System.out.println("Notebook: " + notebook.getName());
    NoteFilter filter = new NoteFilter();
    filter.setNotebookGuid(notebook.getGuid());
    filter.setOrder(NoteSortOrder.CREATED.getValue());
    filter.setAscending(true);

    NoteList noteList = noteStore.findNotes(filter, 0, 100);
    List<Note> notes = noteList.getNotes();
    for (Note note : notes)
    {
      System.out.println(" * " + note.getTitle());
    }
    return notes;
  }

  private static byte[] hexStringToByteArray(String s)
  {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2)
    {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
          + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }
}
