package at.lmk.webapp.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public class Table extends Component {

	private boolean showHeader;
	private boolean showFooter;
	private String[] header;
	private Collection<String[]> collection;

	public Table(String title, boolean showHeader, boolean showFooter, String[] header,
			Collection<String[]> collection) {
		super(title);
		this.showHeader = showHeader;
		this.showFooter = showFooter;
		this.header = header;
		this.collection = collection;
	}

	@Override
	public DomContent render() {
		return div(div(i().withClass("fas fa-table mr-1")).withText(title).withClass("card-header"),
				div(div(table(getHeader(showHeader, header), getFooter(showFooter, header), tbody(getData(collection)))
						.withClass("table table-bordered").withId("dataTable")).withClass("table-responsive"))
								.withClass("card-body")).withClass("card mb-4");
	}

	private DomContent[] getData(Collection<String[]> collection) {
		List<DomContent> content = new ArrayList<>();
		for (String[] line : collection)
			content.add(tr(Arrays.stream(line).map(s -> td(s)).toArray(DomContent[]::new)));
		return content.toArray(new DomContent[content.size()]);
	}

	private DomContent getHeader(boolean show, String[] header) {
		return !show ? text("") : thead(getHeadings(header));
	}

	private DomContent getFooter(boolean show, String[] header) {
		return !show ? text("") : tfoot(getHeadings(header));
	}

	private ContainerTag getHeadings(String[] header) {
		return tr(Arrays.stream(header).map(s -> th(s)).toArray(DomContent[]::new));
	}

}
