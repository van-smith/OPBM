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
 *
 * @author Rick C. Hodgin
 */
public class Tuple
{
	/**
	 * Constructor, creates a UUID for unique reference to the created tuple.
	 * If needed through global access throughout the system, also pass a
	 * reference to the Opbm class, so a callback can be made to register it
	 * globally.  This constructor is only used for local uses, not global.
	 */
	public Tuple()
	{
		m_opbm				= null;
		m_uuid				= Utils.getUUID();
		m_first				= new ArrayList<String>(0);
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
		m_opbm				= opbm;
		m_uuid				= Utils.getUUID();
		m_first				= new ArrayList<String>(0);
		m_second			= new ArrayList<Object>(0);
		m_third				= new ArrayList<Object>(0);
		m_fourth			= new ArrayList<Object>(0);
		m_fifth				= new ArrayList<Object>(0);
		m_sixth				= new ArrayList<Object>(0);
		m_seventh			= new ArrayList<Object>(0);
		m_triggerCommand	= new ArrayList<String>(0);
		m_triggerFilters	= new ArrayList<String>(0);
		opbm.addTuple(this);
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
	 * Adds a new entry to the tuple, which could be a duplicate.
	 *
	 * @param first <code>String</code> to identify the object
	 */
	public int add(String first)
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
	public int add(String first, Object second)
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
	public int add(String first, Object second, Object third)
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
	public int add(String first, Object second, Object third, Object fourth)
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
	public int add(String first, Object second, Object third, Object fourth, Object fifth)
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
	public int add(String first, Object second, Object third, Object fourth, Object fifth, Object sixth)
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
	public int add(String first, Object second, Object third, Object fourth, Object fifth, Object sixth, Object seventh)
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
				return(m_first.get(i));
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
				returnValue = m_first.get(i);
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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
			if (m_first.get(i).equalsIgnoreCase(name))
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

	/**
	 * Returns the explicit item within the list
	 * @param item item number to access
	 * @return first item (String) associated with the <code>Tuple</code> entry
	 */
	public String getFirst(int item)
	{
		if (item < m_first.size())
			return(m_first.get(item));
		return("");
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
		if (item < m_second.size())
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
	 * Holds list of String items for the tuple.  Has a 1:1 relationship with
	 * the entries in m_objects.
	 */
	private volatile List<String>	m_first;

	/**
	 * Holds list of Object items for the named tuple.  Each line has a 1:1
	 * relationship with the entries in m_names.
	 */
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
