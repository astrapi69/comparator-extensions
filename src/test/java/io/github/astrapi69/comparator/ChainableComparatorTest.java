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

import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.factory.ComparatorFactory;
import io.github.astrapi69.test.object.Employee;
import io.github.astrapi69.test.object.Person;


/**
 * The unit test class for the class {@link ChainableComparator}.
 */
public class ChainableComparatorTest
{

	/** The employee with the id 1, Alex. */
	static final Employee FIRST = Employee.builder()
		.person(Person.builder().name("Alex").married(Boolean.FALSE).build()).id("1").build();

	/** The employee with the id 2, John. */
	static final Employee SECOND = Employee.builder()
		.person(Person.builder().name("John").married(Boolean.FALSE).build()).id("2").build();

	/**
	 * Two employees, the chain that compares them and the sign of the result
	 *
	 * @param description
	 *            the name of the case
	 * @param chain
	 *            the chain
	 * @param object
	 *            the first employee
	 * @param compareWithObject
	 *            the employee to compare with
	 * @param expectedSign
	 *            the sign of the result: -1, 0 or 1
	 */
	record ChainComparison(String description, Comparator<Employee> chain, Employee object,
		Employee compareWithObject, int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of a chain whose first comparator orders the employees by their id: an employee is
	 * equal to itself, and the employee with the id 1 comes first, or last if the chain reverses
	 * that comparator
	 *
	 * @param name
	 *            the name of the chain
	 * @param chain
	 *            the chain
	 * @param reversed
	 *            whether the chain reverses the comparator by id
	 * @return the cases
	 */
	static Stream<ChainComparison> order(final String name, final Comparator<Employee> chain,
		final boolean reversed)
	{
		return Stream.of(
			new ChainComparison(name + ", id 1 with itself: zero", chain, FIRST, FIRST, 0),
			new ChainComparison(
				name + ", id 1 with id 2: "
					+ (reversed ? "positive, id 2 first" : "negative, id 1 first"),
				chain, FIRST, SECOND, reversed ? 1 : -1),
			new ChainComparison(
				name + ", id 2 with id 1: "
					+ (reversed ? "negative, id 2 first" : "positive, id 1 first"),
				chain, SECOND, FIRST, reversed ? -1 : 1));
	}

	/**
	 * The cases of every factory method of {@link ChainableComparator}, with a comparator by the id
	 * and one by the person: the bits 0 and 1 of the bit set reverse both
	 *
	 * @return the cases
	 */
	static Stream<ChainComparison> chainComparisons()
	{
		final Comparator<Employee> byId = ComparatorFactory
			.newBeanPropertyComparator(Employee::getId);
		final Comparator<Employee> byPerson = ComparatorFactory
			.newBeanPropertyComparator(Employee::getPerson);
		final List<Comparator<Employee>> byIdThenByPerson = List.of(byId, byPerson);
		final BitSet bothReversed = new BitSet();
		bothReversed.set(0);
		bothReversed.set(1);
		final ChainableComparator<Employee> added = ChainableComparator.of();
		added.addComparator(byId);
		added.addComparator(byPerson);
		return Stream
			.of(order("of(), then byId and byPerson added", added, false),
				order("of(byId)", ChainableComparator.of(byId), false),
				order("of([byId, byPerson])", ChainableComparator.of(byIdThenByPerson), false),
				order("of([byId, byPerson], bits 0 and 1 set)",
					ChainableComparator.of(byIdThenByPerson, bothReversed), true),
				order("of(byId, true)", ChainableComparator.of(byId, true), true))
			.flatMap(Function.identity());
	}

	/**
	 * Test method for {@link ChainableComparator#compare(Object, Object)} with every factory method
	 *
	 * @param comparison
	 *            the employees, the chain and the sign of the result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("chainComparisons")
	public void testCompare(final ChainComparison comparison)
	{
		assertEquals(comparison.expectedSign(), Integer.signum(
			comparison.chain().compare(comparison.object(), comparison.compareWithObject())));
	}

}
