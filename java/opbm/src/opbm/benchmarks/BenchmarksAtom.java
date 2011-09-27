/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for atom benchmarking.  It executes atoms,
 * including all abstracts, flow controls one-by-one, through the entire
 * sequence provided for by the source Xml, called "atom" here.
 *
  * Last Updated:  Sep 12, 2011
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

package opbm.benchmarks;

import opbm.benchmarks.hud.StreamGobbler;
import opbm.benchmarks.environment.Variables;
import opbm.benchmarks.environment.Stack;
import opbm.common.Tuple;
import opbm.common.Utils;
import java.util.ArrayList;
import java.util.List;
import opbm.Opbm;
import opbm.common.Xml;
import opbm.dialogs.OpbmDialog;

public class BenchmarksAtom
{
	public BenchmarksAtom(BenchmarkParams bp)
	{
		// Store our benchmark parameters
		m_bp		= bp;
	}

	/**
	 * Called to execute the current command pointed to
	 * @param thisCommand command to process
	 * @param atom parent atom source of thisCommand
	 * @param xmlRun_Success xml to append tags to for this portion of the run when it succeeds
	 * @param xmlRun_Failure xml to append tags to for this portion of the run if there are failures
	 * @return next command to execute (if any)
	 */
	public Xml processCommand(Xml	thisCommand,
							  Xml	atom,
							  Xml	xmlRun_Success,
							  Xml	xmlRun_Failure)
	{
//////////
// FLOW
		if (thisCommand.getName().equalsIgnoreCase("flow")) {
			// It's a flow-control directive
			return(processFlow_Atom(thisCommand, atom, xmlRun_Success, xmlRun_Failure));

//////////
// ABSTRACT
		} else if (thisCommand.getName().equalsIgnoreCase("abstract")) {
			// It's an abstract command
			return(processAbstract_Atom(thisCommand, xmlRun_Success, xmlRun_Failure));

		}
		// If we get here, we found something invalid.
		// Ignore it and its invalidity, but log the entry for future reference
		xmlRun_Success.appendChild(new Xml("error", thisCommand.getName(), "type", "Unrecognized keyword for atom, expected flow or abstract"));
		return(thisCommand.getNext());
	}

	/**
	 * Processes a flow-control directive specified by thisCommand
	 * @param thisCommand command to process
	 * @param atom parent atom source of thisCommand
	 * @param xmlRun_Success xml to append tags to for this portion of the run when it succeeds
	 * @param xmlRun_Failure xml to append tags to for this portion of the run if there are failures
	 * @return
	 */
	public Xml processFlow_Atom(Xml		thisCommand,
								Xml		atom,
								Xml		xmlRun_Success,
								Xml		xmlRun_Failure)
	{
		String name1, value1, value2, value3, sourceName, left, comparator, right, variable, start, finish, step;
		Variables var1, var2;
		Xml options, next, target, xmlFlow;
		boolean result;

		options		= thisCommand.getChildNode("options");
		sourceName	= m_bp.m_macroMaster.parseMacros(thisCommand.getAttributeOrChild("sourcename"));
		xmlFlow		= xmlRun_Success.appendChild(Utils.processExecutableLine("flow",     /* = */ Utils.getTimestamp(),
																			 "command",  /* = */ sourceName,
																			 "name",     /* = */ thisCommand.getAttribute("name")));

//////////
// CALL
// Validated
		if (sourceName.equalsIgnoreCase("call")) {
			// call
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("function"));
			xmlFlow.appendChild(new Xml("note", "calling " + name1));
			target	= processFlow_LocateFlowWithName_DownFromHere("function", "function", name1, atom.getFirstChild());
			if (target == null)
			{
				xmlFlow.appendChild(new Xml("error", name1 + " not found"));
				return(null);
			}
			Stack.enterNewBlock(Stack._STACK_FUNCTION, thisCommand.getNext(), m_bp.m_atomStack);
			return(target.getNext());


//////////
// FUNCTION
// Validated
		} else if (sourceName.equalsIgnoreCase("function")) {
			// At the function definition, we skip past it to the functionEnd, and continue on after that
			xmlFlow.appendChild(new Xml("note", "goto next line after functionEnd"));
			next = processFlow_LocateFlow_DownFromHere("functionEnd", thisCommand.getNext());
			if (next == null)
			{
				// REMEMBER what to do here with this error
				int i = 5;
			}
			return(next.getNext());


//////////
// FUNCTIONEND
// Validated
		} else if (sourceName.equalsIgnoreCase("functionEnd")) {
			xmlFlow.appendChild(new Xml("note", "reached functionEnd"));
			if (!Stack.isPreviousBlock(Stack._STACK_FUNCTION, m_bp.m_atomStack))
			{
				xmlFlow.appendChild(new Xml("error", "functionEnd not inside function block"));
				return(null);
			}
			// Grab the return address
			next = (Xml)Stack.getReference(m_bp.m_atomStack);
			Stack.popBack(m_bp.m_atomStack);
			xmlFlow.appendChild(new Xml("note", "returning to line after call"));
			return(next);


//////////
// RETURN
// Validated
		} else if (sourceName.equalsIgnoreCase("return")) {
			xmlFlow.appendChild(new Xml("note", "reached return"));
			if (!Stack.isThereAPreviousBlockOfType(Stack._STACK_FUNCTION, m_bp.m_atomStack))
			{
				// REMEMBER, if we are in a called state, such as an Atom, Molecule, Scenario or Suite calling an Atom, then we can return to that calling thing
				xmlFlow.appendChild(new Xml("error", "not inside function block"));
				return(null);
			}
			// Grab the return address
			Stack.popBackTo(Stack._STACK_FUNCTION, m_bp.m_atomStack);	// Remove any if/switch/whatever-we-might-be-in right now
			next = (Xml)Stack.getReference(m_bp.m_atomStack);
			xmlFlow.appendChild(new Xml("note", "returning to line after call"));
			Stack.popBack(m_bp.m_atomStack);
			return(next);


//////////
// VARIABLE
// Validated
		} else if (sourceName.equalsIgnoreCase("variable")) {
			// They want to define a variable
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("name"));
			value1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value"));
			var1		= Variables.findVariable(name1, m_bp.m_atomVariables);
			if (var1 != null)
			{
				xmlFlow.appendChild(new Xml("note", "defining " + name1 + " to be variable " + value1 + ", which was previously " + var1.getValue()));
				Variables.updateOrAdd(name1, var1.getValue(), m_bp.m_atomVariables);	// They're defining this one to be a variable's value1

			} else {
				xmlFlow.appendChild(new Xml("note", "defining " + name1 + " to be constant " + value1));
				Variables.updateOrAdd(name1, value1, m_bp.m_atomVariables);				// Defining it to be a hard constant

			}
			return(thisCommand.getNext());


//////////
// ++
// Validated
		} else if (sourceName.equalsIgnoreCase("++")) {
			// They are incrementing a variable
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("name"));
			var1		= Variables.findVariable(name1, m_bp.m_atomVariables);
			if (var1 != null)
			{
				xmlFlow.appendChild(new Xml("note", "incrementing " + name1 + " by one, was " + var1.getValue()));
				var1.increaseValue();											// Incrementing an existing variable

			} else {
				xmlFlow.appendChild(new Xml("note", "defining new variable " + name1 + " to be 1"));
				m_bp.m_atomVariables.add(new Variables(name1, "1"));				// Does not exist, we define the value1 now to be 1
			}

			return(thisCommand.getNext());


//////////
// --
// Validated
		} else if (sourceName.equalsIgnoreCase("--")) {
			// They are decrementing a variable
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("name"));
			var1		= Variables.findVariable(name1, m_bp.m_atomVariables);
			if (var1 != null)
			{
				xmlFlow.appendChild(new Xml("note", "decrementing " + name1 + " by one, was " + var1.getValue()));
				var1.decreaseValue();		// Decrementing an existing variable

			} else {
				xmlFlow.appendChild(new Xml("note", "defining new variable " + name1 + " to be -1"));
				m_bp.m_atomVariables.add(new Variables(name1, "-1"));	// Does not exist, we define the value1 now to be -1

			}

			return(thisCommand.getNext());


