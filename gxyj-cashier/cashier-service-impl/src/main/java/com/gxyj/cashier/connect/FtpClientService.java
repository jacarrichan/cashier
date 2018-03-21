/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.connect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.config.CashierConfig;
import com.gxyj.cashier.exception.CashierServiceException;
import com.gxyj.cashier.utils.CashierErrorCode;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * FTP服务类，封装Apache的commons.net.ftp来实现FTP相关操作
 * 
 * @author Danny
 */
@Component
public class FtpClientService {

	private static final Log log = LogFactory.getLog(FtpClientService.class);

	@Autowired
	CashierConfig cashierConfig;

	FTPClient ftp;
	/**
	 * FTP 服务器地址
	 */
	private String ftpServerAddress = null;
	/**
	 * FTP 服务端口
	 */
	private int port = 21;
	/**
	 * FTP 用户名
	 */
	private String user = null;
	/**
	 * FTP 密码
	 */
	private String password = null;
	/**
	 * FTP 数据传输超时时间
	 */
	private int timeout = 0;

	public FtpClientService() {

	}

	/* sftp 密码认证方式的相关操作 */
	/**
	 * 连接sftp服务器
	 * @return ChannelSftp
	 */
	public ChannelSftp connect() {
		String user = cashierConfig.getFtpUser();
		String ftpServerAddress = cashierConfig.getFtpServer();
		int port = cashierConfig.getFtpPort();
		String passwd=cashierConfig.getFtpPwd();
		
		ChannelSftp sftp = connect(user,passwd,port,ftpServerAddress);
		
		return sftp;
	}
	
