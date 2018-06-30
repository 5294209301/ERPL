package com.graly.erp.inv.in.mo;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.out.OutLineLotDialog;

public class MoLLotOutDialog extends MoLotDialog {
	
	public MoLLotOutDialog(Shell shell) {
		super(shell);
	}
	
	protected void createSection(Composite composite) {
		lotSection = new MoLLotOutSection(table, this);
		lotSection.createContents(managedForm, composite);
	}
}
