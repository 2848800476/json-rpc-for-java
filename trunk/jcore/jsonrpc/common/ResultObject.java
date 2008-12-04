package jcore.jsonrpc.common;

import java.io.Serializable;

import jcore.jsonrpc.common.face.IResultObject;

public class ResultObject implements IResultObject,Serializable {
	private static final long serialVersionUID = -7059298543537434669L;
	private String errMsg;
	public transient Object result = null;
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

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
