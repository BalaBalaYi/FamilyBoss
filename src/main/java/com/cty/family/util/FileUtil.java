package com.cty.family.util;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileUtil {
	
	/**
	 * 按固定长宽进行图片缩放
	 * @param src 源文件
	 * @param dest 目标文件
	 * @param w 目标宽度
	 * @param h 目标高度
	 * @throws Exception
	 */
	public static void zoomImage(String src,String dest,int w,int h) throws Exception {
		double wr=0,hr=0;
		File srcFile = new File(src);
		File destFile = new File(dest);
		
		BufferedImage bufImg = ImageIO.read(srcFile); // 读取图片
		Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH); // 设置缩放目标图片模板
		
		wr = w*1.0/bufImg.getWidth(); // 获取缩放比例
		hr = h*1.0 / bufImg.getHeight();
		
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		Itemp = ato.filter(bufImg, null);
		try {
			ImageIO.write((BufferedImage) Itemp, dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 按固定文件大小进行图片缩放
	 * @param src 源文件
	 * @param dest 目标文件
	 * @param size 文件大小(kb)
	 * @throws Exception
	 */
	public static void zoomImage(String src,String dest,Integer size) throws Exception {
		File srcFile = new File(src);
		File destFile = new File(dest);
		
		long fileSize = srcFile.length();
		if(fileSize < size * 1024) // 文件大于size k时，才进行缩放
			return;
		
		Double rate = (size * 1024 * 0.5) / fileSize; // 获取长宽缩放比例
		
		BufferedImage bufImg = ImageIO.read(srcFile);
		Image Itemp = bufImg.getScaledInstance(bufImg.getWidth(), bufImg.getHeight(), bufImg.SCALE_SMOOTH);

		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
		Itemp = ato.filter(bufImg, null);
		try {
			ImageIO.write((BufferedImage) Itemp, dest.substring(dest.lastIndexOf(".")+1), destFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	public static void main(String[] args) throws Exception {
		
		//FileUtil.zoomImage("D:\\temp\\img\\1.jpg", "D:\\temp\\img\\1-small.jpg", 16);
		FileUtil.zoomImage("D:\\temp\\img\\1.jpg", "D:\\temp\\img\\1-small.jpg", 200, 200);
	}
	
}
