package at.lmk.webapp.components.charts;

import at.lmk.webapp.components.Component;
import j2html.tags.DomContent;

public abstract class Chart extends Component {

	protected String id;
	private String fa;

	public Chart(String title, String id, String fa) {
		super(title);
		this.id = id;
		this.fa = fa;
	}

	public abstract DomContent getChartScript();

	@Override
	public DomContent render() {
		return div(div(i().withClass("fas " + fa)).withText(title).withClass("card-header"),
				div(canvas().withId(id).attr("width", "100%").attr("height", "30")).withClass("card-body")
		/*
		 * ,div("Updated yesterday at 11:59 PM").
		 * withClass("card-footer small text-muted")
		 */).withClass("card mb-4");
	}

}
