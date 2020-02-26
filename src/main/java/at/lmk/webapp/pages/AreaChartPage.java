package at.lmk.webapp.pages;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import at.lmk.webapp.Page;
import at.lmk.webapp.components.charts.AreaChart;
import j2html.tags.DomContent;

@WebServlet("/AreaChart")
public class AreaChartPage extends Page {

	private static final long serialVersionUID = -6858360971345145800L;
	private HashMap<String, Double> map;
	private AreaChart chart;

	public AreaChartPage() {
		map = new HashMap<>();
		map.put("2000", Double.valueOf(105));
		map.put("2001", Double.valueOf(56));
		map.put("2002", Double.valueOf(125));
		map.put("2003", Double.valueOf(90));
		map.put("2004", Double.valueOf(75));
		map.put("2005", Double.valueOf(70));
		map.put("2006", Double.valueOf(110));

		chart = new AreaChart("Titel", "idAreaChart", map, 0, 150);
	}

	@Override
	public DomContent getContents() {
		return chart.render();
	}

	@Override
	protected DomContent getScript() {
		return chart.getChartScript();
	}
}
