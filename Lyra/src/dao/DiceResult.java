package dao;

public class DiceResult {
	int result;
	String toString;
	
	public DiceResult(int result, String toString) {
		super();
		this.result = result;
		this.toString = toString;
	}

	public int toInt() {
		return result;
	}

	public String toString() {
		return toString;
	}		
	
}
