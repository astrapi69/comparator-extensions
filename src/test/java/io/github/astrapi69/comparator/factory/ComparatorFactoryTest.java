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
package io.github.astrapi69.comparator.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.meanbean.test.BeanTester;

import com.google.common.collect.Lists;

import io.github.astrapi69.test.object.Employee;
import io.github.astrapi69.test.object.Person;

/**
 * The unit test class for the class {@link ComparatorFactory}
 */
public class ComparatorFactoryTest
{

	/**
	 * The numbers that the random comparators order
	 */
	private static final List<Integer> ONE_TO_TEN = IntStream.rangeClosed(1, 10).boxed().toList();

	/**
	 * Test for method {@link ComparatorFactory#newDefinedOrderComparator(List)}
	 */
	@Test
	public void testNewDefinedOrderComparator()
	{
		List<Integer> values;
		List<Integer> actual;
		List<Integer> expected;
		Comparator<Integer> customComparator;
		// new scenario...
		values = Lists.newArrayList(1, 2, 3, 4, 5);
		// change list to random sort order
		Collections.shuffle(values);
		// create the custom Comparator from the given list
		customComparator = ComparatorFactory.newDefinedOrderComparator(values);
		// create a new list to sort with the custom Comparator
		actual = Lists.newArrayList(1, 2, 3, 4, 5);
		// sort with the custom Comparator
		actual.sort(customComparator);
		// now the actual list have to be sorted as the values list
		expected = values;
		assertEquals(actual, expected);
	}