	/**
	 * 连接指定的FTP服务器
	 * @param user 用户名称
	 * @param passwd 密码
	 * @param port 端口
	 * @param srvAddress 服务器地址
	 * @return ChannelSftp
	 */
	public ChannelSftp connect(String user,String passwd,int port,String srvAddress) {
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			
			jsch.getSession(user, srvAddress, port);
			Session sshSession = jsch.getSession(user, srvAddress, port);
			log.debug("Session created");
			sshSession.setPassword(passwd);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			log.debug("Session connected.");
			Channel channel = sshSession.openChannel("sftp");
			log.debug("Opening Channel.");
			channel.connect();
			sftp = (ChannelSftp) channel;
			
			log.debug("Connected to " + ftpServerAddress + ".");
		}
		catch (Exception e) {
			log.error("Connected to " + ftpServerAddress + "failed.");
		}
		return sftp;
	}
	
	

	/**
	 * FTP连接，并进行用户登录
	 * 
	 * @return FTPClient FTPCLient instance
	 * @throws CashierServiceException 异常发生
	 */
	public FTPClient initConnection() throws CashierServiceException {
		ftp = new FTPClient();
		try {
			// 连接到FTP
			ftp.connect(ftpServerAddress, port);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,"Fail to connection to the ftp server.");
			}
			// 登录
			if (!ftp.login(user, password)) {
				throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,"Fail to login to the ftp server.");
			}
			// 传输模式使用passive
			ftp.enterLocalPassiveMode();
			// 设置数据传输超时时间
			ftp.setDataTimeout(timeout);
			log.debug("Connect to ftp server success. address = " + ftpServerAddress + ", port = " + port);
		}
		catch (IOException ex) {
			throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,"Error when init ftp connection.", ex);
		}
		return ftp;
	}

	/**
	 * 设置传输方式
	 * 
	 * @param ftp ftpclient instance
	 * @param binaryFile true:二进制/false:ASCII
	 * @throws CashierServiceException 异常
	 */
	public void setTransferMode(FTPClient ftp, boolean binaryFile) throws CashierServiceException {
		try {
			if (binaryFile) {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				log.debug("Set transfer mode to binary.");
			}
			else {
				ftp.setFileType(FTP.ASCII_FILE_TYPE);
				log.debug("Set transfer mode to ASCII.");
			}
		}
		catch (IOException ex) {
			throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,"Error when set transfer mode.", ex);
		}
	}

	/**
	 * 在当前工作目录下建立多级目录结构
	 * 
	 * @param ftp ftpclient
	 * @param dir 目录
	 * @throws CashierServiceException 异常
	 */
	public void makeMultiDirectory(FTPClient ftp, String dir) throws CashierServiceException {
		try {
			StringBuffer fullDirectory = new StringBuffer();
			StringTokenizer toke = new StringTokenizer(dir, "/");
			while (toke.hasMoreElements()) {
				String currentDirectory = (String) toke.nextElement();
				fullDirectory.append(currentDirectory);
				ftp.makeDirectory(fullDirectory.toString());
				if (toke.hasMoreElements()) {
					fullDirectory.append('/');
				}
			}
		}
		catch (IOException ex) {
			throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,"Error when make multiply directory on the ftp server.", ex);
		}
	}

	/**
	 * 更改服务器当前路径
	 * 
	 * @param ftp ftpclient
	 * @param dir 目录
	 * @throws CashierServiceException 异常
	 */
	public void changeWorkingDirectory(FTPClient ftp, String dir) throws CashierServiceException {
		try {
			if (!ftp.changeWorkingDirectory(dir)) {
				new CashierServiceException(CashierErrorCode.REMOTE_DIR_NOT_EXISTS,"Error when change working directory on the ftp server.");
			}
		}
		catch (IOException ex) {
			throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,"Error when change working directory on the ftp server.", ex);
		}
	}

	/**
	 * 上传文件到FTP服务器
	 * 
	 * @param ftp FTPClient
	 * @param localFilePathName 本地文件
	 * @param remoteFilePathName 远程文件
	 * @throws CashierServiceException 异常
	 */
	public void uploadFile(FTPClient ftp, String localFilePathName, String remoteFilePathName) throws CashierServiceException {
		InputStream input = null;
		try {
			input = new FileInputStream(localFilePathName);
			boolean flag = ftp.storeFile(remoteFilePathName, input);
			log.debug(localFilePathName + "-->" + remoteFilePathName);
			log.debug("Upload file success.status-->" + flag);
		}
		catch (IOException ex) {
			throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,"Upload file to ftp server fail.", ex);
		}
		finally {
			try {
				if (input != null) {
					input.close();
				}
			}
			catch (IOException ex) {
			}
		}
	}

	/**
	 * 下载文件
	 * @param directory 下载目录
	 * @param downloadFile 下载的文件
	 * @param sftp ChannelSftp
	 * @param recvdir 接收文件的目录
	 * @param checkinterval 文件检测时间间隔
	 * @return true/false
	 */
	public boolean download_sftp(String directory, String downloadFile, String recvdir, String checkinterval, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File recvdirFile = new File(recvdir);
			if (!recvdirFile.exists()) {
				recvdirFile.mkdirs();
			}
			File file = new File(recvdirFile, downloadFile);
			String ftpfilepath = new File(new File(directory), downloadFile).getAbsolutePath();
			if (!checkFileOk(ftpfilepath, checkinterval)) {
				log.error(downloadFile + "文件完整性检测失败");
				return false;
			}
			FileOutputStream fos = new FileOutputStream(file);
			log.info("文件" + downloadFile + "开始获取");
			sftp.get(downloadFile, fos);
			log.info("文件" + downloadFile + "获取成功");
			fos.close();
			return true;
		}
		catch (Exception e) {
			log.error("文件" + downloadFile + "获取失败,出现异常" + e.getMessage());
			return false;
		}
	}

	/**
	 * 列出目录下的文件
	 * @param directory 要列出的目录
	 * @param sftp ChannelSftp
	 * @return Vector Vector
	 * @throws CashierServiceException 异常
	 */
	public Vector listFiles(String directory, ChannelSftp sftp) throws CashierServiceException {
		try {
			return sftp.ls(directory);
		}
		catch (SftpException e) {
			throw new CashierServiceException(CashierErrorCode.CONNECT_SERVER_ERROR,e.getMessage(),e);
		}
	}

	/**
	 * 从FTP服务器下载文件至本地
	 * @param directory 远程FTP文件目录
	 * @param remoteFileName 文件名称
	 * @param localFileName 本地文件名全名
	 * @param checkinterval 检查间隔
	 * @return true/false
	 * @throws IOException IO异常
	 */
	public boolean download_ftp(String directory, String remoteFileName, String localFileName, String checkinterval)
			throws IOException {
		boolean flag = false;
		File outfile = new File(localFileName);
		OutputStream oStream = null;
		String ftpfilepath;
		try {
			ftpfilepath = new File(new File(directory), remoteFileName).getAbsolutePath();
			if (!checkFileOk(ftpfilepath, checkinterval)) {
				log.error(remoteFileName + "文件完整性检测失败");
				return false;
			}
			oStream = new FileOutputStream(outfile);
			flag = ftp.retrieveFile(remoteFileName, oStream);
		}
		catch (IOException e) {
			flag = false;
			return flag;
		}
		finally {
			oStream.close();
		}
		return flag;
	}

	/**
	 * 
	 * 功能描述：文件完整性检查，接收保险公司文件时需要用到，隔一段时间判断文件大小是否变化， 不变了表示传输完了 输入参数：无 输出参数：无
	 * @param filename 文件名称
	 * @param interval 检查的时间间隔
	 * @return true/false
	 * 
	 */
	public boolean checkFileOk(String filename, String interval) {
		File tempfile = new File(filename);
		long filelastmodify = tempfile.lastModified();
		long filelength = tempfile.length();
		log.debug("文件最后修改日期:" + Long.toString(filelastmodify) + "文件大小:" + Long.toString(filelength));
		log.debug("interval:" + interval);
		while (true) {
			try {
				Thread.sleep(Long.parseLong(interval));
				if (filelastmodify == tempfile.lastModified() && filelength == tempfile.length()) {
					log.info(filename + "文件检测文件完整性成功返回");
					return true;
				}
				filelastmodify = tempfile.lastModified();
				filelength = tempfile.length();
				log.debug("文件最后修改日期:" + Long.toString(filelastmodify) + "文件大小:" + Long.toString(filelength));
			}
			catch (InterruptedException e) {
				log.error("检测文件完整性时候发生异常" + e.getMessage());
				return true;
			}
		}
	}

	/**
	 * 按指定时间间隔从FTP下载指定文件到接收目录
	 * @param directory FTP Server dir
	 * @param remoteFileattrbute remote fiel attribute
	 * @param recvdir recev dir
	 * @param checkinterval check interval
	 * @return true/false
	 * @throws IOException IO Exception
	 */
	public boolean downloadfiles_ftp(String directory, String remoteFileattrbute, String recvdir, String checkinterval)
			throws IOException {
		boolean flag = false;
		String[] ftpfileset;
		String ftpfilepath;
		try {
			ftpfileset = ftp.listNames();
			if (ftpfileset == null || ftpfileset.length == 0) {
				// 没有文件到达
				log.info("目录没有文件");
			}
			// 依次处理每一个文件或zip包
			for (int i = 0; i < ftpfileset.length; i++) {
				// log.info(ftpfileset[i]);
				if (ftpfileset[i].toUpperCase().startsWith(remoteFileattrbute)) {
					log.info(ftpfileset[i]);

					File localfile = new File(new File(recvdir), ftpfileset[i]);
					download_ftp(directory, ftpfileset[i], localfile.getAbsolutePath(), checkinterval);
					flag = true;
				}

			}
		}
		catch (IOException e) {
			flag = false;
			return flag;
		}
		
		return flag;
	}

	/**
	 * 以文件流形式从FTP读取文件
	 * @param sourceFileName 源文件
	 * @return Inputstream
	 * @throws IOException IOException
	 */
	public InputStream downFile(String sourceFileName) throws IOException {
		return ftp.retrieveFileStream(sourceFileName);
	}

	/**
	 * 下载文件到本地
	 * 
	 * @param ftp ftpclient instance
	 * @param remoteFilePathName 远程文件名称
	 * @param localFilePathName 本地文件名称
	 * @throws CashierServiceException 收银台异常
	 */
	public void downloadFile(FTPClient ftp, String remoteFilePathName, String localFilePathName) throws CashierServiceException {

		OutputStream output = null;
		try {
			output = new FileOutputStream(localFilePathName);
			if (ftp.retrieveFile(remoteFilePathName, output)) {
				log.debug("Download file success.");
			}
			else {
				throw new CashierServiceException(CashierErrorCode.DOWNLOAD_FILE_FAILURE,"Download file from the ftp server fail.");
			}
		}
		catch (IOException ex) {
			throw new CashierServiceException(CashierErrorCode.DOWNLOAD_FILE_FAILURE,"Download file from the ftp server fail.", ex);
		}
		finally {
			try {
				if (output != null) {
					output.close();
				}
			}
			catch (IOException ex) {
			}
		}
	}

	public String[] downAndRemoveFiles(FTPClient ftp, String remoteFilePath, String localFilePath)
			throws CashierServiceException {
		StringBuffer remoteFile = null;
		StringBuffer localFile = null;
		String[] fileNames = null;
		try {
			FTPFile[] files = ftp.listFiles(remoteFilePath);
			fileNames = new String[files.length];
			File locPathf = new File(localFilePath);
			if (!locPathf.exists()) {
				locPathf.mkdirs();
			}
			for (int i = 0; i < files.length; i++) {
				fileNames[i] = files[i].getName();
				log.debug(fileNames[i] + ">> this file have download");
				log.debug("from ||" + remoteFilePath + ">>to>>" + localFilePath);

				remoteFile = new StringBuffer(remoteFilePath).append(files[i].getName());
				localFile = new StringBuffer(localFilePath).append(files[i].getName());
				// 下载到本地文件系统上
				downloadFile(ftp, remoteFile.toString(), localFile.toString());
				// 删除已下载文件

				//// 暂时不删除.测试用
				ftp.deleteFile(remoteFile.toString());
			}
		}
		catch (IOException e) {
			throw new CashierServiceException(CashierErrorCode.DOWNLOAD_FILE_FAILURE,"Download file from the ftp server fail.");
		}
		return fileNames;
	}

	public void sendSftpFile(String filename, String senddir, String remoteGenerateDir, String sendbak, String faildir,
			ChannelSftp sftp) throws Exception {
		boolean sendflag = false;
		File desfile = new File(new File(senddir), filename);
		try {
			sendflag = upload(remoteGenerateDir, desfile.getAbsolutePath(), sftp);
		}
		catch (Exception e) {
			log.error(desfile.getAbsolutePath() + "文件准备发送时候发生异常" + e.getMessage());
			sendflag = false;
		}
		finally {
			// if(sendflag)
			// {
			// //把desfile移动到备份目录
			// FileUtils.moveFile(desfile,sendbak);
			// log.info(desfile.getAbsolutePath()+"文件ftp发送成功，移动到备份目录");
			// }
			// else
			// {
			// //把desfile移动到失败目录
			// FileUtils.moveFile(desfile,faildir);
			// log.info(desfile.getAbsolutePath()+"文件ftp发送失败，移动到失败目录");
			// }
		}
	}

	/**
	 * 删除文件
	 * @param directory 要删除文件所在目录
	 * @param deleteFile 要删除的文件
	 * @param sftp com.jcraft.jsch.ChannelSftp
	 * @return true/false
	 */
	public boolean delete(String directory, String deleteFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			log.info("文件" + deleteFile + "开始删除");
			sftp.rm(deleteFile);
			log.info("文件" + deleteFile + "删除成功");
			return true;
		}
		catch (Exception e) {
			log.error("文件" + deleteFile + "删除失败，出现异常" + e.getMessage());
			return false;
		}
	}

	/**
	 * 上传文件
	 * @param directory 上传的目录
	 * @param uploadFile 要上传的文件
	 * @param sftp com.jcraft.jsch.ChannelSftp
	 * @return true/false
	 */
	public boolean upload(String directory, String uploadFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(uploadFile);
			log.info("开始上传文件" + uploadFile);
			sftp.put(new FileInputStream(file), file.getName());
			log.info("文件" + uploadFile + "上传成功");
			return true;
		}
		catch (Exception e) {
			log.error("文件" + uploadFile + "上传失败，出现异常" + e.getMessage());
			return false;
			// e.printStackTrace(); 屏蔽控制台输出错误
		}
	}

	/**
	 * Method setFtpServerAddress.
	 * 
	 * @param ftpServerAddress String
	 */
	public void setFtpServerAddress(String ftpServerAddress) {
		this.ftpServerAddress = ftpServerAddress;
	}

	/**
	 * Method setUser.
	 * 
	 * @param user String
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Method setPassword.
	 * 
	 * @param password String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Method setTimeout.
	 * 
	 * @param timeout String
	 */
	public void setTimeout(String timeout) {
		try {
			this.timeout = Integer.parseInt(timeout);
		}
		catch (NumberFormatException ex) {
			// 默认超时时间500毫秒
			this.timeout = 500;
		}
	}

	/**
	 * Method setPort.
	 * 
	 * @param port String
	 */
	public void setPort(String port) {
		try {
			this.port = Integer.parseInt(port);
		}
		catch (NumberFormatException ex) {
			// 默认端口21
			this.port = 21;
		}
	}

	/**
	 * 上传文件
	 * @param directory 上传的目录
	 * @param file 要上传的文件
	 * @param sftp com.jcraft.jsch.ChannelSftp实例
	 * @return true/false
	 */
	private boolean upload(String directory, File file, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			log.info("上传的目录" + directory + "---要上传的文件---" + file);
			sftp.put(new FileInputStream(file), file.getName());
			log.info("文件上传成功");
			return true;
		}
		catch (Exception e) {
			log.error("文件上传失败，出现异常" + e.getMessage());
			return false;
			// e.printStackTrace(); 屏蔽控制台输出错误
		}
	}

	/**
	 * 传送文件至FTP Server
	 * @param directory 远程目录
	 * @param file 本地文件
	 * @param sftp Sftp实例
	 * @return true/false
	 */
	public boolean sendSftpFile(String directory, File file, ChannelSftp sftp) {

		boolean sendflag = false;
		try {
			sendflag = upload(directory, file, sftp);
		}
		catch (Exception e) {
			log.error(file.getAbsolutePath() + "文件准备发送时候发生异常" + e.getMessage());
		}
		return sendflag;
	}

}
