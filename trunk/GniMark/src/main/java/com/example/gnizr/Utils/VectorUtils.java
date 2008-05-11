package com.example.gnizr.Utils;
import java.util.Collections;
import java.util.Vector;



public class VectorUtils 
{
	public static Vector removeDuplicates(Vector v1)
	{
		Vector result = new Vector();
		
		if(v1 != null)
		{
			for(int i=0; i < v1.size(); i++)
			{
				if(! result.contains(v1.get(i)))
					result.add(v1.get(i));
			}
		}
		
		return result;
	}
	
	public static Vector and(Vector v1, Vector v2)
	{
		Vector answer = new Vector();
		
		if((v1 != null) && (v2 != null))
		{
			if((v1.size() > 0) && (v2.size() > 0))
			{
				for(int a=0; a < v1.size(); a++)
				{
					if(v2.contains(v1.get(a)))
						answer.add(v1.get(a));
				}	
			}
		}
		
		return removeDuplicates(answer);
	}
	
	public static Vector nand(Vector v1, Vector v2)
	{
		Vector answer = new Vector();
		
		if((v1 != null) && (v2 != null))
		{
			if((v1.size() > 0) && (v2.size() > 0))
			{
				for(int a=0; a < v1.size(); a++)
				{
					if(! v2.contains(v1.get(a)))
						answer.add(v1.get(a));
				}	
				for(int a=0; a < v2.size(); a++)
				{
					if(! v1.contains(v2.get(a)))
						answer.add(v2.get(a));
				}
			}
			else
			{
				if(v1.size() > 0)
					return v1;
				else if(v2.size() > 0)
					return v2;
			}
		}
		else
		{
			if(v1 != null)
				return v1;
			else if(v2 != null)
				return v2;
		}
		
		return removeDuplicates(answer);
	}
	
	public static boolean containsIgnoreCase(Vector<String> searchVector, String word)
	{
		for(int i=0; i < searchVector.size(); i++)
		{
			if(searchVector.get(i).equalsIgnoreCase(word))
				return true;
		}
		
		return false;
	}
	
	public static void main(String[] args)
	{
		Vector<String> s1 = new Vector<String>();
		Vector<String> s2 = new Vector<String>();
		
		s1.add("Rank");
		s1.add("Name");
		s1.add("flag");
		s1.add("Abbreviation");
		s1.add("Population");
		s1.add("Percentage");
		s1.add("national");
		s1.add("pop");
		s1.add("Land");
		s1.add("area");
		s1.add("Population");
		s1.add("density");
		s1.add("Seats");
		s1.add("House");
		s1.add("Commons");
		
		s2.add("Name");
		s2.add("Population");
		s2.add("Population");
		s2.add("Abbreviation");
		
		Vector<String> res = VectorUtils.nand(s1, s2);
		
		for(int i=0; i < res.size(); i++)
			System.out.println(res.get(i)+" ");
	}
	
	public static Vector<Double> normalize(Vector<Double> freqVector)
	{
		Vector<Double> normalizedVector = null;
		double avg = 0;
		
		if(freqVector.size() > 0)
		{
			normalizedVector = new Vector<Double>();
			double highestFreq = Collections.max(freqVector);
			for(int l = 0 ; l< freqVector.size(); l++)
			{
				double newVal = (double) (((double) freqVector.get(l)) / highestFreq);
				normalizedVector.add( newVal );
				avg = avg + newVal;
				
			}
			avg = avg /((double) normalizedVector.size());
		}
		
		return normalizedVector;
	}
	
//	add the smoothing const to each int and then normalize
	public static Vector<Double> addSmoothingConst(Vector<Double> freqVector, double smoothConst)
	{
		Vector<Double> answer = null;
		if(freqVector != null)
		{
			if(freqVector.size() > 0)
			{
				answer = new Vector<Double>();
				for(int l = 0 ; l< freqVector.size(); l++)
				{
					answer.add(freqVector.get(l) + smoothConst);
				}
			}
		}
		
		return answer;
	}
	
//	add the smoothing const to each int and then normalize
	public static Vector<Double> normalize(Vector<Double> freqVector, int smoothConst)
	{
		return VectorUtils.normalize(VectorUtils.addSmoothingConst(freqVector,smoothConst));
	}
}
