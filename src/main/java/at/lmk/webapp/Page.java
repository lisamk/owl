package at.lmk.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public abstract class Page extends HttpServlet implements Container {

	private enum Block {
		CORE(BlockEntry.INDEX), INTERFACE(BlockEntry.TABLES, BlockEntry.CHARTS), ADDONS;

		BlockEntry[] pages;

		Block(BlockEntry... pages) {
			this.pages = pages;
		}
	}

	public enum BlockEntry {
		INDEX("Home", "Index", "fas fa-tachometer-alt"), TABLES("Tables", "Tables", "fas fa-table"),
		CHARTS("Charts", "Charts", "fas fa-chart-pie");

		String name;
		String href;
		String fa;

		BlockEntry(String name, String href, String fa) {
			this.name = name;
			this.href = href;
			this.fa = fa;
		}
	}

	private static final long serialVersionUID = -2283378096598115276L;

	private static final String PAGE_TITLE = "Titel";

	public abstract DomContent getContents();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		out.write(render(this));
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private String render(Page page) {
		return document(html(getHead(PAGE_TITLE), body(getBodyContent(page)).withClass("sb-nav-fixed")));
	}

	private DomContent[] getBodyContent(Page page) {
		List<DomContent> content = new ArrayList<>();
		content.add(getTopNav());
		content.add(getContent(page));
		for (DomContent d : getScripts())
			content.add(d);
		if (getScript() != null)
			content.add(getScript());
		return content.toArray(new DomContent[content.size()]);
	}

	protected abstract DomContent getScript();

	private ContainerTag getTopNav() {
		return nav(a(PAGE_TITLE).withClass("navbar-brand").withHref("Index"),
				button(i().withClass("fas fa-bars")).withClass("btn btn-link btn-sm order-1 order-lg-0")
						.withId("sidebarToggle"),
				form(div(input().withClass("form-control").withType("text").withPlaceholder("Search for..."),
						div(button(i().withClass("fas fa-search")).withClass("btn btn-primary").withType("button"))
								.withClass("input-group-append")).withClass("input-group")).withClass(
										"d-none d-md-inline-block form-inline ml-auto mr-0 mr-md-3 my-2 my-md-0"),
				ul(li(a(i().withClass("fas fa-user fa-fw")).withClass("nav-link dropdown-toggle").withId("userDropdown")
						.withHref("#").withRole("button").attr("data-toggle", "dropdown").attr("aria-haspopup", "true")
						.attr("aria-expanded", "false"),
						div(a("Settings").withClass("dropdown-item").withHref("#"),
								a("Activity Log").withClass("dropdown-item").withHref("#"),
								div().withClass("dropdown-divider"),
								a("Logout").withClass("dropdown-item").withHref("#"))
										.withClass("dropdown-menu dropdown-menu-right")
										.attr("aria-labelledby", "userDropdown")).withClass("nav-item dropdown"))
												.withClass("navbar-nav ml-auto ml-md-0")).withClass(
														"sb-topnav navbar navbar-expand navbar-dark bg-dark");
	}

	private DomContent getSideNav() {
		return div(nav(div(div(getSideNavBlocks()).withClass("nav")).withClass("sb-sidenav-menu"),
				div(div("Logged in as:").withClass("small")).withText("Start Bootstrap").withClass("sb-sidenav-footer"))
						.withClass("sb-sidenav accordion sb-sidenav-dark").withId("sidenavAccordion"))
								.withId("layoutSidenav_nav");
	}

	private DomContent[] getSideNavBlocks() {
		List<DomContent> blocks = new ArrayList<>();
		for (Block b : Block.values()) {
			blocks.add(div(b.name()).withClass("sb-sidenav-menu-heading"));
			for (BlockEntry e : b.pages)
				blocks.add(a(div(i().withClass(e.fa)).withClass("sb-nav-link-icon")).withText(e.name)
						.withClass("nav-link").withHref(e.href));
		}
		return blocks.toArray(new DomContent[blocks.size()]);
	}

	private DomContent getContent(Page page) {
		return div(getSideNav(), getMain(page)).withId("layoutSidenav");
	}

	private DomContent getMain(Page page) {
		return div(main(div(h1(page.getTitle()).withClass("mt-4"),
				ol(li(page.getTitle()).withClass("breadcrumb-item active"))
						.withClass("breadcrumb mb-4"),
				div(page.getContents())).withClass("container-fluid")),
				footer(div(div(div(
						div(text("Copyright "), rawHtml("&copy;"),
								text(" " + PAGE_TITLE + " " + Calendar.getInstance().get(Calendar.YEAR)))
										.withClass("text-muted"),
						div(a("Privacy Policy").withHref("#"), text(" - "), a("Terms & Conditions").withHref("#"))))
								.withClass("d-flex align-items-center justify-content-between small"))
										.withClass("container-fluid")).withClass("py-4 bg-light mt-auto"))
												.withId("layoutSidenav_content");
	}

	protected String getTitle() {
		String className = this.getClass().getSimpleName();
		return BlockEntry.valueOf(className.toUpperCase()) != null ? BlockEntry.valueOf(className.toUpperCase()).name
				: className;
	}

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
