package com.gitplex.jsymbol.vba.symbols;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.gitplex.jsymbol.Range;
import com.gitplex.jsymbol.TokenPosition;
import com.gitplex.jsymbol.util.NoAntiCacheImage;
import com.gitplex.jsymbol.vba.symbols.ui.icon.IconLocator;

public class SubSymbol extends VbaSymbol
{

	private static final long serialVersionUID = 1L;
	private String kind;
	private String capacity;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public SubSymbol(VbaSymbol parent, String name, TokenPosition position, TokenPosition scope, String kind) {
		super(parent, name, position, scope);
		this.kind = kind;
	}

	public SubSymbol(VbaSymbol parent, String name, TokenPosition position, TokenPosition scope, String kind,
			String capacity) {
		super(parent, name, position, scope);
		this.kind = kind;
		this.capacity = capacity;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public SubSymbol(VbaSymbol parent, String name, TokenPosition position, TokenPosition scope)
	{
		super(parent, name, position, scope);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isPrimary()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Image renderIcon(String componentId)
	{
		// TODO Auto-generated method stub
		Image icon = null;
		if (kind == null) {
			icon = new NoAntiCacheImage(componentId, new PackageResourceReference(IconLocator.class, "methpub_obj.png"));
			icon.add(AttributeAppender.append("title", "Public class"));
		}else if (kind.equals("Private")) {
			icon = new NoAntiCacheImage(componentId, new PackageResourceReference(IconLocator.class, "methpri_obj.png"));
			icon.add(AttributeAppender.append("title", "Private class"));
		}else if (kind.equals("Public")) {
			icon = new NoAntiCacheImage(componentId, new PackageResourceReference(IconLocator.class, "methpub_obj.png"));
			icon.add(AttributeAppender.append("title", "Public class"));
		}
		return icon;
	}

	@Override
	public Component render(String componentId, Range highlight)
	{
		// TODO Auto-generated method stub
		return new Label(componentId, getName());
	}

}
