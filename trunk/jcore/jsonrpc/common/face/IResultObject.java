package jcore.jsonrpc.common.face;

public interface IResultObject {

	/***
	 * ��ȡ�����쳣��Ϣ
	 * @return
	 */
	public String getErrMsg();
	
	/***
	 * �����쳣������Ϣ���������쳣��ʱ�����Զ�ץȡ�쳣��Ϣ��ͨ����ע��
	 * @param s
	 */
	public void setErrMsg(String s);
}
