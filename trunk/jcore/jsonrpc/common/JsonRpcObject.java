package jcore.jsonrpc.common;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yinhai.webframework.session.UserSession;

/***
 * JSON-RPC �������
 * @author ����
 *
 */
public abstract class JsonRpcObject implements Serializable
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
	 * ��ȡRequest����
	 * @return
	 */
	public HttpServletRequest getRequest()
	{
		return this.request;
	}
}
