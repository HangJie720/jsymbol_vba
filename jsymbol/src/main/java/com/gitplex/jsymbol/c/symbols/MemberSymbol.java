package com.gitplex.jsymbol.c.symbols;

import javax.annotation.Nullable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.c.symbols.ui.MemberSymbolPanel;
import com.gitplex.jsymbol.c.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

public class MemberSymbol extends CSymbol {

	private static final long serialVersionUID = 1L;
	
	private final String type;
	
	public MemberSymbol(CSymbol parent, String name, @Nullable String type, TokenPosition position) {
		super(parent, name, false, position, null);
		this.type = type;
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	@Nullable
	public String getType() {
		return type;
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage(componentId, 
				new PackageResourceReference(IconLocator.class, "field_public_obj.gif"));
        icon.add(AttributeAppender.append("title", "member"));
        return icon;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new MemberSymbolPanel(componentId, this, highlight);
	}

}
