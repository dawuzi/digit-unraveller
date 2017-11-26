package com.dawuzi.digitunraveller.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.dawuzi.digitunraveller.model.Digits;
import com.dawuzi.digitunraveller.model.SingleDigit;
import com.dawuzi.digitunraveller.pojos.DigitExtraInfoResult;

/**
 * @author DAWUZI
 *
 */

public class DigitCoreHandler {
	
	public Map<String, List<Digits>> permutationsMemory = new ConcurrentHashMap<>();
	
	public Map<String, List<Digits>> permutationsMemoryParallel = new ConcurrentHashMap<>();
	public Map<String, List<Digits>> permutationsMemoryParallelIncludingInvalids = new ConcurrentHashMap<>();
	
	public Long getHighestViaDigitRemoval(long value, int noOfMoves){
		Digits digits = getHighestDigitsViaDeletion(new Digits(value), noOfMoves);
		return digits.getValue();
	}
//	
//	public Set<Long> getHighestViaDigitRemoval(int value, int noOfMoves, int resultCount) {
//		
//		int noOfPossibleExtraDigits = getNoOfPossibleExtraDigits(value, noOfMoves);
//		
//		int noOfOnesThatCanBeFormed = 2 * noOfPossibleExtraDigits;
//		
//		Digits startDigits = new Digits(value);
//		
//		List<Digits> digits = getHighestDigitsViaDeletion(startDigits, noOfMoves);
//		
//		TreeSet<Long> result = new TreeSet<>(Collections.reverseOrder());
//		
//		int size = digits.size();
//		
//		for(int x=0; x<size; x++){
//			result.add(digits.get(x).getValue());
//		}
//		
//		return result;
//	}

	private Digits getHighestDigitsViaDeletion(Digits digits, int noOfMoves) {

		String rawBinaryStringValue = digits.getRawBinaryStringValue();
		
		if(isDigitAllOnes(digits)){
			return digits;
		}
		
		DigitExtraInfoResult digitExtraInfoResult = getMaxResultViaDeletionForwardDirection(digits, noOfMoves);
		
		System.out.println("here digitExtraInfoResult : "+digitExtraInfoResult);
		
		int currentNoOfMoves = digitExtraInfoResult.getCurrentNoOfMoves();
		int noOfBarsRemoved = digitExtraInfoResult.getNoOfBarsRemoved();
		Digits newDigits = digitExtraInfoResult.getDigits();
		
		if(currentNoOfMoves == 0 && noOfBarsRemoved%2 == 0){
			return getMaxDigits(newDigits, noOfBarsRemoved);
		}
		
		if(currentNoOfMoves == 0){
			if(noOfBarsRemoved%2 == 0){
				return getMaxDigits(newDigits, noOfBarsRemoved);
			} else {
				DigitExtraInfoResult additionIncreasedDigit = increaseDigitByAdditionOrRearranging(newDigits, 1);
				
				Digits increasedDigits = additionIncreasedDigit.getDigits();

				return getMaxDigits(increasedDigits, noOfBarsRemoved, !additionIncreasedDigit.isAltered());
			}
		} else if(currentNoOfMoves > 0 && currentNoOfMoves <= 2) {
			
			boolean startExtraDigitWithSeven = noOfBarsRemoved%2 != 0;
			
			if(isDigitAllOnes(newDigits)){
				return getMaxDigits(newDigits, noOfBarsRemoved, startExtraDigitWithSeven );
			}
			
			List<Digits> allPermutations = getAllPermutations(newDigits, currentNoOfMoves, 1);
			
			Digits highest;
			
			if(allPermutations.isEmpty()){
				highest = newDigits;
			} else {
				Digits highestPermutableDigit = allPermutations.get(0);
				
				if(highestPermutableDigit.compareTo(newDigits) > 0){
					highest = highestPermutableDigit;
				} else {
					highest = newDigits;
				}
			}
			
			if(noOfBarsRemoved%2 == 0){
				return getMaxDigits(highest, noOfBarsRemoved);
			} else {
				DigitExtraInfoResult additionIncreasedDigit = increaseDigitByAdditionOrRearranging(newDigits, 1);
				
				Digits increasedDigits = additionIncreasedDigit.getDigits();

				return getMaxDigits(increasedDigits, noOfBarsRemoved, !additionIncreasedDigit.isAltered());
			}
		} else {
	
			boolean startExtraDigitWithSeven = noOfBarsRemoved%2 != 0;
			
			if(isDigitAllOnes(newDigits)){
				return getMaxDigits(newDigits, noOfBarsRemoved, startExtraDigitWithSeven );
			}
			
			
			
		}
		
		return null;
	}

