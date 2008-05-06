public class Tuple implements Comparable
{

	public double num;
	public String str;
	
	Tuple(String strIn, double numIn){
		num = numIn;
		str = strIn;
	}
	
	public int compareTo(Object o) 
	{
		Tuple rhs = (Tuple) o;
		
		if(rhs.num < num)
			return -1;
		else if(rhs.num == num)
			return 0;
		else return 1;
	}
}
