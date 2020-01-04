package com.lt.multiAlg;

import cspAlgorithms.Common;
import randomTopology.Constant;

public class MCommon extends Common{
	public static boolean great(double a, double b) {
		return a - b > Constant.esp;
	}
	
	public static boolean greatEqual(double a, double b) {
		return great(a, b) || equal(a, b);
	}
	
	public static boolean equal(double a, double b) {
		return b - a <= Constant.esp && a - b <= Constant.esp;
	}
	
	public static boolean small(double a, double b) {
		return !greatEqual(a, b);
	}
	
	public static boolean smallEqual(double a, double b) {
		return !great(a, b);
	}
}
