package com.graly.framework.base.application;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
    protected void makeActions(IWorkbenchWindow window) {
    }
    
    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
    }
    
    @Override
    protected void fillCoolBar(ICoolBarManager coolBar) {
    }
    
}