	public boolean isDigitAllOnes(Digits digits) {
		
		boolean isAllOnes = true;
		int digitCount = digits.getDigitCount();
		
		for(int x = 0; x < digitCount; x++){
			SingleDigit singleDigit = digits.getSingleDigit(x);
			
			if(singleDigit.getCharValue() != '1'){
				isAllOnes = false;
				break;
			}
		}
		
		return isAllOnes;
	}
	
	private Digits getMaxDigits(Digits digits, int noOfBarsRemoved) {
		return getMaxDigits(digits, noOfBarsRemoved, false);
	}
	
	private Digits getMaxDigits(Digits digits, int noOfBarsRemoved, boolean startWithSeven) {
		StringBuffer buffer = new StringBuffer();
		int maxLoopCount = noOfBarsRemoved / 2; //it requires two digit to form 1
		
		for(int x=0; x<maxLoopCount; x++){
			if(x == 0 && startWithSeven){
				buffer.append('7');
			} else {
				buffer.append('1');
			}
		}
		
		String maxValue = digits.getValue() + "" + buffer.toString();
		
		Digits maxDigits = new Digits(maxValue);
		
		return maxDigits;
	}

	public DigitExtraInfoResult increaseDigitByAdditionOrRearranging(Digits digits, int noOfAdditionsOrRearrangements) {
		
		DigitRemovalNavigatorUtil digitRemovalNavigatorUtil = new DigitRemovalNavigatorUtil();
		
		int digitCount = digits.getDigitCount();
		
		String rawBinaryStringValue = digits.getRawBinaryStringValue();
		
		boolean altered = false;
		
		digits = new Digits();
		
		digits.initViaRawBinaryString(rawBinaryStringValue);
		
		for(int x=0; x<digitCount; x++){
			
			if(noOfAdditionsOrRearrangements < 0){
				throw new IllegalStateException("noOfAdditionsOrRearrangements should never be negative : "+noOfAdditionsOrRearrangements);
			}
			
			if(noOfAdditionsOrRearrangements == 0){
				break;
			}
			
			SingleDigit singleDigit = digits.getSingleDigit(x);
			
			int value = singleDigit.getValue();
			
			int[][] possibleDigitsViaAddition = digitRemovalNavigatorUtil.getPossibleDigitsViaAddition(value);
			int[][] possibleDigitsViaRearranging = digitRemovalNavigatorUtil.getPossibleDigitsViaRearranging(value);
			
			int maxValueViaAddition = -1;
			int noOfStepsViaAddition = -1;
			
			int maxValueViaRearranging = -1;
			int noOfStepsViaRearranging = -1;
			
			for (int i = 0; i < possibleDigitsViaRearranging.length; i++) {
				int[] js = possibleDigitsViaRearranging[i];
				
				int newDigitFormed = js[0];
				int noOfSteps = js[1];
				
				if(newDigitFormed > value && noOfSteps <= noOfAdditionsOrRearrangements){
					maxValueViaRearranging = newDigitFormed;
					noOfStepsViaRearranging = noOfSteps;
					break;
				}
			}
			
			for (int i = 0; i < possibleDigitsViaAddition.length; i++) {
				int[] js = possibleDigitsViaAddition[i];
				
				int newDigitFormed = js[0];
				int noOfSteps = js[1];
				
				if(newDigitFormed > value && noOfSteps <= noOfAdditionsOrRearrangements){
					maxValueViaAddition = newDigitFormed;
					noOfStepsViaAddition = noOfSteps;
					break;
				}
			}
			
			if(maxValueViaAddition < 0 && maxValueViaRearranging < 0){
				continue;
			}
			
			int maxValue = Math.max(maxValueViaAddition, maxValueViaRearranging);
			
			if(maxValue <= value){
				continue;
			}
			
			int noOfStepsTaken;
			
			if(maxValueViaAddition == maxValueViaRearranging){
				noOfStepsTaken = Math.min(noOfStepsViaRearranging, noOfStepsViaAddition);
			} else if(maxValueViaAddition > maxValueViaRearranging) {
				noOfStepsTaken = noOfStepsViaAddition;
			} else {
				noOfStepsTaken = noOfStepsViaRearranging;
			}
			
			noOfAdditionsOrRearrangements -= noOfStepsTaken;

//			System.out.println("singleDigit : "+singleDigit+", maxValueViaRearranging : "+maxValueViaRearranging
//					+", maxValueViaAddition : "+maxValueViaAddition+", maxValue : "+maxValue
//					+", noOfStepsTaken : "+noOfStepsTaken+", noOfStepsViaAddition : "+noOfStepsViaAddition
//					+", noOfStepsViaRearranging : "+noOfStepsViaRearranging
//					+", noOfAdditionsOrRearrangements : "+noOfAdditionsOrRearrangements);
			
			singleDigit.reInit(maxValue);
			altered = true;
			
			
		}
		
		DigitExtraInfoResult digitExtraInfoResult = new DigitExtraInfoResult();
		
		digitExtraInfoResult.setAltered(altered);
		digitExtraInfoResult.setDigits(digits);
		digitExtraInfoResult.setNoOfStepsLeft(noOfAdditionsOrRearrangements);
		
		return digitExtraInfoResult;
	}

