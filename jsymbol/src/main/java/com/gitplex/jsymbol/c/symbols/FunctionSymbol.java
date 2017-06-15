package com.gitplex.jsymbol.c.symbols;

import javax.annotation.Nullable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.c.symbols.ui.FunctionSymbolPanel;
import com.gitplex.jsymbol.c.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

public class FunctionSymbol extends CSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean definition;
	
	private final String params;

	private final String type;
	
	public FunctionSymbol(CSymbol parent, String name, boolean local, boolean definition, @Nullable String params, 
			@Nullable String type, TokenPosition position, TokenPosition scope) {
		super(parent, name, local, position, scope);
		
		this.definition = definition;
		this.params = params;
		this.type = type;
	}

	@Override
	public boolean isPrimary() {
		return true;
	}

	public boolean isDefinition() {
		return definition;
	}

	@Nullable
	public String getParams() {
		return params;
	}

	@Nullable
	public String getType() {
		return type;
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage(componentId, 
				new PackageResourceReference(IconLocator.class, "function_obj.gif"));
        icon.add(AttributeAppender.append("title", "function"));
        return icon;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new FunctionSymbolPanel(componentId, this, highlight);
	}

}
