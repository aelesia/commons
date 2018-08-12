package me.aelesia.commons.utils;

import java.util.Random;

public class RandomUtils {

	static Random rand = new Random(); 
	
	public static int randomInt(int num1, int num2) {
		return rand.nextInt(num2-num1+1) + num1;
	}
}
