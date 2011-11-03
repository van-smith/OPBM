/*
 * OPBM - Office Productivity Benchmark
 *
 * This class stores two primary items, along with extra items.  The first
 * item is typically a name.  It's used to allow disparate, un-connected
 * classes to pass "lazy" messages back and forth, or to store items across
 * method boundaries which may be executed in different threads, etc.  This
 * allows an indeterminate amount of time to elapse between uses, and for data
 * to be handled appropriately across lifespans.  In addition, what would be
 * otherwise unassociated generic items can be sorted and stored by this class,
 * allowing for a single name to reference a lot of information about a
 * particular item of interest.
 *
 * Originally there were two items, with one extra item.  It has since evolved
 * into a seven item (plus a trigger command + filters) construction, making
 * it extremely useful for correlating large sets of related data, that must
 * be maintained separately, but by a single, generic tool.
 *
 * This evolution has added a minor impact to performance.  It should not be
 * used where performance is necessary, unless more than two or three items are
 * required.  For the two- or three-instance items, it might be worth copying
 * this class and creating a true Tuple and remaining this one to TuplePlus or
 * something similar.
 *
 * Last Updated:  Sep 14, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.2.0
 *
 */

package opbm.common;

import java.util.ArrayList;
import java.util.List;
import opbm.Opbm;

/**
 * Class globally storing two items, a string and an object.  This class was
 * later to expanded to include far more items.  However, the principle is
 * the same.  The first item is the id/name associated with searching for items
 * contained within.  The other items are data items.  However, all items can
 * also be data items by using an explicit index and then referencing the first,
 * second, third, fourth, fifth, sixth, seventh... items individually.
 *
 * The <code>Tuple</code> generates its own UUID in the constructor.  This item
 * is said to be "crytographically strong" and in practice should be completely
 * unique throughout the duration of an instance of a running JVM.
 *
 * It's functionality is governed by:  http://www.ietf.org/rfc/rfc4122.txt
 *
 * A trigger command has also been added, so that when an item is updated, if
 * there is a trigger it will execute that command.  This is useful for other-
 * thread updates, which may happen far later than the code was established.
 * Rather than executing a continuous loop looking for that other code to
 * complete at some point, assuming it was spawned in another thread, it can
 * go ahead and continue processing the rest of its operation by setting up a
 * command in the Commands class, and allowing it to proceed.
 */
public final class Tuple
{
	/**
	 * Constructor only used for local uses, not global (does not report
	 * itself to master class Opbm).
	 */
	public Tuple()
	{
		m_opbm = null;
		initialize();
	}

	/**
	 * Constructor only used for local uses, not global (does not report
	 * itself to master class Opbm).
	 * @param first string to add to the tuple
	 */
	public Tuple(String first)
	{
		m_opbm = null;
		initialize();
		add(first);
	}

	/**
	 * Constructor only used for local uses, not global (does not report
	 * itself to master class Opbm).
	 * @param first string to add to the new tuple
	 * @param second object to add to the new tuple
	 */
	public Tuple(String		first,
				 Object		second)
	{
		m_opbm = null;
		initialize();
		add(first, second);
	}

	/**
	 * Constructor only used for local uses, not global (does not report
	 * itself to master class Opbm).
	 * @param first string to add to the new tuple
	 * @param second object to add to the new tuple
	 * @param third object to add to the new tuple
	 */
	public Tuple(String		first,
				 Object		second,
				 Object		third)
	{
		m_opbm = null;
		initialize();
		add(first, second, third);
	}

	/**
	 * Constructor only used for local uses, not global (does not report
	 * itself to master class Opbm).
	 * @param first string to add to the new tuple
	 * @param second object to add to the new tuple
	 * @param third object to add to the new tuple
	 * @param fourth object to add to the new tuple
	 */
	public Tuple(String		first,
				 Object		second,
				 Object		third,
				 Object		fourth)
	{
		m_opbm = null;
		initialize();
		add(first, second, third, fourth);
	}

