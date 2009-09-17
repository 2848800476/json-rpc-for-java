package jcore.jsonrpc.humanity;

import java.io.InputStream;
import java.io.Serializable;

import jcore.jsonrpc.common.Content;
import jcore.jsonrpc.common.JsonRpcObject;
import jcore.jsonrpc.common.ResultObject;
import jcore.jsonrpc.common.face.IJsonRpcObject;
import jcore.jsonrpc.tools.Tools;

/***
 * ��ȡJS����
 * @author ����
 *
 */
public class LoadJsObj extends JsonRpcObject implements IJsonRpcObject, Serializable{
	private static final long serialVersionUID = -1988214985561562945L;
	
	/***
	 * ��ȡһ��δע���java���󣬸ö�����Ҫ�̳�
	 * jcore.jsonrpc.common.JsonRpcObject
	 * ��ʵ�ֽӿ�jcore.jsonrpc.common.face.IJsonRpcObject
	 * ����Ĭ�ϵĹ��캯��
	 * @param szClassPathName
	 * @return
	 */
	public Object getRpcObj(String szClassPathName)
	{
		try
		{
			return (Class.forName(szClassPathName).newInstance());
		}catch(Exception e)
		{
			e.printStackTrace();
			log.debug(e);
		}
		return null;
	}
	
	/***
	 * ͨ��js��������ȡ�������ִ�Сд
	 * @param szName
	 * @return
	 * @throws Exception
	 */
	public ResultObject getJsObj(String szName) 
	{
		ResultObject oRst = new ResultObject();
		InputStream f = null;
		try
		{
			String szCharset = "UTF-8"; // UTF-8  GBK
			f = Tools.getResourceAsStream("jcore/jsonrpc/humanity/" + szName + ".js");
			if(null != f)
			{
				StringBuffer buf = new StringBuffer();
				byte []b = new byte[1024];
				int j = 0;
				while(1024 == (j = f.read(b, 0, 1024)))
				{
					buf.append(new String(b, szCharset));
				}
				if(0 < j)
				{
					byte []b1 = new byte[j];
					System.arraycopy(b, 0, b1, 0, j);
					buf.append(new String(b1, szCharset));
				}
				String s = Content.JS(buf.toString().trim());// Content.JS(buf.toString().trim()).replaceAll("\\/\\*[^\\*]+\\*\\/", "");
//				s = Content.JS(s);
				s = s.replaceFirst("^\\\\ufeff", "");
//				s = s.replaceAll("([\\t ]*\\r\\n[\\t ]*)+", "\r\n");
//				s = s.replaceAll("[ \\t]*\\n[\\t ]+", "\n");
//				s = s.replaceAll("([^\\r])\\n", "$1");
				oRst.setResult(s);
			}
			else oRst.setErrMsg("ָ���Ķ��󲻴��ڣ���ȷ�ϴ�Сд�Ƿ���ȷ��");
		}catch(Exception e)
		{
			e.printStackTrace();
			log.debug(e);
			oRst.setErrMsg(e.getMessage());
		}
		finally
		{
			if(null != f)
				try{f.close();}catch(Exception e)
				{
					oRst.setErrMsg(e.getMessage());
				}
			f = null;
		}
		return oRst;
	}
}
