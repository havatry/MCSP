package com.lt.trail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lt.multiAlg.AbstractMCSPMethods;
import com.lt.multiAlg.MBiLAD;
import com.lt.multiAlg.MDijkstra;
import cspAlgorithms.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lt.topology.MTopology;

import fileInput.IdFile;
import randomTopology.Constant;

/**
 * Test MBiLAD class
 * 项目依赖的CSP.jar文件中关于djkstra两个方法前驱矩阵初始化为起点
 * <br>当且仅当保证测试的拓扑结构是连通图时候，结果才是符合预期的
 * 2020年1月6日 下午11:34:39
 */
public class Main {
	private AbstractMCSPMethods abstractMCSPMethods;
	private Integer start;
	private Integer end;
	private Integer delayConstraint;
	private Integer lossConstraint;
	private int callTime;
	private DesignExcel designExcel = new DesignExcel();
	private static Logger log = LoggerFactory.getLogger(Main.class);
	private final static boolean SPEC = true;

    /**
     * 对算例进行测试， 一般需要指定Write_TimeFor的变量来测试特定文件
     * @return 测试结果
     */
	public double[] compute() {
		// 指定文件
		double[][] Id = IdFile.GetId(true);
		double[][] origin = new double[Id.length][];
		for (int i = 0; i < Id.length; i++) {
		    origin[i] = Id[i].clone();
        }
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
		// 最小度和最大度
        int[] minAndMaxDegree = Common.GetMinAndMaxDegree(Node.length, Id);
        // 总边数
        int edgeNum = Id.length / 2;
        // 平均度
        double averageDegree = AbstractMCSPMethods.getAverageDegree(Node, Id) * 2;
		delayConstraint = delayConstraint == null ? delayConstraint = (int)(minDelay + Math.random() * 10 + 1) : delayConstraint;
		lossConstraint = lossConstraint== null ? (int)(minLoss + Math.random() * 10 + 1) : lossConstraint;
		log.info("初始变量,测试序号 = {}, 起点 = {}, 终点 = {}, 延时约束 = {}, 丢包约束 = {}, 节点个数 = {}",
				new Object[] {Constant.WriteFile_TimeFor, start, end, delayConstraint, lossConstraint, 20});
		long startTime = System.currentTimeMillis();
		abstractMCSPMethods.OptimalPath(Node, Id, IdLink, start, end, delayConstraint, lossConstraint);
		long executeTime = System.currentTimeMillis() - startTime;
		double[] result = new double[3];
		startTime = System.currentTimeMillis();
		// 执行YEN
        MDijkstra mDijkstra = new MDijkstra();
        List<Integer> pathYen = mDijkstra.YenFindPath(Node, Common.getEdge(Node, origin, IdLink, 0.0D),
                origin, IdLink, start, end, delayConstraint, lossConstraint);
        int execteTime2 = (int) (System.currentTimeMillis() - startTime);
        int yc = -1, yd = -1, yl = -1;
        if (pathYen != null) {
            yc = (int) Common.Ctheta(pathYen, origin, IdLink);
            yd = (int) Common.Ptheta(pathYen, origin, IdLink);
            yl = (int) abstractMCSPMethods.Ltheta(pathYen, origin, IdLink);
        }
        int callTime2 = mDijkstra.getCallDijkstraTime();
		try {
			if (abstractMCSPMethods instanceof MBiLAD) {
				MBiLAD mBiLAD = (MBiLAD)abstractMCSPMethods;
				Integer v = mBiLAD.getValue();
				if (v != null && v == 0) {
//					throw new Exception("MBiLAD can not find a path");
					log.info("MBiLAD can not find a path");
                    if (!SPEC) {
                        designExcel.writeData(Constant.WriteFile_TimeFor + 1, new Object[]
                                {Node.length, edgeNum, averageDegree,
                                        minAndMaxDegree[0], minAndMaxDegree[1], (int)minDelay, (int)minLoss, delayConstraint,
                                        lossConstraint, -1, -1, -1, ((MBiLAD) abstractMCSPMethods).getValue(),
                                        callTime, (int)executeTime, yc, yd, yl, callTime2, execteTime2});
                    }
				} else {
					// 存在解
					List<Integer> optimal_path = mBiLAD.getP_negative();
					result[0] = mBiLAD.Ctheta(optimal_path, origin, IdLink);
					result[1] = mBiLAD.Ptheta(optimal_path, origin, IdLink);
					result[2] = mBiLAD.Ltheta(optimal_path, origin, IdLink);
					callTime = abstractMCSPMethods.getCallDijkstraTime();
					// 写入
                    if (!SPEC) {
                        designExcel.writeData(Constant.WriteFile_TimeFor + 1, new Object[]
                                {Node.length, edgeNum, averageDegree,
                                        minAndMaxDegree[0], minAndMaxDegree[1], (int)minDelay, (int)minLoss, delayConstraint,
                                        lossConstraint, (int)result[0], (int)result[1], (int)result[2], ((MBiLAD) abstractMCSPMethods).getValue(),
                                        callTime, (int)executeTime, yc, yd, yl, callTime2, execteTime2});
                    }
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
	
	public static void main(String[] args) throws IOException {
		Main main = new Main();
		if (SPEC) {
            String dirName = "20200320021019"; // 指定
            int index = Constant.idFile.lastIndexOf("/");
            Constant.idFile = Constant.idFile.substring(0, index) + "/" + dirName + Constant.idFile.substring(index);
            Constant.TimeForTest = 34; // 指定
            main.delayConstraint = 21; // 指定
            main.lossConstraint = 24; // 指定
            main.compute();
            System.out.println(main.callTime);
        } else {
            String dirName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            int index = Constant.idFile.lastIndexOf("/");
            Constant.idFile = Constant.idFile.substring(0, index) + "/" + dirName + Constant.idFile.substring(index);
            Files.createDirectory(Paths.get(Constant.idFile).getParent());
            for (int i = 0; i < 100; i++) {
                // 20个节点的
                try {
                    double[] result = main.compute(20);
                    if (result != null) {
                        log.info("运算结果: 代价 = {}, 延时 = {}, 丢包 = {}, 调用次数 = {}", new Object[]{result[0], result[1], result[2], main.callTime});
                    }
                } catch (Exception e) {
                    System.err.println(e);
                    // 文件转储
                    if (!Files.exists(Paths.get("resource/save/" + dirName))) {
                        Files.createDirectory(Paths.get("resource/save/" + dirName));
                    }
                    Files.copy(Paths.get("resource/file/" + dirName + "/id_" + Constant.TimeForTest + ".txt"),
                            Paths.get("resource/save/" + dirName + "id_" + Constant.TimeForTest + ",txt"));
                    // 保存上下文信息
                    PrintWriter out = new PrintWriter("resource/save/" + dirName + "/some_info_" + Constant.TimeForTest + ".txt");
                    out.println("上下文信息");
                    out.println("延时约束 = " + main.delayConstraint + ", 丢包约束 = " + main.lossConstraint);
                    out.println("异常信息");
                    out.println(e);
                    out.close();
                }
                log.info("");
                main.start = null;
                main.end = null;
                main.delayConstraint = null;
                main.lossConstraint = null;
                Constant.WriteFile_TimeFor++;
                Constant.TimeForTest++;
            }
        }
        main.designExcel.close(); // 对于仅测试不写入excel的也需要进行关闭
	}
}
