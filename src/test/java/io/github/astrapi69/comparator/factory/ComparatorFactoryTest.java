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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
	 * The numbers that the random comparators of a list order with a random of their own: a shuffle
	 * leaves 50 numbers in their order with a chance of 1 in 50!, so a test can assert that their
	 * order is shuffled without a seed
	 */
	private static final List<Integer> ONE_TO_FIFTY = IntStream.rangeClosed(1, 50).boxed().toList();

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
	 * The overload with the function alone and the four combinations of the flags: ascending true
	 * sorts ascending and ascending false sorts descending, where they sorted the other way round
	 * before (#22); the overload with the function alone sorts ascending with the null last, as it
	 * did before; nullsFirst places the null whatever ascending is
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
			new BeanPropertyOrder("nulls last, ascending true: ascending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), false, true),
				ascendingNullLast),
			new BeanPropertyOrder("nulls last, ascending false: descending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), false, false),
				descendingNullLast),
			new BeanPropertyOrder("nulls first, ascending true: ascending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), true, true),
				ascendingNullFirst),
			new BeanPropertyOrder("nulls first, ascending false: descending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), true, false),
				descendingNullFirst));
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
	 * Test for the methods {@link ComparatorFactory#newBeanPropertyComparator(Function)} and
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)} with the
	 * comparators of {@link #beanPropertyOrders()}: two elements with equal properties compare as
	 * equal, whatever the flags, two equal words as well as two nulls
	 *
	 * @param order
	 *            the comparator
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("beanPropertyOrders")
	public void testNewBeanPropertyComparatorEqualProperties(final BeanPropertyOrder order)
	{
		// another instance, equal to b, so that the comparison does not end at the same instance
		assertEquals(0, order.comparator().compare("b", new String("b")), "two equal words");
		assertEquals(0, order.comparator().compare(null, null), "two nulls");
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
	 * A factory method of a random comparator that takes a list
	 *
	 * @param description
	 *            the factory method
	 * @param newComparator
	 *            the call of the factory method with the given list
	 */
	record RandomComparatorOfAList(String description,
		Function<List<Integer>, Comparator<Integer>> newComparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The two factory methods that take a list: {@link ComparatorFactory#newRandomComparator(List)}
	 * with a random of its own, {@link ComparatorFactory#newRandomComparator(List, SecureRandom)}
	 * with a new {@link SecureRandom}
	 *
	 * @return the factory methods
	 */
	static Stream<RandomComparatorOfAList> randomComparatorsOfAList()
	{
		return Stream.of(
			new RandomComparatorOfAList("newRandomComparator(List)",
				ComparatorFactory::newRandomComparator),
			new RandomComparatorOfAList("newRandomComparator(List, SecureRandom)",
				list -> ComparatorFactory.newRandomComparator(list, new SecureRandom())));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newRandomComparator(List)} and
	 * {@link ComparatorFactory#newRandomComparator(List, SecureRandom)} with a list of the numbers
	 * {@link #ONE_TO_FIFTY} in their order: they shuffle a copy (#24), so the given list stays as
	 * it was and a later change of it does not change the order of the comparator, which is a
	 * shuffled order, not the order given
	 *
	 * @param factory
	 *            the factory method
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("randomComparatorsOfAList")
	public void testNewRandomComparatorLeavesTheGivenList(final RandomComparatorOfAList factory)
	{
		final List<Integer> givenList = new ArrayList<>(ONE_TO_FIFTY);

		final Comparator<Integer> comparator = factory.newComparator().apply(givenList);

		assertEquals(ONE_TO_FIFTY, givenList, "the given list stays as it was");
		final List<Integer> order = sortedBy(comparator, ONE_TO_FIFTY);
		assertNotEquals(ONE_TO_FIFTY, order, "a shuffled order, not the order given");
		Collections.reverse(givenList);
		assertEquals(order, sortedBy(comparator, ONE_TO_FIFTY),
			"a later change of the given list does not change the order");
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newRandomComparator(List)} and
	 * {@link ComparatorFactory#newRandomComparator(List, SecureRandom)} with an unmodifiable list
	 * of the numbers {@link #ONE_TO_FIFTY} in their order: they take it, as they shuffle a copy
	 * (#24), and the comparator orders the numbers in a shuffled order, not the order given
	 *
	 * @param factory
	 *            the factory method
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("randomComparatorsOfAList")
	public void testNewRandomComparatorTakesAnUnmodifiableList(
		final RandomComparatorOfAList factory)
	{
		final Comparator<Integer> comparator = factory.newComparator().apply(ONE_TO_FIFTY);

		assertNotEquals(ONE_TO_FIFTY, sortedBy(comparator, ONE_TO_FIFTY),
			"a shuffled order, not the order given");
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
	 *             if the SUN provider has no SHA1PRNG, which it has in the JDK
	 * @throws NoSuchProviderException
	 *             if the JDK has no SUN provider
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("randomOrders")
	public void testRandomComparatorOrdersByTheGivenRandom(final RandomOrder randomOrder)
		throws NoSuchAlgorithmException, NoSuchProviderException
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
		return sortedBy(comparator, ONE_TO_TEN);
	}

	/**
	 * Sorts a copy of the given numbers with the given comparator
	 *
	 * @param comparator
	 *            the comparator
	 * @param numbers
	 *            the numbers
	 * @return the sorted copy
	 */
	private static List<Integer> sortedBy(final Comparator<Integer> comparator,
		final List<Integer> numbers)
	{
		final List<Integer> sorted = new ArrayList<>(numbers);
		sorted.sort(comparator);
		return sorted;
	}

	/**
	 * A random that gives the same numbers on every run: SHA1PRNG, which the method asks the SUN
	 * provider for, and not the first provider that has it, takes a seed that is set before its
	 * first use as its only seed. The seed must not be 0: {@link SecureRandom#setSeed(long)}
	 * ignores 0, and the random then seeds itself at its first use, with another seed on every run
	 *
	 * @param seed
	 *            the seed, not 0
	 * @return the random
	 * @throws NoSuchAlgorithmException
	 *             if the SUN provider has no SHA1PRNG
	 * @throws NoSuchProviderException
	 *             if the JDK has no SUN provider
	 */
	private static SecureRandom seeded(final long seed)
		throws NoSuchAlgorithmException, NoSuchProviderException
	{
		final SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
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
