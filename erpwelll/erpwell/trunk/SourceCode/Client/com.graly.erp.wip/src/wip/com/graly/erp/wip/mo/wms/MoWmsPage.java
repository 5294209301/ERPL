package com.graly.erp.wip.mo.wms;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.views.TableListManager;

public class MoWmsPage extends SectionEntryPage {

	public MoWmsPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public MoWmsPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MoWmsSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" receiptType='WIN'  ");
	}
}
