package com.gitplex.jsymbol.cpp.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.cpp.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.HighlightableLabel;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

public class MacroSymbol extends CppSymbol {

	private static final long serialVersionUID = 1L;
	
	public MacroSymbol(CppSymbol parent, String name, boolean local, TokenPosition position) {
		super(parent, name, local, position, null);
	}

	@Override
	public boolean isPrimary() {
		return true;
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage(componentId, 
				new PackageResourceReference(IconLocator.class, "define_obj.gif"));
        icon.add(AttributeAppender.append("title", "macro"));
        return icon;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new HighlightableLabel(componentId, getName(), highlight);
	}

}
