package jcore.jsonrpc.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcore.jsonrpc.common.JSONObject;

public class Tools {

	/***************************************************************************
	 * ͨ��·����ȡFile����
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static File getResourceAsFile(String resource) throws IOException {
		return new File(getResourceURL(resource).getFile());
	}

	public static Reader getResourceAsReader(String resource)
			throws IOException {
		return new InputStreamReader(getResourceAsStream(resource));
	}
	 public static InputStream getResourceAsStream(String resource) throws IOException {
		    InputStream in = null;
		    ClassLoader loader = Tools.class.getClassLoader();
		    if (loader != null) in = loader.getResourceAsStream(resource);
		    if (in == null) in = ClassLoader.getSystemResourceAsStream(resource);
		    if (in == null) throw new IOException("Could not find resource " + resource);
		    return in;
		  }


	/***************************************************************************
	 * ͨ��·����ȡURL����
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static URL getResourceURL(String resource) throws IOException {
		URL url = null;
		ClassLoader loader = Tools.class.getClassLoader();
		if (loader != null)
			url = loader.getResource(resource);
		if (url == null)
			url = ClassLoader.getSystemResource(resource);
		if (url == null)
			url = Tools.class.getResource(resource);

		if (url == null)
			throw new IOException("Could not find resource " + resource);
		return url;
	}
	
	 private static final char c[] = { '<', '>', '&', '\"'};
	 private static final String expansion[] = {"&lt;", "&gt;", "&amp;", "&quot;"};
	 /**
	  * �����е� <, >, &, " ����Ϊhtml�ı�ʾ��ʽ
	  * @param s
	  * @return
	  */
	public static String HTMLEncode(String s) {
	      StringBuffer st = new StringBuffer();
	      for (int i = 0; i < s.length(); i++) {
	          boolean copy = true;
	          char ch = s.charAt(i);
	          for (int j = 0; j < c.length ; j++) {
	            if (c[j]==ch) {
	                st.append(expansion[j]);
	                copy = false;
	                break;
	            }
	          }
	          if (copy) st.append(ch);
	      }
	      return st.toString();
	    }
	
	/**
	 * html��ʽ�Ľ���
	 * @param s
	 * @return
	 */
	public static String HTMLDecode(String s) {
		  if(null == s || 0 == (s = s.trim()).length())return s;
	      return s.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
	    }

	/***************************************************************************
	 * ����html��ʽ��������ĺ��� �����罫�� "&#24322;&#24120;" ����Ϊ "�쳣"
	 * ���ϵĺ���������ʽ��Χ�ǣ�[\u4E00-\u9FA5]
	 * 
	 * @param szStr
	 * @return
	 */
	public static String decodeUnicodeHtm(String szStr) {
		Pattern p = Pattern.compile("&#(\\d+);", Pattern.MULTILINE);
		Matcher m = null;
		try {
			m = p.matcher(java.net.URLDecoder.decode(szStr, "UTF-8"));
		} catch (Exception e) {
			return szStr;
		}
		StringBuffer buf = new StringBuffer();
		if(null != m)
		while (m.find())
			m.appendReplacement(buf, (char) Integer.valueOf(m.group(1))
					.intValue()
					+ "");
		m.appendTail(buf);
		return buf.toString();
	}
	
	/*****************************************************************************
	 * �������ַ���Ϊhtml��ʽ��������ĺ��֣����罫�� "�쳣" ����Ϊ "&#24322;&#24120;"
	 * ���ϵĺ���������ʽ��Χ�ǣ�[\u4E00-\u9FA5]
	 * 
	 * @param szStr
	 * @return
	 */
	public static String encodeUnicodeHtm(String szStr) {
		if (null == szStr || 0 == szStr.trim().length())
			return szStr;
		Pattern p = Pattern.compile("[\u4E00-\u9FA5]", Pattern.MULTILINE);
		Matcher m = p.matcher(szStr);
		StringBuffer buf = new StringBuffer();
		while (m.find())
			m.appendReplacement(buf, "&#" + (int) m.group(0).toCharArray()[0] + ";");
		m.appendTail(buf);
		return buf.toString();
	}
	