//////////
// +=
// Validated
		} else if (sourceName.equalsIgnoreCase("+=")) {
			// They are adding something to the original variable, and assigning the new value1 to the first
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("name"));
			xmlFlow.appendChild(new Xml("note", "increasing " + name1));
			var1	= Variables.findVariable(name1, m_bp.m_atomVariables);
			if (var1 == null)
			{	// Does not exist, this is an error
				xmlFlow.appendChild(new Xml("error", "variable " + name1 + " not found"));
				return(null);
			}
			// Adding to the existing variable
			value1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value"));
			var2	= Variables.findVariable(value1, m_bp.m_atomVariables);
			if (var2 != null)
			{
				xmlFlow.appendChild(new Xml("note", "increasing " + name1 + " by variable " + value1 + ", which is " + var2.getValue()));
				var1.addValue(Integer.valueOf(var2.getValue()));			// Adding an existing variable to an existing variable

			} else {
				xmlFlow.appendChild(new Xml("note", "increasing " + name1 + " by constant " + value1));
				var1.addValue(Integer.valueOf(value1));						// Just adding a value1

			}
			return(thisCommand.getNext());


//////////
// -=
// Validated
		} else if (sourceName.equalsIgnoreCase("-=")) {
			// They are subtracting something from the original variable, and assigning the new value1 to the first
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("name"));
			xmlFlow.appendChild(new Xml("note", "decreasing " + name1));
			var1		= Variables.findVariable(name1, m_bp.m_atomVariables);
			if (var1 == null)
			{	// Does not exist, this is an error
				xmlFlow.appendChild(new Xml("error", "variable " + name1 + " not found"));
				return(null);
			}
			// Adding to the existing variable
			value1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value"));
			var2	= Variables.findVariable(value1, m_bp.m_atomVariables);
			if (var2 != null)
			{
				xmlFlow.appendChild(new Xml("note", "decreasing " + name1 + " by variable " + value1 + ", which was previously " + var2.getValue()));
				var1.subtractValue(Integer.valueOf(var2.getValue()));	// Subtracting an existing variable to an existing variable

			} else {
				xmlFlow.appendChild(new Xml("note", "decreasing " + name1 + " by constant " + value1));
				var1.subtractValue(Integer.valueOf(value1));	// Just subtracting a value1

			}
			return(thisCommand.getNext());


//////////
// GOTO
// Validated
		} else if (sourceName.equalsIgnoreCase("goto")) {
			// goto
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("label"));
			xmlFlow.appendChild(new Xml("note", "moving to label " + name1));
			target	= processFlow_LocateFlowWithName_DownFromHere("label", "label", name1, atom.getFirstChild());
			if (target == null)
			{
				xmlFlow.appendChild(new Xml("error", "label " + name1 + " was not found"));
				return(null);
			}
			return(target.getNext());


//////////
// LABEL
// Validated
		} else if (sourceName.equalsIgnoreCase("label")) {
			// We ignore labels, and just continue onward, they are only used on goto or onOption1/onOption2
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("label"));
			xmlFlow.appendChild(new Xml("note", "label " + name1 + " encountered, continuing on"));
			return(thisCommand.getNext());


//////////
// SWITCH
// Validated
		} else if (sourceName.equalsIgnoreCase("switch")) {
			// switch
			name1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("variable"));
			xmlFlow.appendChild(new Xml("note", "switching " + name1));
			var1		= Variables.findVariable(name1, m_bp.m_atomVariables);
			if (var1 == null)
			{
				xmlFlow.appendChild(new Xml("error", "variable " + name1 + " not found"));
				return(null);
			}
			value1 = var1.getValue();
			xmlFlow.appendChild(new Xml("note", name1 + " has value of " + value1));

			// Check for case conditions until we reach the switchEnd
			target	= processFlow_LocateFlowCase_DownFromHere(xmlFlow, value1, thisCommand.getNext());
			if (target == null)
			{
				xmlFlow.appendChild(new Xml("error", "switch cannot continue"));
				return(null);
			}
			if (target == thisCommand)
			{
				// No case clause was found that matched.
				// This isn't an error, it just means we need to skip past the entire switch block, and continue on
				xmlFlow.appendChild(new Xml("note", "no case condition matched, skipping switch block"));
				target	= processFlow_LocateFlow_DownFromHere("switchEnd", thisCommand);
				if (target == null)
				{
					xmlFlow.appendChild(new Xml("error", "syntax error on switch block, no switchEnd found"));
					return(null);
				}
				return(target.getNext());

			} else {
				// We're good, we found the case, now move to it
				Stack.enterNewBlock(Stack._STACK_SWITCH, target, m_bp.m_atomStack);
				return(target);
			}


//////////
// CASE
// Validated
		} else if (sourceName.equalsIgnoreCase("case")) {
			// We ignore case conditions, and just fall through and let the processing continue onward, they are only used on switch commands
			xmlFlow.appendChild(new Xml("note", "encountered case condition without a preceding break, continuing"));
			return(thisCommand.getNext());


//////////
// BREAK
// Validated
		} else if (sourceName.equalsIgnoreCase("break")) {
			// break
			// We must move to the switchEnd
			if (!Stack.isPreviousBlock(Stack._STACK_SWITCH, m_bp.m_atomStack))
			{
				xmlFlow.appendChild(new Xml("error", "break encountered not in a switch block"));
				return(null);
			}
			// Grab the return address
			next = (Xml)Stack.getReference(m_bp.m_atomStack);
			Stack.popBack(m_bp.m_atomStack);
			// Continue on from this point until we find the switchEnd
			target	= processFlow_LocateFlow_DownFromHere("switchEnd", next);
			if (target == null)
			{
				xmlFlow.appendChild(new Xml("error", "syntax error on switch block, no switchEnd found"));
				return(null);
			}
			xmlFlow.appendChild(new Xml("note", "continuing after switch block"));
			return(target.getNext());


//////////
// DEFAULT
// Validated
		} else if (sourceName.equalsIgnoreCase("default")) {
			// We ignore the default case clause, and just fall through and let the processing continue onward, they are only used on switch commands
			xmlFlow.appendChild(new Xml("note", "encountered default condition without a preceding break, continuing"));
			return(thisCommand.getNext());


//////////
// SWITCHEND
// Validated
		} else if (sourceName.equalsIgnoreCase("switchEnd")) {
			// Signal end of switch with flow control
			if (!Stack.leaveBlockIfMatch(Stack._STACK_SWITCH, m_bp.m_atomStack))
			{
				xmlFlow.appendChild(new Xml("error", "encountered switchEnd not in a switch block"));
				return(null);
			}
			xmlFlow.appendChild(new Xml("note", "continuing after switch block"));
			return(thisCommand.getNext());


//////////
// DO
// Validated
		} else if (sourceName.equalsIgnoreCase("do")) {
			// do
			xmlFlow.appendChild(new Xml("note", "beginning do..until block"));
			Stack.enterNewBlock(Stack._STACK_DO, thisCommand.getNext(), m_bp.m_atomStack);
			return(thisCommand.getNext());


//////////
// UNTIL
// Validated
		} else if (sourceName.equalsIgnoreCase("until")) {
			// until
			if (!Stack.isPreviousBlock(Stack._STACK_DO, m_bp.m_atomStack))
			{
				xmlFlow.appendChild(new Xml("error", "encountered until not inside a do..until block"));
				return(null);
			}

			// Grab the location to return to if the logic condition passes
			next = (Xml)Stack.getReference(m_bp.m_atomStack);

			// Check the logic condition
			// Grab left-hand variable or constant
			left	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value1"));
			var1	= Variables.findVariable(left, m_bp.m_atomVariables);
			if (var1 != null)
				value1 = var1.getValue();
			else
				value1 = left;

			// Grab the comparator value
			comparator	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("comparator"));

			// Grab the right-hand variable or constant
			right	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value2"));
			var1	= Variables.findVariable(right, m_bp.m_atomVariables);
			if (var1 != null)
				value2 = var1.getValue();
			else
				value2 = right;

			if (!processFlow_IsValidLogicCondition(comparator))
			{
				xmlFlow.appendChild(new Xml("error", "invalid comparator found, " + comparator));
				return(null);
			}

			// We are repeating UNTIL the condition is true, so as long as it's false, we keep going
			xmlFlow.appendChild(new Xml("note", "testing until condition, " + left + "(" + value1 + ") " + comparator + " " + right + "(" + value2 + ")"));
			if (processFlow_TestLogicCondition(left, comparator, right))
			{
				// We're done with this, so we fall through and continue on
				xmlFlow.appendChild(new Xml("note", "leaving do..until block"));
				Stack.popBack(m_bp.m_atomStack);
				return(thisCommand.getNext());
			}
			xmlFlow.appendChild(new Xml("note", "looping through the do..until block again"));
			return(next);	// It is false, we do the loop again


