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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for the type of the comparator that {@link NullCheckComparator} decorates, with the forms of
 * {@link java.util.Comparator#nullsFirst(java.util.Comparator)} and
 * {@link java.util.Comparator#nullsLast(java.util.Comparator)}, which take a
 * {@code Comparator<? super T>}: each form is compiled with the JDK compiler against the classes of
 * this library, once with {@link NullCheckComparator} and once with the JDK method (#38)
 */
public class NullCheckComparatorWildcardTest
{

	/**
	 * A statement with {@link NullCheckComparator}, the same statement with the JDK method, and the
	 * first line of the error javac gives for the first
	 *
	 * @param description
	 *            the name of the case
	 * @param statement
	 *            the statement with {@link NullCheckComparator}
	 * @param jdkStatement
	 *            the same statement with {@code Comparator.nullsFirst} or {@code nullsLast}
	 * @param error
	 *            the first line of the error javac gives for the statement
	 */
	record Form(String description, String statement, String jdkStatement, String error) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The seven forms of #38: a comparator whose type the call infers, a comparator of a supertype
	 * where the target type is the subtype, and a comparator of {@code ? super T} in a generic
	 * method
	 *
	 * @return the forms
	 */
	static Stream<Form> forms()
	{
		return Stream.of(
			new Form("of(naturalOrder())",
				"names.sort(NullCheckComparator.of(Comparator.naturalOrder()));",
				"names.sort(Comparator.nullsFirst(Comparator.naturalOrder()));",
				"no suitable method found for of(java.util.Comparator<T>)"),
			new Form("of(naturalOrder(), true)",
				"names.sort(NullCheckComparator.of(Comparator.naturalOrder(), true));",
				"names.sort(Comparator.nullsLast(Comparator.naturalOrder()));",
				"no suitable method found for of(java.util.Comparator<T>,boolean)"),
			new Form("new NullCheckComparator<>(naturalOrder())",
				"names.sort(new NullCheckComparator<>(Comparator.naturalOrder()));",
				"names.sort(Comparator.nullsFirst(Comparator.naturalOrder()));",
				"cannot infer type arguments for io.github.astrapi69.comparator.NullCheckComparator<>"),
			new Form("of(a comparator of CharSequence) as a comparator of String",
				"Comparator<String> c = NullCheckComparator.of(byLength);",
				"Comparator<String> c = Comparator.nullsFirst(byLength);",
				"incompatible types: inference variable T has incompatible equality constraints "
					+ "java.lang.String,java.lang.CharSequence"),
			new Form("builder() with a comparator of CharSequence",
				"Comparator<String> c = NullCheckComparator.<String> builder()"
					+ ".decoratedComparator(byLength).build();",
				"Comparator<String> c = Comparator.nullsFirst(byLength);",
				"incompatible types: java.util.Comparator<java.lang.CharSequence> cannot be "
					+ "converted to java.util.Comparator<java.lang.String>"),
			new Form("of(a Collator, a comparator of Object) as a comparator of String",
				"Comparator<String> c = NullCheckComparator.of(german);",
				"Comparator<String> c = Comparator.nullsFirst(german);",
				"incompatible types: inference variable T has incompatible equality constraints "
					+ "java.lang.String,java.lang.Object"),
			new Form("of(a comparator of ? super T) in a generic method",
				"Comparator<T> c = NullCheckComparator.of(comparator);",
				"Comparator<T> c = Comparator.nullsFirst(comparator);",
				"incompatible types: inference variable T has incompatible equality constraints "
					+ "T,capture#1 of ? super T"));
	}

	/**
	 * Test that the forms of {@link #forms()} do not compile with {@link NullCheckComparator},
	 * which takes the decorated comparator as a {@code Comparator<T>}, where they compile with the
	 * JDK method
	 *
	 * @param form
	 *            the statements and the error
	 * @param directory
	 *            the directory for the sources and classes
	 * @throws Exception
	 *             if the compiler cannot be run
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("forms")
	public void testTheFormsOfTheJdk(final Form form, @TempDir final Path directory)
		throws Exception
	{
		assertEquals(List.of(), errors(directory, form.jdkStatement()), "the JDK form compiles");
		assertEquals(List.of(form.error()), errors(directory, form.statement()));
	}

	/**
	 * Compiles the given statement in a method of a class against the classes of this library and
	 * returns the first line of each error, in English
	 *
	 * @param directory
	 *            the directory for the source and the class
	 * @param statement
	 *            the statement
	 * @return the first line of each error
	 * @throws Exception
	 *             if the compiler cannot be run
	 */
	private static List<String> errors(final Path directory, final String statement)
		throws Exception
	{
		final Path classes = Path.of(
			NullCheckComparator.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		final Path output = Files.createTempDirectory(directory, "call");
		final Path source = Files.writeString(output.resolve("Call.java"),
			String.join("\n", "import java.text.Collator;", "import java.util.Comparator;",
				"import java.util.List;",
				"import io.github.astrapi69.comparator.NullCheckComparator;", "class Call", "{",
				"	<T> void call(List<String> names, Comparator<CharSequence> byLength,",
				"		Collator german, Comparator<? super T> comparator)", "	{",
				"		" + statement, "	}", "}", ""));
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics,
			Locale.ROOT, null))
		{
			compiler.getTask(null, fileManager, diagnostics,
				List.of("-proc:none", "-classpath", classes.toString(), "-d", output.toString()),
				null, fileManager.getJavaFileObjects(source)).call();
		}
		return diagnostics.getDiagnostics().stream()
			.filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR)
			.map(diagnostic -> diagnostic.getMessage(Locale.ROOT).lines().findFirst().orElse(""))
			.collect(Collectors.toList());
	}

}
