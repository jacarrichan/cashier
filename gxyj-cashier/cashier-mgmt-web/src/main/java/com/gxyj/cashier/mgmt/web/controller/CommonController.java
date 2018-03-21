/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.FileUtil;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.CsrSysDict;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.mgmt.aop.http.OperationRight;
import com.gxyj.cashier.mgmt.aop.http.RightType;
import com.gxyj.cashier.service.CommonService;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;
import com.gxyj.cashier.service.uia.CsrSysDictService;
import com.yinsin.utils.CommonUtils;

/**
 * 公共处理Controller
 * 
 * @author chensj
 */
@RestController
@RequestMapping("/m800")
public class CommonController extends BaseController {

	private final static Logger log = LoggerFactory.getLogger(CommonController.class);
	private final static Map<String, Map<String, Object>> userLockMap = new HashMap<String, Map<String, Object>>();

	/*@Inject
	FssSysDictService fssSysDictServiceImpl;
	
	@Inject
    FssUserSettingService userSettingService;
	
	@Inject
	FssSysParamService sysParamServiceImpl;
	*/
	@Inject
	CommonService commonService;
	
	@Inject
	CsrSysDictService sysDictService;
	
	@Inject
    private ParamSettingsService sysParamService;
	
	
	/**
	 * 
	 * @param jsonValue
	 */
	/*@PostMapping("/f80000")
	@OperationRight(RightType.filter)
	public Response getUserSession() {
		Response res = new Response();
		FssUserInfo user = getUserInfo();
		if (user != null) {
		    Map<String, Object> userData = new HashMap<String, Object>();
		    userData.put("userId", user.getUserId());
		    userData.put("userName", user.getUserName());
		    userData.put("trueName", user.getTrueName());
		    FssBrch brch = getUserBrch();
		    if(brch !=null ){
			    userData.put("brchId", brch.getBrchId());
			    userData.put("brchName", brch.getBrchName());
		    }
		    FssMallInfo mall = getUserMallInfo();
		    if(mall !=null ){
			    userData.put("mallId", mall.getMallId());
			    userData.put("mallName", mall.getMallName());
		    }
		    
		    FssSysParam workParam = sysParamServiceImpl.findByParamCode(Constants.CURRENT_DAY_CODE);
		    if(workParam !=null ){
		    	userData.put("workDate" ,workParam.getParamValue());
		    }
		    FssSysParam statusParam = sysParamServiceImpl.findByParamCode(Constants.CURRENT_SYS_STS);
		    if(statusParam !=null ){
		    	userData.put("sysStatus" ,MapContants.SysStatusMap.get(statusParam.getParamValue()));
		    }
			res.success().setDataToRtn(userData);
		} else {
			log.debug("未登录或登录超时....");
			res.fail();
		}
		return res;
	}*/

	@PostMapping("/f80001")
	@OperationRight(RightType.filter)
	public Response getSysDictByDataName(@RequestParam String jsonValue) {
		Response res = new Response();
		JSONObject value = parseJsonValue(jsonValue);
		String dataName = value.getString("dataName");
		if (CommonUtils.isNotBlank(dataName)) {
//			Argument arg = new Argument();
//			arg.setToReq("dataName", dataName);
//			arg = fssSysDictServiceImpl.findAllByDataName(arg);
			List<CsrSysDict> dicts = sysDictService.findAllByDataName(dataName);
			/*if (arg.isSuccess()) {
				res.success().setDataToRtn(arg.getDataForRtn());
			}*/
			res.success().setDataToRtn(dicts);
		}
		return res;
	}
	
