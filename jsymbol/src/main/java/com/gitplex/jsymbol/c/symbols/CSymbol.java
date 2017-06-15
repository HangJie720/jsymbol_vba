package com.gitplex.jsymbol.c.symbols;

import javax.annotation.Nullable;

import com.gitplex.jsymbol.Symbol;
import com.gitplex.jsymbol.TokenPosition;

public abstract class CSymbol extends Symbol {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	private final CSymbol parent;
	
	private final boolean local;
	
	private final TokenPosition position;
	
	private final TokenPosition scope;
	
	public CSymbol(CSymbol parent, String name, boolean local, 
			@Nullable TokenPosition position, @Nullable TokenPosition scope) {
		this.parent = parent;
		this.name = name;
		this.local = local;
		this.position = position;
		this.scope = scope;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CSymbol getParent() {
		return parent;
	}

	@Override
	public TokenPosition getPosition() {
		return position;
	}

	@Override
	public TokenPosition getScope() {
		return scope;
	}

	@Override
	public boolean isLocal() {
		return local;
	}

	@Override
	public String getFQNSeparator() {
		return ".";
	}
	
}
