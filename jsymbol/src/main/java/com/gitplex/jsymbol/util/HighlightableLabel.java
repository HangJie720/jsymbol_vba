package com.gitplex.jsymbol.util;

import javax.annotation.Nullable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import com.gitplex.jsyntax.TokenizerUtils;
import com.gitplex.jsymbol.Range;

@SuppressWarnings("serial")
public class HighlightableLabel extends Label {

	public HighlightableLabel(String id, @Nullable String label, @Nullable Range highlight) {
		super(id, new LoadableDetachableModel<String>() {

			@Override
			protected String load() {
				if (label != null) {
					if (highlight != null) {
						String prefix = label.substring(0, highlight.getFrom());
						String middle = label.substring(highlight.getFrom(), highlight.getTo());
						String suffix = label.substring(highlight.getTo());
						return TokenizerUtils.escapeHtml(prefix) 
								+ "<b>" 
								+ TokenizerUtils.escapeHtml(middle) 
								+ "</b>" 
								+ TokenizerUtils.escapeHtml(suffix);
					} else {
						return TokenizerUtils.escapeHtml(label);
					}
				} else {
					return "";
				}
			}
			
		});
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		setEscapeModelStrings(false);
	}

}
