package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.Vector;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class Bot extends PircBot{

	String botNick;
	String channel;
	String irc;
	String admin;
	
	Vector<Lmessage> vecMessage = new Vector<Lmessage>();

	public Bot(String irc, String channel, String nick, String admin){

		loadMessages();
		
		this.irc = irc;
		this.channel = channel;
		this.botNick = nick;
		this.admin = admin;

		this.setName(nick);
		this.setAutoNickChange(true);
		this.setLogin(nick);
		this.setVersion("yay");
		this.setFinger("hum");

		try {
			this.connect(irc);
			this.joinChannel(channel);
		} catch (IOException | IrcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Override
	protected void onConnect() {
		// TODO Auto-generated method stub
		super.onConnect();
		this.botNick = this.getNick();
	}


	@Override
	protected void onJoin(String channel, String sender, String login,
			String hostname) {
		// TODO Auto-generated method stub
		super.onJoin(channel, sender, login, hostname);
		
		try {
			if(!Manager.nickExists(sender)){
				Manager.addNick(sender);
				addNewInfos(sender, hostname, login);

			}
			else{
				Manager.incrementCo(sender);
				Manager.updateDate(hostname, sender);
				if(!Manager.hostnameExists(hostname) || !Manager.loginExists(login)){
					addNewInfos(sender, hostname, login);
					Manager.incrementCo(sender);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onNickChange(String oldNick, String login, String hostname,
			String newNick) {
		// TODO Auto-generated method stub
		super.onNickChange(oldNick, login, hostname, newNick);
		
		String sender = newNick;
		
		try {
			if(!Manager.nickExists(sender)){
				Manager.addNick(sender);
				addNewInfos(sender, hostname, login);

			}
			else{
				Manager.incrementCo(sender);
				Manager.updateDate(hostname, sender);
				if(!Manager.hostnameExists(hostname) || !Manager.loginExists(login)){
					addNewInfos(sender, hostname, login);
					Manager.incrementCo(sender);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		// TODO Auto-generated method stub
		super.onMessage(channel, sender, login, hostname, message);
		
		try {
			if(!Manager.nickExists(sender)){
				Manager.addNick(sender);
				Manager.incrementTalk(sender);
				addNewInfos(sender, hostname, login);
			}
			else{
				Manager.incrementTalk(sender);
				Manager.updateDate(hostname, sender);
				if(!Manager.hostnameExists(hostname) || !Manager.loginExists(login)){
					addNewInfos(sender, hostname, login);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//commands
		getInfo(sender, message);
		whoTalkedMost(sender, message);
		getNicksFromHostnames(message);
		getNicksFromLogin(message);
		getTime(message);
		youtube(channel, message, sender);
		count(message);
		help(sender, message);
		
		if(sender.equals(admin) && message.toLowerCase().startsWith(botNick.toLowerCase() + ", do you know us")){
			spyThem(channel);
		}
		
		deliverMessages(sender);
		setMessage(sender, message);
		deleteMyMessages(sender, message);
		
	}


	protected void onAction(String sender, String login, String hostname, String target, String action) {

		try {
			if(!Manager.nickExists(sender)){
				Manager.addNick(sender);
				Manager.incrementTalk(sender);
				addNewInfos(sender, hostname, login);
			}
			else{
				Manager.incrementTalk(sender);
				Manager.updateDate(hostname, sender);
				if(!Manager.hostnameExists(hostname) || !Manager.loginExists(login)){
					addNewInfos(sender, hostname, login);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		deliverMessages(sender);
		
	};


	@Override
	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {
		// TODO Auto-generated method stub
		super.onPrivateMessage(sender, login, hostname, message);
	}



	public void addNewNick(String sender, String hostname, String login){
		int idNick;
		try {
			idNick = Manager.addNick(sender);
			Manager.addInfos(hostname, login, idNick);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addNewInfos(String sender, String hostname, String login){
		int id;
		try {
			id = Manager.getIDfromNick(sender);
			Manager.addInfos(hostname, login, id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void spyThem(String channel){
		
		User[] users = getUsers(channel);
		String list = "";
		
		for(User usr : users){
			String sender = usr.getNick();
			
			try {
				if(!Manager.nickExists(sender)){
					list += sender + ", ";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		list = list.substring(0, (list.length() - 2));
		
		sendMessage(channel, "I never heard of " + list + " before.");
		

	}
	
	public static Vector<String> cuter(String message, String separator){
		Vector<String> vec = new Vector<String>();
		int start = message.indexOf(separator) + 1;
		int stop = message.indexOf(separator, start);
		String ret = "";

		while(stop <= message.length() && stop != -1){
			ret = message.substring(start, stop);
			vec.add(ret);
			start = stop + 1;
			stop = message.indexOf(separator, start);
		}

		ret = message.substring(start);
		vec.add(ret);

		return vec;
	}

	
	public void getInfo(String sender, String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", check info about ")){
			String name = cuter(message, " ").elementAt(3);
			try {
				Chatter chatter = Manager.getChatter(name);
				if(chatter != null){
					sendMessage(channel, name + " connected " + chatter.getCo() + " times and talked " + chatter.getTalk() + " times while I was here.");
				}
				else{
					sendMessage(channel, "Never heard of any " + name + " before, " + sender + ".");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void whoTalkedMost(String sender, String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", who talked the most")){
			try {
				Vector<Chatter> vec = Manager.getAllChatter();
				int max = 0;
				String nick = "";
				for(Chatter chat : vec){
					if(max < chat.getTalk()){
						max = chat.getTalk();
						nick = chat.getNick();
					}
					else if(max == chat.getTalk()){
						nick += ", " + chat.getNick();
					}
				}
				
				if(nick.indexOf(",") > 0){
					nick = nick.substring(0, nick.length() - 2);
				}
				
				sendMessage(channel, nick + " talked most with " + max + " messages.");
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getNicksFromHostnames(String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", check hostnames for ")){
			String name = cuter(message, " ").elementAt(3);
			name = name.replace("?", "");
			name = name.replace(" ", "");
			System.out.println("nick cut = " + name);
			
			try {
				Vector<Chatter> vec = Manager.getAllChattersFromHostnames(name);
				String list = "";
				if(vec != null && vec.size() > 0){
					for(Chatter ct : vec){
						list += ct.getNick() + ", ";
					}
					list = list.substring(0, (list.length() - 2));
					sendMessage(channel, name + "'s hostname was used with those other nicks: " + list + ".");
				}
				else{
					sendMessage(channel, name + " has no other nick associated to his/her hostname.");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getNicksFromLogin(String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", check logins for ")){
			String name = cuter(message, " ").elementAt(3);
			name = name.replace("?", "");
			name = name.replace(" ", "");
			
			try {
				Vector<Chatter> vec = Manager.getAllChattersFromHostnames(name);
				String list = "";
				if(vec != null && vec.size() > 0){
					for(Chatter ct : vec){
						list += ct.getNick() + ", ";
					}
					list = list.substring(0, (list.length() - 2));
					sendMessage(channel, name + "'s login was used with those other nicks: " + list + ".");
				}
				else{
					sendMessage(channel, name + " has no other login associated to his/her hostname.");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getTime(String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", check time for ")){
			String name = cuter(message, " ").elementAt(3);
			name = name.replace("?", "");
			name = name.replace(" ", "");
			System.out.println("name cut = " + name);
			try {
				if(Manager.nickExists(name)){
					String time = Manager.getTime(name);
					sendMessage(channel, name + " was last seen " + time + " ago.");
				}
				else{
					sendMessage(channel, "I never heard of " + name + ".");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void youtube(String channel, String message, String sender){
		if(message.startsWith(botNick + ", youtube ")){
			Vector<String> param = cuter(message, " ");

			String search = "";

			for(String str : param){
				if(str != param.firstElement()){
					search += str + "+";
				}
			}
			search = search.substring(0, search.length() - 1);

			try {
				URL url = new URL("https://www.youtube.com/results?search_query=" + search);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(url.openStream()));
				String inputLine;
				String all = "";
				while ((inputLine = in.readLine()) != null){
					all += inputLine;
				}
				in.close();

				System.out.println(all);
				int start = all.indexOf("/watch?v=");
				int end = all.indexOf("\"", start);
				String ret = all.substring(start, end);
				sendMessage(channel, "Here's the result, " + sender + " : " + "https://www.youtube.com" + ret);

			} catch (IOException | StringIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();

			}
		}
	}
	
	public void count(String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", count hostnames for ")){
			String name = cuter(message, " ").elementAt(3);
			name = name.replace("?", "");
			name = name.replace(" ", "");
			try {
				if(Manager.nickExists(name)){
					int[] count = Manager.getCount(name);
					sendMessage(channel, name + " has had " + count[0] + " different hostnames and " + count[1] + " different logins.");
				}
				else{
					sendMessage(channel, "I never heard of " + name);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setMessage(String sender, String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", tell ")){
			String name = cuter(message, " ").elementAt(1);
			String mess = message.substring(message.indexOf(name) + name.length() + 1);
			
			try {
				int nb = LmessageManager.getNumberSender(sender);
				if(nb < 3){
					int nb2 = LmessageManager.getNumberTarget(name);
					if(nb2 < 3){
						try {
							LmessageManager.addMessage(sender, name, mess);
							loadMessages();
							sendMessage(channel, "Got it, I'll tell " + name);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						sendMessage(channel, name + " already has 3 messages pending");
					}
		
				}
				else{
					sendMessage(channel, "You already sent 3 messages currently pending");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void loadMessages(){
		try {
			vecMessage = LmessageManager.getAllMessages();
			System.out.println("messages loaded: " + vecMessage.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deliverMessages(String target){
		System.out.println("MAIL!");
		Vector<Lmessage> vecDel = new Vector<Lmessage>();
		boolean delivered = false;
		
		for(Lmessage ms : vecMessage){
			if(ms.target.toLowerCase().equals(target.toLowerCase())){
				System.out.println("message for " + target);
				sendMessage(channel, ms.sender + " left a message for " + ms.target + ": \"" + ms.message + "\"");
				vecDel.add(ms);
				delivered = true;
			}
		}
		
		if(delivered){
			for(Lmessage ms : vecDel){
				try {
					LmessageManager.deleteMessage(ms);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			loadMessages();
		}
	}
	
	public void deleteMyMessages(String sender, String message){
		if(message.toLowerCase().startsWith(botNick.toLowerCase() + ", delete my messages")){
			int i = 0;
			for(Lmessage mess : vecMessage ){
				if(mess.sender.toLowerCase().equals(sender.toLowerCase())){
					try {
						LmessageManager.deleteMessage(mess);
						i++;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			sendMessage(channel, i + " messages were deleted, " + sender);
			loadMessages();
		}
	}
	
	public void help(String sender, String message){
		if(message.toLowerCase().indexOf(botNick.toLowerCase()) > -1 && message.toLowerCase().indexOf("help") > -1){
			sendMessage(sender, "Here's my commands: ");
			sendMessage(sender, botNick + ", <command>");
			sendMessage(sender, "check info about <name> [sends info about name]");
			sendMessage(sender, "who talked the most");
			sendMessage(sender, "check hostnames for <name> [returns other nicks used by this nick's hostname]");
			sendMessage(sender, "check logins for <name> [returns other nicks used by this nick's login]");
			sendMessage(sender, "check time for <name> [returns since when that user last spoke]");
			sendMessage(sender, "count hostnames for <name> [displays the number or hostnames and logins for this nick]");
			sendMessage(sender, "tell <name> <message> [register a message for <name> that will be displayed next time they are active]");
			sendMessage(sender, "delete my messages [deletes the messages you sent with this nick]");
		}
	}
	
}
