package bin.xing.entest;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController extends BaseController
{
  // URLs
  static final String URL_HOME = "/home";
  static final String URL_VIEW_NOTEBOOK = "/viewNotebook";
  static final String URL_VIEW_NOTE = "/viewNote";

  // Model attributes
  private static final String NOTEBOOKS = "notebooks";
  private static final String NOTEBOOK = "notebook";
  private static final String NOTES = "notes";
  private static final String NOTE = "note";
  private static final String NOTE_CONTENT = "noteContent";

  // Pages
  static final String PAGE_HOME = "home";
  static final String PAGE_NOTEBOOK = "notebook";
  static final String PAGE_NOTE = "note";

  @RequestMapping(value = URL_HOME, method = RequestMethod.GET)
  public String home(Model model, HttpSession session)
  {
    System.out.println("Enter home");
    try
    {
      NoteManager noteManager = getNoteManager(session);
      model.addAttribute(NOTEBOOKS, noteManager.getNotebooks());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      model.addAttribute(ERROR, e.getMessage());
      return PAGE_ERROR;
    }
    return PAGE_HOME;
  }

  @RequestMapping(value = URL_VIEW_NOTEBOOK, method = RequestMethod.GET)
  public String viewNotebook(Model model, HttpSession session, @RequestParam("notebook") String guid)
  {
    System.out.println("Enter viewNotebook");
    try
    {
      NoteManager noteManager = getNoteManager(session);
      Notebook notebook = noteManager.getNotebookByGuid(guid);
      List<Note> notes = noteManager.getNotes(notebook);
      model.addAttribute(NOTEBOOK, notebook);
      model.addAttribute(NOTES, notes);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      model.addAttribute(ERROR, e.getMessage());
      return PAGE_ERROR;
    }
    return PAGE_NOTEBOOK;
  }

  @RequestMapping(value = URL_VIEW_NOTE, method = RequestMethod.GET)
  public String viewNote(Model model, HttpSession session, @RequestParam("note") String guid)
  {
    System.out.println("Enter viewNote");
    try
    {
      NoteManager noteManager = getNoteManager(session);
      Note note = noteManager.getNoteByGuid(guid);
      model.addAttribute(NOTE, note);
      model.addAttribute(NOTE_CONTENT, noteManager.composeNoteContentHtml(note));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      model.addAttribute(ERROR, e.getMessage());
      return PAGE_ERROR;
    }
    return PAGE_NOTE;
  }

  private static NoteManager getNoteManager(HttpSession session)
  {
    return getUserAccount(session).createNoteManager();
  }

}
