package at.lmk.webapp.pages;

import javax.servlet.annotation.WebServlet;

import at.lmk.webapp.Page;
import j2html.tags.DomContent;

@WebServlet(urlPatterns = "/Index")
public class Index extends Page {

	private static final long serialVersionUID = -17158504081538020L;

	@Override
	public DomContent getContents() {
		return text("Hallo!");
	}

	@Override
	protected DomContent getScript() {
		return null;
	}

}
