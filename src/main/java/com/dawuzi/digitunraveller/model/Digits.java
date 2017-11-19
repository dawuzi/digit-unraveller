package com.dawuzi.digitunraveller.model;

import java.util.Arrays;

/**
 * @author DAWUZI
 *
 */

public class Digits implements Comparable<Digits> {
	
	private SingleDigit[] digits;

	public Digits(int value) {
		initDigits(value);
	}

	public Digits(String value) {
		initDigits(value);
	}

	public void initDigits(String value) {
		
		if(digits == null || digits.length != value.length()){
			digits = new SingleDigit[value.length()];
		}
		
		for(int x = 0; x<value.length(); x++){
			digits[x] = new SingleDigit(Integer.parseInt(value.substring(x, (x+1))));
		}
	}
	
	private void initDigits(int value) {
		initDigits(String.valueOf(value));
	}
	
	public int getValue(){
		return Integer.parseInt(getStringValue());
	}
	
	public String getStringValue(){
		String val = "";
		
		for(int x=0; x<digits.length; x++){
			
			int value = digits[x].getValue();
			
			if(value < 0){
				return "-1";
			}
			
			val += value;
		}
		
		return val;
	}
	
	public int getDigitCount(){
		return digits.length;
	}
	
	public boolean getDigitBar(int normalizedIndex){
		int barIndex = normalizedIndex % 7;
		int digitIndex = normalizedIndex / 7;
		return getDigitBar(digitIndex, barIndex);
	}
	
	public boolean getDigitBar(int digitIndex, int barIndex){
		if(digitIndex < 0 || digitIndex >= digits.length){
			throw new IllegalArgumentException("Invalid digit index. It must be between 0 and "+digits.length+" (exclusive)");
		}
		if(barIndex < 0 || barIndex >= 7){
			throw new IllegalArgumentException("Invalid bar index. It must be between 0 and 7 (exclusive)");
		}
		return digits[digitIndex].getDigitBar(barIndex);
	}
	
	public void swapDigitBar(int normalizedIndex, int normalizedIndex2){
		
		validateValue(normalizedIndex, ((digits.length * 7) - 1 ));
		validateValue(normalizedIndex2, ((digits.length * 7) - 1 ));
		
		if(normalizedIndex == normalizedIndex2){
			return; // there is no work to be done
		}
		
		int barIndex = normalizedIndex % 7;
		int digitIndex = normalizedIndex / 7;

		int barIndex2 = normalizedIndex2 % 7;
		int digitIndex2 = normalizedIndex2 / 7;
		
//		same digit so we just swap
		if(digitIndex == digitIndex2){
			digits[digitIndex].swap(barIndex, barIndex2);
		} else {
			
			 SingleDigit singleDigit = digits[digitIndex];
			 SingleDigit singleDigit2 = digits[digitIndex2];
			
			boolean digitBarValue = singleDigit.getDigitBar(barIndex);
			boolean digitBarValue2 = singleDigit2.getDigitBar(barIndex2);
			
			singleDigit.setDigitBar(digitBarValue2, barIndex);
			singleDigit2.setDigitBar(digitBarValue, barIndex2);
		}
	}
	
	private void validateValue(int value, int max) {
		if(value < 0 || value > max){
			throw new IllegalArgumentException("value must be between 0 and "+max);
		}
	}
	
	@Override
	public int compareTo(Digits o) {
		if(o == null){
			return -1;
		}
		return getValue() - o.getValue();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(digits);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Digits other = (Digits) obj;
		if (!Arrays.equals(digits, other.digits))
			return false;
		return true;
	}

	public static void main(String[] args) {
		test3();
	}
	
	public static void test3() {
		Digits digits = new Digits(2);
		System.out.println(digits.getValue());
		digits.swapDigitBar(4, 5);
		
		if(digits.getValue() == 3){
			System.out.println("yaay ");
		} else {
			System.out.println("damn");
		}
		System.out.println(digits.getValue());
		
		digits.swapDigitBar(1, 2);
		if(digits.getValue() == 5){
			System.out.println("yaay ");
		} else {
			System.out.println("damn");
		}
		
		System.out.println(digits.getValue());
		
		System.out.println("===================================================");
		
		digits.initDigits(89);
		System.out.println(digits.getValue());

		digits.swapDigitBar(3, 11);
		System.out.println(digits.getValue());
		
		System.out.println("===================================================");
		
		digits.initDigits(90);
		System.out.println(digits.getValue());

		digits.swapDigitBar(2, 10);
		System.out.println(digits.getValue());
		
		digits.swapDigitBar(2, 3);
		System.out.println(digits.getValue());
		
	}	
	
	public static void test2() {
		for(int x=0; x<36; x++){
			int y = x/7;
			System.out.println("x : "+x+", y : "+y);
		}
	}	
	
	public static void test() {
		Digits digits = new Digits(1022);
		System.out.println(digits.getValue());
	}
}
