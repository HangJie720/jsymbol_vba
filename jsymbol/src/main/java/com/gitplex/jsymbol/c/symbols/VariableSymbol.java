package com.gitplex.jsymbol.c.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.c.symbols.ui.VariableSymbolPanel;
import com.gitplex.jsymbol.c.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

public class VariableSymbol extends CSymbol {

	private static final long serialVersionUID = 1L;
	
	private final String type;
	
	public VariableSymbol(CSymbol parent, String name, boolean local, String type, TokenPosition position) {
		super(parent, name, local, position, null);
		this.type = type;
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	public String getType() {
		return type;
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage(componentId, 
            		new PackageResourceReference(IconLocator.class, "variable_obj.gif"));
		icon.add(AttributeAppender.append("title", "variable"));
        return icon;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new VariableSymbolPanel(componentId, this, highlight);
	}

}
