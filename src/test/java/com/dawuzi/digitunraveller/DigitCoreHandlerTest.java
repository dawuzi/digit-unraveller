package com.dawuzi.digitunraveller;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.dawuzi.digitunraveller.core.DigitCoreHandler;
import com.dawuzi.digitunraveller.model.Digits;
import com.dawuzi.digitunraveller.model.SingleDigit;

/**
 * @author DAWUZI
 *
 */

public class DigitCoreHandlerTest {

	@Test
	public void test(){
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		Integer highestValue = coreHandler.getHighestValue(5008, 2);
		
		Assert.assertEquals(highestValue, (Integer)9909);
		
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
		
		Digits digits = new Digits(" 0");
		DigitCoreHandler digitCoreHandler = new DigitCoreHandler();
		
		List<Digits> allPermutations = digitCoreHandler.getAllPermutations(digits);
		
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
				{2, 1, 2},
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
	
}