	/**
	 * Constructor, creates a UUID for unique reference to the created tuple.
	 * This is necessary for an external <code>List<Tuple></code> to maintain
	 * the global state, hence the callback to the Opbm class, and its
	 * <code>add</code> function.
	 *
	 * @param opbm
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public Tuple(Opbm opbm)
	{
		m_opbm = opbm;
		initialize();
		opbm.addTuple(this);
	}

	/**
	 * Creates a UUID for unique reference to the created tuple.
	 */
	public void initialize()
	{
		m_uuid				= Utils.getUUID();
		m_first				= new ArrayList<Object>(0);
		m_second			= new ArrayList<Object>(0);
		m_third				= new ArrayList<Object>(0);
		m_fourth			= new ArrayList<Object>(0);
		m_fifth				= new ArrayList<Object>(0);
		m_sixth				= new ArrayList<Object>(0);
		m_seventh			= new ArrayList<Object>(0);
		m_triggerCommand	= new ArrayList<String>(0);
		m_triggerFilters	= new ArrayList<String>(0);
	}

	/**
	 * UUID in <code>String</code> form.
	 * @return UUID as a <code>String</code>
	 */
	public String getUUID()
	{
		return(m_uuid);
	}

	/**
	 * Called to setup a specified number of slots that need to exist for
	 * whatever processing is taking place
	 * @param max number of slots
	 */
	public void ensureThisManySlots(int max)
	{
		int i;

		// Make sure every entry has its place
		for (i = 0; i < max; i++)
		{
			if (m_first.size() < i + 1)
				add("");		// Add in a blank entry for this position
		}
	}

	/**
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 */
	public int add(Object first)
	{
		m_first.add(first);
		m_second.add(null);
		m_third.add(null);
		m_fourth.add(null);
		m_fifth.add(null);
		m_sixth.add(null);
		m_seventh.add(null);
		m_triggerCommand.add(null);
		m_triggerFilters.add(null);
		return(size() - 1);
	}

	/**
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 */
	public int add(Object first, Object second)
	{
		m_first.add(first);
		m_second.add(second);
		m_third.add(null);
		m_fourth.add(null);
		m_fifth.add(null);
		m_sixth.add(null);
		m_seventh.add(null);
		m_triggerCommand.add(null);
		m_triggerFilters.add(null);
		return(size() - 1);
	}

	/**
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 */
	public int add(Object first, Object second, Object third)
	{
		m_first.add(first);
		m_second.add(second);
		m_third.add(third);
		m_fourth.add(null);
		m_fifth.add(null);
		m_sixth.add(null);
		m_seventh.add(null);
		m_triggerCommand.add(null);
		m_triggerFilters.add(null);
		return(size() - 1);
	}

	/**
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 */
	public int add(Object first, Object second, Object third, Object fourth)
	{
		m_first.add(first);
		m_second.add(second);
		m_third.add(third);
		m_fourth.add(fourth);
		m_fifth.add(null);
		m_sixth.add(null);
		m_seventh.add(null);
		m_triggerCommand.add(null);
		m_triggerFilters.add(null);
		return(size() - 1);
	}

	/**
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 * @param fifth extra object to add
	 */
	public int add(Object first, Object second, Object third, Object fourth, Object fifth)
	{
		m_first.add(first);
		m_second.add(second);
		m_third.add(third);
		m_fourth.add(fourth);
		m_fifth.add(fifth);
		m_sixth.add(null);
		m_seventh.add(null);
		m_triggerCommand.add(null);
		m_triggerFilters.add(null);
		return(size() - 1);
	}

	/**
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 * @param fifth extra object to add
	 * @param sixth extra object to add
	 */
	public int add(Object first, Object second, Object third, Object fourth, Object fifth, Object sixth)
	{
		m_first.add(first);
		m_second.add(second);
		m_third.add(third);
		m_fourth.add(fourth);
		m_fifth.add(fifth);
		m_sixth.add(sixth);
		m_seventh.add(null);
		m_triggerCommand.add(null);
		m_triggerFilters.add(null);
		return(size() - 1);
	}

