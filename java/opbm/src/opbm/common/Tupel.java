/*
 * OPBM - Office Productivity Benchmark
 *
 * This class stores two primary items, along with an extra item.  The first
 * item is typically a name.  It's used to allow disparate, un-connected
 * classes to pass "lazy" messages back and forth, allowing an indeterminate
 * amount of time to elapse between uses.  In addition, otherwise unassociated
 * generic items can be sorted and stored by this class, allowing for a single
 * name to reference a lot of information about a particular item of interest.
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Van Smith, Rick C. Hodgin
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @author Rick C. Hodgin
 * @version 1.0.2
 *
 */

package opbm.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import opbm.Opbm;

/**
 * Class globally storing two items, a string and an object.  The
 * <code>Tupel</code> can be referenced by its UUID, which is generated in the
 * constructor.
 *
 * @author Rick C. Hodgin
 */
public class Tupel
{
	/**
	 * Constructor, creates a UUID for unique reference to the created tupel.
	 * This is necessary for an external <code>List<Tupel></code> to maintain
	 * the global state, hence the callback to the Opbm class, and its
	 * <code>add</code> function.
	 *
	 * @param opbm
	 */
	public Tupel(Opbm opbm)
	{
		m_uuid		= UUID.randomUUID();
		m_names		= new ArrayList<String>(0);
		m_objects	= new ArrayList<Object>(0);
		m_extra1	= new ArrayList<Object>(0);
		m_extra2	= new ArrayList<Object>(0);
		m_extra3	= new ArrayList<Object>(0);
		m_extra4	= new ArrayList<Object>(0);
		m_extra5	= new ArrayList<Object>(0);
		opbm.addTupel(this);
	}

	/**
	 * UUID in <code>String</code> form.
	 * @return UUID as a <code>String</code>
	 */
	public String getName()
	{
		return(m_uuid.toString());
	}

	/**
	 * Adds a new entry to the tupel, which could be a duplicate.
	 *
	 * @param name <code>String</code> to identify the object
	 * @param o object to add
	 */
	public void add(String name, Object o)
	{
		m_names.add(name);
		m_objects.add(o);
		m_extra1.add(null);
		m_extra2.add(null);
		m_extra3.add(null);
		m_extra4.add(null);
		m_extra5.add(null);
	}

	/**
	 * Adds a new entry to the tupel, which could be a duplicate.
	 *
	 * @param name <code>String</code> to identify the object
	 * @param o object to add
	 * @param extra1 extra object to add
	 */
	public void add(String name, Object o, Object extra1)
	{
		m_names.add(name);
		m_objects.add(o);
		m_extra1.add(extra1);
	}

	/**
	 * Searches the tupels by its second (the object) and returns the
	 * associated string if found.
	 *
	 * @param o object to search
	 * @return associated string if found, empty string otherwise
	 */
	public String getFirst(Object o)
	{
		int i;

		for (i = 0; i < m_objects.size(); i++)
		{
			if (m_objects.get(i).equals(o))
			{
				return(m_names.get(i));
			}
		}
		return("");
	}

