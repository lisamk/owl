package at.lmk.webapp;

import java.io.IOException;

import at.lmk.db.HibernateUtil;
import at.lmk.db.entities.User;

public class Tests implements Tags {

	public static void main(String[] args) throws IOException {
		HibernateUtil.insert(new User().init("info@test.at", "Lisa", "Lastname", "ksjdlf342l"));
		HibernateUtil.insert(new User().init("info@test2.at", "Mimi", "Nachname", "sdf454"));
	}

}
