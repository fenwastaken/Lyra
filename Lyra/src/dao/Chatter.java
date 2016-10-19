package dao;

public class Chatter {

	String nick;
	int talk;
	int co;
	
	public Chatter(String nick, int talk, int co){
		this.nick = nick;
		this.talk = talk;
		this.co = co;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public int getTalk() {
		return talk;
	}

	public void setTalk(int talk) {
		this.talk = talk;
	}

	public int getCo() {
		return co;
	}

	public void setCo(int co) {
		this.co = co;
	}
	
	
	
}
