package com.lt.multiAlg;

import java.util.List;

import randomTopology.Constant;

/**
 * Main Algorithm
 * 2020年1月4日 下午1:45:28
 */
public class MBiLAD extends AbstractMCSPMethods{
	private int value ;
	private double lambda1star;
	private double lambda2star;
	private List<Integer> p_positive;
	private List<Integer> p_negative; // 最佳路径
	private double detal_below;
	private double detal_top;
	private List<Integer> p_below_negative;
	private List<Integer> p_below_postive;
	private List<Integer> p_top_negative;
	private List<Integer> p_top_positive;
	private double v2_positive;
	private double v2_negative;
	private double c_positive;
	private double c_negative;
	private double delta;
	private double lambda2;
	private double[][] origin;
	
	@Override
	public void OptimalPath(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) { // integer can swap? not
		//Created method stubs
		origin = MCommon.deepCloneEdge(Id);
		compute(Node, Id, IdLink, start, end, v1, v2);
	}
	
	private void compute(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		List<Integer> p1 = getPath(Node, Id, Math.PI / 2, start, end);
		if (MCommon.great(Ptheta(p1, Id, IdLink), v1)) {
			value = 0;
			return;
		}
		swap(Id, 3, 4);
		List<Integer> p2 = getPath(Node, Id, Math.PI / 2, start, end);
		if (MCommon.great(Ptheta(p2, Id, IdLink), v2)) {
			value = 0;
			return;
		}
		step_1(Node, Id, IdLink, start, end, v1, v2);
	}
	
	private void step_1(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		List<Integer> pc = getPath(Node, Id, 0, start, end);
		double f1_value = Ltheta(pc, Id, IdLink); // change
		double f2_value = Ptheta(pc, Id, IdLink);
		if (MCommon.smallEqual(f1_value, v1) && MCommon.smallEqual(f2_value, v2)) {
			p_negative = pc;
			value = 1;
			return;
		}
		if (MCommon.great(f1_value, v1)) {
			swap(Id, 3, 4); // reset
			step_2_3_6(Node, Id, IdLink, start, end, v1, v2, 2);
		} else {
			step_2_3_6(Node, Id, IdLink, start, end, v2, v1, 2);
		}
	}
	
	private void step_2_3_6(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2, int mode) {
		if (mode == 3) {
			// f2 f1 c
			swap(Id, 2, 4);
		}
		ExtendBiLAD extendBiLAD = new ExtendBiLAD();
		extendBiLAD.OptimalPath(Node, Id, IdLink, v1, start, end);
		List<List<Integer>> paths = extendBiLAD.getTwoPaths();
		// 0 is positive and 1 is negative
		if (paths == null || paths.size() < 2) {
			value = 0;
			return;
		}
		double f1_value = Ptheta(paths.get(0), Id, IdLink); // +
		double f2_value = Ptheta(paths.get(1), Id, IdLink); // -
		double alpha = 0;
		if (MCommon.equal(f1_value, f2_value)) {
			alpha = (v1 - f2_value) / (f1_value - f2_value);
		}
		double v2_wave = (1 - alpha) * Ltheta(paths.get(1), Id, IdLink) + alpha * Ltheta(paths.get(0), Id, IdLink);
		switch (mode) {
		case 2:
			if (MCommon.smallEqual(v2_wave, v2)) {
				lambda1star = Math.abs(Math.tan(extendBiLAD.getTheta()));
				lambda2star = 0;
				p_negative = paths.get(1);
				p_positive = paths.get(0);
				value = 2;
				return;
			} else {
				detal_below = 0;
				p_below_negative = paths.get(1);
				p_below_postive = paths.get(0);
				v2_positive = v2_wave;
				c_positive = (1 - alpha) * Ctheta(paths.get(1), Id, IdLink) + alpha * Ctheta(paths.get(0), Id, IdLink);
				step_2_3_6(Node, Id, IdLink, start, end, v1, v2, 3);
			}
		case 3:
			if (MCommon.great(v2_wave, v2)) {
				value = 0;
				return;
			} else if (MCommon.small(v2_wave, v2)) {
				// <
				detal_top = Math.PI / 2;
				p_top_negative = paths.get(1);
				p_top_positive = paths.get(0);
				v2_negative = v2_wave;
				c_negative = (1 - alpha) * Ltheta(paths.get(1), Id, IdLink) + alpha * Ltheta(paths.get(0), Id, IdLink);
				value = 1;
				lambda1star = Math.abs(Math.tan(theta));
				step_4(Node, Id, IdLink, start, end, v1, v2);
			} else {
				// call yen
				MDijkstra mDijkstra = new MDijkstra();
				List<Integer> p = mDijkstra.YenFindPath(Node, MCommon.getEdge(Node, Id, IdLink, theta), 
						Id, IdLink, start, end, v1, v2);
				CallDijkstraTime += mDijkstra.getCallDijkstraTime(); // update
				if (p == null) {
					value = 0;
					return;
				} else {
					p_negative = p;
					value = 1;
					return;
				}
			}
		case 6:
			add(Id, 3, 5, 1, -lambda2); // reset
			if (MCommon.equal(v2_wave, v2)) {
				lambda1star = Math.abs(Math.tan(theta));
				lambda2star = lambda2;
				value = 6;
				return;
			} else if (MCommon.small(v2_wave, v2)) {
				detal_top = delta;
				p_top_negative = paths.get(1);
				p_top_positive = paths.get(0);
				v2_negative = v2_wave;
				c_negative = (1 - alpha) * Ctheta(paths.get(1), origin, IdLink) + alpha * Ctheta(paths.get(0), origin, IdLink);
				lambda1star = Math.abs(Math.tan(theta));
			} else {
				detal_below = delta;
				p_below_negative = paths.get(1);
				p_below_postive = paths.get(0);
				v2_positive = v2_wave;
				c_positive = (1 - alpha) * Ctheta(paths.get(1), Id, IdLink) + alpha * Ctheta(paths.get(0), Id, IdLink);
			}
			step_4(Node, Id, IdLink, start, end, v1, v2);
		}
	}
	
