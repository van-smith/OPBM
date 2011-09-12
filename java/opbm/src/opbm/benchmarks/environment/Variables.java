/*
 * OPBM - Office Productivity Benchmark
 *
 * This class holds all variables that are used in flow control for benchmark
 * running and examination in the debugger.  It is not really used right now,
 * but may be used in the future.
 *
 * Last Updated:  Sep 12, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.1.0
 *
 */

package opbm.benchmarks.environment;

import java.util.List;

public class Variables
{
	public Variables(String	name,
					 String	value)
	{
		m_name		= name;
		m_value		= value;
	}

	public String getName()				{	return(m_name);						}
	public String getValue()			{	return(m_value);					}
	public int getValueAsInt()			{	return(Integer.valueOf(m_value));	}
	public void setName(String name)	{	m_name = name;						}
	public void setValue(String value)	{	m_value = value;					}

	public void increaseValue()
	{
		m_value = Integer.toString(Integer.valueOf(m_value) + 1);
	}

	public void decreaseValue()
	{
		m_value = Integer.toString(Integer.valueOf(m_value) - 1);
	}

	public void addValue(int number)
	{
		m_value = Integer.toString(Integer.valueOf(m_value) + number);
	}

	public void subtractValue(int number)
	{
		m_value = Integer.toString(Integer.valueOf(m_value) - number);
	}

	public static void updateOrAdd(String			name,
								   String			value,
								   List<Variables>	haystack)
	{
		Variables v = findVariable(name, haystack);
		if (v != null)
		{
			// We are updating the value
			v.setValue(value);

		} else {
			// We are adding a new variable
			haystack.add(new Variables(name, value));

		}
	}

	public static Variables findVariable(String				needle,
										 List<Variables>	haystack)
	{
		int i;

		// Search through the entire haystack for the needle
		for (i = 0; i < haystack.size(); i++)
		{
			if (haystack.get(i).getName().equalsIgnoreCase(needle))
				return(haystack.get(i));
		}
		// If we get here, it wasn't found
		return(null);
	}

	public String	m_name;
	public String	m_value;
}
