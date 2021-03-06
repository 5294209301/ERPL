package com.graly.promisone.base.entitymanager.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.promisone.base.entitymanager.adapter.EntityItemInput;
import com.graly.promisone.base.entitymanager.forms.QueryForm;
import com.graly.promisone.base.entitymanager.views.TableListManager;
import com.graly.promisone.base.security.usergroup.UserQueryDialog;
import com.graly.promisone.base.ui.forms.field.IField;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.runtime.Framework;

public class SingleQueryDialog extends TitleAreaDialog {
	Logger logger = Logger.getLogger(UserQueryDialog.class);
	
	protected CheckboxTableViewer viewer;
	protected final int SEARCH_OK = 1001;
	protected final int SEARCH_CANCEL = 1002;
	protected Object object;
	protected TableListManager listTableManager;
	protected QueryForm queryForm;
	protected StringBuffer sb = new StringBuffer("");
	protected List<ADBase> selectedItems = new ArrayList<ADBase>();
	protected List<ADBase> exsitedItems;
	protected String whereClause;
	protected int mStyle = SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK;
	
	public SingleQueryDialog(Shell shell) {
		super(shell);
	}
	
	public SingleQueryDialog(Shell parent, TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style){
		this(parent);
		this.listTableManager = listTableManager;
		this.whereClause = whereClause;
		this.mStyle = style;
	}
	
	public SingleQueryDialog(Shell parent, StructuredViewer viewer, Object object) {
		super(parent);
		this.viewer = (CheckboxTableViewer)viewer;
		this.object = object;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle(Message.getString("common.search_Title"));
        setMessage(Message.getString("common.keys"));
        
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        composite.setLayout(gl);
        
        Composite queryComp = new Composite(composite, SWT.NONE);
        queryComp.setLayout(new GridLayout());
        queryComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite resultComp = new Composite(composite, SWT.NONE);
        resultComp.setLayout(new GridLayout());
        resultComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Composite buttonComp = new Composite(resultComp, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalAlignment = GridData.END;
        buttonComp.setLayoutData(gd);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 0;
        buttonComp.setLayout(gridLayout);
        
        queryForm = new QueryForm(queryComp, SWT.NONE, listTableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        Button ok = createButton(buttonComp, SEARCH_OK,
        		Message.getString("common.search"), false);
        Button cancel = createButton(buttonComp, SEARCH_CANCEL,
        		Message.getString("common.clean"), false);
        
        createSearchTableViewer(resultComp);

        ok.addSelectionListener(new SelectionListener() {
        	public void widgetSelected(SelectionEvent e) {
        		createWhereClause();
                refresh(true);
        	}        	
        	public void widgetDefaultSelected(SelectionEvent e) {
        		widgetSelected(e);
        	}        	
        });
        
        cancel.addSelectionListener(new SelectionListener() {
        	public void widgetSelected(SelectionEvent e) {
        		LinkedHashMap<String, IField> fields = queryForm.getFields();
                for(IField f : fields.values()) {
                	f.setValue(null);
                	f.refresh();
                }
        	}        	
        	public void widgetDefaultSelected(SelectionEvent e){
        		widgetSelected(e);
        	}        	
        });
        return composite;
    }
	
	protected void createSearchTableViewer(Composite parent) {
        listTableManager.setStyle(mStyle);
		viewer = (CheckboxTableViewer)listTableManager.createViewer(parent,
				new FormToolkit(Display.getCurrent()));        
	}

	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
	
	protected String validate() {
		return null;
	}
	
	public String getKeys() {
		return sb.toString();
	}
	
	protected void refresh(boolean clearFlag) {
		List<ADBase> l = new ArrayList<ADBase>();
		try {
        	ADManager manager = Framework.getService(ADManager.class);
        	long objectId = listTableManager.getADTable().getObjectId();
            l = manager.getEntityList(Env.getOrgId(), objectId, 
            		Env.getMaxResult(), getKeys(), "");
        } catch (Exception e) {
        	logger.error("Error SingleQueryDialog : refresh() " + e.getMessage(), e);
        }
		if (object instanceof List) {
			exsitedItems = (List)object;
			if (exsitedItems != null) {
				l.removeAll(exsitedItems);
			}
		}
		viewer.setInput(l);			
		listTableManager.updateView(viewer);
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	protected void  createWhereClause() {
		String modelName = listTableManager.getADTable().getModelName() + ".";
		sb = new StringBuffer("");
		if(whereClause != null && !"".equals(whereClause)) {
			sb.append(" ");
			sb.append(whereClause);
			sb.append(" AND ");
		}
		LinkedHashMap<String, IField> fields = queryForm.getFields();
        sb.append(" 1=1 ");
        for(IField f : fields.values()) {
        	if(f.getLabel().endsWith("*")){
        		if(f.getValue() == null)
        			sb.append(" AND 1<>1 ");
        	}
			Object t = f.getValue();
			if (t instanceof Date) {
				Date cc = (Date)t;
				if(cc != null) {
					sb.append(" AND ");
					sb.append("TO_CHAR(");
					sb.append(modelName);
					sb.append(f.getId());
					sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
					sb.append(I18nUtil.formatDate(cc));
					sb.append("'");
				}
			} else if(t instanceof String) {
				String txt = (String)t;
				if(!txt.trim().equals("") && txt.length() != 0) {
					sb.append(" AND ");
					sb.append(modelName);
					sb.append(f.getId());
					sb.append(" LIKE '");
					sb.append(txt);
					sb.append("'");
				}
			} else if(t instanceof Boolean) {
				 Boolean bl = (Boolean)t;
				 sb.append(" AND ");
				 sb.append(modelName);
				 sb.append(f.getId());
				 sb.append(" = '");
				 if(bl) {
					sb.append("Y");
				 } else if(!bl) {
					sb.append("N");
				 }
				 sb.append("'");
			} else {
			}
        }
	}
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			Object[] os = viewer.getCheckedElements();
			if(os.length != 0) {
				for(Object o : os) {
					ADBase adBase = (ADBase)o;
					selectedItems.add(adBase);
					
				}
			}
		}
		super.buttonPressed(buttonId);
	}
	
	public List<ADBase> getSelectionList() {
		return this.selectedItems;
	}

}