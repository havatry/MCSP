package com.lt.multiAlg;

import java.util.List;

import cspAlgorithms.BiLAD;
import cspAlgorithms.Common;

public class ExtendBiLAD extends BiLAD{
	private List<List<Integer>> twoPaths;
	
	@Override
	public List<Integer> OptimalPath(int[] Node, double[][] Id, int[][] IdLink, int delayConstraint, int start,
			int end) {
		//Created method stubs
		// TODO Auto-generated method stub
		// 计算c最小路径
		double thetaBelow = 0;
		List<Integer> pathBelow = getPath(Node, Id, thetaBelow, start, end);// c-minimal
																			// path,p+
		double CthetaBelow = Ctheta(pathBelow, Id, IdLink);
		double PthetaBelow = Ptheta(pathBelow, Id, IdLink);
		// 判断，如果c最小的路径对应的延时比延时阈值小。说明此时的路径是最优的解。直接返回。
		if (PthetaBelow <= delayConstraint) {
			theta = thetaBelow;
			return pathBelow;// if d(pc)<=D then return Pc
		}

		// 计算d最小路径
		double thetaTop = (double) Math.PI / 2;
		List<Integer> pathTop = getPath(Node, Id, thetaTop, start, end);// d-minimal
																		// path
																		// p
		double CthetaTop = Ctheta(pathTop, Id, IdLink);
		double PthetaTop = Ptheta(pathTop, Id, IdLink);
		// 判断，如果d最小的路径对应延时比延时阈值还大。说明网络中不存在符合条件的路径。直接返回null。
		if (PthetaTop > delayConstraint)
			return null;// there is no solution

		// 核心操作
		while (true) {
			// 处理这类特殊情况。因为当CthetaTop和CthetaBelow相等的时候，说明连线处于水平状态。
			// 会出现水平线问题，关于水平线问题，待整理......
			if (CthetaTop == CthetaBelow) {
				theta = 0.0;
				return pathTop; // special
			}
			// if(thetaTop-thetaBelow<esp) return pathTop;
			// //该条件在实际运行中很少用到，这里discard
			// theta2Current是两点连线斜率对应的角度
			double theta2Current = Math.abs(Math.atan(Math.abs((CthetaTop - CthetaBelow) / (PthetaBelow - PthetaTop))));// 保证atan里面为正数,理论上也是正的
			// 这里表示是否进行二分的条件，该条件由原作者提供，细节暂不提供......
			if (Math.abs(theta2Current - (thetaTop + thetaBelow) / 2) >= (0.5 - gama) * (thetaTop - thetaBelow))
				theta2Current = (thetaTop + thetaBelow) / 2;
			// 返回两条路径。这两条路径分别是c最小路径和d最小路径。该技术是由AdujstDijkstraOfPath来实现，具体细节见本包中Dijkstra类。
			List<List<Integer>> paths = new MDijkstra().AdjustDijkstraOfPath(Node,
					Common.getEdge(Node, Id, IdLink, theta2Current), Id, IdLink, start, end);
			CallDijkstraTime++;// 上面的调用，实际上用到了一次dijkstra算法，这里进行自增。
			// 下面的dPc表示c最小路径对应的延时
			// dPd表示d最小路径对应的延时
			double dPc = Ptheta(paths.get(0), Id, IdLink);
			double dPd = Ptheta(paths.get(1), Id, IdLink);
			// 下面的if..else if..else由原作者提供，这里暂不描述...
			if (dPd <= delayConstraint && dPc >= delayConstraint) {
				theta = theta2Current;
				twoPaths = paths;
				return null;//不需要返回
			} else if (dPd > delayConstraint) {
				pathBelow = paths.get(1);
				CthetaBelow = Ctheta(pathBelow, Id, IdLink);
				PthetaBelow = Ptheta(pathBelow, Id, IdLink);
				thetaBelow = theta2Current;
			} else {
				pathTop = paths.get(0);
				CthetaTop = Ctheta(pathTop, Id, IdLink);
				PthetaTop = Ptheta(pathTop, Id, IdLink);
				thetaTop = theta2Current;
			}
		}
	}
	
	public List<List<Integer>> getTwoPaths() {
		return twoPaths;
	}
}
