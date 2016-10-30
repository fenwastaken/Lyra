package dao;

public class DiceType {

	int sides;
	int number;
	int modifier = 0;

	public DiceType(int sides, int number) {
		super();
		this.sides = sides;
		this.number = number;
	}

	public int getSides() {
		return sides;
	}
	public void setSides(int sides) {
		this.sides = sides;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	public int getModifier() {
		return modifier;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	public DiceResult roll(){

		String ret = "";
		int total = modifier;
		for(int i = 0; i < this.number ; i++){

			int random = (int) (Math.random()*this.sides)+1;
			total = total + random;
			if(i+1 != this.number){
				ret = ret + random + ", ";
			}
			else if(i+1 == this.number){

				if(modifier == 0){
					ret = "(" + ret + random + ")";
				}
				else{
					ret = "(" + ret + random + ")(+" + modifier + ")";
				}
				ret = ret + " : total = " + total;

			}

		}
		DiceResult dr = new DiceResult(total, ret);
		return dr;
	}

	public String toString(){
		if(modifier == 0){
			return this.getNumber() + "d" + this.getSides();
		}
		else{
			return this.getNumber() + "d" + this.getSides() + "+" + modifier;
		}
	}

}
