package jcore.jsonrpc.common;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jcore.jsonrpc.common.face.IJsonRpcObject;
import jcore.jsonrpc.common.face.IResultObject;
import jcore.jsonrpc.tools.Tools;

/***
 * 
 * @author ����
 *
 */
public class JSONRPCBridge implements Serializable{
	private static final long serialVersionUID = 1L;
	// Ϊ�˼�Ⱥ��ʹ���ڴ渴��ʹ��
	private HttpSession session = null;
	// ע���еĶ���
	private Map globalMap = Collections.synchronizedMap(new HashMap());
	
	// ���涥���ı�ע������JSON��ʽ
	private Map cache = Collections.synchronizedMap(new HashMap());
	// ����������·
	private Map links = Collections.synchronizedMap(new HashMap());
	// ������ע�������
	private Map topNms = Collections.synchronizedMap(new HashMap());
	
	/***
	 * ��������ע��ȫ�ֵĶ���ΪJSON���ַ�����ʽ
	 * @return
	 */
	public String getRegObjsToString()
	{
		StringBuffer buf = new StringBuffer("{\"result\":[");
		int n = 0;
		Iterator oIt = topNms.entrySet().iterator();
		while(oIt.hasNext())
		{
			if(0 < n)
				buf.append(",");
			Map.Entry oKey = (Map.Entry)oIt.next();
			
			// Ϊ��������ܣ�����cache
			String szTmp  = (String)cache.get(oKey.getKey());
			if(null == szTmp)
			{
				// ֻ�Զ���ע��Ľ��л�ȡ, oKey.getValue()ֻ��ȫ���е�key
				szTmp = new ObjectToJSON(getObject(oKey.getValue().toString()), this).toJSON(oKey.getKey().toString());
				// szTmp = szTmp.replaceFirst("\\{", "{name:\"" + oKey.getKey() + "\",");
				cache.put(oKey.getKey(), szTmp);
				// �ٴε��ã��Ա㼯Ⱥ����������������
				if(null != session)
					session.setAttribute(Content.RegSessionJSONRPCName, this);
			}
			n++;
			buf.append(szTmp);
		}
		buf.append("]}");
		return buf.toString();
	}
	
	/***
	 * ���캯���������һ��ҪsetSession
	 */
	public JSONRPCBridge(){}
	
	/***
	 * �����ʱ����Դ���session����
	 * @param session
	 */
	public JSONRPCBridge(HttpSession session)
	{
		this.session = session;
	}
	
	/***
	 * ע�Ḹ�׶�����·
	 * @param nSelfHashCode
	 * @param nParentHashCode
	 * @return
	 */
	public JSONRPCBridge registerParentObject(int nSelfHashCode, int nParentHashCode)
	{
		String szKeyName = nSelfHashCode + "";
		if(null == links.get(szKeyName))
		{
			// ע��
			links.put(szKeyName, nParentHashCode + "");
		}
		// �ٴε��ã��Ա㼯Ⱥ����������������
		if(null != session)
			session.setAttribute(Content.RegSessionJSONRPCName, this);
		return this;
	}
	
	/***
	 * �Ƴ������Ӧ�Ķ��������ע����Ϣ
	 * @param nSelfHashCode
	 */
	public void removeParentRegInfo(int nSelfHashCode)
	{
		String szKeyName = nSelfHashCode + "";
		links.remove(szKeyName);
		
		// �Ƴ��Ӷ���
		Iterator oIt = links.entrySet().iterator();
		while(oIt.hasNext())
		{
			Map.Entry oKey = (Map.Entry)oIt.next();
			String szChildId = (String)oKey.getValue();
			if(szKeyName.equals(szChildId))
			{
				links.remove(szChildId);
				removeObject(Integer.parseInt((String)oKey.getKey()));
			}
		}

		
		// �ٴε��ã��Ա㼯Ⱥ����������������
		if(null != session)
			session.setAttribute(Content.RegSessionJSONRPCName, this);
	}
	
	/***
	 * ��ȡ����Ķ�������
	 * @param nSelfHashCode
	 * @return
	 */
	public Object getParentObject(int nSelfHashCode)
	{
		String szKeyName = nSelfHashCode + "";
		int nRst = 0;
		while(0 == nRst)
		{
			Object o = links.get(szKeyName);
			if(null == o)
				break;
			nRst = Integer.parseInt((String)o);
		}
		if(0 < nRst)
			return this.getObject(nRst + "");
		return null;
	}
	
	/***
	 * ע�����
	 * @param nHashCodeName ����hashcodeע����󣬷�ֹͬһʵ��ע����
	 * @param o
	 * @return this
	 */
	public JSONRPCBridge registerObject(int nHashCodeName, Object o)
	{
		String szKeyName = nHashCodeName + "";
		if(null == globalMap.get(szKeyName))
		{
			// ע��
			globalMap.put(szKeyName, o);
		}
		// �ٴε��ã��Ա㼯Ⱥ����������������
		if(null != session)
			session.setAttribute(Content.RegSessionJSONRPCName, this);
		return this;
	}
	
