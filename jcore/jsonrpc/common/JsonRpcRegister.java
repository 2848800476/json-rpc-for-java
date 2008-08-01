package jcore.jsonrpc.common;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/***
 * ע��JsonRpc����
 * @author just
 *
 */
public class JsonRpcRegister {
	
	/***
	 * ͨ��request��ע�����
	 * @param request
	 * @param szKeyName
	 * @param o
	 */
	public static void registerObject(HttpServletRequest request, String szKeyName, Class o)
	{
		HttpSession session = request.getSession(false);
		if(null == session)session = request.getSession(true);
		JSONRPCBridge brg = (JSONRPCBridge)session.getAttribute(Content.RegSessionJSONRPCName);
		// ����ǵ�һ�ξ�ע�����
		if(null == brg)
			 session.setAttribute(Content.RegSessionJSONRPCName, brg = new JSONRPCBridge().setSession(session));
		try
		{
			brg.registerObject(szKeyName, o.newInstance());
		}catch(Exception e)
		{}
	}
}
