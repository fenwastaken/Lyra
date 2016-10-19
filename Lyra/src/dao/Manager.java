package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

public class Manager {

	//all
	//SELECT n.id, nick, talk, connexion, login, hostname, date FROM public.Nick as n JOIN infos as i ON i."idNick" = n.id ORDER BY hostname, date DESC

	//see last nick of hostname
	//SELECT nick, date FROM public.Nick as n JOIN infos as i ON i."idNick" = n.id WHERE hostname = (SELECT hostname from infos WHERE "idNick" = (SELECT id FROM nick WHERE nick = ?)) ORDER BY date DESC LIMIT 1

	public static int addNick(String nick) throws SQLException{
		String sql = "INSERT INTO public.nick(nick, talk, connexion)VALUES (?, 0, 1) RETURNING id;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		ResultSet rs = st.executeQuery();
		int ret = -2;
		if(rs.next()){
			ret = rs.getInt("id");
		}
		return ret;
	}

	public static int addInfos(String hostname, String login, int idNick) throws SQLException{
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String sql = "INSERT INTO public.infos(hostname, login, date, \"idNick\")VALUES (?, ?, ?, ?);";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, hostname);
		st.setString(2, login);
		st.setTimestamp(3, ts);
		st.setInt(4, idNick);
		int ret = st.executeUpdate();
		return ret;
	}

	public static void incrementTalk(String nick) throws SQLException{
		String sql = "UPDATE public.nick SET talk = talk + 1 WHERE nick = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		st.executeUpdate();
	}

	public static void incrementCo(String nick) throws SQLException{
		String sql = "UPDATE public.nick SET connexion = connexion + 1 WHERE nick = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		st.executeUpdate();
	}

	public static boolean nickExists(String nick) throws SQLException{
		String sql = "SELECT nick FROM public.nick WHERE nick = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}

	public static boolean hostnameExists(String hostname) throws SQLException{
		String sql = "SELECT hostname FROM public.infos WHERE hostname = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, hostname);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}

	public static boolean loginExists(String login) throws SQLException{
		String sql = "SELECT login FROM public.infos WHERE login = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, login);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}

	public static int getIDfromNick(String nick) throws SQLException{
		int ret = -1;
		String sql = "SELECT id FROM public.nick WHERE nick = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			ret = rs.getInt("id");
		}
		return ret;
	}

	public static void updateDate(String hostname, String nick) throws SQLException{

		int id = getIDfromNick(nick);

		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String sql = "UPDATE public.infos SET date = ? WHERE hostname = ? AND \"idNick\" = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setTimestamp(1, ts);
		st.setString(2, hostname);
		st.setInt(3, id);
		st.executeUpdate();
	}

	public static Chatter getChatter(String nick) throws SQLException{
		String sql = "SELECT nick, talk, connexion FROM public.nick WHERE nick = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		ResultSet rs = st.executeQuery();
		Chatter chatter = null;
		if(rs.next()){
			String userNick = rs.getString("nick");
			int talk = rs.getInt("talk");
			int co = rs.getInt("connexion");
			chatter = new Chatter(userNick, talk, co);
		}

		return chatter;
	}

	public static Vector<Chatter> getAllChatter() throws SQLException{
		String sql = "SELECT nick, talk, connexion FROM public.nick;";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		Vector<Chatter> vecChatter = new Vector<Chatter>();
		while(rs.next()){
			String userNick = rs.getString("nick");
			int talk = rs.getInt("talk");
			int co = rs.getInt("connexion");
			Chatter chatter = new Chatter(userNick, talk, co);
			vecChatter.add(chatter);
		}

		return vecChatter;
	}


	public static Vector<Chatter> getAllChattersFromHostnames(String name) throws SQLException{
		String sql = "SELECT nick, login, hostname, talk, connexion, date "
				+ "FROM public.infos as i JOIN public.nick as n ON n.id = i.\"idNick\" "
				+ "WHERE hostname = "
				+ "(SELECT hostname FROM infos WHERE \"idNick\" = "
				+ "(SELECT id FROM Nick where nick = ?) ORDER BY date DESC LIMIT 1) AND n.nick != ?;";

		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, name);
		st.setString(2, name);

		ResultSet rs = st.executeQuery();
		Vector<Chatter> vecChatter = new Vector<Chatter>();
		while(rs.next()){
			String userNick = rs.getString("nick");
			Chatter chatter = new Chatter(userNick, 0, 0);
			vecChatter.add(chatter);
		}

		return vecChatter;
	}


	public static Vector<Chatter> getAllChattersFromLogins(String name) throws SQLException{
		String sql = "SELECT nick, login, hostname, talk, connexion, date "
				+ "FROM public.infos as i JOIN public.nick as n ON n.id = i.\"idNick\" "
				+ "WHERE login = "
				+ "(SELECT login FROM infos WHERE \"idNick\" = "
				+ "(SELECT id FROM Nick where nick = ?) ORDER BY date DESC LIMIT 1) AND n.nick != ?;";

		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, name);
		st.setString(2, name);

		ResultSet rs = st.executeQuery();
		Vector<Chatter> vecChatter = new Vector<Chatter>();
		while(rs.next()){
			String userNick = rs.getString("nick");
			Chatter chatter = new Chatter(userNick, 0, 0);
			vecChatter.add(chatter);
		}

		return vecChatter;
	}

	public static String getTime(String nick) throws SQLException{
		String sql = "SELECT n.nick, DATE_TRUNC('seconds', CURRENT_TIMESTAMP - date) AS time FROM public.infos as i JOIN public.nick as n on n.id = i.\"idNick\" WHERE n.nick = ? order by time LIMIT 1";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("time");
		}
		return ret;
	}


	public static int[] getCount(String nick) throws SQLException{
		String sql = "	SELECT COUNT(hostname) as host, COUNT(login) as log FROM public.Nick as n JOIN infos as i ON i.\"idNick\" = n.id WHERE n.nick = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick);
		ResultSet rs = st.executeQuery();
		int[] count = {-1,-1};
		if(rs.next()){
			count[0] = rs.getInt("host");
			count[1] = rs.getInt("log");

		}
		return count;
	}

}
