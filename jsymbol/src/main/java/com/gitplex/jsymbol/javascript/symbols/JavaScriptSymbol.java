package com.gitplex.jsymbol.javascript.symbols;

import com.gitplex.jsymbol.Symbol;
import com.gitplex.jsymbol.TokenPosition;

public abstract class JavaScriptSymbol extends Symbol {

	private static final long serialVersionUID = 1L;

	private JavaScriptSymbol parent;
	
	private String name;
	
	private TokenPosition position;
	
	private TokenPosition scope;
	
	private boolean searchable = true;
	
	private ModuleAccess moduleAccess = ModuleAccess.NORMAL;
	
	/**
	 * Whether or not the symbol is a property of its parent symbol. Property symbol can be referenced with property 
	 * path from parent symbol. Considering below code:
	 * <pre><code>
	 * function test() {
	 *   function inner() {
	 *   }
	 * }
	 * test.usage = "some usage guide";
	 * </code></pre>
	 * 
	 * The symbol <tt>usage</tt> is a property symbol, while <tt>inner</tt> is not
	 */
	private boolean property;
	
	@Override
    public JavaScriptSymbol getParent() {
		return parent;
	}

	public void setParent(JavaScriptSymbol parent) {
		this.parent = parent;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public TokenPosition getPosition() {
		return position;
	}

	public void setPosition(TokenPosition position) {
		this.position = position;
	}

	@Override
	public TokenPosition getScope() {
		return scope;
	}

	public void setScope(TokenPosition scope) {
		this.scope = scope;
	}

	@Override
	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public boolean isProperty() {
		return property;
	}

	public void setProperty(boolean property) {
		this.property = property;
	}

	public ModuleAccess getModuleAccess() {
		return moduleAccess;
	}

	public void setModuleAccess(ModuleAccess moduleAccess) {
		this.moduleAccess = moduleAccess;
	}

	@Override
	public boolean isLocal() {
		return moduleAccess != ModuleAccess.EXPORT 
				&& !isProperty() 
				&& (getParent() != null || moduleAccess == ModuleAccess.IMPORT);
	}

	@Override
	public boolean isPrimary() {
		return moduleAccess == ModuleAccess.EXPORT;
	}

	@Override
	public String getFQNSeparator() {
		return ".";
	}

}
