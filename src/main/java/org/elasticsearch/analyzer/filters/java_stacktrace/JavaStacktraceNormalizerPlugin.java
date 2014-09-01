package org.elasticsearch.analyzer.filters.java_stacktrace;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

/**
 * Plugin definition which register custom Character Filter.
 */
public class JavaStacktraceNormalizerPlugin extends AbstractPlugin {
	public String name() {
		return "java-stacktrace-normalizer";
	}

	public String description() {
		return "filter that normalizes java stacktrace";
	}

	public void onModule(AnalysisModule module) {
		module.addCharFilter("java-stacktrace-normalizer", JavaStacktraceNormalizatorFilter.class);
	}
}
