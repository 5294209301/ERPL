package com.graly.erp.xz.pdm.material;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 行政ERP物料设置
 * */
public class XZMaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.xz.pdm.material.XZMaterialEditor";
	
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new XZMaterialEntryPage(this, "", "");
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