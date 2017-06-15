package com.gitplex.jsymbol.java.symbols.ui;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.java.symbols.FieldSymbol;
import com.gitplex.jsymbol.util.HighlightableLabel;

@SuppressWarnings("serial")
public class FieldSymbolPanel extends Panel {

	private final FieldSymbol fieldSymbol;
	
	private final Range highlight;
	
	public FieldSymbolPanel(String id, FieldSymbol fieldSymbol, Range highlight) {
		super(id);
		this.fieldSymbol = fieldSymbol;
		this.highlight = highlight;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new HighlightableLabel("name", fieldSymbol.getName(), highlight));
		add(new Label("type", fieldSymbol.getType()).setVisible(fieldSymbol.getType()!=null));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new JavaSymbolResourceReference()));
	}

}
