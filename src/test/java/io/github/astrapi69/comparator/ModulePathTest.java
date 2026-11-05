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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test that a consumer that declares this library alone compiles against it: a module on the module
 * path that requires this module alone (#15), and a class on the class path (#36).
 * {@link ChainableComparator} extends the commons-collections4 class {@code ComparatorChain}, so
 * the compiler of every consumer needs collections4: on the class path to see the superclass of
 * {@code ChainableComparator}, on the module path to resolve the module
 * {@code org.apache.commons.collections4} that this module requires {@code transitive}, which lets
 * a consumer module name {@code ComparatorChain} as well; without {@code transitive} javac reports
 * that the package {@code org.apache.commons.collections4.comparators} is not visible. Gradle's
 * test task hands over what such a consumer compiles against in the system property
 * {@value #CONSUMER_COMPILE_CLASSPATH_PROPERTY}: this project resolved as a consumer's compile
 * class path resolves it, its apiElements, that is the built jar and the api dependencies
 * (gradle/testing.gradle). commons-collections4 is an api dependency, so it is on that path; as an
 * implementation dependency it was not, and no consumer compiled (#36)
 */
public class ModulePathTest
{

	/**
	 * The system property with the compile class path of a consumer that declares this library
	 * alone, separated by the platform's path separator
	 */
	private static final String CONSUMER_COMPILE_CLASSPATH_PROPERTY = "module.path.test.consumer.compile.classpath";

	/**
	 * The module descriptor of a consumer module, which requires this module and nothing else
	 */
	private static final String CONSUMER_MODULE_INFO = String.join("\n", "module consumer.check",
		"{", "	requires io.github.astrapisixtynine.comparator.extensions;", "}", "");

	/**
	 * A consumer: what it compiles, and on which path
	 *
	 * @param description
	 *            the description of the consumer, the name of the case
	 * @param pathOption
	 *            the javac option that names the path: {@code --module-path} or
	 *            {@code --class-path}
	 * @param sources
	 *            the sources of the consumer, by their path relative to the source directory
	 */
	record Consumer(String description, String pathOption, Map<String, String> sources) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The consumers: two modules on the module path, the one of #15 that names
	 * {@code ComparatorChain} and one that uses no {@code ChainableComparator} at all, and the
	 * README's Chains example on the class path (README.md, "Chains")
	 *
	 * @return the consumers
	 */
	static Stream<Consumer> consumers()
	{
		final String namesComparatorChain = String.join("\n", "package consumer.check;", "",
			"import java.util.Comparator;", "",
			"import org.apache.commons.collections4.comparators.ComparatorChain;", "",
			"import io.github.astrapi69.comparator.ChainableComparator;", "",
			"public class UseChain", "{", "	public static ComparatorChain<String> chain()", "	{",
			"		return ChainableComparator.of(Comparator.<String> naturalOrder());", "	}", "}",
			"");
		final String usesNoChain = String.join("\n", "package consumer.check;", "",
			"import io.github.astrapi69.comparator.ComparatorExtensions;", "",
			"public class Compare", "{", "	public static int compare()", "	{",
			"		return ComparatorExtensions.compare(\"a\", \"b\");", "	}", "}", "");
		final String readmeChains = String.join("\n", "package consumer.check;", "",
			"import java.util.ArrayList;", "import java.util.Comparator;", "import java.util.List;",
			"", "import io.github.astrapi69.comparator.ChainableComparator;", "",
			"public class Chains", "{", "	public static List<String> sorted()", "	{",
			"		ChainableComparator<String> byLength = ChainableComparator",
			"			.of(Comparator.comparingInt(String::length));",
			"		byLength.addComparator(Comparator.naturalOrder());",
			"		List<String> words = new ArrayList<>(List.of(\"bb\", \"c\", \"ab\", \"a\"));",
			"		words.sort(byLength);", "		return words;", "	}", "}", "");
		return Stream.of(
			new Consumer("a module that names ComparatorChain", "--module-path",
				Map.of("module-info.java", CONSUMER_MODULE_INFO, "consumer/check/UseChain.java",
					namesComparatorChain)),
			new Consumer("a module that uses no ChainableComparator", "--module-path",
				Map.of("module-info.java", CONSUMER_MODULE_INFO, "consumer/check/Compare.java",
					usesNoChain)),
			new Consumer("the README's Chains example on the class path", "--class-path",
				Map.of("consumer/check/Chains.java", readmeChains)));
	}

	/**
	 * The compile class path of a consumer that declares this library alone. A missing property
	 * fails the test: a check that has nothing to compile against does not pass
	 *
	 * @return the compile class path
	 */
	private static String consumerCompileClasspath()
	{
		final String classpath = System.getProperty(CONSUMER_COMPILE_CLASSPATH_PROPERTY);
		assertNotNull(classpath,
			"the system property " + CONSUMER_COMPILE_CLASSPATH_PROPERTY + " is not set");
		return classpath;
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
	 * Test that a consumer that declares this library alone compiles against it, as collections4 is
	 * on its compile class path (#36)
	 *
	 * @param consumer
	 *            the consumer
	 * @param directory
	 *            the directory for the sources and the classes of the consumer
	 * @throws IOException
	 *             if a source file cannot be written
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("consumers")
	public void testConsumerCompiles(final Consumer consumer, @TempDir final Path directory)
		throws IOException
	{
		final List<Path> sources = new ArrayList<>();
		for (final Map.Entry<String, String> source : consumer.sources().entrySet())
		{
			sources
				.add(write(directory.resolve("src").resolve(source.getKey()), source.getValue()));
		}
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "no system Java compiler");
		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics,
			Locale.ENGLISH, StandardCharsets.UTF_8))
		{
			final Boolean compiled = compiler.getTask(null, fileManager, diagnostics,
				List.of(consumer.pathOption(), consumerCompileClasspath(), "-d",
					directory.resolve("classes").toString()),
				null, fileManager.getJavaFileObjects(sources.toArray(Path[]::new))).call();
			final String messages = diagnostics.getDiagnostics().stream()
				.map(diagnostic -> diagnostic.getMessage(Locale.ENGLISH))
				.collect(Collectors.joining("; "));
			assertTrue(compiled, () -> "the consumer does not compile: " + messages);
		}
	}

}
