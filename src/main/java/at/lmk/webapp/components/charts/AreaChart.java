package at.lmk.webapp.components.charts;

import java.util.Map;
import java.util.stream.Collectors;

import j2html.tags.DomContent;

public class AreaChart extends Chart {

	Map<String, Double> entries;
	int min;
	int max;

	/**
	 *
	 * @param title
	 * @param id
	 * @param fa
	 * @param entries
	 * @param min
	 * @param max
	 */
	public AreaChart(String title, String id, Map<String, Double> entries, int min, int max) {
		super(title, id, "fa-chart-area mr-1");
		this.entries = entries;
		this.min = min;
		this.max = max;
	}

	@Override
	public DomContent getChartScript() {
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
