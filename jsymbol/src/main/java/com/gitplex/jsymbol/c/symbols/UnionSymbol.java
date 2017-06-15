package com.gitplex.jsymbol.c.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.c.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.HighlightableLabel;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

public class UnionSymbol extends CSymbol {

	private static final long serialVersionUID = 1L;
	
	public UnionSymbol(CSymbol parent, String name, boolean local, TokenPosition position, 
			TokenPosition scope) {
		super(parent, name, parent == null, position, null);
	}

	@Override
	public boolean isPrimary() {
		return true;
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage(componentId, 
            		new PackageResourceReference(IconLocator.class, "union_obj.gif"));
		icon.add(AttributeAppender.append("title", "union"));
        return icon;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new HighlightableLabel(componentId, getName(), highlight);
	}

}