	/**
	 * ��s�еĺ���ת��Ϊ\u4E00-\u9FA5��������ʽ
	 * @param s
	 * @return
	 */
	public static String encodeUnicode2Js(String s)
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0, j = s.length(); i < j; i++)
		{
			int n = (int)s.charAt(i);
			// if(0x4E00 <= n && n <= 0x9FA5)
			if(255 < n && n <= 0)
			   buf.append("\\u" + Integer.toHexString(n));
			else buf.append((char)n);
		}
		return buf.toString();
	}
	
	/**
	 * ��s�еĺ���ת��Ϊ\u4E00-\u9FA5��������ʽ
	 * @param s
	 * @return
	 */
	public static String decodeUnicode4Js(String szStr)
	{
		if (null == szStr || 0 == szStr.trim().length())
			return szStr;
		Pattern p = Pattern.compile("\\\\u([0-9A-Fa-f])", Pattern.MULTILINE);
		Matcher m = p.matcher(szStr);
		StringBuffer buf = new StringBuffer();
		while (m.find())
		{
			m.appendReplacement(buf, "" + (char)Integer.parseInt( m.group(0), 16));
		}
		m.appendTail(buf);
		return buf.toString();
	}

	/***************************************************************************
	 * �ж϶���o�Ƿ���Ϊ����Ҫע���"��"����
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isSimpleType(Object o) {
		if (null == o)
			return false;
		String szType = o.getClass().getName();
		Pattern pa = Pattern.compile("^class \\[[ZBCISJFDL]");
		Matcher m = pa.matcher(szType);

		if (-1 < ",java.lang.String,java.util.Date,java.sql.Timestamp,java.lang.Boolean,java.lang.Character,java.lang.Short,java.lang.Integer,java.lang.Long,java.lang.Float,java.lang.Double,boolean,char,byte,short,int,long,float,double,"
				.indexOf("," + szType + ",")
				|| m.find()) {
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

	/***************************************************************************
	 * �ж϶���oʵ�ֵ����нӿ����Ƿ���szInterface 2008-08-07 ������̳����жϽӿڵĹ��ܣ� �Լ������ӿڼ̳к���жϹ���
	 * package test;
	 * 
	 * public interface ITest extends Serializable public interface ITest1
	 * extends ITest public class Test1 implements ITest1 public class Test2
	 * extends Test1 public class Test3 extends Test2
	 * 
	 * isInterface(Test3.class, "java.io.Serializable") = true
	 * isInterface(Test3.class, "test.ITest") = true isInterface(Test3.class,
	 * "test.ITest1") = true
	 * 
	 * @param c
	 * @param szInterface
	 * @return
	 */
	public static boolean isInterface(Class c, String szInterface) {
		if (c.getName().equals(szInterface))
			return true;
		Class[] face = c.getInterfaces();
		for (int i = 0, j = face.length; i < j; i++) {
			if (face[i].getName().equals(szInterface)) {
				return true;
			} else {
				Class[] face1 = face[i].getInterfaces();
				for (int x = 0; x < face1.length; x++) {
					if (face1[x].getName().equals(szInterface)) {
						return true;
					} else if (isInterface(face1[x], szInterface)) {
						return true;
					}
				}
			}
		}
		if (null != c.getSuperclass()) {
			return isInterface(c.getSuperclass(), szInterface);
		}
		return false;
	}

	/***************************************************************************
	 * ��c�л�ȡ��ָ�����ֵķ������󣬲������ͱ������clsParm�������������Դ�super�н�������
	 * 
	 * @param c
	 * @param clsParm
	 * @param szName
	 * @return
	 */
	public static Method getSpecifyNameMethod(Class c, Class[] clsParm,
			String szName) {
		try {
			return c.getDeclaredMethod(szName, clsParm);
		} catch (Exception e) {
		}
		if (!c.getSuperclass().getName().equals("java.lang.Object"))
			return getSpecifyNameMethod(c.getSuperclass(), clsParm, szName);
		return null;
	}

	/***************************************************************************
	 * �ڶ���̳���·��Ѱ��ָ����field����
	 * 
	 * @param c
	 * @param szName
	 * @return
	 */
	public static Field getSpecifyNameField(Class c, String szName) {
		try {
			Field[] f = c.getDeclaredFields();
			for (int i = 0; i < f.length; i++) {
				if (f[i].getName().equals(szName)) {
					f[i].setAccessible(true);
					return f[i];
				}
			}
		} catch (Exception e) {
		}
		if (!c.getSuperclass().getName().equals("java.lang.Object"))
			return getSpecifyNameField(c.getSuperclass(), szName);
		return null;
	}

	/***************************************************************************
	 * ������oValue��convert2TypeName���ͽ���ת��
	 * 
	 * @param convert2TypeName
	 * @param oValue
	 * @return
	 */
	public static Object convertObject(Class convert2TypeName, Object oValue) {
		String szNm = convert2TypeName.getName();
		// ������Ͳ�ƥ�䣬�ͽ���һϵ��ת��
		// �����������ڽ���ת��
		if (null != oValue && !szNm.equals(oValue.getClass().getName())
				&& null != oValue) {
			String s = oValue.toString().trim(), szTmp01 = s.replaceAll(
					"[^\\d\\.\\-]", "");
			// ֧�ֵĲ������͡��������Ͷ�����Ĵ���
			// ��ֹ��Ч��ֵ��ǿ��ת���з����쳣
			try {
				if (szNm.equals("java.util.Date"))
					return new Date(Long.parseLong(szTmp01));
				else if (szNm.equals("java.math.BigDecimal"))
					return new BigDecimal(szTmp01);
				else if (szNm.equals("boolean")
						|| szNm.equals("java.lang.Boolean"))
					return new Boolean(szTmp01);
				else if (szNm.equals("char")
						|| szNm.equals("java.lang.Character"))
					return new Character(s.charAt(0));
				else if (szNm.equals("float") || szNm.equals("java.lang.Float"))
					return new Float(szTmp01);
				else if (szNm.equals("java.lang.Short"))
					return new Short(szTmp01);
				else if (szNm.equals("int") || szNm.equals("java.lang.Integer"))
					return new Integer(szTmp01);
				else if (szNm.equals("long") || szNm.equals("java.lang.Long"))
					return new Long(szTmp01);
				else if (szNm.equals("double")
						|| szNm.equals("java.lang.Double"))
					return new Double(szTmp01);
				else if (szNm.equals("java.lang.String")) {
					return s;
				} else {
					// ���϶���Ĵ���
					if (s.startsWith("{") && s.endsWith("}")) {
						boolean bMap = isInterface(convert2TypeName,
								"java.util.Map");
						// ��ڲ����ķ��϶������ͱ������ܹ�ʵ�����Ķ���
						Object oRst = bMap ? new HashMap() : convert2TypeName
								.newInstance();
						Map map = new JSONObject(s).getHashMap();
						if (bMap)
							return map;
						else {
							// ��������Ĳ���
							Iterator it = map.entrySet().iterator();
							map = null;
							while (it.hasNext()) {
								Map.Entry entry = (Map.Entry) it.next();
								String szKey = (String) entry.getKey();
								if (null != szKey) {
									Field field = getSpecifyNameField(
											convert2TypeName, szKey);
									if (null != field) {
										field.setAccessible(true);
										field.set(oRst, entry.getValue());
									}
									// Ѱ��set����
									else {
										Method mehod = getSpecifyNameMethod(
												convert2TypeName,
												new Class[] { java.lang.Object.class },
												"set"
														+ szKey.substring(0, 1)
																.toUpperCase()
														+ szKey.substring(1));
										// ע������
										if (null != mehod)
											try {
												mehod.invoke(oRst,
														new Object[] { entry
																.getValue() });
											} catch (Exception e) {
											}
									}
									field = null;
								}
								entry = null;
								szKey = null;
							}
							return oRst;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			return oValue;
		return null;
	}
}