//////////
// WHILE
// Validated
		} else if (sourceName.equalsIgnoreCase("while")) {
			// while
			// Check the logic condition
			// Grab left-hand variable or constant
			left	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value1"));
			var1	= Variables.findVariable(left, m_bp.m_atomVariables);
			if (var1 != null)
				value1 = var1.getValue();
			else
				value1 = left;

			// Grab the comparator value
			comparator	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("comparator"));

			// Grab the right-hand variable or constant
			right	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value2"));
			var1	= Variables.findVariable(right, m_bp.m_atomVariables);
			if (var1 != null)
				value2 = var1.getValue();
			else
				value2 = right;

			if (!processFlow_IsValidLogicCondition(comparator))
			{
				xmlFlow.appendChild(new Xml("error", "invalid comparator found, " + comparator));
				return(null);
			}

			xmlFlow.appendChild(new Xml("note", "testing while condition, " + left + "(" + value1 + ") " + comparator + " " + right + "(" + value2 + ")"));
			if (processFlow_TestLogicCondition(left, comparator, right))
			{
				// True, we enter the block (or process the block again if not the first time through
				if (!(Stack.isPreviousBlock(Stack._STACK_WHILE, m_bp.m_atomStack) && (Xml)Stack.getReference(m_bp.m_atomStack) == thisCommand))
				{
					xmlFlow.appendChild(new Xml("note", "entering while..whileEnd loop"));
					Stack.enterNewBlock(Stack._STACK_WHILE, thisCommand, m_bp.m_atomStack);

				} else {
					xmlFlow.appendChild(new Xml("note", "while..whileEnd condition still true, continuing"));

				}
				return(thisCommand.getNext());		// It is true, so we enter the loop

			}
			// False, we skip past this block
			xmlFlow.appendChild(new Xml("note", "while..whileEnd condition not true"));
			// Pop it back off the stack
			if (Stack.isPreviousBlock(Stack._STACK_WHILE, m_bp.m_atomStack) && (Xml)Stack.getReference(m_bp.m_atomStack) == thisCommand)
			{
				xmlFlow.appendChild(new Xml("note", "leaving while..whileEnd loop"));
				Stack.popBack(m_bp.m_atomStack);

			} else {
				// We have never entered the loop, it failed on the initial test
				xmlFlow.appendChild(new Xml("note", "skipping while..whileEnd block entirely"));

			}

			// Move to the whileEnd from there
			next = processFlow_LocateFlow_DownFromHere("whileEnd", thisCommand.getNext());
			if (next == null)
			{
				xmlFlow.appendChild(new Xml("error", "an internal error was encountered, please contact OPBM authors"));
				return(null);
			}
			xmlFlow.appendChild(new Xml("note", "continuing on after whileEnd"));
			return(next.getNext());


//////////
// WHILEEND
// Validated
		} else if (sourceName.equalsIgnoreCase("whileEnd")) {
			// whileEnd
			if (!Stack.isPreviousBlock(Stack._STACK_WHILE, m_bp.m_atomStack))
			{
				xmlFlow.appendChild(new Xml("error", "whileEnd encountered that didn't match up inside a while block"));
				return(null);
			}
			xmlFlow.appendChild(new Xml("note", "whileEnd encountered, testing while condition again"));
			next = (Xml)Stack.getReference(m_bp.m_atomStack);
			return(next);


//////////
// FOR
// Validated
		} else if (sourceName.equalsIgnoreCase("for")) {
			// for
			variable	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("variable"));

			start		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("startvalue"));
			var2		= Variables.findVariable(start, m_bp.m_atomVariables);
			if (var2 != null)
				value1 = var2.getValue();
			else
				value1 = start;

			finish		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("finishvalue"));
			var2		= Variables.findVariable(finish, m_bp.m_atomVariables);
			if (var2 != null)
				value2 = var2.getValue();
			else
				value2 = finish;

			// We default to a single step if not empty
			step		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("stepvalue")).trim();
			if (step == null || step.isEmpty() || Integer.valueOf(step) == 0)	// Step is an optional setting, defaults to 1
				step = "1";

			var2		= Variables.findVariable(step, m_bp.m_atomVariables);
			if (var2 != null)
				value3 = var2.getValue();
			else
				value3 = step;

			// See where we are, either first time through, or iterating another step
			if (!(Stack.isPreviousBlock(Stack._STACK_FOR, m_bp.m_atomStack) && (Xml)Stack.getReference(m_bp.m_atomStack) == thisCommand))
			{
				// First time through, set the variable to its initial value
				xmlFlow.appendChild(new Xml("note", "setting up for..next condition, for " + variable + " = " + start + " to " + finish + " step " + step + "(" + value3 + ")"));
				Variables.updateOrAdd(variable, start, m_bp.m_atomVariables);
				var1 = Variables.findVariable(variable, m_bp.m_atomVariables);
				Stack.enterNewBlock(Stack._STACK_FOR, thisCommand, m_bp.m_atomStack);
				next = thisCommand.getNext();

			} else {
				// We're iterating another step, increase the variable by its step value
				xmlFlow.appendChild(new Xml("note", "incrementing " + variable + " by step " + step + "(" + value3 + ")"));
				var1 = Variables.findVariable(variable, m_bp.m_atomVariables);
				var1.addValue(Integer.valueOf(step));

			}

			// Check the logic
			xmlFlow.appendChild(new Xml("note", variable + " = " + var1.getValue()));
			variable = var1.getValue();
			if (Integer.valueOf(step) > 0)
			{
				xmlFlow.appendChild(new Xml("note", "testing for condition, " + variable + " (lessThanOrEqualTo) " + value2));
				result = processFlow_TestLogicCondition(variable, "<=", value2);		// They're increasing

			} else {
				xmlFlow.appendChild(new Xml("note", "testing for condition, " + variable + " (greaterThanOrEqualTo) " + value2));
				result = processFlow_TestLogicCondition(variable, ">=", value2);		// They're decreasing
			}

			if (result)
			{
				xmlFlow.appendChild(new Xml("note", "continuing through another iteration"));
				return(thisCommand.getNext());	// We're still going, continue on through the for loop again

			} else {
				xmlFlow.appendChild(new Xml("note", "exiting the for..next loop"));
			}

			// We're done
			if (!Stack.isPreviousBlock(Stack._STACK_FOR, m_bp.m_atomStack))
			{
				xmlFlow.appendChild(new Xml("error", "an internal error was encountered, please contact OPBM authors"));
				return(null);
			}
			Stack.popBack(m_bp.m_atomStack);

			// Locate the "next" and be done with it
			next = processFlow_LocateFlow_DownFromHere("next", thisCommand.getNext());
			if (next == null)
			{
				xmlFlow.appendChild(new Xml("error", "a matching next was not found for this for..next block"));
				return(null);
			}
			xmlFlow.appendChild(new Xml("note", "continuing on after for..next block"));
			return(next.getNext());


//////////
// NEXT
// Validated
		} else if (sourceName.equalsIgnoreCase("next")) {
			// next
			xmlFlow.appendChild(new Xml("note", "next encountered"));
			if (!Stack.isPreviousBlock(Stack._STACK_FOR, m_bp.m_atomStack))
			{
				xmlFlow.appendChild(new Xml("error", "no matching for..next block was found"));
				return(null);
			}
			xmlFlow.appendChild(new Xml("error", "will test for condition again"));
			next = (Xml)Stack.getReference(m_bp.m_atomStack);
			return(next);


//////////
// IF
		} else if (sourceName.equalsIgnoreCase("if")) {
			// if
			Stack.enterNewBlock(Stack._STACK_IF, thisCommand.getNext(), m_bp.m_atomStack);
			return(thisCommand.getNext());


//////////
// ELSEIF
		} else if (sourceName.equalsIgnoreCase("elseif")) {
			// elseif
			return(thisCommand.getNext());


//////////
// ELSE
		} else if (sourceName.equalsIgnoreCase("else")) {
			// else
			return(thisCommand.getNext());


//////////
// IFEND
		} else if (sourceName.equalsIgnoreCase("ifEnd")) {
			// ifend
			if (!Stack.leaveBlockIfMatch(Stack._STACK_IF, m_bp.m_atomStack))
				return(null);	// REMEMBER what to do with an error like this

			return(thisCommand.getNext());


//////////
// LOG
		} else if (sourceName.equalsIgnoreCase("log")) {
			// log
			value1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("message"));
			xmlRun_Success.appendChild(new Xml("log", value1));
			return(thisCommand.getNext());


