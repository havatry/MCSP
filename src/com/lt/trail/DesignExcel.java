package com.lt.trail;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 设计输出的Excel格式
 */
public class DesignExcel {
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;

    public DesignExcel() {
        // 初始化一个Excel表格
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("MSCP");
        // 冻结前两列
        sheet.createFreezePane(0, 2);
        init();
    }

    // 初始化表格样式
    private void init() {
        // 第一行
        sheet.createRow(0).createCell(9).setCellValue("MBiLAD");
        CellRangeAddress address = new CellRangeAddress(0, 0, 9, 14);
        sheet.addMergedRegion(address); // 合并MBiLAD
        CellRangeAddress address2 = new CellRangeAddress(0, 0, 14, 18);
        sheet.addMergedRegion(address2); // 合并YEN
        // 第二行
        HSSFRow row = sheet.createRow(1);
        row.createCell(0).setCellValue("节点数");
        row.createCell(1).setCellValue("总边数");
        row.createCell(2).setCellValue("平均度");
        row.createCell(3).setCellValue("最小度");
        row.createCell(4).setCellValue("最大度");
        row.createCell(5).setCellValue("最小延时");
        row.createCell(6).setCellValue("最小丢包");
        row.createCell(7).setCellValue("延时约束");
        row.createCell(8).setCellValue("丢包约束");
        row.createCell(9).setCellValue("c");
        row.createCell(10).setCellValue("d");
        row.createCell(11).setCellValue("l");
        row.createCell(12).setCellValue("退出标记");
        row.createCell(13).setCellValue("调用次数");
        row.createCell(14).setCellValue("运行时间/ms");
        row.createCell(15).setCellValue("c");
        row.createCell(16).setCellValue("d");
        row.createCell(17).setCellValue("l");
        row.createCell(18).setCellValue("调用次数");
        row.createCell(19).setCellValue("运行时间/ms");
    }

    // 向excel中写入数据
    protected void writeData(int r, Object[] data) {
        HSSFRow row = sheet.createRow(r);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        for (int i = 0; i < data.length; i++) {
            if (i == 2) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellValue((Double)data[i]);
            } else {
                row.createCell(i).setCellValue((Integer) data[i]);
            }
        }
    }

    protected void close() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        try {
            workbook.write(new FileOutputStream("resource/excel/result_" + sdf.format(new Date()) + ".xls"));
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
