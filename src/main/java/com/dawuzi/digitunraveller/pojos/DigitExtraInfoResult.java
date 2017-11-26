package com.dawuzi.digitunraveller.pojos;

import com.dawuzi.digitunraveller.model.Digits;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author DAWUZI
 *
 */

@Getter
@Setter
@ToString
public class DigitExtraInfoResult {

	private Digits digits;
	private int currentNoOfMoves;
	private int noOfBarsRemoved;
	
	private int noOfStepsLeft;
	private boolean altered;
	
}