	private void step_4(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		if (MCommon.smallEqual(detal_top, detal_below)) {
			lambda2star = Math.abs(Math.tan(detal_top));
			value = 4;
			return;
		}
		updateMultiplier();
		step_5(Node, Id, IdLink, start, end, v1, v2);
	}
	
	
	private void step_5(int[] Node, double[][] Id, int[][] IdLink, int start, int end, int v1, int v2) {
		swap(Id, 2, 4);
		add(Id, 2, 4, 1, lambda2);
		List<Integer> pc = getPath(Node, Id, 0, start, end);
		double f1_value = Ptheta(pc, Id, IdLink);
		double f2_value = Ltheta(pc, Id, IdLink);
		if (MCommon.smallEqual(f1_value, v1)) {
			lambda1star = 0;
			if (MCommon.equal(f2_value, v2)) {
				lambda2star = lambda2;
				value = 5;
				return;
			} else if (MCommon.small(f2_value, v2)) {
				detal_top = delta;
				p_top_negative = pc;
				step_4(Node, Id, IdLink, start, end, v1, v2);
			} else {
				detal_below = delta;
				p_below_postive = pc;
				step_4(Node, Id, IdLink, start, end, v1, v2);
			}
		} else {
			step_2_3_6(Node, Id, IdLink, start, end, v1, v2, 6);
		}
	}
	
	// swap column i and j in Id
	private void swap(double[][] Id, int i, int j) {
		for (double[] dt : Id) {
			double tp = dt[i];
			dt[i] = dt[j];
			dt[j] = tp;
		}		
	}
	
	private void add(double[][] Id, int i, int j, double im, double jm) {
		for (double[] dt : Id) {
			dt[i] = im * dt[i] +  jm * dt[j];
		}
	}
	
	private void updateMultiplier() {
		assert v2_negative != v2_positive;
		lambda2 = (c_positive - c_negative) / (v2_negative - v2_positive);
		delta = Math.atan(lambda2);
		if (MCommon.greatEqual(Math.abs(delta - (detal_below + detal_top) / 2), (0.5 - Constant.gama) * (detal_top - detal_below))) {
			delta = (detal_below + detal_top) / 2;
			lambda2 = Math.abs(Math.tan(delta));
		}
	}
}
