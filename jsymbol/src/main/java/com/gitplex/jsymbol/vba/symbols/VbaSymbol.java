package com.gitplex.jsymbol.vba.symbols;

import com.gitplex.jsymbol.Symbol;
import com.gitplex.jsymbol.TokenPosition;

public abstract class VbaSymbol extends Symbol
{
	private static final long serialVersionUID = 1L;
	
	private final String name;	
	private final VbaSymbol parent;	
	private final TokenPosition position;	
	private final TokenPosition scope;
	
	public VbaSymbol(VbaSymbol parent, String name, TokenPosition position, TokenPosition scope){
		this.parent=parent;
		this.name = name;
		this.position=position;
		this.scope=scope;
	}

	@Override
	public Symbol getParent()
	{
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public TokenPosition getPosition()
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public TokenPosition getScope()
	{
		// TODO Auto-generated method stub
		return scope;
	}

	@Override
	public boolean isLocal()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFQNSeparator()
	{
		// TODO Auto-generated method stub
		return ".";
	}

}
