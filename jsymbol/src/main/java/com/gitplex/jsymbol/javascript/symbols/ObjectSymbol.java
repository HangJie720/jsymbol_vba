package com.gitplex.jsymbol.javascript.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.javascript.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.HighlightableLabel;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

public class ObjectSymbol extends JavaScriptSymbol {

	private static final long serialVersionUID = 1L;

	@Override
	public Image renderIcon(String componentId) {
		Image icon;
		if (getModuleAccess() == ModuleAccess.EXPORT) {
            icon = new NoAntiCacheImage(componentId, new PackageResourceReference(IconLocator.class, "exported_object.png"));
            icon.add(AttributeAppender.append("title", "exported object"));
        } else if (isLocal()) {
            icon = new NoAntiCacheImage(componentId, new PackageResourceReference(IconLocator.class, "local_object.png"));
            icon.add(AttributeAppender.append("title", "local object"));
		} else {
			icon = new NoAntiCacheImage(componentId, new PackageResourceReference(IconLocator.class, "object.png"));
			icon.add(AttributeAppender.append("title", "object"));
		}
		return icon;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new HighlightableLabel(componentId, getName(), highlight);
	}

}
