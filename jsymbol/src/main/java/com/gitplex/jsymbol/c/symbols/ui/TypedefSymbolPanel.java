package com.gitplex.jsymbol.c.symbols.ui;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.c.symbols.TypedefSymbol;
import com.gitplex.jsymbol.util.HighlightableLabel;

@SuppressWarnings("serial")
public class TypedefSymbolPanel extends Panel {

	private final TypedefSymbol typedefSymbol;
	
	private final Range highlight;
	
	public TypedefSymbolPanel(String id, TypedefSymbol typedefSymbol, Range highlight) {
		super(id);
		this.typedefSymbol = typedefSymbol;
		this.highlight = highlight;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new HighlightableLabel("name", typedefSymbol.getName(), highlight));
		
		add(new Label("type", typedefSymbol.getType()).setVisible(typedefSymbol.getType()!=null));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CSymbolResourceReference()));
	}
	
}