	private DigitExtraInfoResult getMaxResultViaDeletionForwardDirection(Digits digits, int noOfMoves) {

//		System.out.println("digits value : "+digits.getValue()+", noOfAdditionsOrRearrangements : "+noOfMoves);
		
		int digitCount = digits.getDigitCount();
		int currentNoOfMoves = noOfMoves;
		int noOfBarsRemoved = 0;
		
		String rawBinaryStringValue = digits.getRawBinaryStringValue();
		
		digits = new Digits();
		
		digits.initViaRawBinaryString(rawBinaryStringValue);
		
		DigitRemovalNavigatorUtil digitRemovalNavigatorUtil = new DigitRemovalNavigatorUtil();
		
		for(int x=0; x<digitCount; x++){
			
			if(currentNoOfMoves < 0){
				throw new IllegalStateException("current no of moves should never be negative : "+currentNoOfMoves);
			}

			if(currentNoOfMoves == 0){
				break;
			}
			
			SingleDigit singleDigit = digits.getSingleDigit(x);
			
			int value = singleDigit.getValue();
			
			if(digitRemovalNavigatorUtil.canBeMovedToFormHigherNumber(value)){
				
				int[][] possibleDigitsViaRemoval = digitRemovalNavigatorUtil.getPossibleDigitsViaRemoval(value);
				
				for (int i = 0; i < possibleDigitsViaRemoval.length; i++) {
					int[] js = possibleDigitsViaRemoval[i]; 
					
					int newDigitFormed = js[0];
					int noOfBarsDeletionRequired = js[1];
					
					if(noOfBarsDeletionRequired <= currentNoOfMoves && newDigitFormed > value){
						currentNoOfMoves -= noOfBarsDeletionRequired;
						noOfBarsRemoved += noOfBarsDeletionRequired;
						singleDigit.reInit(newDigitFormed);
						break;
					}
				}
			}
		}
		
//		System.out.println("2 digits value : "+digits.getValue()+", currentNoOfMoves : "+currentNoOfMoves);

		for(int x = digitCount-1; x >= 0; x--){
			
			if(currentNoOfMoves < 0){
				throw new IllegalStateException("current no of moves should never be negative : "+currentNoOfMoves);
			}
			
			if(currentNoOfMoves == 0){
				break;
			}
			
			SingleDigit singleDigit = digits.getSingleDigit(x);
			
			int value = singleDigit.getValue();

			int[][] possibleDigitsViaRemoval = digitRemovalNavigatorUtil.getPossibleDigitsViaRemoval(value);
			
			for (int i = 0; i < possibleDigitsViaRemoval.length; i++) {
				int[] js = possibleDigitsViaRemoval[i]; 
				
				int newDigitFormed = js[0];
				int noOfBarsDeletionRequired = js[1];
				
				if(noOfBarsDeletionRequired <= currentNoOfMoves){
					currentNoOfMoves -= noOfBarsDeletionRequired;
					noOfBarsRemoved += noOfBarsDeletionRequired;
					singleDigit.reInit(newDigitFormed);
					break;
				}
			}
		}
		
		DigitExtraInfoResult digitExtraInfoResult = new DigitExtraInfoResult();
			
		digitExtraInfoResult.setCurrentNoOfMoves(currentNoOfMoves);
		digitExtraInfoResult.setDigits(digits);
		digitExtraInfoResult.setNoOfBarsRemoved(noOfBarsRemoved);

		return digitExtraInfoResult;
		
	}

