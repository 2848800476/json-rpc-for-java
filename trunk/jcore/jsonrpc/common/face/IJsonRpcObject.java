package jcore.jsonrpc.common.face;

import javax.servlet.http.HttpServletRequest;

import jcore.jsonrpc.common.JsonRpcObject;

public interface IJsonRpcObject extends IResultObject{

	/***
	 * ���������������ʹ�õ�ʱ���ɿ��ע��request����
	 * @param r
	 * @return
	 */
	public JsonRpcObject setRequest(HttpServletRequest r);
	
	/***
	 * ��ȡrequest����ͨ�������Ի�ȡ��session����
	 * @return
	 */
	public HttpServletRequest getRequest();
	
	/***
	 * �ͷ���Դ
	 */
	public void release();
}
