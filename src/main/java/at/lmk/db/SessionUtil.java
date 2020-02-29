package at.lmk.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.mindrot.jbcrypt.BCrypt;

import at.lmk.db.entities.User;
import at.lmk.db.entities.UserSession;

public class SessionUtil {

	private static final long COOLDOWN = 15;

	private static Map<String, UserSession> sessions = new HashMap<>();
	private static Map<Long, User> user = new HashMap<>();

	public static boolean checkForUserLogin(HttpServletRequest request) {
		String agent = getAgent(request);
		String ip = getIp(request);
		Date d = new Date();
		if (sessions.containsKey(agent + ip)
				&& TimeUnit.MINUTES.convert(Math.abs(d.getTime() - sessions.get(agent + ip).getTimestamp().getTime()),
						TimeUnit.MILLISECONDS) < COOLDOWN)
			return true;
		List<UserSession> sessions = HibernateUtil.list(UserSession.class);
		for (UserSession s : sessions) {
			if (s.getAgent().equals(agent) && s.getIp().equals(ip)) {
				s.setTimestamp(d);
				HibernateUtil.update(s);
				if (!SessionUtil.sessions.containsKey(agent + ip))
					SessionUtil.sessions.put(agent + ip, s);
				if (!user.containsKey(s.getUserId()))
					user.put(s.getUserId(), HibernateUtil.get(User.class, s.getUserId()));
				return true;
			}
		}
		return false;
	}

	public static boolean login(HttpServletRequest request) {
		String user = request.getParameter("login");
		String pw = request.getParameter("password");
		String agent = getAgent(request);
		String ip = getIp(request);
		for (User u : HibernateUtil.list(User.class))
			if (u.getEmail().equals(user) && BCrypt.checkpw(pw, u.getPassword())) {
				UserSession session = new UserSession().init(u.getId(), agent, ip, new Date());
				sessions.put(agent + ip, session);
				SessionUtil.user.put(u.getId(), u);
				HibernateUtil.insert(session);
				return true;
			}
		return false;
	}

	public static void logout(HttpServletRequest request) {
		String agent = getAgent(request);
		String ip = getIp(request);
		UserSession session = sessions.get(agent + ip);
		sessions.remove(agent + ip);
		user.remove(session.getUserId());
		HibernateUtil.delete(session);
	}

	public static String getAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null)
			ip = request.getRemoteAddr();
		return ip;
	}

	public static User getUser(HttpServletRequest request) {
		return user.get(sessions.get(getAgent(request) + getIp(request)).getUserId());
	}

}
