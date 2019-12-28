package com.lt.multiAlg;

import java.util.List;

import cspAlgorithms.AbstractCSPMethods;

public abstract class AbstractMCSPMethods extends AbstractCSPMethods{

	@Override
	public List<Integer> OptimalPath(int[] arg0, double[][] arg1, int[][] arg2, int arg3, int arg4, int arg5) {
		//Created method stubs
		throw new IllegalAccessError("This method is not allowed to call, because the current context is Multiple Constraints of Shortest path");
	}

	public abstract void OptimalPath(int[] Node, double[][] Id, int[][] IdLink, int start,
			int end, int v1, int v2);
	
	public double Ltheta(List<Integer> path, double[][] Id, int[][] IdLink) {
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
