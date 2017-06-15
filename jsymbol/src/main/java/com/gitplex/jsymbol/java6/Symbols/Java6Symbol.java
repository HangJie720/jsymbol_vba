package com.gitplex.jsymbol.java6.Symbols;

import com.gitplex.jsymbol.Symbol;
import com.gitplex.jsymbol.TokenPosition;

public abstract class Java6Symbol extends Symbol{
	
	private final String name;	
	private final Java6Symbol parent;	
	private final TokenPosition position;	
	private final TokenPosition scope;
	
	public Java6Symbol(Java6Symbol parent, String name, TokenPosition position, TokenPosition scope){
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
	public String getFQNSeparator() {
		
		return ".";
	}


	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

}
