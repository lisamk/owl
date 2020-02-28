package at.lmk.webapp.pages;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import at.lmk.webapp.ScriptPage;
import at.lmk.webapp.components.charts.PieChart;
import j2html.tags.DomContent;

@WebServlet("/PieChart")
public class PieChartPage extends ScriptPage {

	private static final long serialVersionUID = -6858360971345145800L;
	private HashMap<String, Double> map;
	private PieChart chart;

	public PieChartPage() {
		map = new HashMap<>();
		map.put("One", Double.valueOf(105));
		map.put("Two", Double.valueOf(56));
		map.put("Three", Double.valueOf(125));
		map.put("Four", Double.valueOf(90));

		chart = new PieChart("Pie", "pieId", map);
	}

	@Override
	public DomContent getContents() {
		return chart.render();
	}

	@Override
	public DomContent getScript() {
		return chart.getChartScript();
	}
}
