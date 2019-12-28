package com.lt.multiAlg;

import java.util.ArrayList;
import java.util.List;

import cspAlgorithms.Common;
import cspAlgorithms.Dijkstra;
import randomTopology.Constant;

public class MDijkstra extends Dijkstra{
	public List<Integer> YenFindPath(int[] Node, double[][] Edge, double[][] Id, int[][] IdLink, int start,
			int end, int v1, int v2) {
		// ��ȷҪʹ������������һ������װ��ѡ·��B��һ��װ���ն�������·��A
		List<List<Integer>> A = new ArrayList<>();
		List<List<Integer>> B = new ArrayList<>();
		List<Integer> shortestPath = DijkstraOfPath(Node, Edge, start, end);// ����dijkstra�㷨������·��
		double[][] subEdge = Common.deepCloneEdge(Edge);// ����ߴ��۾���
		while (true) {
			Edge = Common.deepCloneEdge(subEdge);// ÿ��ִ��ǰ���ᱣ����󣬷�ֹ�������з����仯����Ҫ��
			// ���������shortCost��Ӧ�Ĵ���(�������е�)
			double shortCost = 0;
			for (int i = shortestPath.size() - 1; i >= 1; i--) {
				int node1 = shortestPath.get(i);
				int node2 = shortestPath.get(i - 1);
				shortCost += Edge[node1][node2];
			}
			// �ж����·���Ĵ����ǲ��ǳ���������һ��ֵ���������ʵ���ϸ�·���Ѿ��ǲ����ڵ��ˡ�
			// ���ǵ�����֮ǰ��һ���ܴ��ֵ��������֮��û��·����
			if (shortCost > Constant.notExistsPathForValue)// ���ݾ����������
				return null;

			double MAX_NUMBER = Constant.ExistsPathForValue;// ����ɸѡ���·�������ݾ����������
			A.add(shortestPath);// ��һ��������ض�������·��
			if (A.size() >= Constant.notExistsPathForYenKValue)
				return null;// ��Ϊ������·������Ϊ�÷�������������һ����·��
			// �����ж�
			double cs = Common.Ctheta(shortestPath, Id, IdLink);
			double ps = Common.Ptheta(shortestPath, Id, IdLink);
			if((cs < v1 + Constant.esp && v1 - Constant.esp < cs)
					&& (ps < v2 + Constant.esp && v2 - Constant.esp < ps)) {
				return shortestPath;
			}
			
			// ���濪ʼ����ɾ���ߣ�����YEN�㷨����ο�ά���ٿƹ���Yen�㷨����ϸ���̡�
			List<Integer> rootPath = new ArrayList<>();
			for (int i = shortestPath.size() - 1; i >= 1; i--) {
				int relateI = shortestPath.size() - 1 - i;// ��Ա���i��0��size-1
				rootPath.add(shortestPath.get(i));
				int spurNode = shortestPath.get(i);
				Edge = Common.deepCloneEdge(subEdge);// ʹ��δ��ĸ���
				// ������ɾ������spurNode֮������нڵ�
				for (int j = 0; j < rootPath.size(); j++) {
					if (rootPath.get(j) != spurNode) {
						for (int m = 0; m < Edge.length; m++) {
							Edge[rootPath.get(j)][m] = Constant.MAX_VALUE;// ɾ����
							Edge[m][rootPath.get(j)] = Constant.MAX_VALUE;// ɾ����
							// ���ϱ�ʾ�Ѿ�ɾ����spur�ڵ�
						}
					}
				}
				// �õ����º��Edge
				// ������spurPath
				int leftNode = shortestPath.get(i);// Ҫɾ���ߵĸ���
				List<Integer> nodes = new ArrayList<>();// Ҫɾ���ߵ���һ���ڵ�ļ���
				for (int j = 0; j < A.size(); j++) {// ���α���A�����ж�����������
					if (relateI >= A.get(j).size() - 1)// �Ѿ��Ķ�
						continue;
					if (A.get(j).size() <= rootPath.size())
						break;
					int len = A.get(j).size();
					int current = 0;
					boolean add = true;
					int h = len - 1;
					for (; h >= len - rootPath.size(); h--) {// ����֮���
						if (A.get(j).get(h) == rootPath.get(current)) {
							current++;
						} else {
							add = false;
							break;
						}
					}
					if (add)
						nodes.add(A.get(j).get(h));
				}
				// �����Ѿ��õ�������leftNode������A�е�
				// �ص㣺��ʼ����ɾ��
				for (int j = 0; j < nodes.size(); j++) {
					int rightNode = nodes.get(j);
					Edge[leftNode][rightNode] = Constant.MAX_VALUE;
				}
				List<Integer> spurpath = DijkstraOfPath(Node, Edge, spurNode, end);// ����ɾ���ߺ�õ����·��
				List<Integer> path = conj(rootPath, spurpath);
				if ((!A.contains(path)) && (!B.contains(path)))// ��֤��·����֮ǰ�Ĳ��ظ�
					B.add(path);
			}

			if (B.size() == 0) {
				return null;// ���û�к�ѡ�ľ�����
			}

			// ��B��������ѡ·�����ҳ�������С���Ǹ�·��������B������ɾ������Ϊ��һ��shortestPath����ѭ��
			Edge = Common.deepCloneEdge(subEdge);
			int u = -1;// �������µ���С��·��
			for (int i = 0; i < B.size(); i++) {
				double cost = 0;// ���ڱȽ���С��cost
				for (int j = B.get(i).size() - 1; j >= 1; j--) {
					int foreNode = B.get(i).get(j);
					int nextNode = B.get(i).get(j - 1);
					cost += Edge[foreNode][nextNode];
				} // ����ó����ĵ�һ��·���Ĵ���
				if (cost < MAX_NUMBER) {
					MAX_NUMBER = cost;
					shortestPath = Common.deepCloneList(B.get(i));
					u = i;
				}
			}
			B.remove(u);// ����·����������ɾ��֮
		}
	}
}
