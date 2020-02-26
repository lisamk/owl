package at.lmk.db;

import java.io.File;

import org.hibernate.cfg.Configuration;

public class HibernateConfiguration {
	static final Configuration configuration = new Configuration().configure(new File("hibernate.cfg.xml"));
}
