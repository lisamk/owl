package at.lmk.webapp.elements;

import java.util.Collection;

import j2html.tags.DomContent;

public interface TableList<T> {
	public void addColumn(TableColumn column);

	public void setData(Collection<T> list);

	public String[] getHeadings();

	public Collection<String[]> getData();

	public String getTitle();

	public boolean showHeader();

	public boolean showFooter();

	public DomContent render();
}
