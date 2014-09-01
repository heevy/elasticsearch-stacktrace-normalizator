package org.elasticsearch.analyzer.filters.java_stacktrace;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.elasticsearch.common.Base64;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * Character Filter which can change java stack trace input to normalized stacktrace or its hash (to save the space).
 */
public class JavaStacktraceNormalizatorFilter extends AbstractCharFilterFactory {

	private JavaStacktraceNormalizator normalizator;
	private boolean digest = false;

	@Inject
	public JavaStacktraceNormalizatorFilter(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
		super(index, indexSettings, name);
		boolean lineNumbers = true;
		if (Strings.hasLength(settings.get("lineNumbers"))) {
			lineNumbers = Boolean.valueOf(settings.get("lineNumbers"));
		}
		if (Strings.hasLength(settings.get("digest"))) {
			digest = Boolean.valueOf(settings.get("digest"));
		}
		normalizator = new JavaStacktraceNormalizator(lineNumbers);

	}

	@Override
	public Reader create(Reader tokenStream) {

		String normalizedStacktrace = normalizator.normalize(tokenStream);
		if (digest) {
			return new StringReader(getDigest(normalizedStacktrace));
		}
		return new StringReader(normalizedStacktrace);
	}

	private String getDigest(String msg) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(msg.getBytes("UTF-8"));
			return Base64.encodeBytes(thedigest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Cannot happen.");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Cannot happen.");
		}
	}
}
