package jcore.jsonrpc.common;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import jcore.jsonrpc.common.face.IJsonRpcObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/***
 * JSON-RPC �������
 * @author ����
 *
 */
public abstract class JsonRpcObject implements IJsonRpcObject,Serializable
{
	private transient HttpServletRequest request = null;
	private static final long serialVersionUID = -5362330504532103641L;
	public static final Log log = LogFactory.getLog(JsonRpcObject.class);
   
	public JsonRpcObject(){}
	
	public JsonRpcObject setRequest(HttpServletRequest r)
	{
		this.request = r;
		return this;
	}

	/***
	 * ��ȡRequest����
	 * @return
	 */
	public HttpServletRequest getRequest()
	{
		return this.request;
	}
	
	private String errMsg = "";
	
	 /***
	  * ��ȡ�쳣��������Ϣʹ��
	  * @return
	  */
	public String getErrMsg()
	{
		String s = errMsg;
		errMsg = "";
		return s;
	}
	
	/***
	 * �����쳣������Ϣ
	 */
	public void setErrMsg(String s)
	{
		errMsg = s;
	}

	/* �ͷ���Դ
	 */
	public void release(){
	}

}
