package jcore.jsonrpc.humanity.select;

import java.io.Serializable;
import java.util.List;

public class SelectInputDataObject implements Serializable{
	private static final long serialVersionUID = 7915997089526874810L;
	// ��������
	public List collection;
	// ����༭�ı�־
	public boolean allowEdit;
	// �����ѡ���־
	public boolean multiple;
	// ����ѡ��ʱ��Ϊvalue���ֶ�
	public String valueField;
	// ����Ҫ��ʾ���ֶκ�Ҫ��ʾ��˳�򣬲�������ȫ����ʾ
	public String displayFields;
	/***
	 * ѡ���ʱ��ص����������ã�ͨ����ʽΪ��������
	 * ���������ʽ��function cbk(oRow, o){...}
	 * ����oRowΪѡ���е������ݣ�����û����ʾ���е�����
	 * o��Ϊ��ǰ�������
	 */
	public String selectCallBack;
}
