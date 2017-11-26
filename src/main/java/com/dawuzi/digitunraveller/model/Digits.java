package com.dawuzi.digitunraveller.model;

import java.util.Arrays;

/**
 * @author DAWUZI
 *
 */

public class Digits implements Comparable<Digits> {
	
	private SingleDigit[] digits;
	
	public Digits(){
	}

	public Digits(long value) {
		initDigits(value);
	}

	public Digits(String value) {
		initDigits(value);
	}
	
	public void initViaRawBinaryString(String rawBinaryString){
		if((rawBinaryString.length() % 7) != 0){
			throw new IllegalArgumentException("rawBinaryString length must be a multiple of 7");
		}
		int length = rawBinaryString.length();
		
		int digitLength = length / 7;
		
		if(digits == null || digits.length != digitLength){
			digits = new SingleDigit[digitLength];
		}
		
		for(int x=0,y=0; y<digitLength; x+=7, y++){
			
			SingleDigit digit = digits[y];
			if(digit == null){
				digit = new SingleDigit();
				digits[y] = digit;
			}
			
			digit.reInitWithRawBinaryString(rawBinaryString.substring(x, x+7));
		}
	}

	public void initDigits(String value) {
		
		if(digits == null || digits.length != value.length()){
			digits = new SingleDigit[value.length()];
		}
		
		for(int x = 0; x<value.length(); x++){
			digits[x] = new SingleDigit(value.substring(x, (x+1)));
		}
	}
	
	private void initDigits(long value) {
		initDigits(String.valueOf(value));
	}
	
	public long getValue(){
		
		String stringValue = getStringValue();
		
		if(stringValue.contains(" ")){
			stringValue = stringValue.trim();
		}
		
		if(stringValue.isEmpty()){
			return -1;
		}
		
		return Long.parseLong(stringValue);
	}
	
	public String getStringValue(){
//		String val = "";
		StringBuffer val = new StringBuffer();
		
		boolean firstNonBlankDigitFound = false;
		
		for(int x=0; x<digits.length; x++){
			
			SingleDigit digit = digits[x];
			
			if(!digit.isBlank()){
				firstNonBlankDigitFound = true;
			} else {
				if(firstNonBlankDigitFound){
					return "-1";
				} else {
//					val += " ";
					val.append(" ");
					continue;
				}
			}
			
			int value = digit.getValue();
			
			if(value < 0){
				return "-1";
			}
			
//			val += value;
			val.append(value);
		}
		
//		return val;
		return val.toString();
	}
	
	public String getRawBinaryStringValue(){
		
		StringBuffer val = new StringBuffer();
		
		for(int x=0; x<digits.length; x++){
			
			SingleDigit digit = digits[x];
		
			val.append(digit.getRawBinaryStringValue());
		}
		
		return val.toString();
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
	
	public SingleDigit getSingleDigit(int digitIndex){
		validateValue(digitIndex, getDigitCount() - 1);
		return digits[digitIndex];
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
		
		long val = getValue() - o.getValue();
		
		int compareTo;
		
		if(val == 0){
			compareTo = 0;
		} else if(val > 0){
			compareTo = 1;
		} else {
			compareTo = -1;
		}
		
		return compareTo;
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

	@Override
	public String toString() {
		return getStringValue();
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
