package com.gxyj.cashier.mgmt.aop.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.gxyj.cashier.common.utils.Constants;
import com.gxyj.cashier.common.utils.ExportExcel;
import com.gxyj.cashier.common.utils.PayUtils;
import com.gxyj.cashier.common.web.Processor;
import com.gxyj.cashier.common.web.Response;
import com.gxyj.cashier.domain.ParamSettings;
import com.gxyj.cashier.service.paramsetting.ParamSettingsService;
import com.yinsin.utils.BeanUtils;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.DateUtils;

/**
 * Aspect for http access execution Rest of Spring components.
 */
@Aspect
public class HttpAccessAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected HttpServletRequest request;
    
    @Autowired
    protected HttpServletResponse response;
    
    /*@Inject
    RightServiceClient rightServiceClient;
    
    @Inject
    CommonService commonService;
    */
    @Inject
    private ParamSettingsService sysParamService;

    //&& @within(org.springframework.web.bind.annotation.PostMapping)
    @Pointcut("within(com.echinacoop.gxyjdatascreen.web.rest..*)")
    public void httpAccessPointcut() {
    }

    @AfterThrowing(pointcut = "httpAccessPointcut()", throwing = "e")
    public void accessAfterThrowing(JoinPoint joinPoint, Throwable e) {
        
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName(), e.getCause() != null? e.getCause() : "NULL");
        
    }

    @Around("httpAccessPointcut()")
    public Object httpAccessAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("http access requst: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
        	Object[] args = joinPoint.getArgs();
        	boolean ISEXPORT = false;
        	JSONObject value = null;
        	if (args != null && args.length > 0) {
        	    String jsonValue = "";
	        	if (args[0] instanceof HttpServletRequest) {
	        		HttpServletRequest request = (HttpServletRequest)args[0];
	        		jsonValue = CommonUtils.stringUncode(request.getParameter("jsonValue"));
	        		request.setAttribute("jsonValue", jsonValue);
	        	} else if (args[0] instanceof String){
	        		jsonValue = CommonUtils.stringUncode((String) args[0]);
	        		args[0] = jsonValue;
	        	}
	        	// 获取到是否导出标识
	        	try {
	        	    value = JSONObject.parseObject(jsonValue);
	        	    ISEXPORT = value.getBooleanValue("ISEXPORT");
                } catch (Exception e) {
                }
	        	
	        	// 判断是否有权限
        		Response res = hasPermission(joinPoint, jsonValue);
	        	if(!res.isSuccess()){
	        	 
	        	    return res;
	        	}
        	}
        	Object result = joinPoint.proceed(args);
            if (log.isDebugEnabled()) {
                log.debug("http access Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            }
            if(result instanceof Response){
                Response res = (Response) result;
                Object page = res.getRtn().get(Response.DATA_KEY);
                if(page != null && (page instanceof Page || page instanceof Map)){
                    // 公共处理修改人、创建人显示的名称
                   /* commonService.translateUserNameByPage(page);*/
                    if(ISEXPORT){
                        // 导出数据
                        return exportData(value, page);
                    }
                }
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }
    
    public Response exportData(JSONObject value, Object page){
        Response res = new Response();
        // 生成excel
        try {
            ParamSettings param = sysParamService.findSettingParamCode(Constants.EXPORT_THREAD_NUM);
            Processor arg = new Processor();
            boolean flag = false;
            if(param != null){
                String[] pValue = param.getParamValue().split(",");
                int currNum = CommonUtils.stringToInt(pValue[0]);
                int maxNum = CommonUtils.stringToInt(pValue[1]);
                if(currNum >= maxNum){ // 最大线程数超过限制，不允许使用导出，提示服务器繁忙
                    res.fail("服务器繁忙，请稍候再试.");
                } else {
                    currNum++;
                    param.setParamValue(currNum + "," + maxNum);
                    arg.setObj(param);
                    flag = sysParamService.update(param);
                    if (flag) {
                        arg.setToReq("EXPORTDATA", value.getJSONObject("EXPORTDATA"));
                        if(page instanceof Page){
                        	arg.setDataToReq(page);
                        } else if(page instanceof Map){
                        	arg.setPage((PageInfo<?>) page);
                        }
                        arg = createGridExcel(arg);
                        if (arg.isSuccess()) {
                            res.success().setDataToRtn(arg.getDataForRtn());
                        }
                    }
                    // 导出数据完成后，再更改线程数
                    param = sysParamService.findSettingParamCode(Constants.EXPORT_THREAD_NUM);
                    if(param != null){
                        pValue = param.getParamValue().split(",");
                        currNum = CommonUtils.stringToInt(pValue[0]);
                        maxNum = CommonUtils.stringToInt(pValue[1]);
                        if(currNum > 0){
                            currNum--;
                            param.setParamValue(currNum + "," + maxNum);
                            arg.setObj(param);
                            sysParamService.update(param);
                        }
                    }
                }
            }
        } catch (Exception e) {
            res.fail(e.getMessage());
        }
        return res;
    }
    
    /**
	 * 生成导出数据
	 * 
	 * @param arg
	 * @return
	 */
	public Processor createGridExcel(Processor arg) {
		FileOutputStream fos = null;
		try {
			JSONObject exportData = (JSONObject) arg.getReq("EXPORTDATA");
			PageInfo<?> page = arg.getPage();
			Page map = (Page) arg.getDataForReq();
			String title = exportData.getString("title");
			String resType = exportData.getString("resType");
			JSONArray texts = exportData.getJSONArray("text");
			JSONArray resIndex = exportData.getJSONArray("resIndex");
			JSONArray resKey = exportData.getJSONArray("resKey");
			log.debug("=====> 开始导出数据：" + title);
			if(texts != null){//modify by jiang 2017-04-05 because find bugs
				log.debug("       " + texts.toJSONString());
			}
			String fileName = title + "-" + DateUtils.format(new Date(), "yyyyMMddHHmmss")
					+ CommonUtils.getRandomNumber(4) + ".xls";
			ParamSettings param = sysParamService.findSettingParamCode(Constants.GRID_DATA_EXCEL_PATH);
			if (param != null) {
				List<?> dataList = null;
				if(page != null){
					dataList = page.getList();
				} else if(map != null){
					dataList = map.getContent();
				}
				String path = param.getParamValue() + fileName;
				fos = new FileOutputStream(new File(path));
				
				//当不使用模块导出，按原方式处理
				if (!"template".equals(resType)) {
					// 标题头数据
					List<String> headers = new ArrayList<String>();
					if (texts != null) {
						for (int i = 0, k = texts.size(); i < k; i++) {
							headers.add(texts.getString(i));
						}
					}
					// 实际行数据
					List<List<Object>> exDatas = new ArrayList<List<Object>>();
					List<Object> rowList = null;
					Object value = null;
					Object[] datas = null;
					List<?> dlist = null;
					Object item = null, bean = null, itemValue = null;
					JSONObject itemJson = null;
					String formatType = null, formatValue = null;
					// String statusType = "", statusDesc = "";
					for (int i = 0, k = dataList.size(); i < k; i++) {
						value = dataList.get(i);
						rowList = new ArrayList<Object>();
						if (resType.equalsIgnoreCase("array")) {
							if(value instanceof Object[]){
								datas = (Object[]) value;
							} else if(value instanceof List){
								dlist = (List<?>) value;
							}
							for (int j = 0, l = resIndex.size(); j < l; j++) {
								item = resIndex.get(j);
								itemValue = null;
								if (item instanceof Integer) {
									if(datas != null){
										itemValue = datas[resIndex.getIntValue(j)];
									} else if(dlist != null){
										itemValue = dlist.get(resIndex.getIntValue(j));
									}
									rowList.add(itemValue);
								} else if (item instanceof JSONObject) {
									itemJson = (JSONObject) item;
									if (itemJson.containsKey("index")) {
										if(datas != null){
											bean = datas[itemJson.getIntValue("index")];
											if (itemJson.containsKey("attr")) {
												itemValue = BeanUtils.getFieldValue(bean, itemJson.getString("attr"));
											} else {
												itemValue = bean;
											}
										} else if(dlist != null){
											bean = dlist.get(itemJson.getIntValue("index"));
											if (itemJson.containsKey("attr")) {
												itemValue = ((Map) bean).get(itemJson.getString("attr"));
											} else {
												itemValue = bean;
											}
										}
										if (itemJson.containsKey("statusType")) {
											String statusType = itemJson.getString("statusType");
											String statusDesc = Constants.getStatusDesc(statusType, (String) itemValue);
											if (statusDesc != null) {
												itemValue = statusDesc;
											}
										} else {
	                                        if(itemJson.containsKey("format")){
	                                            formatType = itemJson.getString("format");
	                                            if(formatType.equals("money")){
	                                                itemValue = PayUtils.formatMoney(String.valueOf(itemValue));
	                                            } else if(formatType.equals("date")){
	                                                formatValue = itemJson.getString("formatValue");
	                                                if(itemValue instanceof Date || itemValue instanceof Timestamp){
	                                                    itemValue = DateUtils.format((Date)itemValue, formatValue);
	                                                } else if(itemValue instanceof String){
	                                                    itemValue = DateUtils.format(DateUtils.parse((String)itemValue), formatValue);
	                                                }
	                                            }
	                                        }
	                                        if(itemJson.containsKey("beforeText")){
	                                            itemValue = itemJson.getString("beforeText") + itemValue;
	                                        }
	                                        if(itemJson.containsKey("afterText")){
	                                            itemValue += itemJson.getString("afterText");
	                                        }
	                                    }
									}
									rowList.add(itemValue);
								}
							}
						} else if (resType.equalsIgnoreCase("object")) {
							for (int j = 0, l = resKey.size(); j < l; j++) {
								item = resKey.get(j);
								itemValue = null;
								if (item instanceof String) {
									rowList.add(BeanUtils.getFieldValue(value, (String) item));
								} else if (item instanceof Object) {
									if (item instanceof JSONObject) {
										itemJson = (JSONObject) item;
									} else {
										itemJson = (JSONObject) JSONObject.toJSON(item);
									}

									if (itemJson.containsKey("attr")) {
										if (value instanceof Map) {
											Map valMap = (Map) value;
											itemValue = valMap.get(itemJson.getString("attr"));
										}else{
											itemValue = BeanUtils.getFieldValue(value, itemJson.getString("attr"));
										}
									}
									if (itemJson.containsKey("statusType")) {
										String statusType = itemJson.getString("statusType");
										String statusDesc = Constants.getStatusDesc(statusType, (String) itemValue);
										if (statusDesc != null) {
											itemValue = statusDesc;
										}
									} else {
									    if(itemJson.containsKey("format")){
                                            formatType = itemJson.getString("format");
                                            if(formatType.equals("money")){
                                                itemValue = PayUtils.formatMoney(String.valueOf(itemValue));
                                            } else if(formatType.equals("date")){
                                                formatValue = itemJson.getString("formatValue");
                                                if(itemValue instanceof Date || itemValue instanceof Timestamp){
                                                    itemValue = DateUtils.format((Date)itemValue, formatValue);
                                                } else if(itemValue instanceof String){
                                                    itemValue = DateUtils.format(DateUtils.parse((String)itemValue), formatValue);
                                                }
                                            }
                                        }
									    if(itemJson.containsKey("beforeText")){
									        itemValue = itemJson.getString("beforeText") + itemValue;
									    }
									    if(itemJson.containsKey("afterText")){
									        itemValue += itemJson.getString("afterText");
                                        }
									}
									rowList.add(itemValue);
								}
							}

						}
						exDatas.add(rowList);
					}

					ExportExcel<?> ee = new ExportExcel<Object>();
					boolean result = ee.createExcel(title, headers, exDatas, fos, null);
					if (result) {
						arg.success().setDataToRtn(fileName);
						log.debug("=====> 导出数据成功：" + path);
					} else {
						arg.fail("文件导出失败");
					}
				} else {
					//按模板方式导出 2017-02-16 tangdb
					/*Map<String,Object> dataMap = new HashMap<String,Object>();	
					
					String templateName=exportData.getString("templateName");
					
					//获取预导出在报表中的数据项{"key":value}存入
					JSONArray extDataArray = exportData.getJSONArray("extData");
					if(extDataArray!=null && extDataArray.size()>0){
						for(int i=0;i<extDataArray.size();i++){
							JSONObject item = (JSONObject) extDataArray.get(i);
							if (item instanceof JSONObject) {
								dataMap.putAll(item);
							}
						}
					}
					
					if(dataList != null && dataList.size()> 5000) {//modify by jiang 2017-04-05 because find bugs
						return arg.fail("导出数据过大，请导出5000条以内的数据！");
					}
					dataMap.put("dataList",dataList==null?Collections.EMPTY_LIST:dataList);
					
					BaseTransformExport transformExport=new BaseTransformExport();
					
					boolean result = transformExport.exportExcel(templateName, dataMap, fos);
					
					if (result) {
						arg.success().setDataToRtn(fileName);
						logger.debug("=====> 导出数据成功：" + path);
					} else {
						arg.fail("文件导出失败");
					}*/

				}
			} else {
				arg.fail("文件导出失败，找不到存储目录配置");
			}
		} catch (Exception e) {
			arg.fail("文件导出失败：" + e.getMessage());
			log.error("文件导出失败：" + e.getMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
				}
			}
		}
		return arg;
	}
	
    /**
     * 权限判断
     * @param joinPoint
     * @param jsonValue
     * @return
     */
    public Response hasPermission(ProceedingJoinPoint joinPoint, String jsonValue) throws Throwable {
        try {
            Signature sig = joinPoint.getSignature();
            MethodSignature msig = (MethodSignature) sig;
            Object target = joinPoint.getTarget();
            RequestMapping req = target.getClass().getAnnotation(RequestMapping.class);
            
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
            OperationRight right = currentMethod.getAnnotation(OperationRight.class);
            PostMapping post = currentMethod.getAnnotation(PostMapping.class);
            
            if (req == null || post == null) {
            	//没有注解@RequestMapping的类和@PostMapping方法不对权限控制
            	return new Response().success();
            }
            if(right != null){
                RightType type = right.value();
                if(type == RightType.filter){
                    return new Response().success();
                }
                if(CommonUtils.isNotBlank(jsonValue)){
                	/*Response rsp = rightServiceClient.getCurrentOperationRights(jsonValue);
                	if (rsp.isSuccess()) {
                		List<Map> rightList = (List<Map>)rsp.getRtn().get(Response.DATA_KEY);            
                        if(rightList != null){
                            if(type == RightType.query){
                                return new Response().success();
                            } else {
                                Map<String, String> fssRight = null;
                                for (int i = 0, k = rightList.size(); i < k; i++) {
                                	
                                    fssRight = rightList.get(i);
                                    if(type.toString().equals(fssRight.get("rightCode"))){
                                        return new Response().success();
                                    }
                                }
                            }
                        }
                	} else {                		
                		return rsp;
                	}*/
                	 return new Response().success();
                }
            } else {
                // 没有增加操作权限注解的方法将都被拦截，提示无权限
                
                if(req != null){
                    StringBuilder sb = new StringBuilder("\n=========================================\n未加权限方法：");
                    if(req.value() != null && req.value().length > 0){
                        sb.append(req.value()[0]);
                    }
                    if(post != null && post.value() != null && post.value().length > 0){
                        sb.append(post.value()[0]);
                    }
                    sb.append("\n=========================================");
                    log.error(sb.toString());
                }
                return new Response().noPermissions();
            }
        } catch (NoSuchMethodException | SecurityException e) {
        	log.error("", e);
        	throw e;
        }
        return new Response().noPermissions();
    }
}
