package com.graly.erp.inv.workshop.services.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.WarehouseEntityForm;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopServices;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class ServicesStorageLineEntryBlock extends ParentChildEntityBlock {
	private static final Logger logger = Logger.getLogger(ServicesStorageLineEntryBlock.class);
	protected ToolItem itemOptionalLot;
	protected ToolItem itemApprove;
	protected ServicesStorageLineProperties page;
	
	private MovementWorkShopLine selectedWSLine;
	private String FieldName_TargetWarehouse = "targetWarehouseRrn";
	private boolean flag = false;
	
	public static final String KEY_WS_APPROVED = "INV.WorkShopDelivery.Approved";
	
	public ServicesStorageLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable){
		this(parentTable, parentObject, whereClause, childTable, false);
	}
	
	public ServicesStorageLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag){
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);	
		refresh();
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
		setParenObjectStatusChanged();
//		addLocatorListener();
	}
	
	protected void createParentContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : parentTable.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			WarehouseEntityForm itemForm = new WarehouseEntityForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}
	
	protected void createViewAction(StructuredViewer viewer){
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					Object obj = ((StructuredSelection) event.getSelection()).getFirstElement();
					if(obj instanceof MovementWorkShopLine) {
						selectedWSLine = (MovementWorkShopLine)obj;
					} else {
						selectedWSLine = null;
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try{
			ADTable table = getTableManager().getADTable();
			Class<?> klass = Class.forName(table.getModelClass());
			page = new ServicesStorageLineProperties(this, table, getParentObject(), flag);
			detailsPart.registerPage(klass, page);
		} catch (Exception e){
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
	}
	
	protected void setParenObjectStatusChanged() {
		MovementWorkShopServices wsDelivery = (MovementWorkShopServices)parentObject;
		String status = "";
		if(wsDelivery != null && wsDelivery.getObjectRrn() != null) {
			status = wsDelivery.getDocStatus();			
		}
		if(flag){
			itemApprove.setEnabled(false);
		}else if(MovementWorkShopServices.STATUS_APPROVED.equals(status)) {
			itemApprove.setEnabled(false);
		} else if(MovementWorkShopServices.STATUS_DRAFTED.equals(status)) {
			itemApprove.setEnabled(true);
		} else if(MovementWorkShopServices.STATUS_CLOSED.equals(status)) {
			itemApprove.setEnabled(false);
		} else {
			itemApprove.setEnabled(false);
		}
	}
	
	protected void setChildObjectStatusChanged() {
		ServicesStorageLineProperties page = (ServicesStorageLineProperties)this.detailsPart.getCurrentPage();
		page.setStatusChanged(((MovementWorkShopServices)parentObject).getDocStatus());
	}
	
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new ToolItem(tBar, SWT.PUSH);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}
	
 
	
	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			if (parentObject != null) {
				
				MovementWorkShopServices mt = (MovementWorkShopServices)parentObject;
				INVManager invManager = Framework.getService(INVManager.class);
				mt.setTargetWarehouseRrn(0L);
				parentObject = invManager.approveMovementServicesStorage(mt, Env.getUserRrn());
				// 无需用adManager再获得parentObject，是因为itemApprove受状态控制，不能再次审核
				ADManager adManager = Framework.getService(ADManager.class);
				MovementWorkShopServices  parentObject2 = (MovementWorkShopServices) parentObject;
				parentObject = adManager.getEntity((ADBase) parentObject);
//				this.setParentObject(adManager.getEntity((MovementWorkShopDelivery)parentObject));
				UI.showInfo(Message.getString("common.approve_successed"));
				setParenObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void closeAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (parentObject != null) {
				if(UI.showConfirm(Message.getString("common.confirm_repeal"))){
					
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public boolean isEnableByParentObject() {
		MovementWorkShopServices out = (MovementWorkShopServices)this.getParentObject();
		if(out == null) {
			return false;
		}
		String status = out.getDocStatus();
		if(MovementWorkShopServices.STATUS_CLOSED.equals(status)
				|| MovementWorkShopServices.STATUS_APPROVED.equals(status)
				|| MovementWorkShopServices.STATUS_COMPLETED.equals(status)) {
			return false;
		}
		return true;
	}

//	private void addLocatorListener() {
//		IField targetWarehouseField = getIField(FieldName_TargetWarehouse);
//		targetWarehouseField.addValueChangeListener(getTargetWarehouseChangedListener());
//	}
	
	private IField getIField(String fieldId) {
		IField f = null;
		for(Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if(f != null) {
				return f;
			}
		}
		return f;
	}
	
	private IValueChangeListener getTargetWarehouseChangedListener(){
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				if(page != null){
					page.reputLocator(newValue);
				}
			}
		};
	};
}