//////////
// WARNING
		} else if (sourceName.equalsIgnoreCase("warning")) {
			// warning
			value1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("message"));
			xmlRun_Success.appendChild(new Xml("warning", value1));
			return(thisCommand.getNext());


//////////
// DEBUG
		} else if (sourceName.equalsIgnoreCase("debug")) {
			// debug
			m_bp.m_debuggerActive = true;
			return(thisCommand.getNext());


//////////
// QUIT
// Validated
		} else if (sourceName.equalsIgnoreCase("quit")) {
			// quit
			value1	= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("value"));
			var1	= Variables.findVariable(value1, m_bp.m_atomVariables);
			if (var1 != null)
			{
				xmlFlow.appendChild(new Xml("note", "quitting with variable " + value1 + "(" + var1.getValue() + ")"));
				m_returnValue = Integer.valueOf(var1.getValue());		// They're returning the value1 of a variable

			} else {
				xmlFlow.appendChild(new Xml("note", "quitting with constant " + value1));
				m_returnValue = Integer.valueOf(value1);				// Explicit value1
			}
			return(null);


//////////
// PROMPT
		} else if (sourceName.equalsIgnoreCase("prompt")) {
			// prompt
			return(thisCommand.getNext());


//////////
// OPTIONDUO
		} else if (sourceName.equalsIgnoreCase("optionDuo")) {
			// optionDuo
			return(thisCommand.getNext());


//////////
// ONOPTION1
		} else if (sourceName.equalsIgnoreCase("onOption1")) {
			// onOption1
			return(thisCommand.getNext());


//////////
// ONOPTION2
		} else if (sourceName.equalsIgnoreCase("onOption2")) {
			// onOption2
			return(thisCommand.getNext());

		}
		return(null);
	}

	public Xml processFlow_LocateFlow_DownFromHere(String	flowDirective,
												   Xml		xml)
	{
		while (xml != null)
		{
			if (xml.getName().equalsIgnoreCase("flow") && m_bp.m_macroMaster.parseMacros(xml.getAttributeOrChild("sourcename")).equalsIgnoreCase(flowDirective))
				return(xml);

			// Move to next item
			xml = xml.getNext();
		}
		return(null);
	}


	/**
	 * Search flow items for sourceName = flowDirective, and
	 * *fieldName = optionName
	 * @param flowDirective the command being searched for
	 * @param fieldName the name1 of the field within options
	 * @param optionName the content the field within options should equal
	 * @param xml where to begin searching (until xml.getNext() == NULL)
	 * @return if found, the entry being searched for
	 */
	public Xml processFlow_LocateFlowWithName_DownFromHere(String	flowDirective,
														   String	fieldName,
														   String	optionName,
														   Xml		xml)
	{
		Xml options;

		while (xml != null)
		{
			if (xml.getName().equalsIgnoreCase("flow"))
			{
				// It's a control flow directive
				if (m_bp.m_macroMaster.parseMacros(xml.getAttributeOrChild("sourcename")).equalsIgnoreCase(flowDirective))
				{
					// It's of the type they're looking for
					options = xml.getChildNode("options");
					if (options != null)
					{
						// And it has the exact name1 they're after
						if (m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild(fieldName)).equalsIgnoreCase(optionName))
							return(xml);
					}
				}
			}

			// Move to next item
			xml = xml.getNext();
		}
		return(null);
	}


	/**
	 * Search case flow items that match the value1 specified
	 * @param xmlLog log to update with found information
	 * @param value case value1 we're looking for
	 * @param xml the entry to start looking at
	 * @return if found, the entry being search for
	 */
	public Xml processFlow_LocateFlowCase_DownFromHere(Xml		xmlLog,
													   String	value,
													   Xml		xml)
	{
		String caseValue;
		Xml options, start, defaultCase;
		defaultCase = null;
		start = xml;

		while (xml != null)
		{
			if (xml.getName().equalsIgnoreCase("flow"))
			{
				// It's a control flow directive
				if (m_bp.m_macroMaster.parseMacros(xml.getAttributeOrChild("sourcename")).equalsIgnoreCase("switchEnd")) {
					// We didn't find the value1, look for a default value1
					if (defaultCase != null)
					{
						xmlLog.appendChild(new Xml("note", "no case matched, fell through to default"));
						return(defaultCase.getNext());
					}
					// If we get here, we didn't find the default value1
					xmlLog.appendChild(new Xml("note", "no case matched and no default found"));
					return(start);

				} else if (m_bp.m_macroMaster.parseMacros(xml.getAttributeOrChild("sourcename")).equalsIgnoreCase("default")) {
					// We found the default case clause
					defaultCase = xml;

				} else if (m_bp.m_macroMaster.parseMacros(xml.getAttributeOrChild("sourcename")).equalsIgnoreCase("case")) {
					// It's a case condition
					options = xml.getChildNode("options");
					if (options != null)
					{
						caseValue = options.getAttributeOrChild("value");
						if (!caseValue.isEmpty())
						{
							if (processFlow_TestLogicCondition(value, "==", caseValue))
								return(xml.getNext());		// This is a match
						} else {
							xmlLog.appendChild(new Xml("note", "skipped case " + caseValue));

						}
					}
				}
			}

			// Move to next item
			xml = xml.getNext();
		}
		xmlLog.appendChild(new Xml("error", "syntax error in case definition"));
		return(null);
	}

	public boolean processFlow_IsValidLogicCondition(String comparator)
	{
		if (comparator.equals("=="))			// Equal to
			return(true);
		else if (comparator.equals("!="))		//
			return(true);						//
		else if (comparator.equals("<>"))		// Not equal to
			return(true);						//
		else if (comparator.equals("#"))		//
			return(true);
		else if (comparator.equals(">"))		// Greater than
			return(true);
		else if (comparator.equals(">="))		// Greater than or equal to
			return(true);
		else if (comparator.equals("<"))		// Less than
			return(true);
		else if (comparator.equals("<="))		// Less than or equal to
			return(true);
		else
			return(false);
	}

	public boolean processFlow_TestLogicCondition(String	left,
												  String	comparator,
												  String	right)
	{
		if (comparator.equals("=="))			// Equal to
			return(Integer.valueOf(left) == Integer.valueOf(right));

		else if (comparator.equals("!="))		// Not equal to
			return(Integer.valueOf(left) != Integer.valueOf(right));

		else if (comparator.equals("<>"))		// Not equal to
			return(Integer.valueOf(left) != Integer.valueOf(right));

		else if (comparator.equals("#"))		// Not equal to
			return(Integer.valueOf(left) != Integer.valueOf(right));

		else if (comparator.equals(">"))		// Greater than
			return(Integer.valueOf(left) > Integer.valueOf(right));

		else if (comparator.equals(">="))		// Greater than or equal to
			return(Integer.valueOf(left) >= Integer.valueOf(right));

		else if (comparator.equals("<"))		// Less than
			return(Integer.valueOf(left) < Integer.valueOf(right));

		else if (comparator.equals("<="))		// Less than or equal to
			return(Integer.valueOf(left) <= Integer.valueOf(right));

		else
			return(false);
	}

	/**
	 * Sums up the "timing" entries from the script output, and prepares them
	 * in a form which can be used to generate output CSV files for each pass,
	 * and in total
	 * @param xmlRun_Success
	 */
	public void generateSummaryCSVs(Xml xmlRun_Success)
	{
		int i, j, k, timing0Iterator, timingNIterator, count, iterationThis, iterationMax, thisElement;
		double timing, ofBaseline, power;
		Xml child, useChild, results;
		List<Xml>		xmlNodes	= new ArrayList<Xml>(0);
		List<String>	timings;
		List<String>	qualifieds;
		List<String>	timings0;
		List<String>	timingsN;
		List<String>	groupItems;
		Tuple			tuples		= new Tuple(m_bp.m_opbm);
		Tuple			groups		= new Tuple(m_bp.m_opbm);
		String fileName, status0, statusN, line, command, qualifiedName;

//////////
// Grab only the timing info
		iterationThis = 0;
		child = xmlRun_Success.getFirstChild();
		while (child != null)
		{
			// For iterations, we need to go another level deep
			if (child.getName().equalsIgnoreCase("iteration"))
			{
				useChild		= child.getFirstChild();
				iterationThis	= Integer.valueOf(child.getAttribute("this"));
				iterationMax	= Integer.valueOf(child.getAttribute("max"));
				// Build the qualified name, such as (name of atom).(name of step)
				qualifiedName = xmlRun_Success.getAttributeOrChild("name") + "." + useChild.getAttributeOrChild("name") + "." + child.getAttribute("this");

			} else {
				useChild		= child;
				iterationThis	= 0;
				iterationMax	= 0;
				// Build the qualified name, such as (name of atom).(name of step)
				qualifiedName = xmlRun_Success.getAttributeOrChild("name") + "." + useChild.getAttributeOrChild("name");
			}

			if (useChild.getAttributeOrChild("sourcename").equalsIgnoreCase("execute"))
			{
				// This is a run, parse the output
				// Grab all output lines
				xmlNodes.clear();
				Xml.getNodeList(xmlNodes, useChild.getChildNode("outputs").getChildNode("output"), "output", false);

				// Remove everything except our timing lines
				timings = new ArrayList<String>(0);
				for (i = 0; i < xmlNodes.size(); i++)
				{
					if (xmlNodes.get(i).getText().toLowerCase().startsWith("timing,"))
						timings.add(xmlNodes.get(i).getText().substring(7));
				}

				// Add this list of timing data to this command
				command = useChild.getChildNode("command").getText();
				if (iterationThis != 0)
					command = Utils.forceExtension(command, "(" + Utils.rightJustify(Integer.toString(iterationThis), 4, "0") + "of" + Utils.rightJustify(Integer.toString(iterationMax), 4, "0") + ")" + Utils.getExtension(command));
				tuples.add(command, timings, qualifiedName);
				timings = null;
			}

			// Move to next item and process it
			child = child.getNext();
		}
		// When we get here we have all summary timing data extracted
		// Next, we need to process through the tuples and summarize multiple
		// run averages per command

//////////
// Group it into command-groupings for summary
		for (i = 0; i < tuples.size(); i++)
		{
			if (!tuples.getFirst(i).equalsIgnoreCase("<!-- Processed -->"))
			{
				groupItems = new ArrayList<String>(0);
				groupItems.add(Integer.toString(i));
				for (j = i + 1; j < tuples.size(); j++)
				{
					if (tuples.getFirst(i).equalsIgnoreCase(tuples.getFirst(j)))
					{
						tuples.setFirst(j, "<!-- Processed -->");
						groupItems.add(Integer.toString(j));
						// These two are a match
					}
				}
				// When we get here, add this group
				groups.add(tuples.getFirst(i), groupItems, tuples.getThird(i));
			}
		}
		// When we get here, the groups are identified and we can pull their
		// values for relevant calculations

//////////
// Summarize each command, averaging multiple runs for each command (if any)
		for (i = 0; i < groups.size(); i++)
		{
			fileName	= Opbm.getHarnessCSVDirectory() + Utils.justFileName(Utils.forceExtension(groups.getFirst(i), ".csv"));
			groupItems	= (ArrayList<String>)groups.getSecond(i);
			if (groupItems != null)
			{
				if (groupItems.size() > 1)
				{
					// There's more than one run in this command, we must average first, then write the results
					// Iterate through the items in this group, storing the average in the 0th position, which serves as our "foundation" or "bedrock" entry
					timings0 = (List<String>)tuples.getSecond(Integer.valueOf(groupItems.get(0)));

					// For this bedrock line, average everything that's found, and storing the average back in the first entry's position after each summation
					if (timings0 != null)
					{
						for (timing0Iterator = 0; timing0Iterator < timings0.size(); timing0Iterator++)
						{
							// Grab this item's value
							line	= timings0.get(timing0Iterator);		// The source timing line (extracted initially above) is extracted, and
							m_bp.extractTimingLineElements(line);
							status0	= m_bp.m_timingName;					// the status name is used to identify the sub-entry within the command's group of entries, and
							timing	= m_bp.m_timingInSeconds;				// the timing value is extracted (which sets our initial variable)
							count	= 1;									// So far, we have one entry summed

							// Now, iterate through every item in this N-entry list, searching for matching status names in those other items
							for (j = 1; j < groupItems.size(); j++)
							{
								// Compare this N-item listing's entries to the selected one from the 0-item listing's entries
								timingsN = (List<String>)tuples.getSecond(Integer.valueOf(groupItems.get(j)));

								// See if we find a matching name withing this list
								if (timingsN != null)
								{
									for (timingNIterator = 0; timingNIterator < timingsN.size(); timingNIterator++)
									{
										line	= timingsN.get(timingNIterator);
										m_bp.extractTimingLineElements(line);
										statusN	= m_bp.m_timingName;
										if (status0.equalsIgnoreCase(statusN))
										{
											// We found a match, the status from the 0-entry matches the status from this N-entry
											timing += m_bp.m_timingInSeconds;	// Increase the value
											++count;							// Increase the count
										}
									}
								}
							}
							// When we get here, we have our totals summed up in value, and the count in count
							timing /= count;		// Compute the average

							// And store it back in the 0-entry's location
							// REMEMBER we could add additional information here, such as a string of the numbers used to sum it up, as in [1, 2, 1] resulting in an average of 1.3333
							timings0.set(timing0Iterator, status0 + "," + Double.toString(timing));
							// Once we get here, we've done this single entry in the 0-entry list
							// We need to continue on to the other entries
						}
					}
					// Once we get here, we've processed all "like-minded" groups (if any)
				}

				// Add the totals/summary line to the end of the group's entries
				thisElement		= Integer.valueOf(groupItems.get(0));
				timings			= (List<String>)tuples.getSecond(thisElement);
				qualifiedName	= (String)tuples.getThird(thisElement);
				timing			= 0.0;
				ofBaseline		= 0.0;
				results			= new Xml("results", "", "name", qualifiedName);
				if (timings != null)
				{
					power = 1.0 / (double)timings.size();
					for (k = 0; k < timings.size(); k++)
					{
						// Grab the line and break out its components
						line = timings.get(k);
						m_bp.extractTimingLineElements(line);

						// For timing, sum
						timing += m_bp.m_timingInSeconds;

						// For % of baseline, geometric mean
						if (ofBaseline == 0)
						{	// First time through, we set our value
							ofBaseline	= Math.pow(m_bp.m_timingOfBaseline, power);
						} else {
							// Each successive time through we compute this portion of the total geometric mean
							ofBaseline	*= Math.pow(m_bp.m_timingOfBaseline, power);
						}

						// Add the line into the results tally (as it appeared from the source script)
						results.appendChild("timing", line);
					}
					timings.add("Total," + Double.toString(timing) + "," + Double.toString(ofBaseline));
					timings.add("");	// Add a blank line for a double-space at the end

					// Append the timing entry to the results
					results.appendChild("timing", timings.get(k));
				}
				m_bp.m_xmlRun.appendChild(results);
// REMEMBER we could add additional information here

				// Output the results to the CSV file
				Utils.appendTerminatedLinesToFile(fileName, timings);
			}
		}
	}

	/**
	 * @param thisCommand xml to the atom of this command
	 * @param xmlRun_Success xml to append tags to for this portion of the run when it succeeds
	 * @param xmlRun_Failure xml to append tags to for this portion of the run if there are failures
	 */
	public Xml processAbstract_Atom(Xml		thisCommand,
									Xml		xmlRun_RunSuccess,
									Xml		xmlRun_RunFailure)
	{
		int i, retryCount, retryAttempts, intValue;
		Xml options, xmlType, xmlError, xmlRetry;
		Xml xmlOutput, xmlCommand;
		Process process;
		ProcessBuilder builder;
		String exception, exception2, name, curDir, counter, sourcename, result, value;
		String command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
		List<String> commandsAndParameters = new ArrayList<String>(0);
		boolean record, failure, retryEnabled, stopIfFailure, boolValue;

		// Set our initial values
		retryEnabled	= m_bp.m_retry;
		retryAttempts	= m_bp.m_retryAttempts;
		stopIfFailure	= m_bp.m_settingsMaster.benchmarkStopsIfRetriesFail();

		// See what our command is
		sourcename = thisCommand.getAttributeOrChild("sourcename");
		if (sourcename != null)
		{
			if (sourcename.equalsIgnoreCase("execute") ||
				sourcename.equalsIgnoreCase("executeParams") ||
				sourcename.equalsIgnoreCase("spinUp"))
			{
				// See if we're executing a non-recording script, one that doesn't record its score (spinUp)
				if (sourcename.equalsIgnoreCase("spinUp"))
				{	// We are executing a non-recording script
					// Executes a spinup script that doesn't record a score, but allows
					// everything to be spun back up (used at first benchmark run, or
					// after a reboot to re-load all the DLLs, cache everything, etc.
					record = false;		// Should logs be recorded?
// REMEMBER need to add a place later to record these items that, today, are not being recorded
//          needs to be another place in the results.xml file that is not part of the standard
//          atom/molecule/scenario/suite processing.

				} else {
					record = true;		// Should logs be recorded?
				}

				// Initially set the failure condition to true, flag is lowered at the end if we're okay
				failure		= true;
				retryCount	= 0;

				// Executing with no parameters
				options = thisCommand.getChildNode("options");
				if (options != null)
				{	// It appears to be a properly formatted atom command

					// See if they have override options for:
					// retryEnabled
					value = options.getAttributeOrChild("retryEnabled");
					if (value != null && !value.isEmpty())
					{
						boolValue = Utils.interpretBooleanAsYesNo(value, m_bp.m_retry).equalsIgnoreCase("yes");
						if (retryEnabled != boolValue)
						{	// They have an override
							System.out.println("retryEnabled=\"" + Utils.evaluateLogicalToYesOrNo(retryEnabled) + "\" overridden to \"" + Utils.evaluateLogicalToYesOrNo(boolValue) + "\", per atom overrides");
							retryEnabled = boolValue;
						}
					}
					// retryAttempts
					value = options.getAttributeOrChild("retryAttempts");
					if (value != null && !value.isEmpty())
					{
						intValue = Utils.getValueOf(value, retryAttempts);
						if (retryAttempts != intValue)
						{	// They have an override
							System.out.println("retryAttempts=\"" + Integer.toString(retryAttempts) + "\" overridden to \"" + Integer.toString(intValue) + "\", per atom overrides");
							retryAttempts = intValue;
						}
					}
					// stopIfFailure
					value = options.getAttributeOrChild("stopIfFailure");
					if (value != null && !value.isEmpty())
					{
						boolValue = Utils.interpretBooleanAsYesNo(value, stopIfFailure).equalsIgnoreCase("yes");
						if (stopIfFailure != boolValue)
						{	// They have an override
							System.out.println("stopIfFailure=\"" + Utils.evaluateLogicalToYesOrNo(stopIfFailure) + "\" overridden to \"" + Utils.evaluateLogicalToYesOrNo(boolValue) + "\", per atom overrides");
							stopIfFailure = boolValue;
						}
					}

					// If we are not retrying, set the retry attempts to 0
					if (!retryEnabled)
						retryAttempts = 0;

					while (m_bp.m_debuggerOrHUDAction < BenchmarkParams._STOP && failure && retryCount <= retryAttempts)
					{	// Process repepatedly until we have a success, or our on-failure retry count is reached, or the user forces a stop
						// Prepare for this run
						m_bp.m_wui.prepareBeforeScriptExecution();

						// See what we're doing
						command = m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("executable"));
						p1		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p1"));
						p2		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p2"));
						p3		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p3"));
						p4		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p4"));
						p5		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p5"));
						p6		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p6"));
						p7		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p7"));
						p8		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p8"));
						p9		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p9"));
						p10		= m_bp.m_macroMaster.parseMacros(options.getAttributeOrChild("p10"));
						if (command != null && !command.isEmpty())
						{
							commandsAndParameters.clear();
							commandsAndParameters.add("\"" + command + "\"");
							if (p1  != null && !p1.isEmpty())	commandsAndParameters.add(p1);
							if (p2  != null && !p2.isEmpty())	commandsAndParameters.add(p2);
							if (p3  != null && !p3.isEmpty())	commandsAndParameters.add(p3);
							if (p4  != null && !p4.isEmpty())	commandsAndParameters.add(p4);
							if (p5  != null && !p5.isEmpty())	commandsAndParameters.add(p5);
							if (p6  != null && !p6.isEmpty())	commandsAndParameters.add(p6);
							if (p7  != null && !p7.isEmpty())	commandsAndParameters.add(p7);
							if (p8  != null && !p8.isEmpty())	commandsAndParameters.add(p8);
							if (p9  != null && !p9.isEmpty())	commandsAndParameters.add(p9);
							if (p10 != null && !p10.isEmpty())	commandsAndParameters.add(p10);

							// Update the hud with executed scripts, plus failures
							counter = Integer.toString(m_executeCounter) + " " + Utils.singularOrPlural(m_executeCounter, "Script", "Scripts") + " Executed";
							if (m_failureCounter != 0)
								counter += " + " + Integer.toString(m_failureCounter) + " " + Utils.singularOrPlural(m_failureCounter, "Failure", "Failures");

							if (m_bp.m_thisIteration != 0)
							{	// Update the iteration information
								counter = Integer.toString(m_bp.m_thisIteration) + " of " + Integer.toString(m_bp.m_maxIterations) + "; " + counter;
							}
							if (m_bp.m_hud != null)
								m_bp.m_hud.updateCounter(counter);


							if (retryCount != 0)
							{	// Indicate we are retrying after a failure
								if (m_bp.m_hud != null)
									m_bp.m_hud.updateError("Error, retry #" + Integer.toString(retryCount) + " of #" + Integer.toString(retryAttempts));
							}

							// Change the current directory to the directory of the executable, and get this command's user-readable name
							curDir	= Utils.makeTheCurrentDirectoryThatOfThisFilename(command);
							name	= m_bp.m_macroMaster.parseMacros(thisCommand.getAttribute("name"));
							if (record)
							{	// We are recording logs for this one
								// Add the type of run made
								xmlType = new Xml("abstract");
								xmlType.appendAttribute("name", name);
								xmlType.appendAttribute("sourcename", thisCommand.getAttribute("sourcename"));

								// Indicate our beginning, and the command to execute
								xmlType.appendChild(Utils.processExecutableLine("start", Utils.getTimestamp()));
								xmlCommand = new Xml("command", command);
								xmlType.appendChild(xmlCommand);
								if (commandsAndParameters.size() > 1)
								{	// Append the parameters
									for (i = 1; i < commandsAndParameters.size(); i++)
										xmlType.appendChild("p" + Integer.toString(i), commandsAndParameters.get(i));
								}

								// Add space for any errors
								xmlError = new Xml("errors");
								xmlType.appendChild(xmlError);

							} else {
								xmlError	= null;
								xmlType		= null;

							}

							// If this is a retry attempt, tag it as such
							if (retryCount != 0)
							{	// Add [this="#" max="#"] for the retryCount and m_bp.retryAttempts
								xmlRetry = xmlType.appendChild(new Xml("retry"));
								xmlRetry.appendAttribute(new Xml("this", Integer.toString(retryCount)));
								xmlRetry.appendAttribute(new Xml("max", Integer.toString(retryAttempts)));
							}

							// Clear off any previous "process" that may be hanging on out there for garbage collection
							process = null;
							try {
								// Start the process (with its optional list of parameters)
								m_bp.setLastWorkletStart(Utils.getTimestamp());

//////////
// Used for debugging
// The following lines are used for debugging, to keep it from executing real scripts, but instead executes a simulated success
//////
	//
	if (Opbm.m_debugSimulateRunAtomMode)
	{	// Running in simulated mode
		// The following is used for debugging, to simulate successes for far more rapid testing
		// Simulate successes and failures
		commandsAndParameters.clear();
		if (Math.random() <= Opbm.m_debugSimulateRunAtomModeFailurePercent)	// Setup to fail this percent of the time
			commandsAndParameters.add("c:\\cana\\java\\autoIT\\tests\\failure\\failure.exe");
		else	// The rest of the time, run successes
			commandsAndParameters.add("c:\\cana\\java\\autoIT\\tests\\success\\success.exe");
	}
	//else running in regular (normal) mode, the comand will execute as it was originally built
	//
//////
// End
//////////


								builder = new ProcessBuilder(commandsAndParameters);
								process = builder.start();

								// Grab the output
								m_bp.m_errorGobbler		= new StreamGobbler(process.getErrorStream(),	m_bp.m_errorArray,	"STDERR", name, m_bp, m_bp.m_hud);
								m_bp.m_outputGobbler	= new StreamGobbler(process.getInputStream(),	m_bp.m_outputArray,	"STDOUT", name, m_bp, m_bp.m_hud);
								m_bp.m_errorGobbler.start();
								m_bp.m_outputGobbler.start();

								// Wait for the process to finish
								process.waitFor();
								m_bp.setLastWorkletEnd(Utils.getTimestamp());
								m_bp.accumulateLastWorkletTime();
								// Returns 0 if everything succeeded normally without error

								if (record)
								{
									// Identify the termination time
									xmlType.appendChild(Utils.processExecutableLine("finish", Utils.getTimestamp(), "result", process.exitValue() == 0 ? "success" : "fail"));
								}

								// Make sure we finish reading before continuing
								m_bp.m_errorGobbler.join();
								m_bp.m_outputGobbler.join();

								if (record)
								{
									// Grab the exit value1
									xmlType.appendChild(Utils.processExecutableLine("process", Utils.getTimestamp() + ": Terminated with code " + Integer.toString(process.exitValue())));
								}

								// If we get here, then everything proceeded normally
								failure	= false;

							} catch (Throwable t) {
								// Make sure we finish reading before continuing
								exception2 = "";
								try {
									if (m_bp.m_errorGobbler != null)
										m_bp.m_errorGobbler.join();

									if (m_bp.m_outputGobbler != null)
										m_bp.m_outputGobbler.join();

								} catch (Throwable t2) {
									exception2 = ": (Second exception caught: " + t2.getMessage() + ")";
								}
								exception = Utils.getTimestamp() + ": Threw an exception: " + t.getMessage() + exception2;
								if (record)
								{
									xmlType.appendChild(Utils.processExecutableLine("finish", Utils.getTimestamp(), "result", "exception"));
									xmlType.appendChild(Utils.processExecutableLine("process", exception));
								}
								m_bp.m_outputArray.add(exception);

							}

							// Reset the directory
							Utils.setCurrentDirectory(curDir);

//////////
// FAILURE TRIGGERED BY A REPORTED ERROR FROM/BY THE SCRIPT
							// For each line of errors, save the entry
							if (!m_bp.m_errorArray.isEmpty())
							{	// We had at least one error, which marks this as a failure
								failure		= true;
								for (i = 0; record && i < m_bp.m_errorArray.size(); i++)
									xmlError.appendChild(Utils.processExecutableLine("error", m_bp.m_errorArray.get(i)));
								m_bp.m_errorArray.clear();

							} else {
								// We succeeded
								failure = false;
							}

							// For each line of output, save the entry
							if (record)
							{
								xmlOutput = new Xml("outputs");
								xmlType.appendChild(xmlOutput);

							} else {
								xmlOutput = null;
							}
							if (!m_bp.m_outputArray.isEmpty())
							{	// There were lines intercepted from the script
								for (i = 0; record && i < m_bp.m_outputArray.size(); i++)
								{	// If the script wrote any "error," entries to stdout (instead of stderr)
									// we still need to catch those and interpret their directive as a failure on this pass
									if (!failure && m_bp.m_outputArray.get(i).toLowerCase().contains(" error,"))
									{	// This was an error, we have a failure
										failure = true;
									}
									xmlOutput.appendChild(Utils.processExecutableLine("output", m_bp.m_outputArray.get(i)));
								}
								m_bp.m_outputArray.clear();
							}

//////////
// FAILURE TRIGGERED BY THE EXIT/RETURN VALUE
							// The only condition that will signal a failure outside of an error code,
							// is if the process terminated with a non-zero value
							if (!failure && process != null && process.exitValue() != 0)
							{	// A failure was noted by its exit value, but nothing was logged in the error queue
								failure = true;
								if (record)
									xmlError.appendChild(Utils.processExecutableLine("process", Utils.getTimestamp() + ": Terminated with code " + Integer.toString(process.exitValue())));
							}

							if (m_bp.m_debuggerOrHUDAction >= BenchmarkParams._STOP)
							{	// User force-terminated the execution
								if (record)
									xmlError.appendChild(Utils.processExecutableLine("process", Utils.getTimestamp() + ": User force-terminated benchmark via stop button in HUD"));
							}

							// Now, based on the state of this run, we append its information to the appropriate place
							if (failure /* the old system stored one of the failures in the "success" so the rseults viewer parser could process the failure, the benchmark manifest does not handle it the same way && retryCount != retryAttempts*/)
							{	// An error occurred, so we put this on the retry pipe, all except the last one, which is logged to the success branch itself to record the failure officially
								if (record)
								{
									xmlRun_RunFailure.appendChild(xmlType);
									m_bp.computeLastWorkletScore(xmlOutput);
								}

							} else {
								// Just append like normal, they're not retrying, this failure will persist
								if (record)
								{
									xmlRun_RunSuccess.appendChild(xmlType);
									m_bp.computeLastWorkletScore(xmlOutput);
								}
							}
							// Move the counter for the next retry
							++retryCount;

							// Note:  For non-recording events, even though they weren't recorded into the results.xml,
							//        they still go through the normal error checking and handling, and will retry if
							//        there's a failure

							// Clean up any temporary files and what-not which the script may have left behind
							m_bp.m_wui.cleanupAfterScriptExecution();

							// If there's a failure,
							if (failure && retryCount < retryAttempts)
							{	// stop extraneous processes, and pause briefly before retrying
								Opbm.stopProcesses();
							}
						}
					}
				}
				// Increase our counter
				if (failure)
				{
					m_bp.setLastWorkletResult("failure");

					if (m_isRecordingCounts)
						++m_failureCounter;

					if (stopIfFailure)
					{	// We have to force the stop now
						if (m_bp.m_debuggerOrHUDAction != BenchmarkParams._STOP_USER_CLICKED_STOP)
							m_bp.m_debuggerOrHUDAction = BenchmarkParams._STOPPED_DUE_TO_FAILURE_ON_ALL_RETRIES;
					} else {
						// We mark this entry as finished, because we're not stopping if it fails
						if (m_bp.m_bm != null)
							m_bp.m_bm.setLastWorkletFinished();

					}

				} else {
					m_bp.setLastWorkletResult("success");

					if (m_isRecordingCounts)
						++m_executeCounter;
				}

				// Store the number of retrise (should be 0 if no retries, 1 to m_bp.m_retryAttempts otherwise)
				m_bp.setLastWorkletRetries(retryCount - 1);

				// After the execute is finished, wait for the CPU to settle down
				if (!(m_bp.m_debuggerActive && m_bp.m_singleStepping))
					m_bp.m_wui.pauseAfterScriptExecution();

				// For this statement, we simply proceed on to the next if we're good, otherwise, we're done
				if (failure && stopIfFailure)
				{	// We ended on a failure, so the last atom was a failure
					m_bp.m_bpAtom.m_lastAtomWasFailure = true;
					return(null);
				} else {
					// We ended on a success, so the last atom was not a failure
					m_bp.m_bpAtom.m_lastAtomWasFailure = false;
					return(thisCommand.getNext());
				}

			} else if (sourcename.equalsIgnoreCase("rebootAndTerminate")) {
				// Rebooting, and terminating the benchmark test (will restart the system and leave it in its natural state)
				if (m_bp.m_hud != null)
					m_bp.m_hud.updateStatus("Terminating benchmark...");
				reboot(m_bp.m_macroMaster.parseMacros(thisCommand.getAttribute("name")), false);

			} else if (sourcename.equalsIgnoreCase("rebootAndContinue")) {
				// Rebooting, and continuing back from the next atom after the one we're on now
				// Rebooting, and restarting the benchmark from the beginning
				if (m_bp.m_hud != null)
					m_bp.m_hud.updateDebug("Setting registry key for current user startup");
				// c:\cana\java\opbm\ "c:\program files\java\jdk1.7.0\jre\bin\java.exe" opbm.jar
				result = Opbm.SetRegistryKeyValueAsString("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\RunOnce\\opbm", "\"" + Utils.getCurrentDirectory() + "\\restarter.exe\" \"" + Utils.getCurrentDirectory() + "\" \"" + Opbm.m_jvmHome + "\" opbm.jar");
				if (m_bp.m_hud != null)
					m_bp.m_hud.updateDebug(result);
				if (result.equalsIgnoreCase("success"))
				{
					if (m_bp.m_hud != null)
						m_bp.m_hud.updateStatus("Continue benchmark after system restart...");
					reboot(m_bp.m_macroMaster.parseMacros(thisCommand.getAttribute("name")), true);

				} else {
					// Failure creating the registry key, it won't work
					m_bp.m_debuggerOrHUDAction = BenchmarkParams._STOP_FAILURE_ON_REBOOT_WRITE_REGISTRY;
					OpbmDialog od = new OpbmDialog(m_bp.m_opbm, "Unable to set registry key for auto-restart.<br>Manual intervention required", "OPBM Failure on Reboot and Restart", OpbmDialog._OKAY_BUTTON, "reboot_and_restart", "");
					return(null);
				}

			} else if (sourcename.equalsIgnoreCase("rebootAndRestart")) {
				// Rebooting, and restarting the benchmark from the beginning
				if (m_bp.m_hud != null)
					m_bp.m_hud.updateDebug("Setting registry key for current user startup");
				// c:\cana\java\opbm\ "c:\program files\java\jdk1.7.0\jre\bin\java.exe" opbm.jar
				result = Opbm.SetRegistryKeyValueAsString("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\RunOnce\\opbm", "\"" + Utils.getCurrentDirectory() + "\\restarter.exe\" \"" + Utils.getCurrentDirectory() + "\" \"" + Opbm.m_jvmHome + "\" opbm.jar");
				if (m_bp.m_hud != null)
					m_bp.m_hud.updateDebug(result);
				if (result.equalsIgnoreCase("success"))
				{
					if (m_bp.m_hud != null)
						m_bp.m_hud.updateStatus("Restarting benchmark from beginning...");
					reboot(m_bp.m_macroMaster.parseMacros(thisCommand.getAttribute("name")), true);

				} else {
					// Failure creating the registry key, it won't work
					m_bp.m_debuggerOrHUDAction = BenchmarkParams._STOP_FAILURE_ON_REBOOT_WRITE_REGISTRY;
					OpbmDialog od = new OpbmDialog(m_bp.m_opbm, "Unable to set registry key for auto-restart.<br>Manual intervention required", "OPBM Failure on Reboot and Restart", OpbmDialog._OKAY_BUTTON, "reboot_and_restart", "");
					return(null);
				}

			}
		}
		// An error if we get here
		System.out.println("Error: An unrecognized command was found, ignored: " + thisCommand.getName());
		System.err.println("Error: An unrecognized command was found, ignored: " + thisCommand.getName());
