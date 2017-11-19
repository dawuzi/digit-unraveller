package com.dawuzi.digitunraveller.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.dawuzi.digitunraveller.model.Digits;

/**
 * @author DAWUZI
 *
 */

public class DigitCoreHandler {
	
	public Map<String, List<Digits>> permutationsMemory = new HashMap<>();

	public Integer getHighestValue(int value, int noOfMovements){
		Set<Integer> highestValues = getHighestValues(value, noOfMovements, 1);
		if(highestValues.isEmpty()){
			return null;
		}
		return highestValues.iterator().next();
	}
	
	public Set<Integer> getHighestValues(int value, int noOfMovements, int resultCount){
		return getHighestValues(new Digits(value), noOfMovements, resultCount);
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

	private List<Digits> getAllPermutations(Digits digits) {
		
		List<Digits> cachedResult = permutationsMemory.get(digits.getStringValue());
		
		if(cachedResult != null){
			return cachedResult;
		}
		
		int normalizedLength = digits.getDigitCount() * 7;
		
		if(normalizedLength <= 0){
			Collections.emptyList();
		}
		
		Set<String> resultValueStrings = new HashSet<>();
		String initialValue = digits.getStringValue(); 
		
		for(int x=0; x<normalizedLength; x++){
			
			for(int y=x+1; y<normalizedLength; y++){
				
				digits.initDigits(initialValue); 
			
				boolean digitBar = digits.getDigitBar(x);
				boolean digitBar2 = digits.getDigitBar(y);
				
				if(digitBar != digitBar2){
					
					digits.swapDigitBar(x, y);
					
					if(digits.getValue() >= 0){
						resultValueStrings.add(digits.getStringValue());
					}
				}
			}
		}
		
		List<Digits> results;
		
		if(!resultValueStrings.isEmpty()){
			
			results = new ArrayList<>();
			
			for(String val : resultValueStrings){
				results.add(new Digits(val));
			}
		} else {
			results = Collections.emptyList();
		}
		
		digits.initDigits(initialValue); 
		
		permutationsMemory.put(digits.getStringValue(), results);
		
		return results;
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
		test5();
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
		
		Digits digits = new Digits(1);
		
		DigitCoreHandler coreHandler = new DigitCoreHandler();
		
		List<Digits> allDigitPermutations = coreHandler.getAllPermutations(digits);
		
		for(int x=0; x<20; x++){
			Digits localDigits = new Digits(x);
			
			List<Digits> allPermutations = coreHandler.getAllPermutations(localDigits);
			System.out.println("x : "+x);
			for(Digits permutationDigits : allPermutations){
				System.out.println("permutationDigits value : "+permutationDigits.getStringValue());
			}
		}
		
		for(Digits localDigits : allDigitPermutations){
			System.out.println("localDigits value : "+localDigits.getStringValue());
		}
		
		System.out.println("done");
	}

}
