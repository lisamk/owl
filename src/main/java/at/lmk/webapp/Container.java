package at.lmk.webapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public interface Container extends Tags {

	// Table
	public default DomContent table(String title, boolean showHeader, boolean showFooter, String[] header,
			Collection<String[]> collection) {
		return div(div(i().withClass("fas fa-table mr-1")).withText(title).withClass("card-header"),
				div(div(table(getHeader(showHeader, header), getFooter(showFooter, header), tbody(getData(collection)))
						.withClass("table table-bordered").withId("dataTable")).withClass("table-responsive"))
								.withClass("card-body")).withClass("card mb-4");
	}

	private DomContent[] getData(Collection<String[]> collection) {
		List<DomContent> content = new ArrayList<>();
		for (String[] line : collection)
			content.add(tr(Arrays.stream(line).map(s -> td(s)).toArray(DomContent[]::new)));
		return content.toArray(DomContent[]::new);
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

	// Chart

	public default DomContent areaChartDiv(Map<String, Double> entries, String id) {
		return div(div(i().withClass("fas fa-chart-area mr-1")).withText("Area Chart Example").withClass("card-header"),
				div(canvas().withId(id).attr("width", "100%").attr("height", "30")).withClass("card-body"),
				div("Updated yesterday at 11:59 PM").withClass("card-footer small text-muted")).withClass("card mb-4");
	}

	public default DomContent areaChartScript(Map<String, Double> entries, String id, int min, int max) {
		String scriptContent = ScriptTemplates.CHART_AREA;
		scriptContent = scriptContent.replace("[ID]", id);
		scriptContent = scriptContent.replace("[LABELS]",
				entries.keySet().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")));
		scriptContent = scriptContent.replace("[VALUES]",
				entries.values().stream().map(d -> d.toString()).collect(Collectors.joining(", ")));
		scriptContent = scriptContent.replace("[MIN]", String.valueOf(min));
		scriptContent = scriptContent.replace("[MAX]", String.valueOf(max));

		return script(rawHtml(scriptContent)).withType("text/javascript");
	}
}
