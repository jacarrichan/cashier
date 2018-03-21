/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import com.swetake.util.Qrcode;

/**
 * Created by wanqian
 */
@SuppressWarnings("restriction")
public final class PaymentTools {

	private PaymentTools() {
	}

	public static void getQRcode(String str, HttpServletResponse response) {
		if (StringUtils.isBlank(str)) {
			return;
		}
		byte[] Strbyts = str.getBytes();
		Qrcode rcode = new Qrcode();
		rcode.setQrcodeVersion(3);
		BufferedImage bufImg = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB); // 250
																						// *
																						// 250
																						// 大小
		Graphics2D gs = bufImg.createGraphics();
		gs.setBackground(Color.WHITE);
		gs.clearRect(0, 0, 300, 300);
		gs.setColor(Color.BLACK);

		// 输出内容> 二维码
		if (Strbyts.length > 0 && Strbyts.length < 120) {
			boolean[][] codeOut = rcode.calQrcode(Strbyts);
			for (int i = 0; i < codeOut.length; i++) {
				for (int j = 0; j < codeOut.length; j++) {
					if (codeOut[j][i]) {
						gs.fillRect(j * 10 + 5, i * 10 + 5, 10, 10);
					}
				}
			}
		}
		else {
			System.err.println("QRCode content bytes length = " + Strbyts.length + " not in [ 0,120 ]. ");
		}
		gs.dispose();
		bufImg.flush();
		try {
			// ImageIO.write(bufImg, "png", response.getOutputStream());//图片格式
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(response.getOutputStream());
			encoder.encode(bufImg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getQRcode2(String str, HttpServletResponse response) {
		if (StringUtils.isBlank(str)) {
			return;
		}
		OutputStream out = null;
		byte[] Strbyts = str.getBytes();
		try {
			out = response.getOutputStream();
			ByteArrayInputStream is = new ByteArrayInputStream(Strbyts);
			BufferedImage bi = ImageIO.read(is);
			ImageIO.write(bi, "png", out);

			out.flush();
			out.close();
			bi.flush();
			bi = null;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
