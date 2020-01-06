package com.lt.multiAlg;

import java.util.Arrays;
import java.util.List;

import com.lt.topology.MTopology;

import fileInput.IdFile;
import randomTopology.Constant;

/**
 * Test MBiLAD class
 * 2020年1月6日 下午11:34:39
 */
public class Main {
	private AbstractMCSPMethods abstractMCSPMethods;
	private int start;
	private int end;
	private int callTime;
	
	public Main() {
		//Created constructor stubs
		abstractMCSPMethods = new MBiLAD();
		start = 2;
		end = 8;
	}
	
	public double[] compute(String filename) {
		// 指定文件
		double[][] Id = IdFile.GetId(true);
		double maxIndex = -1;
		for (double[] d : Id) {
			if (Math.max(d[0], d[1]) > maxIndex) {
				maxIndex = Math.max(d[0], d[1]);
			}
		}
		int[] Node = new int[(int) Math.round(maxIndex + 1)];
		for (int s = 0; s < Node.length; s++) {
			Node[s] = s;
		}
		int[][] IdLink = IdFile.GetIdLink(Id);
		double minDelay = abstractMCSPMethods.GetMinDelay(Node, Id, IdLink, start, end);
		double minLoss = abstractMCSPMethods.getMinLoss(Node, Id, IdLink, start, end);
		int delayConstraint = (int)(minDelay + Math.random() * 5 + 1);
		int lossConstraint = (int)(minLoss + Math.random() * 5 + 1);
		abstractMCSPMethods.OptimalPath(Node, Id, IdLink, start, end, delayConstraint, lossConstraint);
		double[] result = new double[3];
		try {
			if (abstractMCSPMethods instanceof MBiLAD) {
				MBiLAD mBiLAD = (MBiLAD)abstractMCSPMethods;
				Integer v = mBiLAD.getValue();
				if (v != null && v == 0) {
					throw new Exception("MBiLAD can not find a path");
				} else {
					// 存在解
					List<Integer> optimal_path = mBiLAD.getP_negative();
					result[0] = mBiLAD.Ctheta(optimal_path, Id, IdLink);
					result[1] = mBiLAD.Ptheta(optimal_path, Id, IdLink);
					result[2] = mBiLAD.Ltheta(optimal_path, Id, IdLink);
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public double[] compute(Integer nodeNum) {
		Constant.step = (Constant.numNodes = nodeNum);
		new MTopology().ProduceTopology();
		String filename = Constant.idFile.replace(".", "_" + Constant.WriteFile_TimeFor + ".");
		return compute(filename);
	}
	
	public int getCallTime() {
		return callTime;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		String filename = Constant.idFile.replace(".", "_" + 1 + ".");
		System.out.println(Arrays.toString(main.compute(filename)));
	}
}
