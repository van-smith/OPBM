/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for benchmarking.  It executes scripts,
 * shows the heads-up display, displays the single-step debugger, etc.
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

package opbm.benchmarks.environment;

import java.util.List;

public class Stack
{
	public Stack(int		type,
				 Object		reference,
				 boolean	branchTaken)
	{
		m_type			= type;
		m_reference		= reference;
		m_branchTaken	= branchTaken;
	}


	public int getType()				{	return(m_type);				}
	public Object getReference()		{	return(m_reference);		}
	public boolean getBranchTaken()		{	return(m_branchTaken);		}


	public static Object getReference(List<Stack> stack)
	{
		if (stack.isEmpty())
			return(null);
		else
			return(stack.get(stack.size() - 1).getReference());
	}


	public static void enterNewBlock(int			type,
									 Object			reference,
									 List<Stack>	stack)
	{
		// These items are broken out explicitly for the purposes of tracing during debugging
		// A simpler method would be to test the values and do a single add
		switch (type)
		{
			case _STACK_ROOT:
				stack.add(new Stack(type, reference, false));
				break;

			case _STACK_SWITCH:
				stack.add(new Stack(type, reference, false));
				break;

			case _STACK_DO:
				stack.add(new Stack(type, reference, false));
				break;

			case _STACK_WHILE:
				stack.add(new Stack(type, reference, false));
				break;

			case _STACK_IF:
				stack.add(new Stack(type, reference, false));
				break;

			case _STACK_FOR:
				stack.add(new Stack(type, reference, false));
				break;

			case _STACK_FUNCTION:
				stack.add(new Stack(type, reference, false));
				break;

		}
	}


	public static boolean leaveBlockIfMatch(int				type,
											List<Stack>		stack)
	{
		if (isPreviousBlock(type, stack))
		{
			stack.remove(stack.size() - 1);
			return(true);
		}
		return(false);
	}


	public static boolean isPreviousBlock(int				type,
										  List<Stack>		stack)
	{

		if (!stack.isEmpty())
		{
			if (stack.get(stack.size() - 1).getType() == type)
			{
				// We're good, it should be what was found
				return(true);

			} else {
				// Syntax error
				return(false);

			}
		} else {
			return(false);

		}
	}


	public static boolean isThereAPreviousBlockOfType(int			type,
													  List<Stack>	stack)
	{
		int i;

		// Search backward from the end to the beginning, looking for the type specified
		for (i = stack.size() - 1; i >= 0 ; i--)
		{
			if (stack.get(i).getType() == type)
				return(true);	// Found it
		}
		// If we get here, the specified type was not found upward on the stack
		return(false);
	}


	public static void popBackTo(int			type,
								 List<Stack>	stack)
	{
		int i;

		// Search backward from the end to the beginning, looking for the type specified
		for (i = stack.size() - 1; i >= 0 ; i--)
		{
			if (stack.get(i).getType() == type)
				return;	// We're where we should be

			// Remove this item
			stack.remove(i);
		}
	}


	public static void popBack(List<Stack> stack)
	{
		if (!stack.isEmpty())
			stack.remove(stack.size() - 1);
	}


	public static final int	_STACK_ROOT			= 0;
	public static final int	_STACK_SWITCH		= 1;
	public static final int	_STACK_DO			= 2;
	public static final int	_STACK_WHILE		= 3;
	public static final int	_STACK_IF			= 4;
	public static final int	_STACK_FOR			= 5;
	public static final int	_STACK_FUNCTION		= 6;

	private int			m_type;
	private Object		m_reference;
	private boolean		m_branchTaken;		// used for if..elseif..else testing
}
