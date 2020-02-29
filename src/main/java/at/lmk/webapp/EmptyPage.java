package at.lmk.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.lmk.db.SessionUtil;
import at.lmk.webapp.pages.Login;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public abstract class EmptyPage extends HttpServlet implements Tags {

	private static final long serialVersionUID = -1012547116201790157L;

	protected static final String PAGE_TITLE = "Titel";

	protected HttpServletRequest request;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.request = request;

		boolean isLoggedIn = SessionUtil.checkForUserLogin(request);

		if (isLoggedIn && this instanceof Login)
			response.sendRedirect("Index");
		else if (isLoggedIn || this instanceof Login) {
			PrintWriter out = response.getWriter();
			out.write(render(this));
		} else if (SessionUtil.login(request))
			response.sendRedirect(request.getServletPath().substring(1));
		else
			response.sendRedirect("Login?return=" + request.getServletPath().substring(1));

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private String render(EmptyPage page) {
		return document(html(getHead(PAGE_TITLE), body(getBodyContent(page)).withClass("sb-nav-fixed")));
	}

	private DomContent[] getBodyContent(EmptyPage page) {
		List<DomContent> content = new ArrayList<>();
		getPageContent(content);
		for (DomContent d : getScripts())
			content.add(d);
		if (this instanceof ScriptPage)
			content.add(((ScriptPage) this).getScript());
		return content.toArray(new DomContent[content.size()]);
	}

	protected abstract void getPageContent(List<DomContent> content);

	public DomContent[] getScripts() {
		return new DomContent[] {
				script().withSrc("https://code.jquery.com/jquery-3.4.1.min.js").attr("crossorigin", "anonymous"),
				script().withSrc("https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.bundle.min.js")
						.attr("crossorigin", "anonymous"),
				script().withSrc("js/scripts.js").attr("crossorigin", "anonymous"),
				script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js")
						.attr("crossorigin", "anonymous"),
				script().withSrc("https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js").attr("crossorigin",
						"anonymous"),
				script().withSrc("https://cdn.datatables.net/1.10.20/js/dataTables.bootstrap4.min.js")
						.attr("crossorigin", "anonymous") };
	}

	private ContainerTag getHead(String title) {
		return head(meta().withCharset("UTF-8"), meta().attr("http-equiv", "X-UA-Compatible").withContent("IE=edge"),
				meta().withName("viewport").withContent("width=device-width, initial-scale=1, shrink-to-fit=no"),
				meta().withName("description").withContent(""),
				meta().withName("author").withContent("Lisa Maria Kritzinger"), title(title),
				link().withRel("icon").withHref("assets/img/favicon.png"),
				link().withRel("stylesheet").withHref("css/styles.css"),
				link().withRel("stylesheet")
						.withHref("https://cdn.datatables.net/1.10.20/css/dataTables.bootstrap4.min.css")
						.attr("crossorigin", "anonymous"),
				script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.11.2/js/all.min.js")
						.attr("crossorigin", "anonymous"));

	}

}
