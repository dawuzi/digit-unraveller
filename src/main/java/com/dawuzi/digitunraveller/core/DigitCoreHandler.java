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

/**
 * @author DAWUZI
 *
 */

public class DigitCoreHandler {
	
	public Map<String, List<Digits>> permutationsMemory = new ConcurrentHashMap<>();
	
	public Map<String, List<Digits>> permutationsMemoryParallel = new ConcurrentHashMap<>();
	public Map<String, List<Digits>> permutationsMemoryParallelIncludingInvalids = new ConcurrentHashMap<>();
	
	
	public Integer getHighestValueIncludingFormationOfNewDigit(int value, int noOfMoves){
		Set<Integer> results = getHighestValueIncludingFormationOfNewDigit(value, noOfMoves, 1);
		if(results == null || results.isEmpty()){
			return null;
		}
		return results.iterator().next();
	}
	
	public Set<Integer> getHighestValueIncludingFormationOfNewDigit(int value, int noOfMoves, int resultCount){
		
		String blanksInFrontValue = getBlankString(value, noOfMoves);
		
		List<Digits> digits = getAllPermutations(new Digits(blanksInFrontValue), noOfMoves, resultCount);
		
		TreeSet<Integer> result = new TreeSet<>(Collections.reverseOrder());
		
		int size = digits.size();
		
		for(int x=0; x<size; x++){
			result.add(digits.get(x).getValue());
		}
		
		return result;
	}

	public Integer getHighestValue(int value, int noOfMovements){
		Set<Integer> highestValues = getHighestValues(value, noOfMovements, 1);
		if(highestValues.isEmpty()){
			return null;
		}
		return highestValues.iterator().next();
	}
	
	public Set<Integer> getHighestValues(int value, int noOfMovements, int resultCount){
		
		String blanksInFrontValue = getBlankString(value, noOfMovements);
		
		return getHighestValues(new Digits(blanksInFrontValue), noOfMovements, resultCount);
	}

	public String getBlankString(int value) {
		return getBlankString(value, Integer.MAX_VALUE);
	}
	
	public String getBlankString(int value, int noOfMovements) {
		
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
		int maxOfBlankSpaces = noOfMovements / 2;
		
		int targetBlankValue = Math.min(noOfPrependedBlanks, maxOfBlankSpaces);
		
		StringBuffer buffer = new StringBuffer();
		
		for(int x=0; x<targetBlankValue; x++){
			buffer.append(' ');
		}
		
		buffer.append(value);
		
		return buffer.toString();
	}

	private TreeSet<Integer> getHighestValues(Digits digits, int noOfMovements, int resultCount) {
		
		TreeSet<Integer> results = new TreeSet<>(Collections.reverseOrder());
		
		if(noOfMovements == 0){
			return results;
		}
		
		if(noOfMovements == 1){
			
			List<Digits> digitPermutations = getAllPermutations(digits);
			
			Set<Integer> values = digitPermutations.stream().map(d -> d.getValue()).collect(Collectors.toSet());
			
			return combinedResult(results, values, resultCount);
		}
		
		List<Digits> digitPermutations = getAllPermutations(digits);
		
		if(digitPermutations.isEmpty()){
			return results;
		}
		
		if(digitPermutations.size() == 1){
			
			TreeSet<Integer> values = getHighestValues(digitPermutations.get(0), 
					noOfMovements - 1, resultCount);
			
			return combinedResult(results, values, resultCount);
			
		}
		
		Digits firstDigit = digitPermutations.get(0);

		CompletableFuture<TreeSet<Integer>> firstDigitFuture = CompletableFuture.supplyAsync(
				() -> {
					return getHighestValues(firstDigit, 
							noOfMovements - 1, resultCount);
				});
		
		CompletableFuture<TreeSet<Integer>> allResultsFuture = firstDigitFuture;

		int size = digitPermutations.size();
		
		for(int x=1; x<size; x++){
			
			Digits currentDigit = digitPermutations.get(x);
			
			CompletableFuture<TreeSet<Integer>> currentDigitFuture = CompletableFuture.supplyAsync(
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
		
		TreeSet<Integer> values;
		
		try {
			values = allResultsFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		return combinedResult(results, values, resultCount);
	}

	private TreeSet<Integer> combinedResult(TreeSet<Integer> results, Collection<Integer> values, int resultCount) {
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
							
							List<Digits> allValidRotations = getAllValidRotations(digits, noOfBlanks);
							
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

	private void purgeCurrentResults(TreeSet<Integer> results, int resultCount) {
		if(results.size() <= resultCount){
			return;
		}
		
		int x = 0;
		
		Integer lastItem = null;
		
		for(Integer val : results){
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
		
		Integer highestValue = coreHandler.getHighestValue(5008, 2);
		
		System.out.println(highestValue);
	}

	public static void test4() {
		Digits digits = new Digits(5008);
		
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		TreeSet<Integer> vals = coreHandler.getHighestValues(digits, 2, 20);
		
		System.out.println(vals);
	}
	
	public static void test3() {
		
		TreeSet<Integer> intSet = new TreeSet<>(Collections.reverseOrder()); 
		
		for(int x=0; x<21; x++){
			intSet.add(x);
		}
		
		System.out.println(intSet);
		
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		coreHandler.purgeCurrentResults(intSet, 2);
		
		System.out.println(intSet);
	}	
	public static void test2() {
		TreeSet<Integer> intSet = new TreeSet<>(Collections.reverseOrder()); 
		
		for(int x=0; x<21; x++){
			intSet.add(x);
			
			for(Integer z : intSet){
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
