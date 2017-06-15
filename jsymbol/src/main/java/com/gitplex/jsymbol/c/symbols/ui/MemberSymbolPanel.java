package com.gitplex.jsymbol.c.symbols.ui;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.c.symbols.MemberSymbol;
import com.gitplex.jsymbol.util.HighlightableLabel;

@SuppressWarnings("serial")
public class MemberSymbolPanel extends Panel {

	private final MemberSymbol memberSymbol;
	
	private final Range highlight;
	
	public MemberSymbolPanel(String id, MemberSymbol memberSymbol, Range highlight) {
		super(id);
		this.memberSymbol = memberSymbol;
		this.highlight = highlight;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new HighlightableLabel("name", memberSymbol.getName(), highlight));
		
		add(new Label("type", memberSymbol.getType()).setVisible(memberSymbol.getType()!=null));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CSymbolResourceReference()));
	}
	
}
