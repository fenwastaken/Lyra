package main;

import dao.Bot;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String irc = "irc.esper.net";
		String channel = "#fourcannon";
		String botName = "SweetLyra";
		String botAdmin = "Duskie";
		
		
		
		Bot bot = new Bot(irc, channel, botName, botAdmin);
		
	}

}