	public Long getHighestValueIncludingFormationOfNewDigit(long value, int noOfMoves){
		Set<Long> results = getHighestValueIncludingFormationOfNewDigit(value, noOfMoves, 1);
		if(results == null || results.isEmpty()){
			return null;
		}
		return results.iterator().next();
	}
	
	public Set<Long> getHighestValueIncludingFormationOfNewDigit(long value, int noOfMoves, int resultCount){
		
		String blanksInFrontValue = getBlankString(value, noOfMoves);
		
		List<Digits> digits = getAllPermutations(new Digits(blanksInFrontValue), noOfMoves, resultCount);
		
		TreeSet<Long> result = new TreeSet<>(Collections.reverseOrder());
		
		int size = digits.size();
		
		for(int x=0; x<size; x++){
			result.add(digits.get(x).getValue());
		}
		
		return result;
	}

	public Long getHighestValue(int value, int noOfMovements){
		Set<Long> highestValues = getHighestValues(value, noOfMovements, 1);
		if(highestValues.isEmpty()){
			return null;
		}
		return highestValues.iterator().next();
	}
	
	public Set<Long> getHighestValues(int value, int noOfMovements, int resultCount){
		
		String blanksInFrontValue = getBlankString(value, noOfMovements);
		
		return getHighestValues(new Digits(blanksInFrontValue), noOfMovements, resultCount);
	}

	public String getBlankString(int value) {
		return getBlankString(value, Integer.MAX_VALUE);
	}
	
	public String getBlankString(long value, int noOfMovements) {

		int targetBlankValue = getNoOfPossibleExtraDigits(value, noOfMovements);
		
		StringBuffer buffer = new StringBuffer();
		
		for(int x=0; x<targetBlankValue; x++){
			buffer.append(' ');
		}
		
		buffer.append(value);
		
		return buffer.toString();
	}

	private int getNoOfPossibleExtraDigits(long value, int noOfMoves) {
		Digits digits = new Digits(value);
		
		int y = 0;
		int normalizedMaxSize = 7 * digits.getDigitCount();
		
		for(int x=0; x<normalizedMaxSize; x++){
			if(digits.getDigitBar(x)){
				y++;
			}
		}
		
		int count = y / 2;
		
		int noOfPrependedBlanks = count - digits.getDigitCount();
		int maxOfBlankSpaces = noOfMoves / 2;
		
		int targetBlankValue = Math.min(noOfPrependedBlanks, maxOfBlankSpaces);
		
		return targetBlankValue;
	}

