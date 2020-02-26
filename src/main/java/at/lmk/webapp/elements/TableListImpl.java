package at.lmk.webapp.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.lmk.webapp.Container;
import j2html.tags.DomContent;

public class TableListImpl<T> implements TableList<T>, Container {

	private String title;
	private boolean showHeader;
	private boolean showFooter;
	private Map<String, TableColumn> columns = new HashMap<>();
	private Collection<T> data;

	public TableListImpl(String title) {
		this(title, true, false);
	}

	public TableListImpl(String title, boolean showHeader, boolean showFooter) {
		this.title = title;
		this.showHeader = showHeader;
		this.showFooter = showFooter;
	}

	@Override
	public void addColumn(TableColumn column) {
		columns.put(column.getTitle(), column);
	}

	@Override
	public void setData(Collection<T> data) {
		this.data = data;
	}

	@Override
	public String[] getHeadings() {
		return columns.keySet().toArray(String[]::new);
	}

	@Override
	public Collection<String[]> getData() {
		List<String[]> dataList = new ArrayList<>();
		for (Object o : data) {
			List<String> field = new ArrayList<>();
			for (TableColumn c : columns.values())
				field.add(c.getContent(o));
			dataList.add(field.toArray(String[]::new));
		}
		return dataList;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean showHeader() {
		return showHeader;
	}

	@Override
	public boolean showFooter() {
		return showFooter;
	}

	@Override
	public DomContent render() {
		return table(getTitle(), showHeader(), showFooter(), getHeadings(), getData());
	}

}
