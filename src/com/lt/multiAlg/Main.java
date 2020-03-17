package com.lt.multiAlg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lt.topology.MTopology;

import fileInput.IdFile;
import randomTopology.Constant;

/**
 * Test MBiLAD class
 * 2020年1月6日 下午11:34:39
 */
public class Main {
	private AbstractMCSPMethods abstractMCSPMethods;
	private Integer start;
	private Integer end;
	private Integer delayConstraint;
	private Integer lossConstraint;
	private int callTime;
	private static Logger log = LoggerFactory.getLogger(Main.class);
	private final static boolean SPEC = false;

    /**
     * 对算例进行测试， 一般需要指定Write_TimeFor的变量来测试特定文件
     * @return 测试结果
     */
	public double[] compute() {
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
		// 设置
        abstractMCSPMethods = new MBiLAD();
		start = start == null ? 2 : start;
		end = end == null ? 8 : end;
		double minDelay = abstractMCSPMethods.GetMinDelay(Node, Id, IdLink, start, end);
		double minLoss = abstractMCSPMethods.getMinLoss(Node, Id, IdLink, start, end);
		delayConstraint = delayConstraint == null ? delayConstraint = (int)(minDelay + Math.random() * 10 + 1) : delayConstraint;
		lossConstraint = lossConstraint== null ? (int)(minLoss + Math.random() * 10 + 1) : lossConstraint;
		log.info("初始变量,测试序号 = {}, 起点 = {}, 终点 = {}, 延时约束 = {}, 丢包约束 = {}, 节点个数 = {}",
				new Object[] {Constant.WriteFile_TimeFor, start, end, delayConstraint, lossConstraint, 20});
		abstractMCSPMethods.OptimalPath(Node, Id, IdLink, start, end, delayConstraint, lossConstraint);
		double[] result = new double[3];
		try {
			if (abstractMCSPMethods instanceof MBiLAD) {
				MBiLAD mBiLAD = (MBiLAD)abstractMCSPMethods;
				Integer v = mBiLAD.getValue();
				if (v != null && v == 0) {
//					throw new Exception("MBiLAD can not find a path");
					log.info("MBiLAD can not find a path");
				} else {
					// 存在解
					List<Integer> optimal_path = mBiLAD.getP_negative();
					result[0] = mBiLAD.Ctheta(optimal_path, Id, IdLink);
					result[1] = mBiLAD.Ptheta(optimal_path, Id, IdLink);
					result[2] = mBiLAD.Ltheta(optimal_path, Id, IdLink);
					callTime = abstractMCSPMethods.getCallDijkstraTime();
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * 构造算例
     * @param nodeNum 算例的节点数
     * @return 对算例的计算结果
     */
	public double[] compute(Integer nodeNum) {
		Constant.step = (Constant.numNodes = nodeNum);
		new MTopology().ProduceTopology();
		return compute();
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
	
	public void setDelayConstraint(Integer delayConstraint) {
		this.delayConstraint = delayConstraint;
	}
	
	public void setLossConstraint(Integer lossConstraint) {
		this.lossConstraint = lossConstraint;
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		if (SPEC) {
            Constant.WriteFile_TimeFor = 7;
            main.compute();
            System.out.println(main.callTime);
        } else {
            for (int i = 0; i < 100; i++) {
                // 20个节点的
                double[] result = main.compute(20);
                if (result != null) {
                    log.info("运算结果: 代价 = {}, 延时 = {}, 丢包 = {}, 调用次数 = {}", new Object[]{result[0], result[1], result[2], main.callTime});
                }
                log.info("");
                main.start = null;
                main.end = null;
                main.delayConstraint = null;
                main.lossConstraint = null;
                Constant.WriteFile_TimeFor++;
            }
        }
	}
}
