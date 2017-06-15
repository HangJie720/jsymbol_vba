package com.gitplex.jsymbol.cpp.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.cpp.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

/**
 * This symbol represents C header file in order to show in which header file a symbol is defined
 * 
 * @author robin
 *
 */
public class HeaderFileSymbol extends CppSymbol {

	private static final long serialVersionUID = 1L;
	
	public HeaderFileSymbol(String name) {
		super(null, name, false, null, null);
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	@Override
	public boolean isSearchable() {
		return false;
	}

	@Override
	public boolean isPassthroughInOutline() {
		return true;
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage(componentId, 
				new PackageResourceReference(IconLocator.class, "hpp_file_obj.png"));
        icon.add(AttributeAppender.append("title", "cpp header file"));
        return icon;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new Label(componentId, getName());
	}

	@Override
	public String getFQNSeparator() {
		return ": ";
	}

}
