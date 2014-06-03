package com.lzq.trafficcaptor;

import java.util.HashSet;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String test="123";
		Connection c1=new Connection(test,test,test,test,test);
		
		Connection c2=new Connection(test,test,test,test,test);
		
		
		HashSet<Connection> testSet=new HashSet<Connection>();
		
		testSet.add(c1);
		
		System.out.println(testSet.contains(c2));
				
				
	}

}
