package at.lmk.db;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import at.lmk.db.entities.User;

public class HibernateUtil {

	private static final Configuration configuration = new Configuration().configure(new File("hibernate.cfg.xml"));
	static {
		configuration.addAnnotatedClass(User.class);
	}

	private static final SessionFactory sessionFactory = configuration.buildSessionFactory();

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}

	public static <T> long insert(T entity) {
		return performAction(s -> (long) s.save(entity));
	}

	public static <T> void insertAll(Collection<T> entities) {
		performAction(s -> {
			entities.forEach(e -> s.save(e));
			return null;
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> list(Class<T> type) {
		return performAction(s -> s.createQuery("From " + type.getName()).list());
	}

	public static <T> void update(long entityId, Class<T> type, Consumer<T> consumer) {
		performAction(s -> {
			T t = s.get(type, entityId);
			consumer.accept(t);
			s.update(t);
			return null;
		});
	}

	public static <T> void delete(long entityId, Class<T> type) {
		performAction(s -> {
			T t = s.get(type, entityId);
			s.delete(t);
			return null;
		});
	}

	public static <T> void delete(T entity) {
		performAction(s -> {
			s.delete(entity);
			return null;
		});
	}

	private static <R> R performAction(Function<Session, R> function) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		R result = null;
		try {
			transaction = session.beginTransaction();
			result = function.apply(session);
			transaction.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
	}
}
