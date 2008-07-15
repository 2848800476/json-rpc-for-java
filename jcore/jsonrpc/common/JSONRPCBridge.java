package jcore.jsonrpc.common;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/***
 * 
 * @author ����
 *
 */
public class JSONRPCBridge implements Serializable{
	private static final long serialVersionUID = 1L;
	private HttpSession session = null;
	// ע���еĶ���
	private Map globalMap = Collections.synchronizedMap(new HashMap());
	
	// ���涥���ı�ע������JSON��ʽ
	private Map cache = Collections.synchronizedMap(new HashMap());
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
	 * ע�����
	 * @param nHashCodeName ����hashcodeע����󣬷�ֹͬһʵ��ע����
	 * @param o
	 * @return this
	 */
	public JSONRPCBridge registerObject(int nHashCodeName, Object o)
	{
		String szKeyName = nHashCodeName + "";
		if(null != session && null != szKeyName && 0 < szKeyName.trim().length())
		{
			if(null == globalMap.get(szKeyName))
			{
				// ע��
				globalMap.put(szKeyName, o);
			}
			// �ٴε��ã��Ա㼯Ⱥ����������������
			session.setAttribute(Content.RegSessionJSONRPCName, this);
		}
		return this;
	}
	
	public JSONRPCBridge registerObject(String szKeyName, Object o)
	{
		if(null != session && null != szKeyName && 0 < szKeyName.trim().length())
		{
			if(null == topNms.get(szKeyName))
				topNms.put(szKeyName, o.hashCode() + "");
			return registerObject(o.hashCode(), o);
		}
		return this;
	}
	
	/***
	 * �ж϶���o�Ƿ���Ϊ����Ҫע���"��"����
	 * @param o
	 * @return
	 */
	private boolean isSimpleType(Object o)
	{
		if(null == o)return false;
		String szType = o.getClass().getName();
		Pattern pa = Pattern.compile("^class \\[[ZBCISJFDL]");
		Matcher m = pa.matcher(szType);
		
		if(-1 < ",java.lang.String,java.util.Date,java.sql.Timestamp,java.lang.Boolean,java.lang.Character,java.lang.Short,java.lang.Integer,java.lang.Long,java.lang.Float,java.lang.Double,boolean,char,byte,short,int,long,float,double,".indexOf("," + szType + ",") || m.find())
		{
			// �������ʹ�ù�����ϵ�������ڴ����Ч����
			m = null;
			pa = null;
			szType = null;
			return true;
		}
		// �������ʹ�ù�����ϵ�������ڴ����Ч����
		m = null;
		pa = null;
		szType = null;
		return false;
	}
	
	/***
	 * ִ��JSON-RPC����ķ�����������JSON��ʽ�Ľ��
	 * @param szParm
	 * @return
	 */
	public String ExecObjectMethod(HttpServletRequest request, String szParm)
	{
		try {
			JSONObject oJson = new JSONObject(szParm);
			String szName = oJson.getString("id"), 
			       szMeshod = oJson.getString("method");
			JSONArray oParams = (JSONArray)oJson.get("params");
			
			// ��Map��List��ע��ͻ�ȡ�����⴦������Ϊ�����е�ÿ��Ԫ�ؿ����ǲ�ͬ�Ķ���
			// ���ע�����������ֿ���
			
			Object o = getObject(szName);
			if(null != o)
			{
				
				Class c = o.getClass();
				Method []m = c.getMethods();
				
				// ע�� reqeust ����
				try
				{
					Class cTmp = c.getSuperclass();
					int i = 10;
					Method setReqeust = null;
					while(null == setReqeust && 0 < i--)
					{
						try{setReqeust = cTmp.getDeclaredMethod("setRequest", new Class[]{javax.servlet.http.HttpServletRequest.class});}catch(Exception e){}
						cTmp = cTmp.getSuperclass();
					}
					if(null != setReqeust)
						setReqeust.invoke(o, new Object[]{request});
					setReqeust = null;
				}catch(Exception e){e.printStackTrace();}
				
				for(int i = 0; i < m.length; i++)
				{
					if(szMeshod.equals(m[i].getName()) && oParams.length() == m[i].getParameterTypes().length)
					{
						try {
							// �������
							Object []aParam = new Object[oParams.length()];
							Class []oTyps = m[i].getParameterTypes();
							for(int j = 0; j < aParam.length; j++)
							{
								aParam[j] = oParams.get(j);
								String szNm = oTyps[j].getName();
								if(!szNm.equals(aParam[j].getClass().getName()))
								{
									if(szNm.equals("java.util.Date"))
										aParam[j] = (Object)new Date(Long.parseLong(aParam[j].toString()));
									else if(szNm.equals("java.math.BigDecimal"))
										aParam[j] = (Object)new BigDecimal(aParam[j].toString());
									else if(szNm.equals("java.lang.Float"))
										aParam[j] = (Object)new Float(aParam[j].toString());
								}
							}
							oTyps = null;
							oParams = null;
							Object oRst = m[i].invoke(o, aParam);
							aParam = null;
							if(null != oRst)
							{
								// ���Ǽ����;�ע����
								if(!isSimpleType(oRst))
									registerObject(oRst.hashCode(), oRst);
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