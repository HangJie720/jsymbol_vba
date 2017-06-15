package com.gitplex.jsymbol.javascript.symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference symbol refers to another symbol. For instance, for below statement:
 * <pre><code>
 * a=b.c
 * </code></pre>
 * <i>a</i> will be created as a reference symbol referencing path <i>b.c</i>
 * 
 * @author robin
 *
 */
public class ReferenceSymbol extends ObjectSymbol {

	private static final long serialVersionUID = 1L;

	private JavaScriptSymbol referencedParent;
	
	private List<String> referencedPath = new ArrayList<>();
	
	public JavaScriptSymbol getReferencedParent() {
		return referencedParent;
	}

	public void setReferencedParent(JavaScriptSymbol referencedParent) {
		this.referencedParent = referencedParent;
	}

	public List<String> getReferencedPath() {
		return referencedPath;
	}

	public void setReferencedPath(List<String> referencedPath) {
		this.referencedPath = referencedPath;
	}

}
