package at.lmk.webapp.components;

import at.lmk.webapp.Tags;
import j2html.tags.DomContent;

public abstract class Component implements Tags {

	protected String title;

	public Component(String title) {
		this.title = title;
	}

	public abstract DomContent render();

}
