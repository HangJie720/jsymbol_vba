package com.gitplex.jsymbol.c.symbols.ui;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.c.symbols.VariableSymbol;
import com.gitplex.jsymbol.util.HighlightableLabel;

@SuppressWarnings("serial")
public class VariableSymbolPanel extends Panel {

	private final VariableSymbol variableSymbol;
	
	private final Range highlight;
	
	public VariableSymbolPanel(String id, VariableSymbol variableSymbol, Range highlight) {
		super(id);
		this.variableSymbol = variableSymbol;
		this.highlight = highlight;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new HighlightableLabel("name", variableSymbol.getName(), highlight));
		
		add(new Label("type", variableSymbol.getType()).setVisible(variableSymbol.getType()!=null));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CSymbolResourceReference()));
	}
	
}