	/**
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 * @param fifth extra object to add
	 * @param sixth extra object to add
	 * @param seventh extra object to add
	 */
	public int add(Object first, Object second, Object third, Object fourth, Object fifth, Object sixth, Object seventh)
	{
		m_first.add(first);
		m_second.add(second);
		m_third.add(third);
		m_fourth.add(fourth);
		m_fifth.add(fifth);
		m_sixth.add(sixth);
		m_seventh.add(seventh);
		m_triggerCommand.add(null);
		m_triggerFilters.add(null);
		return(size() - 1);
	}

	/**
	 * Sets/updates an entry in the tuple, which could be a duplicate, at the
	 * specified index.
	 *
	 * @param first <code>String</code> to identify the object
	 */
	public int addAs(int item, Object first)
	{
		ensureThisManySlots(item);
		m_first.set(item, first);
		return(size() - 1);
	}

	/**
	 * Sets/updates an entry in the tuple, which could be a duplicate, at the
	 * specified index.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 */
	public int addAs(int item, Object first, Object second)
	{
		ensureThisManySlots(item);
		m_first.set(item, first);
		m_second.set(item, second);
		return(size() - 1);
	}

	/**
	 * Sets/updates an entry in the tuple, which could be a duplicate, at the
	 * specified index.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 */
	public int addAs(int item, Object first, Object second, Object third)
	{
		ensureThisManySlots(item);
		m_first.set(item, first);
		m_second.set(item, second);
		m_third.set(item, third);
		return(size() - 1);
	}

	/**
	 * Sets/updates an entry in the tuple, which could be a duplicate, at the
	 * specified index.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 */
	public int addAs(int item, Object first, Object second, Object third, Object fourth)
	{
		ensureThisManySlots(item);
		m_first.set(item, first);
		m_second.set(item, second);
		m_third.set(item, third);
		m_fourth.set(item, fourth);
		return(size() - 1);
	}

	/**
	 * Sets/updates an entry in the tuple, which could be a duplicate, at the
	 * specified index.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 * @param fifth extra object to add
	 */
	public int addAs(int item, Object first, Object second, Object third, Object fourth, Object fifth)
	{
		ensureThisManySlots(item);
		m_first.set(item, first);
		m_second.set(item, second);
		m_third.set(item, third);
		m_fourth.set(item, fourth);
		m_fifth.set(item, fifth);
		return(size() - 1);
	}

	/**
	 * Sets/updates an entry in the tuple, which could be a duplicate, at the
	 * specified index.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 * @param fifth extra object to add
	 * @param sixth extra object to add
	 */
	public int addAs(int item, Object first, Object second, Object third, Object fourth, Object fifth, Object sixth)
	{
		ensureThisManySlots(item);
		m_first.set(item, first);
		m_second.set(item, second);
		m_third.set(item, third);
		m_fourth.set(item, fourth);
		m_fifth.set(item, fifth);
		m_sixth.set(item, sixth);
		return(size() - 1);
	}

	/**
	 * Sets/updates an entry in the tuple, which could be a duplicate, at the
	 * specified index.
	 *
	 * @param first <code>String</code> to identify the object
	 * @param second object to add
	 * @param third extra object to add
	 * @param fourth extra object to add
	 * @param fifth extra object to add
	 * @param sixth extra object to add
	 * @param seventh extra object to add
	 */
	public int addAs(int item, Object first, Object second, Object third, Object fourth, Object fifth, Object sixth, Object seventh)
	{
		ensureThisManySlots(item);
		m_first.set(item, first);
		m_second.set(item, second);
		m_third.set(item, third);
		m_fourth.set(item, fourth);
		m_fifth.set(item, fifth);
		m_sixth.set(item, sixth);
		m_seventh.set(item, seventh);
		return(size() - 1);
	}

