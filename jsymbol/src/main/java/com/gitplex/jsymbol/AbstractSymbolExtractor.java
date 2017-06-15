package com.gitplex.jsymbol;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractSymbolExtractor<T extends Symbol> implements SymbolExtractor<T> {

	protected boolean acceptExtensions(@Nullable String filePath, String...exts) {
		String fileExt = StringUtils.substringAfterLast(filePath, ".");
		for (String ext: exts) {
			if (ext.equalsIgnoreCase(fileExt))
				return true;
		}
		return false;
	}

}
