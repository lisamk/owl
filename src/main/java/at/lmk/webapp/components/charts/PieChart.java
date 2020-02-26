package at.lmk.webapp.components.charts;

import java.util.Map;
import java.util.stream.Collectors;

import j2html.tags.DomContent;

public class PieChart extends Chart {

	Map<String, Double> entries;

	/**
	 *
	 * @param title
	 * @param id
	 * @param fa
	 * @param entries
	 */
	public PieChart(String title, String id, Map<String, Double> entries) {
		super(title, id, "fa-chart-pie mr-1");
		this.entries = entries;
	}

	@Override
	public DomContent getChartScript() {
		String scriptContent = ScriptTemplates.CHART_PIE;
		scriptContent = scriptContent.replace("[ID]", id);
		scriptContent = scriptContent.replace("[LABELS]",
				entries.keySet().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")));
		scriptContent = scriptContent.replace("[VALUES]",
				entries.values().stream().map(d -> d.toString()).collect(Collectors.joining(", ")));

		return script(rawHtml(scriptContent)).withType("text/javascript");
	}

}