	@PostMapping("/f80002")
	@OperationRight(RightType.filter)
	public Response getSysDictByDataNameAndDataCode(@RequestParam String jsonValue) {
		Response res = new Response();
		JSONObject value = parseJsonValue(jsonValue);
		
		CsrSysDict record = parseJsonValueObject(jsonValue, CsrSysDict.class);
		CsrSysDict dict = sysDictService.findByDataNameAndDataCode(record);
		if (dict != null) {
			JSONObject dictJson = new JSONObject();
			dictJson.put("name", dict.getDataName());
			dictJson.put("code", dict.getDataCode());
			dictJson.put("desc", dict.getDataDesc());
			dictJson.put("value", dict.getDataValue());
			res.success().setDataToRtn(dictJson);
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
    @PostMapping("/f80003")
	@OperationRight(RightType.filter)
	public Response getBatchSysDictByDataName(@RequestParam String jsonValue) {
		Response res = new Response();
		JSONObject value = parseJsonValue(jsonValue);
		JSONArray dataNames = value.getJSONArray("dataNames");
		if (dataNames != null && dataNames.size() > 0) {
			List<String> dataName = new ArrayList<String>();
			String name = "";
			for (int i = 0, k = dataNames.size(); i < k; i++) {
				name = dataNames.getString(i);
				if (CommonUtils.isNotBlank(name)) {
					dataName.add(name);
				}
			}
			if (dataName.size() > 0) {
				
				List<CsrSysDict> dataList = sysDictService.findAllInDataName(dataName);
				if (!dataList.isEmpty()) {
					CsrSysDict dict = null;
					JSONArray dictArr = null;
					JSONObject dictJson = null;
					JSONObject dictData = new JSONObject();
					for (int i = 0, k = dataList.size(); i < k; i++) {
						dict = dataList.get(i);
						dictJson = new JSONObject();
						dictJson.put("name", dict.getDataName());
						dictJson.put("code", dict.getDataCode());
						dictJson.put("desc", dict.getDataDesc());
						dictJson.put("value", dict.getDataValue());
						if(!dictData.containsKey(dict.getDataName())){
							dictArr = new JSONArray();
							dictData.put(dict.getDataName(), dictArr);
						}
						dictArr.add(dictJson);
					}
					res.success().setDataToRtn(dictData);
				}
			}
		}
		return res;
	}
	/*
	@PostMapping("/f80004")
	@OperationRight(RightType.filter)
	public Response getAllSysDict(@RequestParam String jsonValue) {
		Response res = new Response();
		Argument arg = new Argument();
		arg = fssSysDictServiceImpl.findAll(arg);
		if (arg.isSuccess()) {
			res.success().setDataToRtn(arg.getDataForRtn());
		}
		return res;
	}
	
	@PostMapping("/f80005")
    @OperationRight(RightType.filter)
    public Response checkLockscreen(@RequestParam String jsonValue) {
        Response res = new Response();
        
        Argument arg = new Argument();
        arg.setToReq("userId", getUserId());
        arg = userSettingService.getUserSetting(arg);
        if(arg.isSuccess()){
            JSONObject userSetJson = (JSONObject) arg.getObj();
            if(userSetJson != null){
                JSONObject lockscreen = userSetJson.getJSONObject("lockscreen");
                if(lockscreen != null && lockscreen.containsKey("pwd")){
                    res.success().setDataToRtn(lockscreen);
                }
            }
        }
        return res;
    }
	
	@PostMapping("/f80006")
    @OperationRight(RightType.filter)
    public Response unlockscreen(@RequestParam String jsonValue) {
        Response res = new Response();
        JSONObject value = parseJsonValue(jsonValue);
        String userId = getUserId();
        
        String type = CommonUtils.excNullToString(value.getString("type"), "lock");
        String lockpwd = CommonUtils.excNullToString(value.getString("lockpwd"), "");
        if(type.equals("lock") && CommonUtils.isNotBlank(lockpwd)){
            JSONObject userSetJson = new JSONObject();
            Argument arg = new Argument();
            arg.setToReq("userId", userId);
            arg = userSettingService.findOne(arg);
            FssUserSetting sett = new FssUserSetting();
            sett.setUserId(userId);
            if(arg.isSuccess()){
                sett = (FssUserSetting) arg.getObj();
                if(sett != null){
                    userSetJson = JSONObject.parseObject(sett.getSetting());
                }
            }
            JSONObject lockscreen = userSetJson.getJSONObject("lockscreen");
            if (lockscreen == null) {
                lockscreen = new JSONObject();
            }
            lockscreen.put("pwd", MD5.md5b2(lockpwd));
            lockscreen.put("time", new Date().getTime());
            userSetJson.put("lockscreen", lockscreen);
            // 保存
            sett.setSetting(userSetJson.toJSONString());
            arg.setObj(sett);
            arg = userSettingService.save(arg);
            if(arg.isSuccess()){
                res.success();
            }
        } else if(type.equals("unlock") && CommonUtils.isNotBlank(lockpwd)){
            lockpwd = MD5.md5b2(lockpwd);
            
            Argument arg = new Argument();
            arg.setToReq("userId", userId);
            arg = userSettingService.findOne(arg);
            if(arg.isSuccess()){
                FssUserSetting sett = (FssUserSetting) arg.getObj();
                if(sett != null){
                    JSONObject userSetJson = JSONObject.parseObject(sett.getSetting());
                    JSONObject lockscreen = userSetJson.getJSONObject("lockscreen");
                    if(lockscreen != null){
                        String pwd = CommonUtils.objectToString(lockscreen.get("pwd"), "");
                        if(lockpwd.equals(pwd)){
                            userSetJson.remove("lockscreen");
                            // 保存
                            sett.setSetting(userSetJson.toJSONString());
                            arg.setObj(sett);
                            arg = userSettingService.save(arg);
                            if(arg.isSuccess()){
                                res.success();
                            }
                        } else {
                            res.fail();
                        }
                    }
                }
            }
        }
        return res;
    }*/

	@PostMapping("/f80007")
    @OperationRight(RightType.filter)
    public Response base64String(@RequestParam String jsonValue) {
		Response res = new Response();
		
		JSONObject value = parseJsonValue(jsonValue);
		String val = value.getString("base64Str");
		/*HttpSession session = request.getSession();
		String validation_code = (String)session.getAttribute("verCode");
		String validationCode = value.getString("validationCode");
		
        if(validationCode.equalsIgnoreCase(validation_code)){
 
            System.out.println("验证码正确");
            res.success("验证码正确");
 
            
        }else{
 
            System.out.println("验证码错误");
            res.fail("验证码错误");
        }*/
		if(CommonUtils.isNotBlank(val)){
			String encryptedData = Base64.encodeBase64String(val.getBytes()).replaceAll("\r\n", "");
			res.success().setDataToRtn(encryptedData);
		}else{
			res.fail("加密失败");
		}
		return res;
	}
	
	@Autowired
	HttpRequestClient httpRequestClient;

	@PostMapping(value = "/f80008")
	@OperationRight(RightType.filter)
	public void springUpload(@RequestParam("uploadFile") MultipartFile file, @RequestParam("userId") String userId, @RequestParam("imagurl") String imagurl, HttpServletResponse response)
			throws IllegalStateException, IOException {
		//Map<String, Object> param = new HashMap<>();
		String content = Base64Utils.encodeToString(FileCopyUtils.copyToByteArray(file.getInputStream()));
		String fileName = String.format("%s%s", userId, ".jpg");
		Map<String, String> map = new HashMap<String, String>();
		map.put("fileContent", content);
		map.put("fileName", fileName);
		String str=httpRequestClient.doPost(imagurl, map);
		log.debug(str);
		response.sendRedirect(request.getContextPath() + "/apps/frame/upload_file.html?fileName="+fileName+"&orgFileName="+file.getOriginalFilename());
		
	}
	
	@PostMapping("/f80602")
    @OperationRight(RightType.filter)
    public void downloadFile(@RequestParam String jsonValue) {
        Response res = new Response();
        JSONObject value = parseJsonValue(jsonValue);
        try {
            String fileName = value.getString("fileName");
            if(CommonUtils.isNotBlank(fileName)){
            	ParamSettings param = sysParamService.findSettingParamCode(Constants.GRID_DATA_EXCEL_PATH);
                if(param != null){
                    String path = param.getParamValue() + fileName;
                    FileUtil.downloadFile(path, request, response);
                } else {
                    res.fail("文件下载失败，找不到文件路径");
                    out(response, res);
                }
            }
        } catch (Exception e1) {
            log.error("文件下载失败：" + jsonValue);
        }
    }
}
