package at.lmk.webapp.pages;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import at.lmk.webapp.Page;
import j2html.tags.DomContent;

@WebServlet("/Charts2")
public class Charts2 extends Page {

	private static final long serialVersionUID = -6858360971345145800L;
	private HashMap<String, Double> map;

	public Charts2() {
		map = new HashMap<>();
		map.put("2000", Double.valueOf(105));
		map.put("2001", Double.valueOf(56));
		map.put("2002", Double.valueOf(125));
		map.put("2003", Double.valueOf(90));
		map.put("2004", Double.valueOf(75));
		map.put("2005", Double.valueOf(70));
		map.put("2006", Double.valueOf(110));
	}

	@Override
	public DomContent getContents() {
		return areaChartDiv(map, "testChart");
	}

	@Override
	protected DomContent getScript() {
		return areaChartScript(map, "testChart", 0, 150);
	}
}
