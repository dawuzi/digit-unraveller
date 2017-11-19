package com.dawuzi.digitunraveller.model;

import java.util.Arrays;

/**
 * @author DAWUZI
 *
 */

public class SingleDigit implements Comparable<SingleDigit> {
	
	private static final boolean[][] DIGIT_DESCRIPTIONS = {
//			{0,		1,		2,		3,		4,		5,		6},
			{true,	true,	true,	false,	true,	true,	true}, 	// 0
			{false,	false,	true,	false,	false,	true,	false},	// 1
			{true,	false,	true,	true,	true,	false,	true},	// 2
			{true,	false,	true,	true,	false,	true,	true},	// 3
			{false,	true,	true,	true,	false,	true,	false},	// 4
			{true,	true,	false,	true,	false,	true,	true},	// 5
			{true,	true,	false,	true,	true,	true,	true},	// 6
			{true,	false,	true,	false,	false,	true,	false},	// 7
			{true,	true,	true,	true,	true,	true,	true},	// 8
			{true,	true,	true,	true,	false,	true,	true},	// 9
	};
	
	private boolean[] digitDescription;

	public SingleDigit(int value) {
		reInit(value);
	}
	
	public void reInit(int value){
		validateValue(value, 9);
		digitDescription = DIGIT_DESCRIPTIONS[value].clone();
	}
	
	public void swap(int index, int index2){
		validateValue(index, 6);
		validateValue(index2, 6);
		boolean temp = digitDescription[index];
		digitDescription[index] = digitDescription[index2];
		digitDescription[index2] = temp;
	}
	
	public void setDigitBar(boolean val, int index){
		doSetDigitBar(val, index);
	}
	
	private void doSetDigitBar(boolean val, int index){
		validateValue(index, 6);
		digitDescription[index] = val;
	}
	
	public void setDigitBars(boolean[] vals, int[] indexes){
		if(vals.length != indexes.length){
			throw new IllegalArgumentException("vals and indexes length must be equal");
		}
		for (int i = 0; i < indexes.length; i++) {
			doSetDigitBar(vals[i], indexes[i]);
		}
	}
	
	public boolean getDigitBar(int index){
		validateValue(index, 6);
		return digitDescription[index];
	}
	
	public int getValue(){
		
		for (int i = 0; i < DIGIT_DESCRIPTIONS.length; i++) {
			boolean[] bs = DIGIT_DESCRIPTIONS[i];
			if(Arrays.equals(bs, digitDescription)){
				return i;
			}
		}
		return -1;
	}

	private void validateValue(int value, int max) {
		if(value < 0 || value > max){
			throw new IllegalArgumentException("value must be between 0 and "+max);
		}
	}

	@Override
	public int compareTo(SingleDigit o) {
		if(o == null){
			return -1;
		}
		return getValue() - o.getValue();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(digitDescription);
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
		SingleDigit other = (SingleDigit) obj;
		if (!Arrays.equals(digitDescription, other.digitDescription))
			return false;
		return true;
	}

	public static void main(String[] args) {
		for(int x=0; x<10; x++){
			SingleDigit singleDigit = new SingleDigit(x);
			System.out.println(singleDigit.getValue());
		}
	}
}
