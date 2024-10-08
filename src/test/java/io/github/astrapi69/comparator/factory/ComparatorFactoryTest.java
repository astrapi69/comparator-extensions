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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
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
	 * Test for method {@link ComparatorFactory#newRandomMapValuesComparator(Map, SecureRandom)}
	 */
	@Test
	public void testNewRandomMapValuesComparator() throws NoSuchAlgorithmException
	{

		List<Integer> values;
		// new scenario...
		values = Lists.newArrayList(1, 2, 3, 4, 5);
		Map<Integer, Integer> numberCounterMap;
		numberCounterMap = TestDataFactory.newCounterMap(new HashMap<>(), values);
		Comparator<Integer> integerComparator = ComparatorFactory
			.newRandomMapValuesComparator(numberCounterMap, SecureRandom.getInstanceStrong());
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
	 * Test for method {@link ComparatorFactory#newRandomComparator(List, SecureRandom)}
	 */
	@Test
	public void testNewRandomComparatorWithSecureRandom()
	{
		List<Integer> values;
		// new scenario...
		values = Lists.newArrayList(1, 2, 3, 4, 5);
		// change list to random sort order
		Collections.shuffle(values);
		// create the random Comparator from the given list
		Comparator<Integer> randomComparator = ComparatorFactory.newRandomComparator(values,
			new SecureRandom());
		assertNotNull(randomComparator);
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
