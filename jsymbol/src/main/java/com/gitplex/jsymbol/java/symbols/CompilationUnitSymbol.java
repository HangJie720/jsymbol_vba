package com.gitplex.jsymbol.java.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.java.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

/**
 * This symbol represents a Java package
 *  
 * @author robin
 *
 */
public class CompilationUnitSymbol extends JavaSymbol {
	
	private static final long serialVersionUID = 1L;
	
	public CompilationUnitSymbol(String packageName, TokenPosition position, TokenPosition scope) {
		super(null, packageName, position, scope);
	}
	
	@Override
	public Component render(String componentId, Range highlight) {
		return new Label(componentId, getName());
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage("icon", new PackageResourceReference(IconLocator.class, "package_obj.png"));
		icon.add(AttributeAppender.append("title", "package"));
		return icon;
	}

	@Override
	public boolean isPassthroughInOutline() {
		return true;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	@Override
	public boolean isSearchable() {
		return false;
	}

}
