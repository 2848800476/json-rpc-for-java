package jcore.jsonrpc.common.face;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISecureCheck extends Serializable {

	/***
	 * �첽��ȫ���ӿ�
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean secureCheck(HttpServletRequest request, HttpServletResponse response);
}