	/**
	 * Searches the tupels by its first (the String) and returns the associated
	 * object if found.
	 *
	 * @param name String to search
	 * @return object if found, null otherwise
	 */
	public Object getSecond(String name)
	{
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				return(m_objects.get(i));
			}
		}
		return(null);
	}

	/**
	 * Searches the tupels by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getExtra1(String name)
	{
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
				return(m_extra1.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tupels by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getExtra2(String name)
	{
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
				return(m_extra2.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tupels by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getExtra3(String name)
	{
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
				return(m_extra3.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tupels by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getExtra4(String name)
	{
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
				return(m_extra4.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tupels by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getExtra5(String name)
	{
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
				return(m_extra5.get(i));
		}
		return(null);
	}

	/**
	 * Sets the first property (the String) of the specified tupel, as searched
	 * by its second property (the object), and updates the String.
	 *
	 * @param o object to search
	 * @param newValue new String value to set
	 * @return old String value
	 */
	public String setFirst(Object o, String newValue)
	{
		String returnValue = "";
		int i;

		for (i = 0; i < m_objects.size(); i++)
		{
			if (m_objects.get(i).equals(o))
			{
				returnValue = m_names.get(i);
				m_names.set(i, newValue);
				break;
			}
		}
		return(returnValue);
	}

	/**
	 * Sets the second property (the object) of the specified tupel, as searched
	 * by its first property (the String), and updates the String.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setSecond(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				returnObject = m_objects.get(i);
				m_objects.set(i, newObject);
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tupel, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setExtra1(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				returnObject = m_extra1.get(i);
				m_extra1.set(i, newObject);
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tupel, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setExtra2(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				returnObject = m_extra2.get(i);
				m_extra2.set(i, newObject);
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tupel, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setExtra3(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				returnObject = m_extra3.get(i);
				m_extra3.set(i, newObject);
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tupel, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setExtra4(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				returnObject = m_extra4.get(i);
				m_extra4.set(i, newObject);
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tupel, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setExtra5(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				returnObject = m_extra5.get(i);
				m_extra5.set(i, newObject);
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Deletes the specfied tupel by searching its first property (the String).
	 * Deletes if found. Ignores otherwise.
	 *
	 * @param name String name to delete
	 */
	public void remove(String name)
	{
		int i;

		for (i = 0; i < m_names.size(); i++)
		{
			if (m_names.get(i).equalsIgnoreCase(name))
			{
				m_names.remove(i);
				m_objects.remove(i);
				m_extra1.remove(i);
				break;
			}
		}
	}

	/**
	 * Deletes the specified tupel by searching its second property (the object).
	 * Deletes if found. Ignores otherwise.
	 *
	 * @param o Object to delete
	 */
	public void remove(Object o)
	{
		int i;

		for (i = 0; i < m_objects.size(); i++)
		{
			if (m_objects.get(i).equals(o))
			{
				m_names.remove(i);
				m_objects.remove(i);
				m_extra1.remove(i);
				m_extra2.remove(i);
				m_extra3.remove(i);
				m_extra4.remove(i);
				m_extra5.remove(i);
				break;
			}
		}
	}

	/**
	 * For iterative functions, returns size of the <code>Tupel</code> list
	 * @return size (count) of m_names in items (which will also be the same
	 * size as m_objects and m_extra1, as these are all linked 1:1)
	 */
	public int size()
	{
		return(m_names.size());
	}

	public boolean isEmpty()
	{
		return(m_names.isEmpty());
	}

	/**
	 * Returns the explicit item within the list
	 * @param iterator item number to access
	 * @return first item (String) associated with the <code>Tupel</code> entry
	 */
	public String getFirst(int iterator)
	{
		if (iterator < m_names.size())
			return(m_names.get(iterator));
		return("");
	}

	public void setFirst(int		iterator,
						 String		name)
	{
		if (iterator < m_objects.size())
			m_names.set(iterator, name);
	}

	/**
	 * Returns the explicit item within the list
	 * @param iterator item number to access
	 * @return second item (Object) associated with the <code>Tupel</code> entry
	 */
	public Object getSecond(int iterator)
	{
		if (iterator < m_objects.size())
			return(m_objects.get(iterator));
		return(null);
	}

	public void setSecond(int		iterator,
						  Object	object)
	{
		if (iterator < m_objects.size())
			m_objects.set(iterator, object);
	}

	/**
	 * Returns the explicit item within the list
	 * @param iterator item number to access
	 * @return second item (Object) associated with the <code>Tupel</code> entry
	 */
	public Object getExtra1(int iterator)
	{
		if (iterator < m_extra1.size())
			return(m_extra1.get(iterator));
		return(null);
	}

	public Object getExtra2(int iterator)
	{
		if (iterator < m_extra2.size())
			return(m_extra2.get(iterator));
		return(null);
	}

	public Object getExtra3(int iterator)
	{
		if (iterator < m_extra3.size())
			return(m_extra3.get(iterator));
		return(null);
	}

	public Object getExtra4(int iterator)
	{
		if (iterator < m_extra4.size())
			return(m_extra4.get(iterator));
		return(null);
	}

	public Object getExtra5(int iterator)
	{
		if (iterator < m_extra5.size())
			return(m_extra5.get(iterator));
		return(null);
	}

	public void setExtra1(int		iterator,
						  Object	object)
	{
		if (iterator < m_extra1.size())
			m_extra1.set(iterator, object);
	}

	public void setExtra2(int		iterator,
						  Object	object)
	{
		if (iterator < m_extra2.size())
			m_extra2.set(iterator, object);
	}

	public void setExtra3(int		iterator,
						  Object	object)
	{
		if (iterator < m_extra3.size())
			m_extra3.set(iterator, object);
	}

	public void setExtra4(int		iterator,
						  Object	object)
	{
		if (iterator < m_extra4.size())
			m_extra4.set(iterator, object);
	}

	public void setExtra5(int		iterator,
						  Object	object)
	{
		if (iterator < m_extra5.size())
			m_extra5.set(iterator, object);
	}

	/**
	 * When the class is created, a UUID is assigned.
	 */
	private UUID			m_uuid;

	/**
	 * Holds list of String items for the tupel.  Has a 1:1 relationship with
	 * the entries in m_objects.
	 */
	private List<String>	m_names;

	/**
	 * Holds list of Object items for the tupel.  Has a 1:1 relationship with
	 * the entries in m_names.
	 */
	private List<Object>	m_objects;

	/**
	 * Holds list of extra Object items for the tupel.  Has a 1:1 relationship
	 * with the entries in m_names.
	 */
	private List<Object>	m_extra1;
	private List<Object>	m_extra2;
	private List<Object>	m_extra3;
	private List<Object>	m_extra4;
	private List<Object>	m_extra5;
}
