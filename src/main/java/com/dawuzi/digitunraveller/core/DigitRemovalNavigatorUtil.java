package com.dawuzi.digitunraveller.core;

/**
 * @author DAWUZI
 *
 */

public class DigitRemovalNavigatorUtil {

	public int[][][] digitNavigatorViaRemoval = {
			//new digit, no of bar removal required
			{{7,3}, {1,4}},	//0
			{},				//1
			{},				//2
			{{7,2}, {1,3}},	//3
			{{1,2}},		//4
			{},				//5
			{{5,1}},		//6
			{{1,1}},		//7
			{{9,1}, {7,4}, {6,1}, {5,2}, {4,3}, {3,2}, {2,2}, {1,5}, {0,1}},		//8
			{{5,1}, {4,2}, {3,1}, {1,4}},		//9
	};
	
	public int[][][] digitNavigatorViaRearranging = {
			//new digit, no of bar switch required
			{{9,1}, {6,1}},	//0
			{},				//1
			{{5,2}, {3,1}},	//2
			{{5,1}, {2,1}},	//3
			{},				//4
			{{3,1}, {2,2}},	//5
			{{9,1}, {0,1}},	//6
			{},				//7
			{},				//8
			{{6,1}, {0,1}},	//9
	};
	
	public int[][][] digitNavigatorViaAddition = {
			//new digit, no of bars addition required
			{{8,1}},				//0
			{{9,4}, {8,5}, {7,1}, {4,2}, {3,3}, {2,2}, {0,4}},	//1
			{{8,2}},		//2
			{{9,1}, {8,2}},	//3
			{{9,2}, {8,3}},	//4
			{{9,1}, {8,2}, {6,1}},	//5
			{{8,1}},	//6
			{{9,3}, {8,4}, {3,2}},	//7
			{},	//8
			{{8,1}},	//9
	};
	
	private void validateValue(int value, int max) {
		if(value < 0 || value > max){
			throw new IllegalArgumentException("value must be between 0 and "+max);
		}
	}
	
	public boolean canBeMovedToFormHigherNumber(int number){
		validateValue(number, 9);
		return number == 0 || number == 3 || number == 8;
	}
	
	/*
	 * Should add some form of immutabilty here later 
	 */
	public int[][] getPossibleDigitsViaRemoval(int number){
		validateValue(number, 9);
		return digitNavigatorViaRemoval[number];
	}
	public int[][] getPossibleDigitsViaRearranging(int number){
		validateValue(number, 9);
		return digitNavigatorViaRearranging[number];
	}
	public int[][] getPossibleDigitsViaAddition(int number){
		validateValue(number, 9);
		return digitNavigatorViaAddition[number];
	}
	
	public static void main(String[] args) {
		DigitRemovalNavigatorUtil navigatorUtil = new DigitRemovalNavigatorUtil();
		
		
		int length = navigatorUtil.digitNavigatorViaRemoval.length;
		
		System.out.println("length : "+length);
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		
		for(int x = 0; x<length; x++){
			int[][] list = navigatorUtil.digitNavigatorViaRemoval[x];
			
			int listLength = list.length;
			
			System.out.println("listLength : "+listLength+", x : "+x);
			System.out.println("====================================");
			
			for (int i = 0; i < list.length; i++) {
				int[] js = list[i];
				
				int jsLength = js.length;
				
				System.out.println("jsLength : "+jsLength+", i : "+i);
				
				for (int j = 0; j < js.length; j++) {
					int k = js[j];
					
					System.out.println("k : "+k+", j : "+j);
				}
			}
		}
	}
	
}
