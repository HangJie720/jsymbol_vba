package com.gitplex.jsymbol.python3.symbols;

import com.gitplex.jsymbol.Symbol;
import com.gitplex.jsymbol.TokenPosition;

public abstract class Python3Symbol extends Symbol{
	
	private static final long serialVersionUID = 1L;
	
	private final String name;	
	private final Python3Symbol parent;	
	private final TokenPosition position;	
	private final TokenPosition scope;
	
	public Python3Symbol(Python3Symbol parent, String name, TokenPosition position, TokenPosition scope){
		this.parent=parent;
		this.name = name;
		this.position=position;
		this.scope=scope;
	}

	@Override
	public Symbol getParent() {
		return parent;
	}

	@Override
	public String getName() {
		return name;
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
		return false;
	}

	@Override
	public String getFQNSeparator() {
		return ".";
	}

}
