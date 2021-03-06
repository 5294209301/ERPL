package com.graly.erp.wip.virtualhouse;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class ByLotVirtualHouseDialog extends InClosableTitleAreaDialog  {
	private static final Logger logger = Logger.getLogger(ByLotVirtualHouseDialog.class);
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 450;
	private String tableName = "VirtualHouseLineLot";
	
	protected ADTable table;
	protected ManagedForm managedForm;
	protected ByLotVirtualHouseSection virtualHouseLotSection;

	protected List<MovementLine> lines;
	public ByLotVirtualHouseDialog(Shell shell) {
		super(shell);
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        getADTableOfInvLot();		
        setTitleMessage();
        Composite composite = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
        // ´´˝¨LotSection
		createSection(body);
        return composite;
    }
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	protected void setTitleMessage() {
		setTitle(Message.getString("inv.barcode_manager"));
	}
	
	protected void createSection(Composite composite) {
		virtualHouseLotSection = new ByLotVirtualHouseSection(table, this);
		virtualHouseLotSection.createContents(managedForm, composite);
	}

	public ByLotVirtualHouseSection getVirtualHouseLotSection() {
		return virtualHouseLotSection;
	}

	public void setVirtualHouseLotSection(
			ByLotVirtualHouseSection virtualHouseLotSection) {
		this.virtualHouseLotSection = virtualHouseLotSection;
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
    	createButton(parent, IDialogConstants.CANCEL_ID,
    			Message.getString("common.exit"), false);
    }
	
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	protected ADTable getADTableOfInvLot() {
		try {
			if(table == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTable(0L, getADTableName());
			}
			return table;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getADTableName() {
		return this.tableName;
	}
}