	/**
	 * Searches the tuples by its second (the object) and returns the
	 * associated string if found.
	 *
	 * @param o object to search
	 * @return associated string if found, empty string otherwise
	 */
	public String getFirst(Object o)
	{
		int i;

		for (i = 0; i < m_second.size(); i++)
		{
			if (m_second.get(i).equals(o))
			{
				return((String)m_first.get(i));
			}
		}
		return("");
	}

	/**
	 * Searches the tuples by its first (the String) and returns the associated
	 * object if found.
	 *
	 * @param name String to search
	 * @return object if found, null otherwise
	 */
	public Object getSecond(String name)
	{
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				return(m_second.get(i));
			}
		}
		return(null);
	}

	/**
	 * Searches the tuples by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getThird(String name)
	{
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
				return(m_third.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tuples by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getFourth(String name)
	{
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
				return(m_fourth.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tuples by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getFifth(String name)
	{
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
				return(m_fifth.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tuples by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getSixth(String name)
	{
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
				return(m_sixth.get(i));
		}
		return(null);
	}

	/**
	 * Searches the tuples by its first (the String) and returns the associated
	 * extra object if found.
	 *
	 * @param name String to search
	 * @return extra object if name found, null otherwise
	 */
	public Object getSeventh(String name)
	{
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
				return(m_seventh.get(i));
		}
		return(null);
	}

	public String getTriggerCommand(int item)
	{
		if (item < 7)
			return(m_triggerCommand.get(item));
		return("");
	}

	/**
	 * Sets the first property (the String) of the specified tuple, as searched
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

		for (i = 0; i < m_second.size(); i++)
		{
			if (m_second.get(i).equals(o))
			{
				returnValue = (String)m_first.get(i);
				m_first.set(i, newValue);
				checkTrigger(i, "1");
				break;
			}
		}
		return(returnValue);
	}

	/**
	 * Sets the second property (the object) of the specified tuple, as searched
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

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				returnObject = m_second.get(i);
				m_second.set(i, newObject);
				checkTrigger(i, "2");
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tuple, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setThird(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				returnObject = m_third.get(i);
				m_third.set(i, newObject);
				checkTrigger(i, "3");
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tuple, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setFourth(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				returnObject = m_fourth.get(i);
				m_fourth.set(i, newObject);
				checkTrigger(i, "4");
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tuple, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setFifth(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				returnObject = m_fifth.get(i);
				m_fifth.set(i, newObject);
				checkTrigger(i, "5");
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tuple, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setSixth(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				returnObject = m_sixth.get(i);
				m_sixth.set(i, newObject);
				checkTrigger(i, "6");
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Sets the second property (the object) of the specified tuple, as searched
	 * by its first property (the String), and updates the extra object.
	 *
	 * @param name String to search
	 * @param newObject new object to set
	 * @return old object value
	 */
	public Object setSeventh(String name, Object newObject)
	{
		Object returnObject = null;
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				returnObject = m_seventh.get(i);
				m_seventh.set(i, newObject);
				checkTrigger(i, "7");
				break;
			}
		}
		return(returnObject);
	}

	/**
	 * Deletes the specfied tuple by searching its first property (the String).
	 * Deletes if found. Ignores otherwise.
	 *
	 * @param name String name to delete
	 */
	public void remove(String name)
	{
		int i;

		for (i = 0; i < m_first.size(); i++)
		{
			if (((String)m_first.get(i)).equalsIgnoreCase(name))
			{
				m_first.remove(i);
				m_second.remove(i);
				m_third.remove(i);
				m_fourth.remove(i);
				m_fifth.remove(i);
				m_sixth.remove(i);
				m_seventh.remove(i);
				m_triggerCommand.remove(i);
				m_triggerFilters.remove(i);
				break;
			}
		}
	}

	/**
	 * Deletes the specified tuple by searching its second property (the object).
	 * Deletes if found. Ignores otherwise.
	 *
	 * @param o Object to delete
	 */
	public void remove(Object o)
	{
		int i;

		for (i = 0; i < m_second.size(); i++)
		{
			if (m_second.get(i).equals(o))
			{
				m_first.remove(i);
				m_second.remove(i);
				m_third.remove(i);
				m_fourth.remove(i);
				m_fifth.remove(i);
				m_sixth.remove(i);
				m_seventh.remove(i);
				m_triggerCommand.remove(i);
				m_triggerFilters.remove(i);
				break;
			}
		}
	}

	/**
	 * Empties out the contents of the tuple
	 */
	public void clear()
	{
		m_first.clear();
		m_second.clear();
		m_third.clear();
		m_fourth.clear();
		m_fifth.clear();
		m_sixth.clear();
		m_seventh.clear();
		m_triggerCommand.clear();
		m_triggerFilters.clear();
	}

	/**
	 * For iterative functions, returns size of the <code>Tuple</code> list
	 * @return size (count) of m_names in items (which will also be the same
	 * size as m_objects and m_extra1, as these are all linked 1:1)
	 */
	public int size()
	{
		return(m_first.size());
	}

	public boolean isEmpty()
	{
		return(m_first.isEmpty());
	}

	public boolean setNumbered(int		number,
							   int		item,
							   Object	data)
	{
		if (number >= 1 && number <= 7)
		{	// We're good, it's a first through seventh
			if (item < m_first.size())
			{	// Still good
				switch (number)
				{	// Return the numbered item
					case 1:
						m_first.set(item, data);
						break;
					case 2:
						m_second.set(item, data);
						break;
					case 3:
						m_third.set(item, data);
						break;
					case 4:
						m_fourth.set(item, data);
						break;
					case 5:
						m_fifth.set(item, data);
						break;
					case 6:
						m_sixth.set(item, data);
						break;
					case 7:
						m_seventh.set(item, data);
						break;
				}
				// Success
				return(true);

			} else {
				// Failure, out of bounds
			}

		} else {
			// Failure, not a first through seventh set action
		}
		return(false);
	}

	public Object getNumbered(int	number,
							  int	item)
	{
		if (number >= 1 && number <= 7)
		{	// We're good, it's a first through seventh
			if (item < m_first.size())
			{	// Still good
				switch (number)
				{	// Return the numbered item
					case 1:
						return(m_first.get(item));
					case 2:
						return(m_second.get(item));
					case 3:
						return(m_third.get(item));
					case 4:
						return(m_fourth.get(item));
					case 5:
						return(m_fifth.get(item));
					case 6:
						return(m_sixth.get(item));
					case 7:
						return(m_seventh.get(item));
				}

			} else {
				// Failure, out of bounds
			}

		} else {
			// Failure
		}
		return(null);
	}

	/**
	 * Returns the explicit item within the list
	 * @param item item number to access
	 * @return first item (String) associated with the <code>Tuple</code> entry
	 */
	public String getFirst(int item)
	{
		if (item < m_first.size())
			return((String)m_first.get(item));
		return("");
	}

	/**
	 * Returns the explicit item within the list
	 * @param item item number to access
	 * @return first item (String) associated with the <code>Tuple</code> entry
	 */
	public Object getFirstObject(int item)
	{
		if (item < m_first.size())
			return(m_first.get(item));
		return(null);
	}

	/**
	 * Returns the explicit item within the list
	 * @param item item number to access
	 * @return second item (Object) associated with the <code>Tuple</code> entry
	 */
	public Object getSecond(int item)
	{
		if (item < m_second.size())
			return(m_second.get(item));
		return(null);
	}

	/**
	 * Returns the explicit item within the list
	 * @param item item number to access
	 * @return second item (Object) associated with the <code>Tuple</code> entry
	 */
	public Object getThird(int item)
	{
		if (item < m_third.size())
			return(m_third.get(item));
		return(null);
	}

	public Object getFourth(int item)
	{
		if (item < m_fourth.size())
			return(m_fourth.get(item));
		return(null);
	}

	public Object getFifth(int item)
	{
		if (item < m_fifth.size())
			return(m_fifth.get(item));
		return(null);
	}

	public Object getSixth(int item)
	{
		if (item < m_sixth.size())
			return(m_sixth.get(item));
		return(null);
	}

	public Object getSeventh(int item)
	{
		if (item < m_seventh.size())
			return(m_seventh.get(item));
		return(null);
	}

	public void setFirst(int		item,
						 String		name)
	{
		if (item < m_first.size())
		{
			m_first.set(item, name);
			checkTrigger(item, "1");
		}
	}

	public void setSecond(int		item,
						  Object	object)
	{
		if (item < m_second.size())
		{
			m_second.set(item, object);
			checkTrigger(item, "2");
		}
	}

	public void setThird(int		item,
						 Object		object)
	{
		if (item < m_third.size())
		{
			m_third.set(item, object);
			checkTrigger(item, "3");
		}
	}

	public void setFourth(int		item,
						  Object	object)
	{
		if (item < m_fourth.size())
		{
			m_fourth.set(item, object);
			checkTrigger(item, "4");
		}
	}

	public void setFifth(int		item,
						 Object		object)
	{
		if (item < m_fifth.size())
		{
			m_fifth.set(item, object);
			checkTrigger(item, "5");
		}
	}

	public void setSixth(int		item,
						 Object		object)
	{
		if (item < m_sixth.size())
		{
			m_sixth.set(item, object);
			checkTrigger(item, "6");
		}
	}

	public void setSeventh(int		item,
						   Object	object)
	{
		if (item < m_seventh.size())
		{
			m_seventh.set(item, object);
			checkTrigger(item, "7");
		}
	}

	public void addSecondDouble(int		item,
								Double	value)
	{
		if (item < m_second.size())
			m_second.set(item, ((Double)m_second.get(item)) + value);
	}

	public void addThirdDouble(int		item,
							   Double	value)
	{
		if (item < m_third.size())
			m_third.set(item, ((Double)m_third.get(item)) + value);
	}

	public void addFourthDouble(int		item,
								Double	value)
	{
		if (item < m_fourth.size())
			m_fourth.set(item, ((Double)m_fourth.get(item)) + value);
	}

	public void addFifthDouble(int		item,
							   Double	value)
	{
		if (item < m_fifth.size())
			m_fifth.set(item, ((Double)m_fifth.get(item)) + value);
	}

	public void addSixthDouble(int		item,
							   Double	value)
	{
		if (item < m_sixth.size())
			m_sixth.set(item, ((Double)m_sixth.get(item)) + value);
	}

	public void addSeventhDouble(int		item,
								 Double		value)
	{
		if (item < m_seventh.size())
			m_seventh.set(item, ((Double)m_seventh.get(item)) + value);
	}

	public void addSecondInteger(int		item,
								 Integer	value)
	{
		if (item < m_second.size())
			m_second.set(item, ((Integer)m_second.get(item)) + value);
	}

	public void addThirdInteger(int			item,
								Integer		value)
	{
		if (item < m_third.size())
			m_third.set(item, ((Integer)m_third.get(item)) + value);
	}

	public void addFourthInteger(int		item,
								 Integer	value)
	{
		if (item < m_fourth.size())
			m_fourth.set(item, ((Integer)m_fourth.get(item)) + value);
	}

	public void addFifthInteger(int			item,
								Integer		value)
	{
		if (item < m_fifth.size())
			m_fifth.set(item, ((Integer)m_fifth.get(item)) + value);
	}

	public void addSixthInteger(int		item,
							   Integer	value)
	{
		if (item < m_sixth.size())
			m_sixth.set(item, ((Integer)m_sixth.get(item)) + value);
	}

	public void addSeventhInteger(int		item,
								  Integer	value)
	{
		if (item < m_seventh.size())
			m_seventh.set(item, ((Integer)m_seventh.get(item)) + value);
	}

	/**
	 * This command will be triggered.  If a trigger filter is in place, it
	 * will only be triggered when the specified item(s) updates.
	 * @param item
	 * @param triggerCommand
	 */
	public void setTriggerCommand(int		item,
								  String	triggerCommand)
	{
		if (item < m_triggerCommand.size())
			m_triggerCommand.set(item, triggerCommand);
	}

	/**
	 * Allows a string like "127" to indicate that the associated command
	 * should only be updated when the first, second or seventh items are
	 * updated.  If this parameter is not specified, any time any of the
	 * seven items are updated, it will be triggered.
	 * Note:  A blocking set of filters can be setup, because the value that
	 *        will be checked for the filter condition being true is the
	 *        number of the item updated, as in "1" through "7".  If a word
	 *        or some invalid number is input (like "8") then the filter will
	 *        always fail.  This can be useful to have a trigger "encoded" into
	 *        the tuple, but to have it ONLY execute when an explicit call to
	 *        executeTrigger() is made.
	 * @param item
	 * @param triggerFilters
	 */
	public void setTriggerFilters(int		item,
								  String	triggerFilters)
	{
		if (item < m_triggerFilters.size())
			m_triggerFilters.set(item, triggerFilters);
	}

	/**
	 * Checks to see if a trigger command is associated with the
	 * @param item the entry in the
	 * @param itemFilter
	 */
	public void checkTrigger(int		item,
							 String		itemFilter)
	{
		String triggerCommand, triggerFilters;

		if (item < m_triggerCommand.size())
		{
			triggerCommand = m_triggerCommand.get(item);
			if (triggerCommand != null && !triggerCommand.isEmpty())
			{	// There is a command
				// See if there are filters
				triggerFilters = m_triggerFilters.get(item);
				if (!triggerFilters.isEmpty())
				{	// There are filters, see if we're okay
					if (!triggerFilters.contains(itemFilter))
					{	// Nope
						return;
					}
					// If we get here, it matched the filter, execute
				}
				// Execute this entry, because it passes our tests
				executeTrigger(item);
			}
		}
	}

	/**
	 * Called internally, and by users, to ALWAYS execute the trigger
	 * command regardless of the filter conditions
	 * @param item
	 */
	public void executeTrigger(int item)
	{
		String triggerCommand;

		if (item < m_triggerCommand.size())
		{
			triggerCommand = m_triggerCommand.get(item);
			if (triggerCommand != null && !triggerCommand.isEmpty())
			{	// There is a command
				if (m_opbm != null)
				{
					m_opbm.getCommandMaster().processCommand(m_uuid,
															 triggerCommand,
															 m_first.get(item),
															 m_second.get(item),
															 m_third.get(item),
															 m_fourth.get(item),
															 m_fifth.get(item),
															 m_sixth.get(item),
															 m_seventh.get(item),
															 Integer.toString(item),	/* item user asked to trigger the command */
															 "--forced--",				/* indicate execution was forced */
															 "--forced--");				/* indicate execution was forced */
				}
			}
		}
	}

	/**
	 * The master parent for this tuple's callbacks
	 */
	private Opbm					m_opbm;

	/**
	 * When the class is created, a UUID is assigned.
	 */
	private String					m_uuid;

	/**
	 * Holds list of entries for the tuple.  Each List<*> item below a 1:1
	 * relationship with every other List<*> entry
	 */
	private volatile List<Object>	m_first;
	private volatile List<Object>	m_second;
	private volatile List<Object>	m_third;
	private volatile List<Object>	m_fourth;
	private volatile List<Object>	m_fifth;
	private volatile List<Object>	m_sixth;
	private volatile List<Object>	m_seventh;
	// The name of a command to trigger when this value is updated:
	private volatile List<String>	m_triggerCommand;	// Commands to execute when each item is updated
	private volatile List<String>	m_triggerFilters;	// Filters, to indicate when commands are executed
}
