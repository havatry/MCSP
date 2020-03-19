package com.lt.multiAlg;

import cspAlgorithms.Common;
import randomTopology.Constant;

import java.util.Arrays;
import java.util.List;

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
	
	// swap column i and j in Id
	public static void swap(double[][] Id, int i, int j) {
		for (double[] dt : Id) {
			double tp = dt[i];
			dt[i] = dt[j];
			dt[j] = tp;
		}		
	}
	
	public static void add(double[][] Id, int i, int j, double im, double jm) {
        for (double[] dt : Id) {
            dt[i] = im * dt[i] + jm * dt[j];
        }
	}
	
	public static double[][] copyArray(double[][] origin) {
		if (origin == null || origin.length == 0 || origin[0] == null) {
			return origin;
		}
		double[][] sub = new double[origin.length][origin[0].length];
		for (int i = 0; i < sub.length; i++) {
			sub[i] = origin[i].clone();
		}
		return sub;
	}

    public static double Ltheta(List<Integer> path, double[][] Id, int[][] IdLink) {
        //Created method stubs
        double v2 = 0;
        for (int i = path.size() - 1; i >= 1; i--) {
            int startOfPath = path.get(i);
            int nextOfPath = path.get(i - 1);
            int id = IdLink[startOfPath][nextOfPath];
            v2 += Id[id][4];
        }
        return v2;
    }
}
