package org.elasticsearch.analyzer.filters.java_stacktrace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.ElasticsearchException;

/**
 * Normalizes java stacktrace by removing all lines which can be change like $Proxy.
 */
public class JavaStacktraceNormalizator {
	/**
	 * Stack trace line pattern.
	 */
	private Pattern stackElementPattern = Pattern.compile("^\\s+at (.*?)\\((.+?):(\\d+)\\)(\\s~?\\[.*\\])?$");

	private boolean withLineNumbers = true;

	public JavaStacktraceNormalizator(boolean withLineNumbers) {
		this.withLineNumbers = withLineNumbers;
	}

	public String normalize(String src) {
		return normalize(new StringReader(src));
	}

	public String normalize(Reader src) {

		BufferedReader br = new BufferedReader(src);
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				Matcher m = stackElementPattern.matcher(line);
				if (m.matches()) {
					sb.append(m.group(1));
					if (withLineNumbers) {
						sb.append(':').append(m.group(3));
					}
					sb.append("\n");
				}
			}
		} catch (IOException e) {
			throw new ElasticsearchException("Cannot read whole stacktrace.", e);
		}
		return sb.toString();
	}
}
