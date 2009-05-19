package jcore.jsonrpc.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcore.jsonrpc.tools.Tools;

/***
 * ����ת��Ϊjson�Ĵ���ʽ
 * @author ����
 *
 */
public class ObjectToJSON implements Serializable{
	private static final long serialVersionUID = 764043683337733449L;
	private Object o = null;
	private JSONRPCBridge brige = null;
	
	/***
	 * ������objת��Ϊ
	 * @param obj   ��Ҫת���Ķ���
	 * @param szCurRegPath ��ת�������ע����·��
	 * @param brige  �ŽӶ���
	 */
	public ObjectToJSON(Object obj, JSONRPCBridge brige)
	{
		this.o = obj;
		this.brige = brige;
	}
	
	/***
	 * ���ַ�������Ҫת����ַ�����ת�壬���磺",\r\n\t\f\b�����ַ���
	 * @param string
	 * @return
	 */
	private static String quote(String string) {
	        if (null == string || 0 == string.length()) 
	            return "\"\"";

	        char         c;
	        int          i;
	        int          len = string.length();
	        StringBuffer sb = new StringBuffer(len + 4);
	        String       t;

	        sb.append('"');
	        for (i = 0; i < len; i += 1) {
	            c = string.charAt(i);
	            switch (c) {
	            case '\\':
	            case '"':
	            case '/':
	                sb.append('\\');
	                sb.append(c);
	                break;
	            case '\b':
	                sb.append("\\b");
	                break;
	            case '\t':
	                sb.append("\\t");
	                break;
	            case '\n':
	                sb.append("\\n");
	                break;
	            case '\f':
	                sb.append("\\f");
	                break;
	            case '\r':
	                sb.append("\\r");
	                break;
	            default:
	                if (c < ' ' || c >= 128) {
	                    t = "000" + Integer.toHexString(c);
	                    sb.append("\\u" + t.substring(t.length() - 4));
	                } else {
	                    sb.append(c);
	                }
	            }
	        }
	        sb.append('"');
	        return sb.toString();
	}
	
	
	/***************************************************************************
	 * ���ض����JSON��ʽ
	 */
	public String toJSON(String szObjName)
	{
		StringBuffer buf = new StringBuffer();
		String szSimpleTypeReg = "^(boolean|char|byte|short|int|long|float|double)$";
		String szSimpleArrTypeReg = "^class \\[([A-Z])";
		Pattern p = Pattern.compile(szSimpleTypeReg);
		Pattern pa = Pattern.compile(szSimpleArrTypeReg); 
		int nPos = 0;
		try
		{
			Class c = o.getClass();
			String szClassName = c.getName();
			// ����Ĵ���
			Pattern pSz = Pattern.compile("^\\[L.+$");
			
			// �������Ĵ���
			if(szClassName.equals("java.lang.String"))
				return buf.append(quote(o.toString())).toString();
			else if(szClassName.equals("java.lang.Object"))
				return buf.append(quote(o.toString())).toString();
			else if(szClassName.equals("java.util.Date") || szClassName.equals("java.sql.Timestamp"))
			{
				Date oDate = (Date)o;
				int m = oDate.getMonth() + 1;
				// ������Ҫ����JavaScript Date���ͣ��ͽ������ע�ʹ�
				// return buf.append("new Date(").append(((Date)o).getTime()).append(")").toString();

				buf.append("'").append(oDate.getYear() + 1900)
				.append("-").append(9 < m ? "" + m : "0" + m)
				.append("-").append(9 < oDate.getDay() ? "" + oDate.getDay(): "0" + oDate.getDay());
				if(0 < oDate.getHours() && 0 < oDate.getMinutes())
					buf.append(" ").append(9 < oDate.getHours() ? "" + oDate.getHours(): "0" + oDate.getHours())
				.append(":").append(9 < oDate.getMinutes() ? "" + oDate.getMinutes(): "0" + oDate.getMinutes())
				.append(":").append(9 < oDate.getSeconds() ? "" + oDate.getSeconds(): "0" + oDate.getSeconds())
				.append(".").append("000");
				buf.append("'");
				return buf.toString();
			}
			// �����͵Ķ����װ��
			else if(szClassName.equals("java.lang.Boolean"))
				return buf.append(((Boolean)o).booleanValue()).toString();
			else if(szClassName.equals("java.lang.Character"))
				return buf.append("'").append(((Character)o).charValue()).append("'").toString();
			else if(szClassName.equals("java.lang.Short"))
				return buf.append(((Short)o).shortValue()).toString();
			else if(szClassName.equals("java.lang.Integer"))
				return buf.append(((Integer)o).intValue()).toString();
			else if(szClassName.equals("java.lang.Long"))
				return buf.append(((Long)o).longValue()).toString();
			else if(szClassName.equals("java.lang.Float"))
				return buf.append(((Float)o).floatValue()).toString();
			else if(szClassName.equals("java.lang.Double"))
				return buf.append(((Double)o).doubleValue()).toString();
			else if(szClassName.equals("java.math.BigDecimal"))
				return buf.append(((BigDecimal)o).doubleValue()).toString();
			// �ӿ���Map
			else if(Tools.isInterface(o.getClass(), "java.util.Map"))
			{
				Iterator mapIt = ((Map)o).entrySet().iterator();
				int i = 0;
				while(mapIt.hasNext())
				{
					Map.Entry entry = (Map.Entry)mapIt.next();
					if(null != entry.getValue())
					{
						if(0 < i)
							buf.append(",");
						buf.append("\"").append(entry.getKey()).append("\":").append(new ObjectToJSON(entry.getValue(), brige).toJSON(null));
						i++;
					}
				}
				if(null != szObjName)
				{
					if(1 < buf.length())buf.append(",");
				    buf.append("_name_:\"").append(szObjName).append("\"");
				}
				if(1 < buf.length())buf.append(",");
				buf.append("\"_id_\":\"").append(this.o.hashCode()).append("\"");
				return "{" + buf.append("}").toString();
			}
			// �ӿ���List
			else if(Tools.isInterface(o.getClass(), "java.util.List"))
			{
				List lst = (List)o;
				for(int i = 0, x = 0, j = lst.size(); i < j; i++)
				{
					if(null != lst.get(i))
					{
						if(0 < x)
							buf.append(",");
						buf.append(new ObjectToJSON(lst.get(i), brige).toJSON(null));
						x++;
					}
				}
				
				return "[" + buf.append("]").toString();
			}
			// ����Ĵ���
			else if(pSz.matcher(szClassName).find())
			{
				Object []tmp09 = (Object [])this.o;
    	    	if(0 < tmp09.length)
    	    	{
    	    		if(null != tmp09[0])
    	    	    buf.append(new ObjectToJSON(tmp09[0], brige).toJSON(null));
	    	    	for(int j = 1; j < tmp09.length; j++)
	    	    	{
	    	    		if(null != tmp09[j])
	    	    			buf.append(",").append(new ObjectToJSON(tmp09[j], brige).toJSON(null));
	    	    	}
    	    	}
    	    	return "[" + buf.append("]").toString();
			}
			// ������������϶��󣬾Ͷ��䷴�䲢�����䷽����Ϣ��������Ϣ
			if(null != brige)
				brige.registerObject(this.o.hashCode(), this.o);
			// ��Ա�����Ĵ���
			Method []oMs = c.getMethods();
			// ������Ӧ������Ҫ���ˣ�������Щ���������
			// "main","getClass","wait","wait","wait","equals","toString","notify","notifyAll"
			if(0 < oMs.length)
			{
				buf.append("\"methods\":[");
				String szFlt = "(notifyAll)|(getClass)|(wait)|(wait)|(equals)|(notify)|(main)|(hashCode)|(toString)";
				Map mMTmp = new HashMap();
				for(int i = 0, k = 0; i < oMs.length; i++)
				{
					String szName = oMs[i].getName();
					if(0 < szName.replaceAll(szFlt, "").length())
					{
						// ͬ���ķ�����ֻ���һ��
						if(null != mMTmp.get(szName))
							continue;
						if(0 < k)
							buf.append(",");
					    buf.append("\"").append(szName).append("\"");
					    mMTmp.put(szName, (k++) + "");
					}
				}
				// �ͽ���������ʹ�����ù�ϵ���������������ն���ʹ�õ��ڴ�
				mMTmp = null;
				buf.append("]");
				nPos++;
			}
			
			// ��Ա�����Ĵ���
			Field []f = c.getDeclaredFields(); // c.getFields();
			if(0 < f.length)
			{
				String szFlter = "(serialVersionUID)";
				for(int i = 0; i < f.length; i++)
				{
					f[i].setAccessible(true);
					// �������public�ľͼ�����һ�ֵĴ���
//					if (!Modifier.isPublic(f[i].getModifiers()))
//		                continue;
					// ������
					if("request".equals(f[i].getName()) || 0 == f[i].getName().replaceAll(szFlter, "").length())
						continue;
					// ������ǵ�һ��
					if(0 < nPos)buf.append(",");
				    buf.append("\"").append(f[i].getName()).append("\":");
				    // ����
				    String szType = f[i].getType().toString();
				    // ֵ
				    Object oValue = f[i].get(o);
				    
				    // ���Ϊnull
				    if(null == oValue)
				    	buf.append("null");
				    else
				    {
					    // ���������
					    Matcher m = pa.matcher(szType);
					    if(m.find()) 
					    {
					    	buf.append("[");
					    	// L��java�������飬�����Ǽ����͵�����
					    	switch(m.group(1).charAt(0))
					    	{
					    	    case 'Z': // boolean []
					    	    	boolean []tmp01 = (boolean [])oValue;
					    	    	if(0 < tmp01.length)
					    	    	{
						    	    	buf.append(tmp01[0]);
						    	    	for(int j = 1; j < tmp01.length; j++)
						    	    		buf.append(",").append(tmp01[j]);
					    	    	}
					    	    	break;
					    	    case 'B': // byte []
					    	    	byte []tmp02 = (byte [])oValue;
					    	    	if(0 < tmp02.length)
					    	    	{
						    	    	buf.append(tmp02[0]);
						    	    	for(int j = 1; j < tmp02.length; j++)
						    	    		buf.append(",").append(tmp02[j]);
					    	    	}
					    	    	break;
					    	    case 'C': // char []
					    	    	char []tmp03 = (char [])oValue;
					    	    	if(0 < tmp03.length)
					    	    	{
						    	    	buf.append("'").append(tmp03[0]).append("'");
						    	    	for(int j = 1; j < tmp03.length; j++)
						    	    		buf.append(",'").append(tmp03[j]).append("'");
					    	    	}
					    	    	break;
					    	    case 'I': // int []
					    	    	int []tmp04 = (int [])oValue;
					    	    	if(0 < tmp04.length)
					    	    	{
						    	    	buf.append(tmp04[0]);
						    	    	for(int j = 1; j < tmp04.length; j++)
						    	    		buf.append(",").append(tmp04[j]);
					    	    	}
					    	    	break;
					    	    case 'S': // short []
					    	    	short []tmp05 = (short [])oValue;
					    	    	if(0 < tmp05.length)
					    	    	{
						    	    	buf.append(tmp05[0]);
						    	    	for(int j = 1; j < tmp05.length; j++)
						    	    		buf.append(",").append(tmp05[j]);
					    	    	}
					    	    	break;
					    	    case 'J': // long []
					    	    	long []tmp06 = (long [])oValue;
					    	    	if(0 < tmp06.length)
					    	    	{
						    	    	buf.append(tmp06[0]);
						    	    	for(int j = 1; j < tmp06.length; j++)
						    	    		buf.append(",").append(tmp06[j]);
					    	    	}
					    	    	break;
					    	    case 'F': // float []
					    	    	float []tmp07 = (float [])oValue;
					    	    	if(0 < tmp07.length)
					    	    	{
						    	    	buf.append(tmp07[0]);
						    	    	for(int j = 1; j < tmp07.length; j++)
						    	    		buf.append(",").append(tmp07[j]);
					    	    	}
					    	    	break;
					    	    case 'D': // double []
					    	    	double []tmp08 = (double [])oValue;
					    	    	if(0 < tmp08.length)
					    	    	{
						    	    	buf.append(tmp08[0]);
						    	    	for(int j = 1; j < tmp08.length; j++)
						    	    		buf.append(",").append(tmp08[j]);
					    	    	}
					    	    	break;
					    	    case 'L': // ������������
					    	    	Object []tmp09 = (Object [])oValue;
					    	    	if(0 < tmp09.length)
					    	    	{
					    	    		if(null != tmp09[0])
					    	    		buf.append(new ObjectToJSON(tmp09[0], brige).toJSON(null));
						    	    	for(int j = 1; j < tmp09.length; j++)
						    	    	{
						    	    		if(null != tmp09[j])
						    	    		buf.append(",").append(new ObjectToJSON(tmp09[j], brige).toJSON(null));
						    	    	}
					    	    	}
					    	    	break;
					    	}
					    	buf.append("]");
					    }
					    else
					    {
						    // ����Ǽ�����
						    Matcher m1 = p.matcher(szType);
						    // m.reset();
						    if(m1.find())
						    {
						    	// ��ȡ�������ͱ�����ֵ
						    	buf.append(oValue);
						    }
						    // ��������
						    else
						    {
						    	// ��ֹʹ���߲�֪��������ע��һ����ʵ������class�ļ����¶�ջ���
						    	if("sun.reflect.ReflectionFactory".equals(oValue.getClass().getName()))
						    		buf.append("null");
						    	else
						    		buf.append(new ObjectToJSON(oValue, brige).toJSON(null));
						    }
					    }
				    }
				    nPos++;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(null != szObjName)
		{
			if(1 < buf.length())buf.append(",");
		    buf.append("_name_:\"").append(szObjName).append("\"");
		}
		if(1 < buf.length())buf.append(",");
		buf.append("\"_id_\":\"").append(this.o.hashCode()).append("\"");
		return "{" + buf.append("}").toString();
	}

}
