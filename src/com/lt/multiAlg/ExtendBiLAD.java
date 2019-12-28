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
		// ����c��С·��
		double thetaBelow = 0;
		List<Integer> pathBelow = getPath(Node, Id, thetaBelow, start, end);// c-minimal
																			// path,p+
		double CthetaBelow = Ctheta(pathBelow, Id, IdLink);
		double PthetaBelow = Ptheta(pathBelow, Id, IdLink);
		// �жϣ����c��С��·����Ӧ����ʱ����ʱ��ֵС��˵����ʱ��·�������ŵĽ⡣ֱ�ӷ��ء�
		if (PthetaBelow <= delayConstraint) {
			theta = thetaBelow;
			return pathBelow;// if d(pc)<=D then return Pc
		}

		// ����d��С·��
		double thetaTop = (double) Math.PI / 2;
		List<Integer> pathTop = getPath(Node, Id, thetaTop, start, end);// d-minimal
																		// path
																		// p
		double CthetaTop = Ctheta(pathTop, Id, IdLink);
		double PthetaTop = Ptheta(pathTop, Id, IdLink);
		// �жϣ����d��С��·����Ӧ��ʱ����ʱ��ֵ����˵�������в����ڷ���������·����ֱ�ӷ���null��
		if (PthetaTop > delayConstraint)
			return null;// there is no solution

		// ���Ĳ���
		while (true) {
			// �������������������Ϊ��CthetaTop��CthetaBelow��ȵ�ʱ��˵�����ߴ���ˮƽ״̬��
			// �����ˮƽ�����⣬����ˮƽ�����⣬������......
			if (CthetaTop == CthetaBelow) {
				theta = 0.0;
				return pathTop; // special
			}
			// if(thetaTop-thetaBelow<esp) return pathTop;
			// //��������ʵ�������к����õ�������discard
			// theta2Current����������б�ʶ�Ӧ�ĽǶ�
			double theta2Current = Math.abs(Math.atan(Math.abs((CthetaTop - CthetaBelow) / (PthetaBelow - PthetaTop))));// ��֤atan����Ϊ����,������Ҳ������
			// �����ʾ�Ƿ���ж��ֵ���������������ԭ�����ṩ��ϸ���ݲ��ṩ......
			if (Math.abs(theta2Current - (thetaTop + thetaBelow) / 2) >= (0.5 - gama) * (thetaTop - thetaBelow))
				theta2Current = (thetaTop + thetaBelow) / 2;
			// ��������·����������·���ֱ���c��С·����d��С·�����ü�������AdujstDijkstraOfPath��ʵ�֣�����ϸ�ڼ�������Dijkstra�ࡣ
			List<List<Integer>> paths = new MDijkstra().AdjustDijkstraOfPath(Node,
					Common.getEdge(Node, Id, IdLink, theta2Current), Id, IdLink, start, end);
			CallDijkstraTime++;// ����ĵ��ã�ʵ�����õ���һ��dijkstra�㷨���������������
			// �����dPc��ʾc��С·����Ӧ����ʱ
			// dPd��ʾd��С·����Ӧ����ʱ
			double dPc = Ptheta(paths.get(0), Id, IdLink);
			double dPd = Ptheta(paths.get(1), Id, IdLink);
			// �����if..else if..else��ԭ�����ṩ�������ݲ�����...
			if (dPd <= delayConstraint && dPc >= delayConstraint) {
				theta = theta2Current;
				twoPaths = paths;
				return null;//����Ҫ����
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