	/**
	 * Test for method
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)}
	 */
	@Test
	public void testNewBeanPropertyComparator()
	{
		int actual;
		boolean expected;
		Comparator<Employee> employeeIdComparator;

		final Person person1 = Person.builder().name("Alex").married(Boolean.FALSE).build();
		final Person person2 = Person.builder().name("John").married(Boolean.FALSE).build();

		final Employee employee1 = Employee.builder().person(person1).id("1").build();
		final Employee employee2 = Employee.builder().person(person2).id("2").build();

		employeeIdComparator = ComparatorFactory.newBeanPropertyComparator(Employee::getId, false,
			false);

		actual = employeeIdComparator.compare(employee1, employee1);
		expected = actual == 0;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee1, employee2);
		expected = actual == -1;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee2, employee1);
		expected = actual == 1;
		assertTrue(expected);

		employeeIdComparator = ComparatorFactory.newBeanPropertyComparator(Employee::getId, false,
			true);

		actual = employeeIdComparator.compare(employee1, employee1);
		expected = actual == 0;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee1, employee2);
		expected = actual == 1;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee2, employee1);
		expected = actual == -1;
		assertTrue(expected);

		employeeIdComparator = ComparatorFactory.newBeanPropertyComparator(Employee::getId, true,
			true);

		actual = employeeIdComparator.compare(employee1, employee1);
		expected = actual == 0;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee1, employee2);
		expected = actual == 1;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee2, employee1);
		expected = actual == -1;
		assertTrue(expected);

		employeeIdComparator = ComparatorFactory.newBeanPropertyComparator(Employee::getId, true,
			false);

		actual = employeeIdComparator.compare(employee1, employee1);
		expected = actual == 0;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee1, employee2);
		expected = actual == -1;
		assertTrue(expected);

		actual = employeeIdComparator.compare(employee2, employee1);
		expected = actual == 1;
		assertTrue(expected);
	}

	/**
	 * A comparator of {@link ComparatorFactory#newBeanPropertyComparator(Function)} or
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)} by the words
	 * themselves, and the order it sorts the words {@code b, null, c, a} in
	 *
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            the comparator
	 * @param sortedWords
	 *            the words sorted with the comparator
	 */
	record BeanPropertyOrder(String description, Comparator<String> comparator,
		List<String> sortedWords) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The overload with the function alone and the four combinations of the flags. What they do
	 * today (#22): ascending true sorts descending and ascending false sorts ascending; the
	 * overload with the function alone sorts ascending with the null last; nullsFirst places the
	 * null whatever ascending is
	 *
	 * @return the comparators and the orders they sort in
	 */
	static Stream<BeanPropertyOrder> beanPropertyOrders()
	{
		final List<String> ascendingNullLast = Arrays.asList("a", "b", "c", null);
		final List<String> descendingNullLast = Arrays.asList("c", "b", "a", null);
		final List<String> ascendingNullFirst = Arrays.asList(null, "a", "b", "c");
		final List<String> descendingNullFirst = Arrays.asList(null, "c", "b", "a");
		return Stream.of(new BeanPropertyOrder("function alone: ascending, the null last",
			ComparatorFactory.newBeanPropertyComparator(Function.identity()), ascendingNullLast),
			new BeanPropertyOrder("nulls last, ascending true: descending today (#22)",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), false, true),
				descendingNullLast),
			new BeanPropertyOrder("nulls last, ascending false: ascending today (#22)",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), false, false),
				ascendingNullLast),
			new BeanPropertyOrder("nulls first, ascending true: descending today (#22)",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), true, true),
				descendingNullFirst),
			new BeanPropertyOrder("nulls first, ascending false: ascending today (#22)",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), true, false),
				ascendingNullFirst));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newBeanPropertyComparator(Function)} and
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)}: the order
	 * the comparator sorts the words {@code b, null, c, a} in
	 *
	 * @param order
	 *            the comparator and the order it sorts in
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("beanPropertyOrders")
	public void testNewBeanPropertyComparatorOrder(final BeanPropertyOrder order)
	{
		final List<String> words = Arrays.asList("b", null, "c", "a");

		words.sort(order.comparator());

		assertEquals(order.sortedWords(), words);
	}

	/**
	 * A combination of the flags of
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)}, and the
	 * index that an employee without id takes among three employees sorted with it
	 *
	 * @param description
	 *            the name of the case
	 * @param nullsFirst
	 *            the flag nullsFirst
	 * @param ascending
	 *            the flag ascending
	 * @param indexOfTheEmployeeWithoutId
	 *            the index of the employee without id in the sorted list
	 */
	record NullPlacement(String description, boolean nullsFirst, boolean ascending,
		int indexOfTheEmployeeWithoutId) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The four combinations of the flags: where the employee without id goes depends on nullsFirst
	 * alone. How ascending orders the employees with an id is not a part of these cases, it is the
	 * one of {@link #beanPropertyOrders()} (#22)
	 *
	 * @return the combinations
	 */
	static Stream<NullPlacement> nullPlacements()
	{
		return Stream.of(new NullPlacement("nulls first, ascending true", true, true, 0),
			new NullPlacement("nulls first, ascending false", true, false, 0),
			new NullPlacement("nulls last, ascending true", false, true, 2),
			new NullPlacement("nulls last, ascending false", false, false, 2));
	}

	/**
	 * Test for method
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)} with a
	 * property that is null: nullsFirst sorts the employee without id before the employees with
	 * one, otherwise after them
	 *
	 * @param placement
	 *            the flags and the index of the employee without id
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("nullPlacements")
	public void testNewBeanPropertyComparatorWithNullProperty(final NullPlacement placement)
	{
		final Employee withoutId = Employee.builder().build();
		final List<Employee> employees = new ArrayList<>(List.of(Employee.builder().id("2").build(),
			withoutId, Employee.builder().id("1").build()));

		employees.sort(ComparatorFactory.newBeanPropertyComparator(Employee::getId,
			placement.nullsFirst(), placement.ascending()));

		assertEquals(placement.indexOfTheEmployeeWithoutId(), employees.indexOf(withoutId));
	}

	/**
	 * Test for method {@link ComparatorFactory#newMapValuesComparator(Map)}
	 */
	@Test
	public void testNewMapValuesComparator()
	{

		List<Integer> values;
		Map<Integer, Integer> numberCounterMap;
		Comparator<Integer> integerComparator;
		// new scenario...
		values = Lists.newArrayList(1, 2, 3, 4, 5);
		numberCounterMap = TestDataFactory.newCounterMap(new HashMap<>(), values);
		integerComparator = ComparatorFactory.newMapValuesComparator(numberCounterMap);
		assertNotNull(integerComparator);
	}

	/**
	 * Test for method {@link ComparatorFactory#newRandomComparator(List)}
	 */
	@Test
	public void testNewRandomComparator()
	{
		List<Integer> values;
		// new scenario...
		values = Lists.newArrayList(1, 2, 3, 4, 5);
		// change list to random sort order
		Collections.shuffle(values);
		// create the random Comparator from the given list
		Comparator<Integer> randomComparator = ComparatorFactory.newRandomComparator(values);
		assertNotNull(randomComparator);
	}

	/**
	 * A factory method of a random comparator for the numbers {@link #ONE_TO_TEN}
	 *
	 * @param description
	 *            the factory method
	 * @param newComparator
	 *            the call of the factory method with the given random
	 */
	record RandomOrder(String description,
		Function<SecureRandom, Comparator<Integer>> newComparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The two factory methods that take a {@link SecureRandom}: one with a list of the numbers 1 to
	 * 10 in their order, one with a map whose values are the numbers 1 to 10
	 *
	 * @return the factory methods
	 */
	static Stream<RandomOrder> randomOrders()
	{
		final Map<String, Integer> numbersByName = ONE_TO_TEN.stream()
			.collect(Collectors.toMap(number -> "number " + number, Function.identity()));
		return Stream.of(
			new RandomOrder("newRandomComparator(List, SecureRandom)",
				random -> ComparatorFactory.newRandomComparator(new ArrayList<>(ONE_TO_TEN),
					random)),
			new RandomOrder("newRandomMapValuesComparator(Map, SecureRandom)",
				random -> ComparatorFactory.newRandomMapValuesComparator(numbersByName, random)));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newRandomComparator(List, SecureRandom)} and
	 * {@link ComparatorFactory#newRandomMapValuesComparator(Map, SecureRandom)}: the comparator
	 * orders the numbers in a shuffled order that the given random decides, the same for the same
	 * seed and another for another seed, and not in the order of the numbers given
	 *
	 * @param randomOrder
	 *            the factory method
	 * @throws NoSuchAlgorithmException
	 *             if no provider has SHA1PRNG, which the SUN provider of the JDK has
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("randomOrders")
	public void testRandomComparatorOrdersByTheGivenRandom(final RandomOrder randomOrder)
		throws NoSuchAlgorithmException
	{
		final List<Integer> order = sortedBy(randomOrder.newComparator().apply(seeded(1L)));

		assertEquals(order, sortedBy(randomOrder.newComparator().apply(seeded(1L))),
			"the same seed, the same order");
		assertNotEquals(order, sortedBy(randomOrder.newComparator().apply(seeded(2L))),
			"another seed, another order");
		assertNotEquals(ONE_TO_TEN, order, "a shuffled order, not the order given");
	}

	/**
	 * Sorts the numbers {@link #ONE_TO_TEN} with the given comparator
	 *
	 * @param comparator
	 *            the comparator
	 * @return the sorted numbers
	 */
	private static List<Integer> sortedBy(final Comparator<Integer> comparator)
	{
		final List<Integer> numbers = new ArrayList<>(ONE_TO_TEN);
		numbers.sort(comparator);
		return numbers;
	}

	/**
	 * A random that gives the same numbers on every run: SHA1PRNG of the SUN provider takes a seed
	 * that is set before its first use as its only seed
	 *
	 * @param seed
	 *            the seed
	 * @return the random
	 * @throws NoSuchAlgorithmException
	 *             if no provider has SHA1PRNG
	 */
	private static SecureRandom seeded(final long seed) throws NoSuchAlgorithmException
	{
		final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(seed);
		return random;
	}

	/**
	 * Test method for {@link ComparatorFactory} with {@link BeanTester}
	 */
	@Test
	public void testWithBeanTester()
	{
		final BeanTester beanTester = new BeanTester();
		beanTester.testBean(ComparatorFactory.class);
	}
}
