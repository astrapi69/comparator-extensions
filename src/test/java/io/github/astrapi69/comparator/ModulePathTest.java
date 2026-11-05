/**
 * The MIT License
 *
 * Copyright (C) 2022 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.comparator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test that the module of this library can be used on the module path by a consumer module that
 * requires this module alone (#15). {@link ChainableComparator} extends the commons-collections4
 * class {@code ComparatorChain}, so a consumer that names that class has to read the module
 * {@code org.apache.commons.collections4}; it does only when this module requires it
 * {@code transitive}. Otherwise javac reports that the package
 * {@code org.apache.commons.collections4.comparators} is not visible. Gradle's test task hands the
 * built jar and the runtime class path over in the system properties {@value #MODULE_JAR_PROPERTY}
 * and {@value #RUNTIME_CLASSPATH_PROPERTY} (gradle/testing.gradle)
 */
public class ModulePathTest
{

	/**
	 * The system property with the path of the built jar
	 */
	private static final String MODULE_JAR_PROPERTY = "module.path.test.jar";

	/**
	 * The system property with the runtime class path, separated by the platform's path separator
	 */
	private static final String RUNTIME_CLASSPATH_PROPERTY = "module.path.test.runtime.classpath";

	/**
	 * The module descriptor of the consumer, which requires this module and nothing else
	 */
	private static final String CONSUMER_MODULE_INFO = String.join("\n", "module consumer.check",
		"{", "	requires io.github.astrapisixtynine.comparator.extensions;", "}", "");

	/**
	 * A class of the consumer that holds a {@link ChainableComparator} as the
	 * {@code ComparatorChain} it is
	 */
	private static final String CONSUMER_CLASS = String.join("\n", "package consumer.check;", "",
		"import java.util.Comparator;", "",
		"import org.apache.commons.collections4.comparators.ComparatorChain;", "",
		"import io.github.astrapi69.comparator.ChainableComparator;", "", "public class UseChain",
		"{", "	public static ComparatorChain<String> chain()", "	{",
		"		return ChainableComparator.of(Comparator.<String> naturalOrder());", "	}", "}",
		"");

	/**
	 * Builds the module path from the built jar and the runtime class path. A missing property
	 * fails the test: a check that has nothing to compile against does not pass
	 *
	 * @return the module path
	 */
	private static String modulePath()
	{
		final String moduleJar = System.getProperty(MODULE_JAR_PROPERTY);
		final String runtimeClasspath = System.getProperty(RUNTIME_CLASSPATH_PROPERTY);
		assertNotNull(moduleJar, "the system property " + MODULE_JAR_PROPERTY + " is not set");
		assertNotNull(runtimeClasspath,
			"the system property " + RUNTIME_CLASSPATH_PROPERTY + " is not set");
		return moduleJar + File.pathSeparator + runtimeClasspath;
	}

	/**
	 * Writes the given content to the given file, with its parent directories
	 *
	 * @param file
	 *            the file
	 * @param content
	 *            the content
	 * @return the file
	 * @throws IOException
	 *             if the file cannot be written
	 */
	private static Path write(final Path file, final String content) throws IOException
	{
		Files.createDirectories(file.getParent());
		return Files.writeString(file, content, StandardCharsets.UTF_8);
	}

	/**
	 * Test that a consumer module that requires only this module and names {@code ComparatorChain}
	 * compiles on the module path
	 *
	 * @param directory
	 *            the directory for the sources and the classes of the consumer
	 * @throws IOException
	 *             if a source file cannot be written
	 */
	@Test
	public void testConsumerModuleNamesComparatorChain(@TempDir final Path directory)
		throws IOException
	{
		final Path moduleInfo = write(directory.resolve("src/module-info.java"),
			CONSUMER_MODULE_INFO);
		final Path consumerClass = write(directory.resolve("src/consumer/check/UseChain.java"),
			CONSUMER_CLASS);
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "no system Java compiler");
		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics,
			Locale.ENGLISH, StandardCharsets.UTF_8))
		{
			final Boolean compiled = compiler.getTask(null, fileManager, diagnostics,
				List.of("--module-path", modulePath(), "-d",
					directory.resolve("classes").toString()),
				null, fileManager.getJavaFileObjects(moduleInfo, consumerClass)).call();
			assertTrue(compiled,
				() -> "the consumer module does not compile on the module path: "
					+ diagnostics.getDiagnostics().stream()
						.map(diagnostic -> diagnostic.getMessage(Locale.ENGLISH))
						.collect(Collectors.joining("; ")));
		}
	}

}
