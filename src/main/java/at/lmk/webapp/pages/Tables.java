package at.lmk.webapp.pages;

import javax.servlet.annotation.WebServlet;

import at.lmk.db.HibernateUtil;
import at.lmk.db.entities.User;
import at.lmk.webapp.Page;
import at.lmk.webapp.elements.ReflectedTableColumn;
import at.lmk.webapp.elements.TableList;
import at.lmk.webapp.elements.TableListImpl;
import j2html.tags.DomContent;

@WebServlet("/Tables")
public class Tables extends Page {

	private static final long serialVersionUID = -5327064381221370972L;

	@Override
	public DomContent getContents() {
		TableList<User> tableList = new TableListImpl<>("User");
		tableList.addColumn(new ReflectedTableColumn("E-Mail", "email"));
		tableList.addColumn(new ReflectedTableColumn("Vorname", "firstname"));
		tableList.addColumn(new ReflectedTableColumn("Nachname", "lastname"));
		tableList.addColumn(new ReflectedTableColumn("Passwort", "password"));
		tableList.setData(HibernateUtil.list(User.class));
		return tableList.render();
	}
}
