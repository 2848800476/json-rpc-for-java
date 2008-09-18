package jcore.jsonrpc.humanity;

import java.io.Reader;

import jcore.jsonrpc.common.JsonRpcObject;
import jcore.jsonrpc.common.ResultObject;
import jcore.jsonrpc.tools.Tools;

/***
 * ��ȡJS����
 * @author ����
 *
 */
public class LoadJsObj extends JsonRpcObject{
	private static final long serialVersionUID = -1988214985561562945L;
	/***
	 * ͨ��js��������ȡ�������ִ�Сд
	 * @param szName
	 * @return
	 * @throws Exception
	 */
	public ResultObject getJsObj(String szName) 
	{
		ResultObject oRst = new ResultObject();
		Reader f = null;
		try
		{
			f = Tools.getResourceAsReader("jcore/jsonrpc/humanity/" + szName + ".js");
			if(null != f)
			{
				StringBuffer buf = new StringBuffer();
				char []b = new char[1024];
				int j = 0;
				while(1024 == (j = f.read(b, 0, 1024)))
				{
					buf.append(b, 0, j);
				}
				if(0 < j)
					buf.append(b, 0, j);				
				oRst.setResult(new String(buf.toString().getBytes(), "UTF-8"));
			}
			else oRst.setErrMsg("ָ���Ķ��󲻴��ڣ���ȷ�ϴ�Сд�Ƿ���ȷ��");
			log.debug(szName + " Ok!");
		}catch(Exception e)
		{
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
