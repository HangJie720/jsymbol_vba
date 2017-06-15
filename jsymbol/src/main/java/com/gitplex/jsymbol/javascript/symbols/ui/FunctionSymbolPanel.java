package com.gitplex.jsymbol.javascript.symbols.ui;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.javascript.symbols.FunctionSymbol;
import com.gitplex.jsymbol.util.HighlightableLabel;

@SuppressWarnings("serial")
public class FunctionSymbolPanel extends Panel {

	private final FunctionSymbol symbol;
	
	private final Range highlight;
	
	public FunctionSymbolPanel(String id, FunctionSymbol symbol, Range highlight) {
		super(id);
		this.symbol = symbol;
		this.highlight = highlight;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
			
		/*
		 * highlight only applies to indexed/searchable name
		 */
		if (symbol.getName() != null)
			add(new HighlightableLabel("name", symbol.getName(), highlight));
		else
			add(new WebMarkupContainer("name").setVisible(false));
		
		add(new Label("params", symbol.getParameters()));
	}

}
