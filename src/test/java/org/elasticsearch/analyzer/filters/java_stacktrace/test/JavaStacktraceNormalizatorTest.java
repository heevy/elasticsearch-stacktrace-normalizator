package org.elasticsearch.analyzer.filters.java_stacktrace.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.analyzer.filters.java_stacktrace.JavaStacktraceNormalizatorFilter;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Java stacktrace normalizer test.
 */
@RunWith(Parameterized.class)
public class JavaStacktraceNormalizatorTest {

	@Parameterized.Parameters(name = "{0}: {2}")
	public static Iterable<Object[]> data() {
		final Settings withLines = ImmutableSettings.builder().put("lineNumbers", true).build();
		final Settings withoutLines = ImmutableSettings.builder().put("lineNumbers", false).build();
		// @formatter:off
		return Arrays.asList(new Object[][] {
                { "withLines",withLines, "/testStacktrace1.txt", "/testStacktrace1-normalized-with-lines.txt"},
                { "withoutLines",withLines, "/testStacktrace1.txt", "/testStacktrace1-normalized.txt"}
                }
        );
        // @formatter:on
	}

	private JavaStacktraceNormalizatorFilter normalizator;

	private Settings settings;
	private String input;
	private String expectedOutput;

	public JavaStacktraceNormalizatorTest(String label,Settings settings, String input, String expectedOutput) {
		this.settings = settings;
		this.input = input;
		this.expectedOutput = expectedOutput;
	}

	@Before
	public void setup() {
		Index index = new Index("test");
		final Settings indexSettings = ImmutableSettings.EMPTY;
		normalizator = new JavaStacktraceNormalizatorFilter(index, indexSettings, "java-stacktrace-normalizator", settings);
	}

	@Test
	public void testNormalizator() throws Exception {
		InputStream is = JavaStacktraceNormalizatorTest.class.getResourceAsStream("/testStacktrace1.txt");
		Reader r = normalizator.create(new InputStreamReader(is));
		String res = org.apache.commons.io.IOUtils.toString(r);
		String expected = IOUtils.toString(JavaStacktraceNormalizatorTest.class.getResourceAsStream(expectedOutput));
		Assert.assertEquals(expected, res);
	}

}
