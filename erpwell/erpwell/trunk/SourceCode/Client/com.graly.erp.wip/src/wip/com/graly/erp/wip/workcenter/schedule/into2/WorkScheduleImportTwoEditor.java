package com.graly.erp.wip.workcenter.schedule.into2;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

//�����ų̵���--����Ϊinto��Ϊimport�ǹؼ��ֲ�����
//��Ҫ���ܣ����ݲ�ͬ���䲻ͬ���ڵ���
//
public class WorkScheduleImportTwoEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.schedule.into2.WorkScheduleImportTwoEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new WorkScheduleImportTwoEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}
}