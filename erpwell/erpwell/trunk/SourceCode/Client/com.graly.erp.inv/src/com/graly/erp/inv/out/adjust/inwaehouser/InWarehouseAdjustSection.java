package com.graly.erp.inv.out.adjust.inwaehouser;

import org.eclipse.jface.dialogs.Dialog;

import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.adjust.AdjustOutSection;
import com.graly.erp.inv.out.adjust.ByLotAdjustOutDialog;
import com.graly.erp.inv.out.adjust.ByLotAdjustOutSection;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.UI;

public class InWarehouseAdjustSection extends AdjustOutSection{

	public InWarehouseAdjustSection(EntityTableManager tableManager) {
		super(tableManager);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void outByLotAdapter() {
		InWarehouseAdjustDialog olbd = new InWarehouseAdjustDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
			MovementOut out = ((ByLotAdjustOutSection)olbd.getLotMasterSection()).getMovementOut();
			if(out != null && out.getObjectRrn() != null) {
				this.selectedOut = out;
				if(selectedOut != null && selectedOut.getObjectRrn() != null)
					refreshAdd(selectedOut);
				editAdapter();
			}
		}
	}

}
