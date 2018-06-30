package com.graly.erp.inv.in.mo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.MovementIn.InType;
import com.graly.erp.inv.model.MovementOut.OutType;
import com.graly.erp.inv.transfer.TrsQtySetupDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;
/**
 * @author Jim
 * 实现按批次出库，直接输入批次，保存成功后生成出库单
 */
public class MoLLotOutSection extends moLineLotSection {
	private static final Logger logger = Logger.getLogger(MoLLotOutSection.class);
	protected HashMap<String, BigDecimal> headerLabels;
	protected RefTableField outWhField, outTypeFiled;
	protected MovementIn in;   //  WIPMovementIn
	protected static final String ADTABLE_WIP_MOVEMENTIN = "WIPMovementIn";
	public MoLLotOutSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		setEnabled(true);
		section.setTextClient(tBar);
	}

	protected void createParentContent(Composite client, FormToolkit toolkit) {
		try {
			Label label = toolkit.createLabel(client, "", SWT.HORIZONTAL | SWT.SEPARATOR);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	 
	
	protected ADTable getADTableBy(String tableName) throws Exception {
		ADTable adTable = null;
		ADManager adManager = Framework.getService(ADManager.class);
		adTable = adManager.getADTable(0L, tableName);
		return adTable;
	}

	 
	
	@Override
	protected void saveAdapter() {
		try {
			String lotId = txtLotId.getText();
			INVManager invManager = Framework.getService(INVManager.class);
			lot = invManager.getSweepStorageLot(lotId);
				List<MovementLine> lines = createMovementLines();
				MovementIn movementIn = new MovementIn();
				movementIn = saveMovementIn();
			    invManager.saveMovementLotLine(movementIn, lines, getMovementInType(), Env.getUserRrn(),Env.getOrgRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				this.refresh();
				this.setIsSaved(true);
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at ByLotOutSection : saveAdapter() ", e);
		}
	}
	
	protected MovementIn saveMovementIn(){
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			ManufactureOrder mo   = new ManufactureOrder();
			mo.setObjectRrn(lot.getMoRrn());
			mo = (ManufactureOrder) adManager.getEntity(mo);
			MovementIn win = new MovementIn();
			win.setDocStatus(MovementIn.STATUS_DRAFTED);
			win.setMoId(mo.getDocId());
			win.setMoRrn(mo.getObjectRrn());
			win.setMovementLines(lines);
			win.setWarehouseRrn(151043L);
			return win;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	protected InType getMovementInType() {
		return MovementIn.InType.WIN;
	}
	
	protected List<MovementLine> createMovementLines() throws Exception {
		List<MovementLine> lines = new ArrayList<MovementLine>();
		
		List<Long> materialRrns = new ArrayList<Long>();
		List<MovementLineLot> lineLots = null;
		BigDecimal total = BigDecimal.ZERO;
		int i = 1;
		for(MovementLineLot lineLot : this.getLineLots()) {
			if(materialRrns.contains(lineLot.getMaterialRrn()))
				continue;
			
			lineLots = new ArrayList<MovementLineLot>();
			total = BigDecimal.ZERO;
			for(MovementLineLot tempLineLot : getLineLots()) {
				if(tempLineLot.getMaterialRrn().equals(lineLot.getMaterialRrn())) {
					lineLots.add(tempLineLot);
					total = total.add(tempLineLot.getQtyMovement());
				}
			}
			materialRrns.add(lineLot.getMaterialRrn());
			if(lineLots.size() > 0) {
				lines.add(generateLine(lineLots, total, i * 10));
				i++;
			}
		}
		return lines;
	}
	 
//	// 单价和行总价没有，movementRrn, movementId, movementLineRrn在后台设置
	protected MovementLine generateLine(List<MovementLineLot> lineLots,BigDecimal qtyOut, int lineNo) throws Exception {
		MovementLine line = null;
		// 如果为再次保存，则根据物料找到已保存的出库单行，重新赋给lineLots, qtyOut
		if(in != null && in.getObjectRrn() != null) {
			MovementLineLot lineLot = lineLots.get(0);
			for(MovementLine movementLine : in.getMovementLines()) {
				if(lineLot.getMaterialRrn().equals(movementLine.getMaterialRrn())
						&& movementLine.getObjectRrn() != null) {
					movementLine.setMovementLots(lineLots);
					movementLine.setQtyMovement(qtyOut);
					return movementLine;
				}
			}
		}
		// 否则重新建个入库单行
		line = new MovementLine();;
		line.setOrgRrn(Env.getOrgRrn());
		line.setLineStatus(MovementTransfer.STATUS_DRAFTED);
		line.setLineNo(new Long(lineNo));
		line.setQtyMovement(qtyOut);
		
		line.setMovementLots(lineLots);
		MovementLineLot lineLot = lineLots.get(0);
		line.setMaterialId(lineLot.getMaterialId());
		line.setMaterialName(lineLot.getMaterialName());
		line.setMaterialRrn(lineLot.getMaterialRrn());
		ADManager adManager = Framework.getService(ADManager.class);
		Material material = new Material();
		material.setObjectRrn(lineLot.getMaterialRrn());
		material = (Material)adManager.getEntity(material);
		line.setUomId(material.getInventoryUom());
		return line;
	}
	
	protected MovementLine isContainsLot(Lot lot) {
		return new MovementLine();
	}
	
	
	
	protected void setErrorMessage(String msg) {
		parentDialog.setErrorMessage(msg);
	}
	
	protected void setEnabled(boolean enabled) {
		this.itemSave.setEnabled(enabled);
		this.itemDelete.setEnabled(enabled);
	}
	
	
	
	@Override
	protected void addLot() {
		String lotId = txtLotId.getText();
		errorLots.clear();
		try {
			if(lotId !=null ){
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getSweepStorageLot(lotId);
				if(lot!= null && lot.getObjectRrn() > 0 && lot.getMoRrn() > 0){
					MoLLotDialog trsQtyDialog = new MoLLotDialog(UI.getActiveShell(),null, lot);
					if(trsQtyDialog.open() == Dialog.OK) {
						MovementLine l = this.isContainsLot(lot);
						MovementLineLot lineLot = pareseMovementLineLot(l, trsQtyDialog.getInputQty(), lot);
						addLineLotToTable(lineLot);
					} else if(trsQtyDialog.open() == Dialog.CANCEL) {
						return;
					}
				}else{
					UI.showInfo("批次或工作令不存在!");
				}
			}
		} catch(Exception e) {
			logger.error("Error at LotMasterSection ：addLot() ", e);
		} 
	}
	
	protected void addLineLotToTable(MovementLineLot lineLot) {
		getLineLots().add(lineLot);						
		refresh();
		setDoOprationsTrue();
	}
	
}
