package com.graly.framework.base.ui.forms;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;

public class MDSashForm extends SashForm {
	
	ArrayList sashes = new ArrayList();
	Listener listener = new Listener () {
		public void handleEvent(Event e) {
			switch (e.type) {
			case SWT.MouseEnter:
				e.widget.setData("hover", Boolean.TRUE); //$NON-NLS-1$
				((Control)e.widget).redraw();
				break;
			case SWT.MouseExit:
				e.widget.setData("hover", null); //$NON-NLS-1$
				((Control)e.widget).redraw();
				break;
			case SWT.Paint:
				onSashPaint(e);
			break;
			case SWT.Resize:
				hookSashListeners();
			break;
			}
		}
	};
	public MDSashForm(Composite parent, int style) {
		super(parent, style);
	}
	
	public void layout(boolean changed) {
		super.layout(changed);
		hookSashListeners();
	}
	
	public void layout(Control [] children) {
		super.layout(children);
		hookSashListeners();
	}

	private void hookSashListeners() {
		purgeSashes();
		Control [] children = getChildren();
		for (int i=0; i<children.length; i++) {
			if (children[i] instanceof Sash) {
				Sash sash = (Sash)children[i];
				if (sashes.contains(sash))
					continue;
				sash.addListener(SWT.Paint, listener);
				sash.addListener(SWT.MouseEnter, listener);
				sash.addListener(SWT.MouseExit, listener);
				sashes.add(sash);
			}
		}
	}
	private void purgeSashes() {
		for (Iterator iter=sashes.iterator(); iter.hasNext();) {
			Sash sash = (Sash)iter.next();
			if (sash.isDisposed())
				iter.remove();
		}
	}


	private void onSashPaint(Event e) {
		Sash sash = (Sash)e.widget;
		IManagedForm form = (IManagedForm)sash.getParent().getData("form"); //$NON-NLS-1$
		FormColors colors = form.getToolkit().getColors();
		boolean vertical = (sash.getStyle() & SWT.VERTICAL)!=0;
		GC gc = e.gc;
		Boolean hover = (Boolean)sash.getData("hover"); //$NON-NLS-1$
		gc.setBackground(colors.getColor(IFormColors.TB_BG));
		gc.setForeground(colors.getColor(IFormColors.TB_BORDER));
		Point size = sash.getSize();
		if (vertical) {
			if (hover!=null)
				gc.fillRectangle(0, 0, size.x, size.y);
		}
		else {
			if (hover!=null)
				gc.fillRectangle(0, 0, size.x, size.y);	
		}
	}
}
