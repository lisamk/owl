package at.lmk.webapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.lmk.webapp.pages.EmptyPage;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public abstract class Page extends EmptyPage implements Tags {

	private enum Block {
		CORE(BlockEntry.INDEX), INTERFACE(BlockEntry.TABLES), CHARTS(BlockEntry.AREACHARTPAGE, BlockEntry.PIECHARTPAGE);

		BlockEntry[] pages;

		Block(BlockEntry... pages) {
			this.pages = pages;
		}
	}

	public enum BlockEntry {
		INDEX("Home", "Index", "fas fa-tachometer-alt"), TABLES("Tables", "Tables", "fas fa-table"),
		AREACHARTPAGE("Area Chart", "AreaChart", "fas fa-chart-area"),
		PIECHARTPAGE("Pie Chart", "PieChart", "fas fa-chart-pie");

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

	public abstract DomContent getContents();

	@Override
	protected void getPageContent(List<DomContent> content) {
		content.add(getTopNav());
		content.add(getContent());
	}

	private ContainerTag getTopNav() {
		return nav(a(img().withSrc("assets/img/owl.svg").attr("srcset", "assets/img/owl.svg").attr("height", "40"))
				.withText("     OWL").withStyle("text-align:center").withClass("navbar-brand").withHref("Index"),
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

	private DomContent getContent() {
		return div(getSideNav(), getMain()).withId("layoutSidenav");
	}

	private DomContent getMain() {
		return div(
				main(div(h1(getTitle()).withClass("mt-4"),
						ol(li(getTitle()).withClass("breadcrumb-item active")).withClass("breadcrumb mb-4"),
						div(getContents())).withClass("container-fluid")),
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
		for (BlockEntry b : BlockEntry.values())
			if (b.name().equals(className.toUpperCase()))
				return BlockEntry.valueOf(className.toUpperCase()).name;
		return className;
	}

	@Override
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

}
