package jcore.jsonrpc.common;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import jcore.jsonrpc.common.face.IJsonRpcObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/***
 * JSON-RPC 服务对象
 * @author 夏天
 *
 */
public abstract class JsonRpcObject implements IJsonRpcObject,Serializable
{
	private HttpServletRequest request = null;
	private static final long serialVersionUID = -5362330504532103641L;
	public static final Log log = LogFactory.getLog(JsonRpcObject.class);
   
	public JsonRpcObject(){}
	
	public JsonRpcObject setRequest(HttpServletRequest r)
	{
		this.request = r;
		return this;
	}

	/***
	 * 获取Request对象
	 * @return
	 */
	public HttpServletRequest getRequest()
	{
		return this.request;
	}
	
	private String errMsg = "";
	
	 /***
	  * 获取异常、错误消息使用
	  * @return
	  */
	public String getErrMsg()
	{
		String s = errMsg;
		errMsg = "";
		return s;
	}
	
	/***
	 * 设置异常错误消息
	 */
	public void setErrMsg(String s)
	{
		errMsg = s;
	}

	/* 释放资源
	 */
	public void release(){
	}

}
