package com.lt.multiAlg;

import java.util.ArrayList;
import java.util.List;

import cspAlgorithms.Common;
import cspAlgorithms.Dijkstra;
import randomTopology.Constant;

public class MDijkstra extends Dijkstra{
	public List<Integer> YenFindPath(int[] Node, double[][] Edge, double[][] Id, int[][] IdLink, int start,
			int end, int v1, int v2) {
		// 明确要使用三个容器，一个用来装候选路径B，一个装最终定下来的路径A
		List<List<Integer>> A = new ArrayList<>();
		List<List<Integer>> B = new ArrayList<>();
		List<Integer> shortestPath = DijkstraOfPath(Node, Edge, start, end);// 调用dijkstra算法求得最短路径
		double[][] subEdge = Common.deepCloneEdge(Edge);// 保存边代价矩阵
		while (true) {
			Edge = Common.deepCloneEdge(subEdge);// 每次执行前都会保存矩阵，防止在运行中发生变化，重要。
			// 计算求出的shortCost对应的代价(新网络中的)
			double shortCost = 0;
			for (int i = shortestPath.size() - 1; i >= 1; i--) {
				int node1 = shortestPath.get(i);
				int node2 = shortestPath.get(i - 1);
				shortCost += Edge[node1][node2];
			}
			// 判断最短路径的代价是不是超过给定的一个值，如果超出实际上该路径已经是不存在的了。
			// 还记得我们之前用一个很大的值代表两点之间没有路径。
			if (shortCost > Constant.notExistsPathForValue)// 依据具体情况调整
				return null;

			double MAX_NUMBER = Constant.ExistsPathForValue;// 用于筛选最短路径，依据具体情况调整
			A.add(shortestPath);// 第一个无争议地定下来的路径
			if (A.size() >= Constant.notExistsPathForYenKValue)
				return null;// 视为不存在路径，因为该方法最多求给定的一万条路径
			// 核心判断 (修正比较的是f1和f2, 之前比较的是c和f1)
//			double cs = Common.Ctheta(shortestPath, Id, IdLink);
//			double ps = Common.Ptheta(shortestPath, Id, IdLink);
            double ps = Common.Ptheta(shortestPath, Id, IdLink);
            double ls = MCommon.Ltheta(shortestPath, Id, IdLink);
            // 判断条件要修改
			if(MCommon.smallEqual(ps, v1)
					&& MCommon.smallEqual(ls, v2)) {
				return shortestPath;
			}
			
			// 下面开始依次删除边，即是YEN算法。请参考维基百科关于Yen算法的详细过程。
			List<Integer> rootPath = new ArrayList<>();
			for (int i = shortestPath.size() - 1; i >= 1; i--) {
				int relateI = shortestPath.size() - 1 - i;// 相对变量i从0到size-1
				rootPath.add(shortestPath.get(i));
				int spurNode = shortestPath.get(i);
				Edge = Common.deepCloneEdge(subEdge);// 使用未变的副本
				// 接下来删除除了spurNode之外的所有节点
				for (int j = 0; j < rootPath.size(); j++) {
					if (rootPath.get(j) != spurNode) {
						for (int m = 0; m < Edge.length; m++) {
							Edge[rootPath.get(j)][m] = Constant.MAX_VALUE;// 删除行
							Edge[m][rootPath.get(j)] = Constant.MAX_VALUE;// 删除列
							// 以上表示已经删除非spur节点
						}
					}
				}
				// 得到更新后的Edge
				// 下面求spurPath
				int leftNode = shortestPath.get(i);// 要删除边的根点
				List<Integer> nodes = new ArrayList<>();// 要删除边的下一个节点的集合
				for (int j = 0; j < A.size(); j++) {// 依次遍历A中所有定下来的链表
					if (relateI >= A.get(j).size() - 1)// 已经改动
						continue;
					if (A.get(j).size() <= rootPath.size())
						break;
					int len = A.get(j).size();
					int current = 0;
					boolean add = true;
					int h = len - 1;
					for (; h >= len - rootPath.size(); h--) {// 更改之后的
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
				// 至此已经得到所有与leftNode相连的A中点
				// 重点：开始依次删边
				for (int j = 0; j < nodes.size(); j++) {
					int rightNode = nodes.get(j);
					Edge[leftNode][rightNode] = Constant.MAX_VALUE;
				}
				List<Integer> spurpath = DijkstraOfPath(Node, Edge, spurNode, end);// 这是删除边后得到最短路径
				List<Integer> path = conj(rootPath, spurpath);
				if ((!A.contains(path)) && (!B.contains(path)))// 保证该路径与之前的不重复
					B.add(path);
			}

			if (B.size() == 0) {
				return null;// 如果没有候选的就跳出
			}

			// 从B容器，候选路径中找出代价最小的那个路径，并从B容器中删除，作为下一个shortestPath进行循环
			Edge = Common.deepCloneEdge(subEdge);
			int u = -1;// 记下留下的最小的路径
			for (int i = 0; i < B.size(); i++) {
				double cost = 0;// 用于比较最小的cost
				for (int j = B.get(i).size() - 1; j >= 1; j--) {
					int foreNode = B.get(i).get(j);
					int nextNode = B.get(i).get(j - 1);
					cost += Edge[foreNode][nextNode];
				} // 求出拿出来的第一个路径的代价
				if (cost < MAX_NUMBER) {
					MAX_NUMBER = cost;
					shortestPath = Common.deepCloneList(B.get(i));
					u = i;
				}
			}
			B.remove(u);// 将该路径从容器中删除之
		}
	}
}