// REMEMBER  need to process this error in the future
		return(thisCommand.getNext());
	}

// REMEMBER this needs to be moved to the BenchmarkShutdown() logic, so it can
// be a full post-process (write out CSV, XML files) activity, and not something
// literally done in the middle of a benchmark.  In that way, it will shutdown
// (and potentially restart) nicely.

	/**
	 * Handles the reboot operation
	 * @param name identifier used for this item (the atom)
	 * @param saveStateBeforeReboot should this method save the complete
	 * benchmark state before a reboot (allowing a restart)
	 */
	public void reboot(String		name,
					   boolean		saveStateBeforeReboot)
	{
		int i, max;
		Process process;

		// Restore the user's registry key settings
		Opbm.Office2010RestoreKeys();

		if (m_bp != null && m_bp.m_hud != null)
			m_bp.m_hud.setRebooting();

		if (m_bp != null && m_bp.m_bm != null)
			m_bp.m_bm.runExecuteSetRebooting();

		if (saveStateBeforeReboot)
		{
			// Update the HUD
			if (m_bp != null && m_bp.m_hud != null)
				m_bp.m_hud.updateStatus("Rebooting...");

			// Stop everything that may be "hanging around" out there
			Opbm.stopProcesses();
		}

		// This dialog will persist until the user clicks okay, or until the system
		// reboots, but it is not a blocking operation, and execution will continue
		// below at the reboot command.
		OpbmDialog od = new OpbmDialog(m_bp.m_opbm, "System will restart...", "Rebooting", OpbmDialog._OKAY_BUTTON, "", "");
		try
		{
			if (m_bp != null && m_bp.m_hud != null)
				m_bp.m_hud.updateStatus("Executing shutdown...");

//////////
// Used for debugging
// The following line is used for debugging, to keep it from rebooting between runs
//////
	//
	if (Opbm.m_debugSimulateRunAtomMode)
	{
		Opbm.quit(0);
	}
	//
//////
// End
//////////

			process = Runtime.getRuntime().exec(Opbm.getCSIDLDirectory("SYSTEM") + "shutdown /r");

			// Grab the output
			m_bp.m_errorGobbler		= new StreamGobbler(process.getErrorStream(),	m_bp.m_errorArray,	"STDERR", name, m_bp, m_bp.m_hud);
			m_bp.m_outputGobbler	= new StreamGobbler(process.getInputStream(),	m_bp.m_outputArray,	"STDOUT", name, m_bp, m_bp.m_hud);
			m_bp.m_errorGobbler.start();
			m_bp.m_outputGobbler.start();

			// Wait for the process to finish
			process.waitFor();

			// Display the messages given
			// See note in Utils.processExecutableLine() for why 44 is used here:
			// updateError() is used show it shows up in red, not because it's an actual error
			for (i = 0; i < m_bp.m_errorArray.size(); i++)
			{
				if (m_bp != null && m_bp.m_hud != null)
					m_bp.m_hud.updateError(m_bp.m_errorArray.get(i).substring(44));

				System.out.println(m_bp.m_errorArray.get(i).substring(44));
			}

			for (i = 0; i < m_bp.m_outputArray.size(); i++)
			{
				if (m_bp != null && m_bp.m_hud != null)
					m_bp.m_hud.updateError(m_bp.m_outputArray.get(i).substring(44));

				System.out.println(m_bp.m_outputArray.get(i).substring(44));
			}

		} catch (Throwable t) {
		}

		// Wait up to 60 seconds for the system to shut down
		try {
			if (!m_bp.m_errorArray.isEmpty() || !m_bp.m_outputArray.isEmpty())
			{	// Wait a short while so they can read the output
				try {
					Thread.sleep(10000);

				} catch (InterruptedException ex) {
				}
				// We've already burned 10 seconds
				i = 10;

			} else {
				i = 0;
			}
			if (m_bp != null && m_bp.m_hud != null)
			{
				m_bp.m_hud.updateStatus("-->  (Watchdog set for 60 seconds)");
				m_bp.m_hud.updateDebug("--> (Type \"shutdown /a\" at C:\\> to abort)");
			}

			max	= 60;
			while (i < max)
			{
				Thread.sleep(1000);
				++i;
				if (i > 20)
				{	// We'll display the countdown after 30 seconds, but not before
					if (m_bp != null && m_bp.m_hud != null)
					{
						if (i < 40)
							m_bp.m_hud.updateDebug(Integer.toString(i));
						else
							m_bp.m_hud.updateDebug(Integer.toString(i) + ", OPBM will exit in " + Integer.toString(60 - i));
					}
				}
			}

		} catch (InterruptedException ex) {
		}
		// If it doesn't shut down within that time, go ahead and exit so the user can restart

		if (m_bp != null && m_bp.m_bm != null)
			m_bp.m_bm.runExecuteSetRebootingFailed();

		// Exit the system after the watchdog
		Opbm.quit(0);
	}

	private BenchmarkParams		m_bp;
	public	int					m_failureCounter;
	public	int					m_executeCounter;
	public	List<Xml>			m_timingEvents;
	public	int					m_returnValue;
	public	boolean				m_lastAtomWasFailure;
	public	boolean				m_isRecordingCounts;
}