	private TreeSet<Long> getHighestValues(Digits digits, int noOfMovements, int resultCount) {
		
		TreeSet<Long> results = new TreeSet<>(Collections.reverseOrder());
		
		if(noOfMovements == 0){
			return results;
		}
		
		if(noOfMovements == 1){
			
			List<Digits> digitPermutations = getAllPermutations(digits);
			
			Set<Long> values = digitPermutations.stream().map(d -> d.getValue()).collect(Collectors.toSet());
			
			return combinedResult(results, values, resultCount);
		}
		
		List<Digits> digitPermutations = getAllPermutations(digits);
		
		if(digitPermutations.isEmpty()){
			return results;
		}
		
		if(digitPermutations.size() == 1){
			
			TreeSet<Long> values = getHighestValues(digitPermutations.get(0), 
					noOfMovements - 1, resultCount);
			
			return combinedResult(results, values, resultCount);
			
		}
		
		Digits firstDigit = digitPermutations.get(0);

		CompletableFuture<TreeSet<Long>> firstDigitFuture = CompletableFuture.supplyAsync(
				() -> {
					return getHighestValues(firstDigit, 
							noOfMovements - 1, resultCount);
				});
		
		CompletableFuture<TreeSet<Long>> allResultsFuture = firstDigitFuture;

		int size = digitPermutations.size();
		
		for(int x=1; x<size; x++){
			
			Digits currentDigit = digitPermutations.get(x);
			
			CompletableFuture<TreeSet<Long>> currentDigitFuture = CompletableFuture.supplyAsync(
					() -> {
						return getHighestValues(currentDigit, 
								noOfMovements - 1, resultCount);
					});	
			
			allResultsFuture = allResultsFuture.thenCombine(currentDigitFuture, 
					(dgs, dgs2) -> {
						dgs.addAll(dgs2);
						purgeCurrentResults(dgs, resultCount);
						return dgs;
					});
		}
		
		TreeSet<Long> values;
		
		try {
			values = allResultsFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		return combinedResult(results, values, resultCount);
	}

	private TreeSet<Long> combinedResult(TreeSet<Long> results, Collection<Long> values, int resultCount) {
		results.addAll(values);
		
		purgeCurrentResults(results, resultCount);
		
		return results;
	}
	
	
	public List<Digits> getAllPermutations(Digits digits) {
		return getAllPermutations(digits, 1);
	}
	
	public List<Digits> getAllPermutations(Digits digits, int noOfMoves) {
		return getAllPermutations(digits, noOfMoves, 5);
	}
	
	public List<Digits> getAllPermutations(Digits digits, int noOfMoves, int maxHighestCount) {
		
		int digitCount = digits.getDigitCount();
		int noOfBlanks = 0;
		
		for(int x=0; x<digitCount; x++){
			
			if(digits.getSingleDigit(x).isBlank()){
				noOfBlanks++;
			} else {
				break;
			}
		}
		
		return getAllPermutations(digits, noOfMoves, maxHighestCount, noOfBlanks);
	}
	
	public List<Digits> getAllPermutations(Digits digits, int noOfMoves, int maxHighestCount, int noOfBlanks) {
		
		StringBuffer keyBuffer = new StringBuffer(digits.getRawBinaryStringValue());
		
		keyBuffer.append('&').append(noOfMoves);
		
		String key = keyBuffer.toString();
		
		List<Digits> cachedResult = permutationsMemory.get(key);
		
		if(cachedResult != null){
			return cachedResult;
		}
		
		int normalizedLength = digits.getDigitCount() * 7;
		
		if(normalizedLength <= 0){
			Collections.emptyList();
		}
		
		Set<String> resultValueStrings = new HashSet<>();
		String initialBinaryRawValue = digits.getRawBinaryStringValue(); 
		
		for(int x=0; x<normalizedLength; x++){
			
			for(int y=x+1; y<normalizedLength; y++){
				
				digits.initViaRawBinaryString(initialBinaryRawValue);
			
				boolean digitBar = digits.getDigitBar(x);
				boolean digitBar2 = digits.getDigitBar(y);
				
				if(digitBar != digitBar2){
					
					digits.swapDigitBar(x, y);
					
					if(noOfMoves == 1){
						if(digits.getValue() >= 0){
							
//							List<Digits> allValidRotations = getAllValidRotations(digits, noOfBlanks);
							
							resultValueStrings.add(digits.getRawBinaryStringValue());
						} 
					} else {
							
						List<Digits> allPermutations = getAllPermutations(digits, noOfMoves - 1, maxHighestCount, noOfBlanks);
						
						for(Digits localDigits : allPermutations){
							if(localDigits.getValue() >= 0){
								resultValueStrings.add(localDigits.getRawBinaryStringValue());
							}
						}
					}
				}
			}
		}
		
		List<Digits> results;
		
		if(!resultValueStrings.isEmpty()){
			
			results = new ArrayList<>(resultValueStrings.size());
			
			for(String val : resultValueStrings){
				Digits localDigits = new Digits();
				
				localDigits.initViaRawBinaryString(val);
				
				results.add(localDigits);
			}
		} else {
			results = Collections.emptyList();
		}
		
		digits.initViaRawBinaryString(initialBinaryRawValue); 
		
		if(!results.isEmpty() && results.size() > maxHighestCount){
			
			Collections.sort(results, Collections.reverseOrder());
			
			List<Digits> temp = new ArrayList<>();
			
			for(int x=0; x<maxHighestCount; x++){
				temp.add(results.get(x));
			}
			
			results = temp;
		}
		
		permutationsMemory.put(key, results);
		
		return results;
	}

	private List<Digits> getAllValidRotations(Digits digits, int noOfBlanks) {
		
		String stringValue = digits.getStringValue();
		
		
		
		return null;
	}

	public List<Digits> getAllPermutationsInParallel(Digits digits, int noOfMoves, 
			int maxHighestCount){
		return getAllPermutationsInParallel(digits, noOfMoves, maxHighestCount, false);
	}	
	
	public List<Digits> getAllPermutationsInParallel(Digits digits, int noOfMoves, 
			int maxHighestCount, boolean addInvalidNumbers) {
		
		StringBuffer keyBuffer = new StringBuffer(digits.getRawBinaryStringValue());
		
		keyBuffer.append('&').append(noOfMoves);
		
		String key = keyBuffer.toString();
		
		List<Digits> cachedResult = getParallelCachedResult(key, addInvalidNumbers);
		
		if(cachedResult != null){
			return cachedResult;
		}
		
		int normalizedLength = digits.getDigitCount() * 7;
		
		if(normalizedLength <= 0){
			Collections.emptyList();
		}
		
		Set<String> resultValueStrings = new HashSet<>();
		String initialBinaryRawValue = digits.getRawBinaryStringValue(); 
		
		for(int x=0; x<normalizedLength; x++){
			
			for(int y=x+1; y<normalizedLength; y++){
				
				digits.initViaRawBinaryString(initialBinaryRawValue);
			
				boolean digitBar = digits.getDigitBar(x);
				boolean digitBar2 = digits.getDigitBar(y);
				
				if(digitBar != digitBar2){
					
					digits.swapDigitBar(x, y);
					
					if(noOfMoves == 1){
						if(digits.getValue() >= 0 || addInvalidNumbers){
							resultValueStrings.add(digits.getRawBinaryStringValue());
						} 
					} else {
							
						List<Digits> allPermutations = getAllPermutations(digits, noOfMoves - 1);
						
						for(Digits localDigits : allPermutations){
							if(localDigits.getValue() >= 0){
								resultValueStrings.add(localDigits.getRawBinaryStringValue());
							}
						}
					}
				}
			}
		}
		
		List<Digits> results;
		
		if(!resultValueStrings.isEmpty()){
			
			results = new ArrayList<>(resultValueStrings.size());
			
			for(String val : resultValueStrings){
				Digits localDigits = new Digits();
				
				localDigits.initViaRawBinaryString(val);
				
				results.add(localDigits);
			}
		} else {
			results = Collections.emptyList();
		}
		
		digits.initViaRawBinaryString(initialBinaryRawValue); 
		
		if(!results.isEmpty() && results.size() > maxHighestCount){
			
			Collections.sort(results, Collections.reverseOrder());
			
			List<Digits> temp = new ArrayList<>();
			
			for(int x=0; x<maxHighestCount; x++){
				temp.add(results.get(x));
			}
			
			results = temp;
		}
		
		putResults(key, addInvalidNumbers, results);
		
		return results;
	}

	private void putResults(String key, boolean addInvalidNumbers, List<Digits> results) {
		if(addInvalidNumbers){
			permutationsMemoryParallel.put(key, results);
		} else {
			permutationsMemoryParallelIncludingInvalids.put(key, results);
		}
	}

	private List<Digits> getParallelCachedResult(String key, boolean addInvalidNumbers) {
		if(addInvalidNumbers){
			return permutationsMemoryParallelIncludingInvalids.get(key);
		} else {
			return permutationsMemoryParallel.get(key);
		}
	}

	private void purgeCurrentResults(TreeSet<Long> results, int resultCount) {
		if(results.size() <= resultCount){
			return;
		}
		
		int x = 0;
		
		Long lastItem = null;
		
		for(Long val : results){
			x++;
			if(x == resultCount){
				lastItem = val;
				break;
			}
		}
		
		int minVal = lastItem.intValue();
		
		results.removeIf(i -> i < minVal);
	}
	
	public static void main(String[] args) {
		test();
	}
	
	public static void test5() {
		
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		Long highestValue = coreHandler.getHighestValue(5008, 2);
		
		System.out.println(highestValue);
	}

	public static void test4() {
		Digits digits = new Digits(5008);
		
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		TreeSet<Long> vals = coreHandler.getHighestValues(digits, 2, 20);
		
		System.out.println(vals);
	}
	
	public static void test3() {
		
		TreeSet<Long> intSet = new TreeSet<>(Collections.reverseOrder()); 
		
		for(int x=0; x<21; x++){
			intSet.add(Long.valueOf(x+""));
		}
		
		System.out.println(intSet);
		
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		coreHandler.purgeCurrentResults(intSet, 2);
		
		System.out.println(intSet);
	}	
	public static void test2() {
		TreeSet<Long> intSet = new TreeSet<>(Collections.reverseOrder()); 
		
		for(int x=0; x<21; x++){
			intSet.add(Long.valueOf(x+""));
			
			for(Long z : intSet){
				System.out.println(z);
			}
			
			System.out.println("x : "+x+" "+intSet);
		}
	}

	public static void test() {
		
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
//		List<Digits> allDigitPermutations = coreHandler.getAllPermutations(digits);
		
		int noOfMovements = 2;
		
		for(int x=0; x<1; x++){
			
			String blankString = coreHandler.getBlankString(3, noOfMovements);
			
			System.out.println("blankString : -"+blankString+"-");
			
			Digits localDigits = new Digits(blankString);
//			Digits localDigits = new Digits(5008);
			
			System.out.println(localDigits.getRawBinaryStringValue());
			
			List<Digits> allPermutations = coreHandler.getAllPermutations(localDigits, noOfMovements);
			
			System.out.println("x : "+x);
			
			allPermutations.sort(Collections.reverseOrder());
			
			for(Digits permutationDigits : allPermutations){
				System.out.println("permutationDigits value : "+permutationDigits.getStringValue());
			}
			
			System.out.println("============================================================================");
		}
		
//		for(Digits localDigits : allDigitPermutations){
//			System.out.println("localDigits value : "+localDigits.getStringValue());
//		}
//		
//		System.out.println("done");
	}

}
