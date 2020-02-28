package at.lmk.webapp.pages;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import j2html.tags.DomContent;

@WebServlet("/Login")
public class Login extends EmptyPage {

	private static final long serialVersionUID = 3767267658630297614L;

	@Override
	protected void getPageContent(List<DomContent> content) {
		String forward = request.getParameter("return");

		content.add(
				div(div(i().withClass("fas fa-user")).withStyle("margin-top:50px"),
						form(input().withType("text").withId("login").withName("login").withPlaceholder("Login"),
								input().withType("text").withId("password").withName("password")
										.withPlaceholder("Password"),
								input().withType("submit").withValue("Login"))
										.withAction(forward == null ? "Index" : forward),
						div(a("Passwort vergessen?").withHref("#")).withId("formFooter")).withId("formContent")
								.withClass("centerDiv"));
	}

}