	public JSONRPCBridge registerObject(String szKeyName, Object o)
	{
		if(null != szKeyName && 0 < szKeyName.trim().length())
		{
			if(null == topNms.get(szKeyName))
				topNms.put(szKeyName, o.hashCode() + "");
			return registerObject(o.hashCode(), o);
		}
		return this;
	}
	
	/***
	 * �Ƴ�ע��Ķ���
	 * @param nHashCodeName ����hashcodeע����󣬷�ֹͬһʵ��ע����
	 * @return this
	 */
	public JSONRPCBridge removeObject(int nHashCodeName)
	{
		String szKeyName = nHashCodeName + "";
		// �Ƴ�
		globalMap.remove(szKeyName);
		// �ٴε��ã��Ա㼯Ⱥ����������������
		if(null != session)
			session.setAttribute(Content.RegSessionJSONRPCName, this);
		return this;
	}
	
	/***
	 * �Ƴ�����
	 * @param szKeyName
	 * @param o
	 * @return
	 */
	public JSONRPCBridge removeObject(String szKeyName, Object o)
	{
		if(null != szKeyName && 0 < szKeyName.trim().length())
		{
			topNms.remove(szKeyName);
			return removeObject(o.hashCode());
		}
		return this;
	}	
	
	/***
	 * ִ��JSON-RPC����ķ�����������JSON��ʽ�Ľ��
	 * @param szParm
	 * @return
	 */
	public String ExecObjectMethod(HttpServletRequest request, String szParm)
	{
		try {
			szParm = Tools.decodeUnicodeHtm(szParm);
			JSONObject oJson = new JSONObject(szParm);
			String szName = oJson.getString("id"), 
			       szMeshod = oJson.getString("method");
			JSONArray oParams = (JSONArray)oJson.get("params");
			// ��ȡ����Ķ���
			Object o = getObject(szName);
			
			if(null != o)
			{
				int nParentHashCode = o.hashCode();
				// ��ȡ����Ķ�������
				Object oParent = this.getParentObject(nParentHashCode);
				if(null != oParent)
					nParentHashCode = oParent.hashCode();
				else oParent = o;
				
				// �����Ҫ���ͷŶ����ڴ���Դ
				if("release".equals(szMeshod))
				{
					// �Ƴ�����ע����Ϣ
					removeObject(oParent.hashCode());
					Iterator oIt = topNms.entrySet().iterator();
					while(oIt.hasNext())
					{
						Map.Entry oKey = (Map.Entry)oIt.next();
						if(szName.equals(oKey.getValue()))
						{
							topNms.remove(oKey.getKey());
							break;
						}
					}
					// �Ƴ���������ע����Ϣ
					removeParentRegInfo(oParent.hashCode());
					return "true";
				}
				
				Class c = o.getClass();
				// ��ȡ����ķ����б�
				Method []m = c.getMethods();
				
				// ע�� reqeust ���� start
				IJsonRpcObject json = null;
				if(Tools.isInterface(o.getClass(), IJsonRpcObject.class.getName()))
				{
					json =(IJsonRpcObject)o;
					json.setRequest(request);
				}
				// ע�� reqeust ���� end
				
				// ���ﲻ�ܲ���getSpecifyNameMethod��ȡ������ԭ���ǣ���Ϊ���������и��϶���
				for(int i = 0; i < m.length; i++)
				{
					// ������ƥ�䣬��������Ҳ����ͬʱƥ�䣬�Ž���ִ��
					if(szMeshod.equals(m[i].getName()) && oParams.length() == m[i].getParameterTypes().length)
					{
						try {
							// �������
							Object []aParam = new Object[oParams.length()];
							// ������������
							Class []oTyps = m[i].getParameterTypes();
							// �����������
							for(int j = 0; j < aParam.length; j++)
							{
								// ������Ͳ�ƥ�䣬�ͽ���һϵ��ת��
								// �����������ڽ���ת��
								aParam[j] = Tools.convertObject(oTyps[j], aParam[j] = oParams.get(j));
							}
							oTyps = null;
							oParams = null;
							Object oRst = null;
							try
							{
								oRst = m[i].invoke(o, aParam);
							} catch (Exception e) 
							{
								if(null != json)
								{
									String szErrMsg = e.getMessage();
									if(null == szErrMsg && null != e.getCause())
										szErrMsg = e.getCause().getMessage();
									json.setErrMsg(szErrMsg);
									szErrMsg = null;
								}
							}
							aParam = null;
							if(null != oRst)
							{
								// ���Ǽ����;�ע����
								if(!Tools.isSimpleType(oRst))
								{
									// ���ö�������
									registerObject(oRst.hashCode(), oRst).registerParentObject(oRst.hashCode(), nParentHashCode);
								}
								String szOut = new ObjectToJSON(oRst, this).toJSON(null);
								return szOut;
							}
							return "true";
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		} catch (ParseException e) {
		}
		return "false";
	}
	
	/***
	 * ����ע��·����ȡע���������Ҳ����ͷ���null
	 * @param szKeyName
	 * @return
	 */
	public Object getObject(String szKeyName)
	{
		return globalMap.get(szKeyName);
	}
	
	/***
	 * ����session����
	 * @param session
	 * @return
	 */
	public JSONRPCBridge setSession(HttpSession session) {
		this.session = session;
		return this;
	}

}
