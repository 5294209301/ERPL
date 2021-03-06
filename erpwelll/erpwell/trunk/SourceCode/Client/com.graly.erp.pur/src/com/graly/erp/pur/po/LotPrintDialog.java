package com.graly.erp.pur.po;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotPrintDialog extends ExtendDialog {
	private static final Logger logger = Logger.getLogger(LotPrintDialog.class);
	private int MIN_DIALOG_WIDTH = 250;
	private int MIN_DIALOG_HEIGHT = 150;
	protected ManagedForm managedForm;
	private Button printAll;
	private Button printSelect;
	private Button printFromSelect;
	private Text txtPrintNums;
	private Text qtyTxt;
	private Button doublePrint;
	private int repeatTime = 1, printNums = -1;;
	protected int selectRadio = 1;
	
	protected List<Lot> lots;
	protected Lot selectLot;
	
	public LotPrintDialog(List<Lot> lots,  Lot selectLot) {
		super();
		this.lots = lots;
		this.selectLot = selectLot;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		 	setTitleImage(SWTResourceCache.getImage("entity-dialog"));
			setTitle(Message.getString("bas.lot_print"));
	        Composite composite = (Composite) super.createDialogArea(parent);

	        FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
			ScrolledForm sForm = toolkit.createScrolledForm(composite);
			sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			managedForm = new ManagedForm(toolkit, sForm);
			Composite body = sForm.getForm().getBody();
			configureBody(body);
			
	        Composite client = toolkit.createComposite(body, SWT.NONE);
	        client.setLayout(new GridLayout(4, false));
	        client.setLayoutData(new GridData(GridData.FILL_BOTH));
	        
	        printAll = toolkit.createButton(client, "", SWT.RADIO);
	        printAll.setLayoutData(new GridData(GridData.CENTER));
	        Label lblPrintAll = toolkit.createLabel(client, Message.getString("bas.print_all"));
	        lblPrintAll.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, true, 3, 1));
	        printSelect = toolkit.createButton(client, "", SWT.RADIO);
	        printSelect.setLayoutData(new GridData(GridData.CENTER));
	        Label lblPrintSelect = toolkit.createLabel(client, Message.getString("bas.print_select"));
	        lblPrintSelect.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, true, 3, 1));
	        printFromSelect = toolkit.createButton(client, "", SWT.RADIO);
	        printFromSelect.setLayoutData(new GridData(GridData.CENTER));
	        toolkit.createLabel(client, Message.getString("bas.print_form_select"));
	        txtPrintNums = toolkit.createText(client, "");
	        txtPrintNums.setLayoutData(new GridData(GridData.CENTER));
	        toolkit.createLabel(client, Message.getString("common.item"));
	        
	        
	        final Composite client2 = toolkit.createComposite(body, SWT.NONE);
	        client2.setLayout(new GridLayout(2, false));
	        client2.setLayoutData(new GridData(GridData.FILL_BOTH));
	        
	        Label separator = toolkit.createSeparator(client2, SWT.HORIZONTAL | SWT.SEPARATOR);
	        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	        gd.horizontalSpan=2;
			separator.setLayoutData(gd);
	        
	        toolkit.createLabel(client2, Message.getString("wip.lot_print_qty"));
	        qtyTxt = toolkit.createText(client2, repeatTime + "", SWT.NONE);
			qtyTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        qtyTxt.setTextLimit(9);
	        doublePrint = toolkit.createButton(client2, "˫�ߴ�ӡ", SWT.CHECK);
	        doublePrint.setSelection(true);
	        doublePrint.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, true, 2, 1));
	        return composite;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		try {
			if (IDialogConstants.OK_ID == buttonId) {
				if(printSelect.getSelection()){
					selectRadio = 2;
				} else if(printFromSelect.getSelection()){
					selectRadio = 3;
					if(!((txtPrintNums.getText() == null) || (txtPrintNums.getText().trim().length() == 0))){
						if (!ValidatorFactory.isValid("Integer", txtPrintNums.getText()) || Integer.parseInt(txtPrintNums.getText()) <= 0) {
							setErrorMessage(Message.getString("common.input_error"));
							printNums = -2;
							return;
						}
						printNums = Integer.parseInt(txtPrintNums.getText());
					} else {
						printNums = -1;
					}
				}
				if(!((qtyTxt.getText() == null) || (qtyTxt.getText().trim().length() == 0))) {
					if (!ValidatorFactory.isValid("Integer", qtyTxt.getText())) {
						setErrorMessage(Message.getString("common.input_error"));
						return;
					}
					repeatTime = Integer.parseInt(qtyTxt.getText());
				}
				
				switch(this.getSelectRadio()){
					case 1:
						break;
					case 2:
						if (this.selectLot != null){
							List<Lot> selectLots = new ArrayList<Lot>();
							selectLots.add(selectLot);
							lots = selectLots;
						} else {
							lots = null;
						}
						break;
					case 3:
						if (this.selectLot != null) {
							List<Lot> splitList = new ArrayList<Lot>();
							int index = lots.indexOf(selectLot);
							if (printNums == -1) {
								splitList = lots.subList(index, lots.size());
							} else if (index + printNums > lots.size()) {
								splitList = lots.subList(index, lots.size());
							} else {
								splitList = lots.subList(index, index + printNums);
							}
							lots = splitList;
						}else {
							lots = null;
						}
					break;
				}
				if(lots == null){
					UI.showWarning(Message.getString("bas.lot_is_null"));
					return;
				}
				if(lots != null && lots.size() != 0){
					LotPrintProgressDialog progressDialog = new LotPrintProgressDialog(UI.getActiveShell());
					LotPrintProgerss progress = new LotPrintProgerss(lots, getRepeatTime(), doublePrint.getSelection());
					progressDialog.run(true, true, progress);
					if (progress.isFinished()) {
						UI.showInfo(Message.getString("bas.lot_print_finished"));
					}
				}
				
				okPressed();
			} else if (IDialogConstants.CANCEL_ID == buttonId) {
				cancelPressed();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}

	public int getSelectRadio() {
		return selectRadio;
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public int getPrintNums() {
		return printNums;
	}

	public void setPrintNums(int printNums) {
		this.printNums = printNums;
	}
}
