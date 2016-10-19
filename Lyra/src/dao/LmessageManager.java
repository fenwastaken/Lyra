package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class LmessageManager {

	public static void addMessage(String sender, String target, String message) throws SQLException{
		String sql = "INSERT INTO message(sender, target, message) VALUES(?, ?, ?)";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, sender);
		st.setString(2, target);
		st.setString(3, message);
		st.executeUpdate();
	}
	
	public static Vector<Lmessage> getAllMessages() throws SQLException{
		String sql = "SELECT * FROM message";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		Vector<Lmessage> vecMessage = new Vector<Lmessage>();
		while(rs.next()){
			String sender = rs.getString("sender");
			String target = rs.getString("target");
			String message = rs.getString("message");
			
			Lmessage mess = new Lmessage(sender, target, message);
			vecMessage.add(mess);
		}
		return vecMessage;
	}
	
	public static void deleteMessage(Lmessage mess) throws SQLException{
		String sql = "DELETE FROM message WHERE target = ? AND sender = ? AND message = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, mess.target);
		st.setString(2, mess.sender);
		st.setString(3, mess.message);
		st.executeUpdate();
	}
	
	public static int getNumberSender(String sender) throws SQLException{
		String sql = "SELECT COUNT(message) as nb FROM message WHERE sender = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, sender);
		ResultSet rs = st.executeQuery();
		int ret = -1;
		if(rs.next()){
			ret = rs.getInt("nb");
		}
		return ret;
	}
	
	public static int getNumberTarget(String sender) throws SQLException{
		String sql = "SELECT COUNT(message) as nb FROM message WHERE target = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, sender);
		ResultSet rs = st.executeQuery();
		int ret = -1;
		if(rs.next()){
			ret = rs.getInt("nb");
		}
		return ret;
	}
	
}
