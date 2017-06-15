package com.gitplex.jsymbol.javascript.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.javascript.symbols.ui.MethodSymbolPanel;
import com.gitplex.jsymbol.javascript.symbols.ui.icon.IconLocator;
import com.gitplex.jsymbol.util.NoAntiCacheImage;

public class MethodSymbol extends JavaScriptSymbol {

	private static final long serialVersionUID = 1L;
	
	private MethodAccess methodAccess;
	
	private String parameters;
	
	public MethodAccess getMethodAccess() {
		return methodAccess;
	}

	public String getParameters() {
		return parameters;
	}

	public void setMethodAccess(MethodAccess methodAccess) {
		this.methodAccess = methodAccess;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@Override
	public Component render(String componentId, Range highlight) {
		return new MethodSymbolPanel(componentId, this, highlight);
	}

	@Override
	public Image renderIcon(String componentId) {
		Image icon = new NoAntiCacheImage(componentId, new PackageResourceReference(IconLocator.class, "method.png"));
		icon.add(AttributeAppender.append("title", "method"));
		return icon;
	}

}
