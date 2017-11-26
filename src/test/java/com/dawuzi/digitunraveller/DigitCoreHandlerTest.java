package com.dawuzi.digitunraveller;

import org.junit.Assert;
import org.junit.Test;

import com.dawuzi.digitunraveller.core.DigitCoreHandler;
import com.dawuzi.digitunraveller.model.Digits;
import com.dawuzi.digitunraveller.model.SingleDigit;
import com.dawuzi.digitunraveller.pojos.DigitExtraInfoResult;

/**
 * @author DAWUZI
 *
 */

public class DigitCoreHandlerTest {

	@Test
	public void test(){
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		Long highestValue = coreHandler.getHighestValue(5008, 2);
		Long expected = (long) 9909;
		
		Assert.assertEquals(highestValue, expected);
		
	}
	
	@Test
	public void test2(){
		Digits digits = new Digits(" 22");
		
		int digitCount = digits.getDigitCount();
		
		System.out.println("digitCount : "+digitCount);
		
		Assert.assertEquals(3, digitCount);
		Assert.assertEquals(22, digits.getValue());
		
		String stringValue = digits.getStringValue();
		
		System.out.println("stringValue : -"+stringValue+"- : "+digits.getValue());
	}

	@Test
	public void testAllPermutations(){
		
		DigitCoreHandler digitCoreHandler = new DigitCoreHandler();
		
		int[][] testCases = {
				{0, 3},
				{1, 1},
				{2, 2},
				{3, 2},
				{4, 2},
				{5, 2},
				{6, 3},
				{7, 1},
				{8, 3},
				{9, 3},
				{88, 7},
		};
		
		for(int x=0; x<testCases.length; x++){
			int[] testCase = testCases[x];
			
			String blankString = digitCoreHandler.getBlankString(testCase[0]);
			
			System.out.println("testCase[0] : "+testCase[0]+", blankString : -"+blankString+"- , length : "+blankString.length());
			
			Assert.assertEquals(testCase[1], blankString.length());
		}
		
		String blankString = digitCoreHandler.getBlankString(8);
		
		Digits localDigits = new Digits(blankString);
		
		System.out.println("localDigits.getDigitCount() : "+localDigits.getDigitCount());
		
		for(int x=0; x<localDigits.getDigitCount(); x++){
			SingleDigit singleDigit = localDigits.getSingleDigit(x);
			
			System.out.println("x : "+x+", is blank : "+singleDigit.isBlank());
			
		}
	}
	
	@Test
	public void testAllPermutationsWithNoOfMoves(){

		DigitCoreHandler digitCoreHandler = new DigitCoreHandler();
		
		int[][] testCases = {
				{0, 3, 10},
				{1, 1, 3},
				{2, 2, 2},
//				{3, 2},
//				{4, 2},
//				{5, 2},
//				{6, 3},
//				{7, 1},
//				{8, 3},
//				{9, 3},
//				{88, 7},
		};
		
		for(int x=0; x<testCases.length; x++){
			int[] testCase = testCases[x];
			
			String blankString = digitCoreHandler.getBlankString(testCase[0], testCase[2]);
			
			System.out.println("testCase[0] : "+testCase[0]+", testCase[1] : "+testCase[1]
					+", blankString : -"+blankString+"- , length : "+blankString.length());
			
			Assert.assertEquals(testCase[1], blankString.length());
		}
	}	
	
	@Test
	public void testRawBinaryStringValues(){
		
		int val = 12;
		
		Digits digits = new Digits(val);
		
		String rawBinaryStringValue = digits.getRawBinaryStringValue();
		
		System.out.println("12 : "+rawBinaryStringValue);
		
		Digits digits2 = new Digits(1);

		System.out.println("digits : "+digits.getStringValue());

		digits2.initViaRawBinaryString(rawBinaryStringValue);
		
		System.out.println("digits : "+digits.getStringValue());
		
		Assert.assertEquals(val, digits2.getValue());
		
	}

