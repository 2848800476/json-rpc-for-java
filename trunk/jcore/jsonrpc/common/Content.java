package jcore.jsonrpc.common;

/***
 * ��������
 * @author summer
 *
 */
public class Content {
	public static final String RegSessionJSONRPCName = "Reg_Session_JSONRPC_Name";
	
	/***
	 * javaScript��������ת���������е���ת��Ϊ\u3434�����ĸ�ʽ
	 * @param szIn
	 * @return
	 */
	public static String JS(String szIn) {
		if (null == szIn)
			return null;
		char[] arrChar = szIn.trim().toCharArray();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < arrChar.length; i++) {
			if((0x7F < arrChar[i] || 0xa > arrChar[i]) && 9 != arrChar[i])
				buf.append("\\u").append(Integer.toHexString((int)arrChar[i]));
			else
				buf.append(arrChar[i]);
		}
		return buf.toString();
	}

}