	@Test
	public void testGetHighestValues(){
		
//		value, number of moves, expected highest value
//		you can add more test cases here
		int[][] testCases = {
				{0, 3, 77},
				{5008, 2, 15005},
				{2, 1, 3},
//				{99, 9, 100},
		};
		
		DigitCoreHandler digitCoreHandler = new DigitCoreHandler();
		
		for(int[] testCase : testCases){
			
			long value = Long.parseLong(testCase[0]+"");
			int noOfMoves = testCase[1];
			int expected = testCase[2];
			
			Long actualInteger = digitCoreHandler.getHighestValueIncludingFormationOfNewDigit(value, noOfMoves);
			long actual = 0;
			
			if(actualInteger != null){
				actual = actualInteger;
			}

			Assert.assertEquals(expected, actual);
			
			System.out.println("value : "+testCase[0]
					+", no of moves : "+testCase[1]+", Expected highest value : "+testCase[2]
					+", Actual : "+digitCoreHandler.getHighestValueIncludingFormationOfNewDigit(value, noOfMoves) 
							);
		}
	}
	
	@Test
	@SuppressWarnings("unused")
	public void testGetHighestValuesByAdditionOrRearrangement(){
		
		DigitCoreHandler digitCoreHandler = new DigitCoreHandler();
		
		int[][] testCases = {
				{62102, 10, 98998},
				{62102, 9, 98993},
				{62102, 8, 98992},
				{62102, 1, 92102},
				{62102, 0, 62102},
//				{4, 2},
//				{5, 2},
//				{6, 3},
//				{7, 1},
//				{8, 3},
//				{9, 3},
//				{88, 7},
		};
		
		for(int x=0; x<testCases.length; x++){
			int[] testCase = testCases[x];
			
			int value = testCase[0];
			int noOfMoves = testCase[1];
			int expectedValue = testCase[2];
			
			DigitExtraInfoResult digitExtraInfoResult = digitCoreHandler.increaseDigitByAdditionOrRearranging(
					new Digits(value), noOfMoves);
			
			boolean altered = digitExtraInfoResult.isAltered();
			int noOfStepsLeft = digitExtraInfoResult.getNoOfStepsLeft();
			Digits digits = digitExtraInfoResult.getDigits();
			
			System.out.println("value : "+value+", noOfMoves : "+noOfMoves+", digitExtraInfoResult : "+digitExtraInfoResult);
		}
	}	
	
	@Test
	public void testIsDigitAllOnes(){
		
		DigitCoreHandler digitCoreHandler = new DigitCoreHandler();
		
		int[] onesDigits = {111, 11, 1};
		int[] notOnesDigits = {1211, 511, 12};
		
		for (int i = 0; i < onesDigits.length; i++) {
			int j = onesDigits[i];
			
			System.out.println("j : "+j+", is all ones : "+digitCoreHandler.isDigitAllOnes(new Digits(j)));
		}
		
		for (int i = 0; i < notOnesDigits.length; i++) {
			int j = notOnesDigits[i];
			
			System.out.println("j : "+j+", is all ones : "+digitCoreHandler.isDigitAllOnes(new Digits(j)));
		}
	}	
	
	@Test
	public void testGetHighestValuesViaDeletion(){
		
		DigitCoreHandler digitCoreHandler = new DigitCoreHandler();
		
		long[][] testCases = {
				{838, 4, 97911},
				{46, 3, 751},
				{5008, 2, 50051},
				{2, 2, 5},
				{8, 4, 711},
				{8, 5, 711},
				{8, 6, 711},
				{8, 7, 711},
				{8, 8, 711},
				{8, 9, 711},
				{46, 4, 751},
				{46, 5, 751},
				{46, 6, 7711},
//				{5008, 2, 751},
		};
		
		for(int x=0; x<testCases.length; x++){
			
			long[] testCase = testCases[x];
			
			long value = testCase[0];
			int noOfMoves = (int) testCase[1];
			long expected = testCase[2];
			
			Long actualInteger = digitCoreHandler.getHighestViaDigitRemoval(value, noOfMoves);
			long actual = 0;
			
			if(actualInteger != null){
				actual = actualInteger;
			}

			Assert.assertEquals(expected, actual);
			
//			System.out.println("x : "+x+", value : "+value+", noOfMoves : "+noOfMoves+", actualInteger : "+actualInteger);
		}
	}	
	
}
