/*
 * OPBM - Office Productivity Benchmark
 *
 * This class handles all Xml operations, converting W3C DOM to Xml class
 * objects.  It provides a full range of facilities for searching, updating,
 * deleting, branching and graphing Xml nodes.
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Rick C. Hodgin
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.w3c.dom.*;
import java.util.List;

/** Handles all items related to Xml processing. Note that this class is used
 * for easier navigation and instrumentation on simple XML files, such as those
 * used in OPBM, rather than navigating Java's W3C DOM, which, while wholly
 * comprehensive, is just so very obscenely so. :-)
 *
 * @author Rick C. Hodgin
 */
public class Xml
{
	/**
	 * Constructor initializes member variables to default (null/empty) values.
	 */
	public Xml() {
		m_parent			= null;
		m_prev				= null;
		m_next				= null;
		m_firstAttribute	= null;
		m_firstChild		= null;
		m_cdata				= "";
		m_text				= "";
		m_comment			= "";
	}

	/**
	 * Constructor initializes member variables to default (null/empty) values.
	 */
	public Xml(String name)
	{
		m_parent			= null;
		m_prev				= null;
		m_next				= null;
		m_firstAttribute	= null;
		m_firstChild		= null;
		m_cdata				= "";
		m_name				= name;
		m_text				= "";
		m_comment			= "";
	}

	/**
	 * Constructor initializes member variables to default (null/empty) values.
	 */
	public Xml(String name, String text)
	{
		m_parent			= null;
		m_prev				= null;
		m_next				= null;
		m_firstAttribute	= null;
		m_firstChild		= null;
		m_cdata				= "";
		m_name				= name;
		m_text				= text;
		m_comment			= "";
	}

	/**
	 * Constructor initializes member variables to default (null/empty) values,
	 * and adds the specified attribute automatically.
	 */
	public Xml(String name, String text, String attributeName, String attributeText)
	{
		m_parent			= null;
		m_prev				= null;
		m_next				= null;
		m_firstAttribute	= new Xml(attributeName, attributeText);
		m_firstChild		= null;
		m_cdata				= "";
		m_name				= name;
		m_text				= text;
		m_comment			= "";
	}

	/**
	 * During instantiation, process nodes from a given root level in the java
	 * w3c dom model into this Xml structure.
	 *
	 * @param root root node to update at this level (either the true root node, or a parent)
	 * @param nl <code>NodeList</code> from W3C DOM model (org.w3c.dom)
	 * @return the top-level Xml object that was created (if any)
	 */
	public static Xml processW3cNodesIntoXml(Xml		root,
								   NodeList	nl)
	{
		String name, value;
		short type;
		Node n;
		Xml xml		= null;
		Xml xmlNew	= null;
		Xml xmlTop	= null;

		try {
			for (int i = 0; i < nl.getLength(); i++) {
				n = nl.item(i);
				name	= n.getNodeName();
				value	= n.getNodeValue();
				type	= n.getNodeType();

				switch (type) {
					case Node.ATTRIBUTE_NODE:					// Is an attribute
						xml.setName(name);
						xml.setText(value);
						break;
					case Node.CDATA_SECTION_NODE:				// Is an CDATA section
						if (root != null)	// In Java's w3c dom model, cdata items are always stored as children
							root.setCdata(value);
						break;
					case Node.TEXT_NODE:						// Is a text node
						if (root != null) {		// In Java's w3c dom model, text items are always stored as children
							if (root.getText() == null || root.getText().isEmpty()) {
 								root.setText(value);
							}
						}
						break;
					case Node.DOCUMENT_POSITION_DISCONNECTED:
						// Items that are disconnected are new items not related to the previous one
						if (xml != null) {
							// This is another sibling needing to be added to the chain
							xmlNew	= new Xml();
							xml.setNext(xmlNew);		// Update the sibling chain
							xmlNew.setPrev(xml);		// Update the sibling chain
							xmlNew.setParent(xmlTop.getParent());
							root	= xml;				// The new "root" becomes the last item that was created at this level
							xml		= xmlNew;			// We're now working on the new sibling

						} else {
							// This is the first one at this level, create it
							xml		= new Xml();
							xmlTop	= xml;
							xml.setParent(root);

						}
						// Store the name of this new node
						xml.setName(name);
						break;
				}
				if (n.hasAttributes()) {
					xml.setFirstAttribute(processAttributes(xml, n.getAttributes()));
				}
				if (n.hasChildNodes()) {
					xml.setFirstChild(processW3cNodesIntoXml(xml, n.getChildNodes()));
				}
			}
		}
		catch (Exception ex) {
		}
		return(xmlTop);
	}

	/** Processes attributes for a given node.
	 *
	 * @param root Xml to update with attributes
	 * @param nnm <code>NamedNodeMap</code> of attributes from W3C DOM
	 * @return top-level Xml class added for attributes (if any)
	 */
	public static Xml processAttributes(Xml				root,
										NamedNodeMap	nnm)
	{
		String name, value;
		short type;
		Node n;
		Xml xml		= null;
		Xml xmlNew	= null;
		Xml xmlTop	= null;

		try {
			for (int i = 0; i < nnm.getLength(); i++) {
				n = nnm.item(i);
				name	= n.getNodeName();
				value	= n.getNodeValue();
				type	= n.getNodeType();

				switch (type) {
					case Node.ATTRIBUTE_NODE:					// Is an attribute
						if (xml != null) {
							// This is another sibling needing to be added to the chain
							xmlNew	= new Xml();
							xml.setNext(xmlNew);		// Update the sibling chain
							xmlNew.setPrev(xml);		// Update the sibling chain
							xmlNew.setParent(xmlTop.getParent());
							root	= xml;				// The new "root" becomes the last item that was created at this level
							xml		= xmlNew;			// We're now working on the new sibling

						} else {
							// This is the first one at this level, create it
							xml		= new Xml();
							xmlTop	= xml;
							xml.setParent(root);

						}
						xml.setName(name);
						xml.setText(value);
						break;
					case Node.CDATA_SECTION_NODE:				// Is an CDATA section
						if (root != null)	// In Java's w3c dom model, cdata items are always stored as children
							root.setCdata(value);
						break;
					case Node.TEXT_NODE:						// Is a text node
						if (root != null) {		// In Java's w3c dom model, text items are always stored as children
							if (root.getText() == null || root.getText().isEmpty()) {
								root.setText(value);
							}
						}
						break;
					case Node.DOCUMENT_POSITION_DISCONNECTED:
						xml.setName(name);
						break;
				}
			}
		}
		catch (Exception ex) {
			int i = 5;
		}
		return(xmlTop);
	}

	/** Non-static version of the static <code>getNodeList()</code> method.
	 *
	 * @param nodes List of nodes to append to
	 * @param searchMaster level to search, as in "opbm.panels.panel"
	 * @param onlyOneLevel are we only processing one level? And not follow-on siblings?
	 * @see #getNodeList(java.util.List, opbm.Xml, java.lang.String, boolean)
	 */
	public void getNodeList(List<Xml>	nodes,
							String		searchMaster,
							boolean		onlyOneLevel)
	{
		Xml.getNodeList(nodes, this, searchMaster, onlyOneLevel);
	}

	/**
	 * Static version which this method, which receives an Xml root for searching
	 * children relative to that level by the searchMaster syntax.
	 *
	 * The node list is built from loaded Xml data, using the . association,
	 * such that "firstName.secondName.thirdName" would reference all third-level
	 * items under every instance of "firstName.secondName" in root that exists,
	 * even if they are separated across interim nodes (unless onlyOneLevel is
	 * true, then only the one level of root is searched).
	 *
	 * June 2, 2011 - RCH - Added multiple theseTags, as in [something,like,this] at
	 *                      any given level, allowing for groups of entries to
	 *                      be selected and included in the node list.
	 *
	 * @param nodes List of nodes to append to
	 * @param root top-level Xml to search, can be true root or any branch
	 * @param searchMaster level to search, as in "opbm.panels.panel"
	 * @param onlyOneLevel are we only processing one level? And not follow-on siblings?
	 */
	public static void getNodeList(List<Xml>	nodes,
								   Xml			root,
								   String		searchMaster,
								   boolean		onlyOneLevel)
	{
		if (root != null)
		{
			Xml xmlThis;
			List<String> theseTags = null;
			String search, nextSearch;
			boolean lLookingForMoreLevels = false;

			// Find out what we're searching for, a single tag or multiple theseTags deep
			if (searchMaster.contains(".")) {		// Looking for "something.like.this" (multiple theseTags/levels deep)
				lLookingForMoreLevels = true;
				search		= searchMaster.substring(0, searchMaster.indexOf("."));
				nextSearch	= searchMaster.substring(search.length() + 1, searchMaster.length());

			} else {								// Looking for something like "this" (one tag only)
				search		= searchMaster;
				nextSearch	= "";

			}
			if (search.trim().startsWith("[") && search.trim().endsWith("]"))
			{	// Looking for something like [multiple,theseTags,like,this]
				theseTags = new ArrayList<String>(0);
				getTagsList(theseTags, search.substring(1, search.length() - 1));
			}
			// When we get here:
			//	1) search is the string we're looking for
			//	2) lLookingForMoreLevels is populated, indicating if we're looking at multiple levels

			// Now, iterate through each sibling at this level to find all of those named search
			xmlThis = root;
			while (xmlThis != null)
			{
				if (theseTags != null)
				{
					if (searchTagsList(theseTags, xmlThis.getName())) {
						// This is a match at this level
						if (lLookingForMoreLevels) {
							// We have to continue at a lower level, which will update nodes
							getNodeList(nodes, xmlThis.getFirstChild(), nextSearch, onlyOneLevel);

						} else {
							// This is one we're adding
							nodes.add(xmlThis);
						}
					}

				} else {
					if (search.isEmpty() || xmlThis.getName().equalsIgnoreCase(search)) {
						// This is a match at this level
						if (lLookingForMoreLevels) {
							// We have to continue at a lower level, which will update nodes
							if (nextSearch.startsWith("#"))
							{	// They're looking for an attribute
								getNodeList(nodes, xmlThis.getFirstAttribute(), nextSearch.substring(1), onlyOneLevel);

							} else {
								getNodeList(nodes, xmlThis.getFirstChild(), nextSearch, onlyOneLevel);
							}

						} else {
							// This is one we're adding
							nodes.add(xmlThis);
						}
					}
				}
				if (onlyOneLevel)
					return;			// We only process through one level, not onto additional siblings

				// Move to next item, see if it's a match
				xmlThis = xmlThis.getNext();
			}
		}
		return;
	}

	/**
	 * Converts the list of theseTags "listed,like,this" into a <code>List</code> of
	 * theseTags that can be processed as input to the
	 * <code>getNodeListOfNamedTags()</code>.
	 *
	 * @param tags array to update
	 * @param search string to convert
	 */
	public static void getTagsList(List<String>		tags,
								   String			search)
	{
		String thisPortion;
		boolean continueOn = !search.isEmpty();

		while (continueOn) {
			// Extract the portion, the "listed" of the "listed,like,this"
			if (search.contains(",")) {
				thisPortion = search.substring(0, search.indexOf(","));

			} else {
				thisPortion = search;
				continueOn = false;
			}

			// Add this tag
			tags.add(thisPortion.trim());

			// Grab the rest of the string
			if (continueOn)
				search = search.substring(thisPortion.length() + 1).trim();
		}
	}

	/**
	 * Searches the list of theseTags for the search string.
	 * @param tags list of theseTags to search
	 * @param search thing searching for
	 * @return true or false if search string was found
	 */
	public static boolean searchTagsList(List<String>	tags,
										 String			search)
	{
		int i;

		for (i = 0; i < tags.size(); i++) {
			if (tags.get(i).equalsIgnoreCase(search))
				return(true);
		}
		return(false);
	}

	/** Non-static version of <code>getNodeListofNamedTags()</code>.
	 *
	 * @param nodes List of nodes to append to
	 * @param searchMaster level to search, as in "opbm.panels.panel"
	 * @param namedTags list of named theseTags at that level to include
	 * @param onlyOneLevel are we only processing one level? And not follow-on siblings?
	 * @see #getNodeListOfNamedTags(java.util.List, opbm.Xml, java.lang.String, java.util.List, boolean)
	 */
	public void getNodeListOfNamedTags(List<Xml>		nodes,
									   String			searchMaster,
									   List<String>		namedTags,
									   boolean			onlyOneLevel)
	{
		Xml.getNodeListOfNamedTags(nodes, this, searchMaster, namedTags, onlyOneLevel);
	}

	/**
	 * The node list is built from loaded Xml data, using the . association,
	 * such that "firstName.secondName.thirdName" would reference all third-level
	 * items under every instance of "firstName.secondName" that exists, even if
	 * they are separated across interim nodes.  However, within that final level,
	 * only those options in the namedTags list are included.
	 *
	 * @param nodes List of nodes to append to
	 * @param root top-level Xml to search, can be true root or any branch
	 * @param searchMaster level to search, as in "opbm.panels.panel"
	 * @param namedTags list of named theseTags at that level to include
	 * @param onlyOneLevel are we only processing one level? And not follow-on siblings?
	 */
	public static void getNodeListOfNamedTags(List<Xml>		nodes,
											  Xml			root,
											  String		searchMaster,
											  List<String>	namedTags,
											  boolean		onlyOneLevel)
	{
		if (root != null) {
			Xml xmlThis, xmlChild;
			String search;
			List<String> theseTags = null;
			int i;
			boolean lLookingForMoreLevels = false;

			// Find out what we're searching for, a single tag or multiple theseTags deep
			if (searchMaster.contains(".")) {		// Looking for "something.like.this" (multiple theseTags/levels deep)
				lLookingForMoreLevels = true;
				search = searchMaster.substring(0, searchMaster.indexOf("."));

			} else {								// Looking for something like "this" (one tag only)
				search = searchMaster;

			}
			if (search.trim().startsWith("[") && search.trim().endsWith("]")) {
				// Looking for something like [multiple,theseTags,like,this]
				theseTags = new ArrayList<String>(0);
				getTagsList(theseTags, search.substring(1, search.length() - 1));
			}
			// When we get here:
			//	1) search is the string we're looking for
			//	2) lLookingForMoreLevels is populated, indicating if we're looking at multiple levels

			// Now, iterate through each sibling at this level to find all of those named search
			xmlThis = root;
			while (xmlThis != null) {
				if (theseTags != null) {
					if (searchTagsList(theseTags, xmlThis.getName())) {
						// This is a match at this level
						if (lLookingForMoreLevels) {
							// We have to continue at a lower level, which will update nodes
							getNodeList(nodes, xmlThis.getFirstChild(), searchMaster.substring(search.length() + 1, searchMaster.length()), onlyOneLevel);

						} else {
							// This is one we're adding
							xmlChild = xmlThis.getFirstChild();
							while (xmlChild != null) {
								// For every child at this level that matches a name in namedTags, add it
								for (i = 0; i < namedTags.size(); i++) {
									if (namedTags.get(i).equalsIgnoreCase(xmlChild.getName())) {
										nodes.add(xmlChild);		// This is a match, add it
									}
								}
								xmlChild = xmlChild.getNext();		// Move to next sibling and try there as well
							}
						}
					}

				} else {
					if (search.isEmpty() || xmlThis.getName().equalsIgnoreCase(search)) {
						// This is a match at this level
						if (lLookingForMoreLevels) {
							// We have to continue at a lower level, which will update nodes
							getNodeList(nodes, xmlThis.getFirstChild(), searchMaster.substring(search.length() + 1, searchMaster.length()), onlyOneLevel);

						} else {
							// This is one we're adding
							xmlChild = xmlThis.getFirstChild();
							while (xmlChild != null) {
								// For every child at this level that matches a name in namedTags, add it
								for (i = 0; i < namedTags.size(); i++) {
									if (namedTags.get(i).equalsIgnoreCase(xmlChild.getName())) {
										nodes.add(xmlChild);		// This is a match, add it
									}
								}
								xmlChild = xmlChild.getNext();		// Move to next sibling and try there as well
							}
						}
					}
				}
				if (onlyOneLevel)
					return;			// We only process through one level, not onto additional siblings

				// Move to next item, see if it's a match
				xmlThis = xmlThis.getNext();
			}
		}
		return;
	}

	/** Non-static version of <code>getAttribute()</code>
	 *
	 * @param name name of attribute
	 * @return value of attribute if found, empty string if not found
	 * @see #getAttribute(opbm.Xml, java.lang.String)
	 */
	public String getAttribute(String name)
	{
		return(Xml.getAttribute(this, name));
	}

	/** Return the value associated with this named attribute.
	 *
	 * @param root
	 * @param searchAttributeName name of attribute
	 * @return value of attribute if found, empty string if not found
	 */
	public static String getAttribute(Xml		root,
									  String	searchAttributeName)
	{
		Xml attrib;
		List<Xml>candidates;
		String s = "";

		if (searchAttributeName.contains("."))
		{	// The thing they're looking for may be deep, load the full list of nodes
			candidates = new ArrayList<Xml>(0);
			getNodeList(candidates, root, searchAttributeName, false);
			return(processCandidates(candidates));

		} else {
			// Just a specific entry, so process it directly
			attrib = root.getFirstAttribute();
			while (attrib != null) {
				if (attrib.getName().equalsIgnoreCase(searchAttributeName)) {
					// This is the match
					s = attrib.getText();
					break;
				}
				// Move to next attribute
				attrib = attrib.getNext();
			}
		}
		return(s);
	}

	public static String processCandidates(List<Xml> candidates)
	{
		int i;
		String s;

		if (candidates.isEmpty())
		{	// Not found
			return("");

		} else if (candidates.size() == 1) {
			// We only found one, so handle that
			return(candidates.get(0).getText());

		} else {
			// Many were pound, so concatenate them in a list, such as "one,two,three,four"
			s = "";
			for (i = 0; i < candidates.size(); i++)
				s += ((i == 0) ? "" : ",") + candidates.get(i).getText();

		}
		return(s);
	}

	/** Non-static version of <code>getAttribute()</code>
	 *
	 * @param name name of attribute
	 * @return value of attribute if found, empty string if not found
	 * @see #getAttributeNode(opbm.Xml, java.lang.String)
	 */
	public Xml getAttributeNode(String name)
	{
		return(Xml.getAttributeNode(this, name));
	}

	/** Return the value associated node with this named attribute.
	 *
	 * @param root
	 * @param name name of attribute
	 * @return value of attribute if found, empty string if not found
	 */
	public static Xml getAttributeNode(Xml		root,
									   String	name)
	{
		Xml attrib;

		attrib = root.getFirstAttribute();
		while (attrib != null) {
			if (attrib.getName().equalsIgnoreCase(name)) {
				// This is the match
				break;
			}
			// Move to next attribute
			attrib = attrib.getNext();
		}
		return(attrib);
	}

	/** Non-static version of <code>getChildNode(Xml, String)</code>
	 *
	 * @param name name of child to get
	 * @return Xml for that child (null if has none)
	 * @see #getChildNode(opbm.Xml, java.lang.String)
	 */
	public Xml getChildNode(String name)
	{
		return(getChildNode(this, name));
	}

	/** Obtains the first instance of the named child node.
	 *
	 * @param root parent to search for named child
	 * @param name name of child to get
	 * @return Xml for that child (null if has none)
	 */
	public static Xml getChildNode(Xml		root,
								  String	name)
	{
		Xml child;
		child = root.getFirstChild();
		while (child != null) {
			if (child.getName().equalsIgnoreCase(name)) {
				return(child);
			}
			// Move to next item
			child = child.getNext();
		}
		return(child);
	}

	/** Non-static version of <code>getChild</code>
	 *
	 * @param searchTagName name of tag to search, as in "tag" in <tag>foo</tag>
	 * @return text or value of tag's contents, as in "foo" in <tag>foo</tag>
	 * @see #getChild(opbm.Xml, java.lang.String)
	 */
	public String getChild(String searchTagName)
	{
		return(getChild(this, searchTagName));
	}

	/**
	 * Non-static version of <code>getAttributeOrChild(Xml, String)</code>.
	 * @param tag tag or child name to search
	 * @return value of specified tag or child
	 * @see #getAttributeOrChild(opbm.Xml, java.lang.String)
	 */
	public String getAttributeOrChild(String tag)
	{
		return(getAttributeOrChild(this, tag));
	}

	/** Returns the contents of the attribute or child tag specified (if found)
	 * empty string otherwise
	 *
	 * @param root parent of
	 * @param searchMaster tag to search for
	 * @return string of tag if found, empty string otherwise
	 */
	public static String getAttributeOrChild(Xml		root,
											 String		searchMaster)
	{
		Xml xml;
		String search;
		boolean lLookingForMoreLevels = false;

		// Find out what we're searching for
		// Either a single tag or multiple levels deep from the root node
		if (searchMaster.contains("."))
		{	// Looking for "something.like.this" or "something.like.#this" for an attribute named "this" of root.something.like
			search = searchMaster.substring(0, searchMaster.indexOf("."));
			searchMaster = searchMaster.substring(search.length() + 1, searchMaster.length()).trim();
			if (search.startsWith("#"))
			{	// Looking for an explicit attribute now
				return(getAttribute(root, search.substring(1)));

			} else {
				// May be looking for a child or an attribute
				xml = root.getChildNode(search);
				if (xml != null)
				{	// We found it in the child
					if (searchMaster.isEmpty())
						return(xml.getText());
					// Continue looking at lower levels
					return(getAttributeOrChild(xml, searchMaster));
				}
				// Was not found as a child, try an attribute
				xml = root.getAttributeNode(search);
				if (xml != null)
				{	// We found it in the attribute
					if (searchMaster.isEmpty())
						return(xml.getText());
					// They want to continue looking at lower levels beyond this attribute, which we can't
					// It's a syntax error
					return("");
				}
			}

		} else {								// Looking for something like "this" (one tag only)
			search = searchMaster;

		}

		// Process this tag
		xml = root.getChildNode(search);
		if (xml != null) {
			// We found it in the child
			return(xml.getText());
		}
		// Was not found as a child, try an attribute
		return(getAttribute(root, search));
	}

	/** Returns the node of the attribute or child tag specified (if found)
	 * or null otherwise
	 *
	 * @param root parent of
	 * @param tag tag to search for
	 * @return string of tag if found, empty string otherwise
	 */
	public static Xml getAttributeOrChildNode(Xml		root,
											  String	tag)
	{
		Xml x;

		x = root.getChildNode(tag);
		if (x != null) {
			// We found it in the child
			return(x);
		}

		// Was not found as a child, try an attribute
		return(getAttributeNode(root, tag));
	}

	public String getAttributeOrChildExplicit(String tag)
	{
		return(getAttributeOrChildExplicit(this, tag, "", "", true));
	}

	public String getAttributeOrChildExplicit(String		tag,
											  boolean		mustBePopulatedOtherwiseAssumeNotFound)
	{
		return(getAttributeOrChildExplicit(this, tag, "", "", mustBePopulatedOtherwiseAssumeNotFound));
	}

	public String getAttributeOrChildExplicit(String		tag,
											  String		ifNotFound,
											  boolean		mustBePopulatedOtherwiseAssumeNotFound)
	{
		return(getAttributeOrChildExplicit(this, tag, "", ifNotFound, mustBePopulatedOtherwiseAssumeNotFound));
	}

	public String getAttributeOrChildExplicit(String		tag,
											  String		prefixIfFound,
											  String		ifNotFound,
											  boolean		mustBePopulatedOtherwiseAssumeNotFound)
	{
		return(getAttributeOrChildExplicit(this, tag, prefixIfFound, ifNotFound, mustBePopulatedOtherwiseAssumeNotFound));
	}

	/**
	 * Returns the contents of the explicitly named attribute or child tag (use
	 * "#" pound sign for attributes).
	 *
	 * @param root root node to search for child or attribute
	 * @param tag attribute or child tag looking for
	 * @param prefixIfFound if the tag is found, return its text prefixed by this
	 * @param ifNotFound if the tag is not found, return this text
	 * @param mustBePopulatedOtherwiseAssumeNotFound specifies a condition
	 * whereby the searched tag must actually have non-blank content to be
	 * considered "found"
	 * @return the string prefixed appropriately based on found, not found,
	 * and text
	 */
	public static String getAttributeOrChildExplicit(Xml		root,
													 String		tag,
													 String		prefixIfFound,
													 String		ifNotFound,
													 boolean	mustBePopulatedOtherwiseAssumeNotFound)
	{
		String s;
		Xml x;
		List<Xml> candidates;

		if (tag.contains("."))
		{	// The thing they're looking for may be deep, load the full list of nodes
			candidates = new ArrayList<Xml>(0);
			getNodeList(candidates, root, tag, false);
			return(processCandidates(candidates));

		} else {
			if (!tag.isEmpty()) {
				if (tag.startsWith("#")) {
					// Looking for an attribute
					s = root.getAttribute(tag.substring(1));
					if (!s.isEmpty()) {
						// We found it as an attribute
						return(prefixIfFound + s);
					}

				} else {
					// Looking for a child
					x = root.getChildNode(tag);
					if (x != null) {
						// We found it in the child, return its text
						if (!(x.getText().isEmpty() && mustBePopulatedOtherwiseAssumeNotFound))
							return(prefixIfFound + x.getText());
					}

				}
				// Not found
				return(ifNotFound);
			}

		}

		// If the tag wasn't specified, we ignore it completely
		return("");
	}

	/**
	 * Returns the <code>Xml</code> node of the explicitly named parameter or
	 * child tag.
	 *
	 * @param root root node to search for child or attribute
	 * @param tag attribute or child tag looking for
	 * @return <code>Xml</code> tag of the attribute or child
	 */
	public static Xml getAttributeOrChildNodeExplicit(Xml		root,
													  String	tag)
	{
		if (!tag.isEmpty()) {
			if (tag.startsWith("#")) {
				// Looking for an attribute
				return(root.getAttributeNode(tag.substring(1)));

			} else {
				// Looking for a child
				return(root.getChildNode(tag));

			}
			// Not found
		}
		// If the tag wasn't specified, we ignore it completely
		return(null);
	}

	/**
	 * Updates the attribute or child content explicitly (prefix attributes
	 * with a "#" pound sign).
	 *
	 * @param root root node to search for child or attribute
	 * @param tag attribute or child tag looking for
	 * @param text what to store there
	 */
	public static void setAttributeOrChildExplicit(Xml		root,
												   String	tag,
												   String	text)
	{
		Xml x;

		x = getAttributeOrChildNodeExplicit(root, tag);
		if (x != null) {
			// We found something to update
			x.setText(text);

		} else {
			// It wasn't found, add it explicitly
			if (tag.startsWith("#")) {
				// We're adding an attribute
				root.addAttribute(tag, text);

			} else {
				// We're adding a child
				root.addChild(tag, text);

			}
		}
	}


	/**
	 * Obtains text from within a tag, as in <tag>text</tag>.
	 *
	 * @param root top-level sibling to being searching
	 * @param searchTagName name of tag to search, as in "tag" in <tag>foo</tag>
	 * @return text or value of tag's contents, as in "foo" in <tag>foo</tag>
	 */
	public static String getChild(Xml		root,
								  String	searchTagName)
	{
		int i;
		List<Xml>candidates;
		Xml xmlThis;
		String s = "";

		if (searchTagName.contains("."))
		{	// The thing they're looking for may be deep, load the full list of nodes
			candidates = new ArrayList<Xml>(0);
			getNodeList(candidates, root, searchTagName, false);
			return(processCandidates(candidates));

		} else {
			// Just a specific entry, so process it directly
			xmlThis = root;
			while (xmlThis != null) {
				if (xmlThis.getName().equalsIgnoreCase(searchTagName)) {
					s = xmlThis.getText();		// This is it
					break;
				}
				// Move to next item
				xmlThis = xmlThis.getNext();
			}

		}
		return(s);
	}

	/**
	 * Another way to iterate through several theseTags to find the specified name
	 * and returns its text or value.
	 *
	 * @param tags list of theseTags to search
	 * @param searchTagName name searching for
	 * @return text or value of tag
	 */
	public static String getTextFromTag(List<Xml>	tags,
										String		searchTagName)
	{
		int i;
		Xml xmlThis;
		String text = "";

		// Search through all theseTags in the list, and return the value of the first one found
		for (i = 0; i < tags.size(); i++)
		{
			xmlThis = tags.get(i);
			if (xmlThis.getName().equalsIgnoreCase(searchTagName))
			{
				text = xmlThis.getText();		// This is it
				break;
			}
			// If we get here, this wasn't it
		}
		return(text);
	}

	/**
	 * Setter. Sets the name of the node, the old is discarded
	 * @param name new name
	 */
	public void setName(String name)
	{
		m_name = name;
	}

	/**
	 * Setter. Sets the cdata for the node, the old is discarded
	 * @param cdata new cdata
	 */
	public void setCdata(String cdata)
	{
		m_cdata = cdata;
	}

	/**
	 * Setter. Sets the text for the node, what appears between theseTags, as in <tag>new text</tag>
	 * @param text new text for the node
	 */
	public void setText(String text)
	{
		m_text = text;
	}

	/**
	 * Setter. Sets the comment for the node, what will be written after the text, as in
	 * <tag>text<!-- comment goes here --></tag>
	 * @param comment new comment
	 */
	public void setComment(String comment)
	{
		m_comment = comment;
	}

	/**
	 * Setter. Sets the new parent for this node, allowing a node to be moved, or to
	 * be initially setup.
	 *
	 * @param parent new parent
	 */
	public void setParent(Xml parent)
	{
		m_parent = parent;
	}

	/**
	 * Setter. Sets the previous sibling for this node
	 *
	 * @param prev new previous sibling. Note: Does not automatically update
	 * any link lists. For those functions use <code>insertNodeBefore()</code> or
	 * <code>insertNodeAfter()</code>.
	 */
	public void setPrev(Xml prev)
	{
		m_prev = prev;
	}

	/**
	 * Setter. Sets the next sibling for this node
	 *
	 * @param next next sibling. Note: Does not automatically update any link
	 * lists. For those functions, use <code>insertNodeBefore()</code> or
	 * <code>insertNodeAfter()</code>.
	 */
	public void setNext(Xml next)
	{
		m_next = next;
	}

	/**
	 * Setter. Sets the first attribute for this node
	 *
	 * @param firstAttribute new first attribute. Note: Does not automatically
	 * update any link lists. For those functions, use <code>insertNodeBefore()</code>
	 * or <code>insertNodeAfter()</code> with the isAttribute flag set to true.
	 */
	public void setFirstAttribute(Xml firstAttribute)
	{
		m_firstAttribute = firstAttribute;
	}

	/**
	 * Setter. Sets the first child for this node
	 *
	 * @param firstChild new first attribute. Note: Does not automatically
	 * update any link lists. For those functions, use <code>insertNodeBefore()</code>
	 * or <code>insertNodeAfter()</code> with the isAttribute flag set to false.
	 */
	public void setFirstChild(Xml firstChild)
	{
		m_firstChild = firstChild;
	}

	/**
	 * Inserts the specified Xml object before this one in the chain.
	 *
	 * @param newNode Xml object to insert
	 * @param isAttribute true if this node is an attribute (alters which parent
	 * item is updated), false if regular child
	 */
	public void insertNodeBefore(Xml		newNode,
								 boolean	isAttribute)
	{
		Xml prev;

		if (newNode.getParent() == null && m_parent != null)
			newNode.setParent(m_parent);

		if (m_prev != null)
		{
			// Inserting in the middle of a chain
			prev = m_prev;
			prev.setNext(newNode);
			setPrev(newNode);
			newNode.setPrev(prev);
			newNode.setNext(this);

		} else {
			// Appending to beginning
			setPrev(newNode);
			newNode.setNext(this);

			if (m_parent != null)
			{
				if (isAttribute)
					m_parent.setFirstAttribute(newNode);
				else
					m_parent.setFirstChild(newNode);
			}
		}
	}

	/**
	 * Alias for <code>insertNodeAfter()</code> used for clarity in contexts.
	 *
	 * @param newNode created Xml to insert after this
	 */
	public void insertNode(Xml newNode)
	{
		insertNodeAfter(newNode);
	}

	/**
	 * Inserts the specified Xml immediately after this one in the chain.  Used
	 * for siblings.  Use <code>setFirstAttribute()</code> or <code>setFirstChild()</code>
	 * to set the first of a branch.
	 *
	 * @param newNode created Xml to insert after this
	 */
	public void insertNodeAfter(Xml newNode)
	{
		Xml next;

		if (newNode.getParent() == null && m_parent != null)
			newNode.setParent(m_parent);

		if (m_next != null)
		{
			// Inserting in the middle of a chain
			next = m_next;
			next.setPrev(newNode);			// next points back to new node
			setNext(newNode);				// this points forward to new node
			newNode.setPrev(this);			// newnode points back to this
			newNode.setNext(next);			// new node points forward to next

		} else {
			// Appending after this, will now be the last one (instead of this one being the last one)
			setNext(newNode);
			newNode.setPrev(this);

		}
	}

	/**
	 * Creates a new <code>Xml</code> and inserts it before the start of the
	 * child list
	 *
	 * @param root <code>Xml</code> to add to
	 * @param tag tag to create
	 * @param text text to store for tag
	 */
	public static void addChild(Xml root, String tag, String text)
	{
		addChild(root, new Xml(tag, text));
	}

	/**
	 * Creates a new <code>Xml</code> and inserts it before the start of the
	 * child list
	 *
	 * @param tag tag to create
	 * @param text text to store for tag
	 */
	public void addChild(String tag, String text)
	{
		addChild(this, new Xml(tag, text));
	}

	/**
	 * Adds a child tag to beginning of the chain of children.
	 *
	 * @param xmlAdd <code>Xml</code> to add
	 * @return xmlAdd as a convenience
	 */
	public Xml addChild(Xml xmlAdd)
	{
		return(addChild(this, xmlAdd));
	}

	/**
	 * Adds a child tag to beginning of the chain of children.
	 *
	 * @param root <code>Xml</code> to add to
	 * @param xmlAdd <code>Xml</code> to add
	 * @return xmlAdd as a convenience
	 */
	public static Xml addChild(Xml	root,
							   Xml	xmlAdd)
	{
		Xml child;

		if (xmlAdd.getParent() == null)
			xmlAdd.setParent(root);

		if (root.m_firstChild != null) {
			// Add to the beginning of the chain
			child = root.m_firstChild;
			child.insertNodeBefore(xmlAdd, false);

		} else {
			// Is the first child
			root.m_firstChild = xmlAdd;

		}

		return(xmlAdd);
	}

	/**
	 * Creates a new <code>Xml</code> and inserts it before the start of the
	 * attribute list
	 *
	 * @param tag tag to create
	 * @param text text to store for tag
	 */
	public void addAttribute(String tag, String text)
	{
		addAttribute(this, new Xml(tag, text));
	}

	/**
	 * Creates a new <code>Xml</code> and inserts it before the start of the
	 * attribute list
	 *
	 * @param root <code>Xml</code> node to add to
	 * @param tag tag to create
	 * @param text text to store for tag
	 */
	public static void addAttribute(Xml root, String tag, String text)
	{
		addAttribute(root, new Xml(tag, text));
	}

	/**
	 * Adds an attribute to the beginning of the attribute list
	 * @param xmlAdd parent xml to which to add this attribute
	 * @return new attribute
	 */
	public Xml addAttribute(Xml xmlAdd)
	{
		return(addAttribute(this, xmlAdd));
	}

	/**
	 * Adds an attribute to the beginning of the attribute list
	 * @param root <code>Xml</code> node we're adding to
	 * @param xmlAdd parent <code>Xml</code> to which to add this attribute
	 * @return new attribute
	 */
	public static Xml addAttribute(Xml root, Xml xmlAdd)
	{
		Xml child, nextChild;

		if (xmlAdd.getParent() == null)
			xmlAdd.setParent(root);

		// See if it already exists, and if so delete the attribute
		child = root.m_firstAttribute;
		while (child != null && child.getNext() != null)
		{
			if (child.getName().equalsIgnoreCase(xmlAdd.getName()))
			{	// The attribute name already exists, so we update it
				nextChild = child.getNext();
				child.deleteNode(true);
				child = nextChild;

			} else {
				child = child.getNext();
			}
		}

		if (root.m_firstAttribute != null) {
			// Add to the beginning of the chain
			root.m_firstAttribute.insertNodeBefore(xmlAdd, true);

		} else {
			// Is the first child
			root.m_firstAttribute = xmlAdd;

		}

		return(xmlAdd);
	}

	/**
	 * Creates a new <code>Xml</code> and appends it to the child list
	 *
	 * @param root <code>Xml</code> node to add to
	 * @param tag tag to create
	 * @param text text to store for tag
	 */
	public static void appendChild(Xml root, String tag, String text)
	{
		appendChild(root, new Xml(tag, text));
	}

	/**
	 * Creates a new <code>Xml</code> and appends it to the child list
	 *
	 * @param tag tag to create
	 * @param text text to store for tag
	 */
	public void appendChild(String tag, String text)
	{
		appendChild(this, new Xml(tag, text));
	}

	/**
	 * Appends a child to the end of the children.
	 *
	 * @param xmlAdd <code>Xml</code> to append
	 * @return new child
	 */
	public Xml appendChild(Xml xmlAdd)
	{
		return(appendChild(this, xmlAdd));
	}

	/**
	 * Appends a child to the end of the children.
	 *
	 * @param root <code>Xml</code> node to add to
	 * @param xmlAdd <code>Xml</code> to append
	 * @return new child
	 */
	public static Xml appendChild(Xml	root,
								  Xml	xmlAdd)
	{
		Xml child;

		if (xmlAdd.getParent() == null)
			xmlAdd.setParent(root);

		if (root.m_firstChild != null) {
			// Add to the end of the chain
			child = root.m_firstChild;
			while (child != null && child.getNext() != null)
			{
				child = child.getNext();
			}
			// Append after this one
			child.setNext(xmlAdd);

		} else {
			// Is the first child
			root.m_firstChild = xmlAdd;

		}

		return(xmlAdd);
	}

	/**
	 * Creates a new <code>Xml</code> and appends it to the attribute list
	 *
	 * @param tag tag to create
	 * @param text text to store for tag
	 */
	public void appendAttribute(String tag, String text)
	{
		appendAttribute(this, new Xml(tag, text));
	}

	/**
	 * Appends a child to the end of the attribute list
	 *
	 * @param xmlAdd <code>Xml</code> node to add
	 * @return new attribute
	 */
	public Xml appendAttribute(Xml xmlAdd) {
		return(appendAttribute(this, xmlAdd));
	}

	/**
	 * Appends a child to the end of the attribute list
	 *
	 * @param root <code>Xml</code> node to add to
	 * @param xmlAdd <code>Xml</code> to add
	 * @return new attribute
	 */
	public static Xml appendAttribute(Xml	root,
									  Xml	xmlAdd)
	{
		Xml child, nextChild;

		if (xmlAdd.getParent() == null)
			xmlAdd.setParent(root);

		if (root.m_firstAttribute != null) {
			// Add to the end of the chain
			child = root.m_firstAttribute;
			while (child != null && child.getNext() != null)
			{
				if (child.getName().equalsIgnoreCase(xmlAdd.getName()))
				{	// The attribute name already exists, so we update it
					nextChild = child.getNext();
					child.deleteNode(true);
					child = nextChild;

				} else {
					child = child.getNext();

				}
			}
			// Append after this one
			child.setNext(xmlAdd);

		} else {
			// Is the first child
			root.m_firstAttribute = xmlAdd;

		}

		return(xmlAdd);
	}

	/**
	 * Clones the node all the way down as far as it goes, including
	 * (optionally) attributes at the parent level (all attributes of child
	 * items are included).
	 *
	 * @param cloneAttributes should attributes be cloned
	 * @return newly created <code>Xml</code>
	 */
	public Xml cloneNode(boolean cloneAttributes)
	{
		Xml clone = new Xml(m_name, m_text);

		// Clone the attributes if instructed to
		if (cloneAttributes) {
			clone.setFirstAttribute(copyBranch(m_firstAttribute, false));
		}

		// Copy the child entry
		clone.setFirstChild(copyBranch(m_firstChild, cloneAttributes));

		// Copy the cdata and comment (which may be null)
		clone.setCdata	(m_cdata);
		clone.setComment(m_comment);

		// All done
		return(clone);
	}

	/**
	 * Recursively calls itself to copy every child and attribute node through
	 * the entire branch as far down as it goes (can be used to completely
	 * duplicate an entire Xml file by passing its root node).
	 *
	 * @param copyFrom
	 * @param copyAttributes
	 * @return new branch
	 * @see #cloneNode(boolean) as proper method to call directly
	 */
	public Xml copyBranch(Xml copyFrom, boolean copyAttributes)
	{
		Xml child = null;

		if (copyFrom != null) {
			// Create the child entry
			child = new Xml(copyFrom.getName(), copyFrom.getText());

			if (copyAttributes) {
				// Add its attributes if instructed to
				child.setFirstAttribute(copyBranch(copyFrom.getFirstAttribute(), false));
			}

			// Add cdata and comment (which may be null)
			child.setCdata	(copyFrom.getCdata());
			child.setComment(copyFrom.getComment());

			// Add its children
			child.setFirstChild(copyBranch(copyFrom.getFirstChild(), copyAttributes));

			// Copy siblings
			child.setNext(copyBranch(copyFrom.getNext(), copyAttributes));
		}
		return(child);
	}

	/**
	 * Clones the node tree structure, but without copying any data (text)
	 * to attributes or theseTags.
	 *
	 * @param cloneAttributes should top-level attributes be included
	 * @return newly added root of cloned branch
	 */
	public Xml cloneNodeTree(boolean cloneAttributes)
	{
		Xml clone = new Xml(m_name);

		// Clone the attributes if instructed to
		if (cloneAttributes) {
			clone.setFirstAttribute(copyBranchTree(m_firstAttribute, false));
		}

		// Copy the child entry
		clone.setFirstChild(copyBranchTree(m_firstChild, cloneAttributes));

		// All done
		return(clone);
	}

	/**
	 * Recursively calls itself to copy every child and attribute node through
	 * the entire branch as far down as it goes (can be used to completely
	 * duplicate an entire Xml file by passing its root node).
	 *
	 * @param copyFrom
	 * @param copyAttributes
	 * @return new branch
	 */
	public Xml copyBranchTree(Xml copyFrom, boolean copyAttributes)
	{
		Xml child = null;

		if (copyFrom != null) {
			// Create the child entry
			child = new Xml(copyFrom.getName());

			if (copyAttributes) {
				// Add its attributes if instructed to
				child.setFirstAttribute(copyBranchTree(copyFrom.getFirstAttribute(), false));
			}

			// Add its children
			child.setFirstChild(copyBranchTree(copyFrom.getFirstChild(), copyAttributes));

			// And its siblings
			child.setNext(copyBranchTree(copyFrom.getNext(), copyAttributes));
		}
		return(child);
	}

	/**
	 * Clones the attributes and optionally values for those attributes.
	 *
	 * @param copyFrom item we're copying our attributes from
	 * @param copyValues should attribute values be copied too
	 */
	public void cloneAttributes(Xml copyFrom, boolean copyValues) {
		Xml child;

		child = copyFrom.getFirstAttribute();
		while (child != null) {
			appendAttribute(new Xml(child.getName(), copyValues ? child.getText() : ""));
			child = child.getNext();
		}
	}

	/**
	 * Called to delete the specified Xml. Does not explicitly delete the object
	 * as Java is garbage collected.  However, does update its position in the
	 * chain, and possibly updates the parent if this was the first/only child.
	 *
	 * @param isAttribute true if this node is an attribute (alters which parent
	 * item is updated), false if regular child
	 */
	public void deleteNode(boolean isAttribute) {
		// See if there's one after this
		if (m_next != null) {
			if (m_prev != null) {
				// There's one before this too
				m_prev.setNext(m_next);
				m_next.setPrev(m_prev);

			} else {
				// This is the first one
				if (m_parent != null) {
					if (isAttribute)
						m_parent.setFirstAttribute(m_next);
					else
						m_parent.setFirstChild(m_next);

				} else {
					// This is likely an error.
					// Every node should have a parent (except the root-most node)

				}

			}

		} else {
			if (m_prev != null) {
				// This was the last one
				m_prev.setNext(null);

			} else {
				// This was the only one
				if (m_parent != null) {
					if (isAttribute)
						m_parent.setFirstAttribute(null);
					else
						m_parent.setFirstChild(null);
				}

			}
		}
	}

	/**
	 * Moves a node down after its next entry (switches positions with its next
	 * sibling -- if any, does not move if no next sibling).
	 */
	public boolean moveNodeDown()
	{
		Xml p, n, pp, pn, np, nn;

		if (m_next != null)
		{
			p = m_prev;
			n = m_next;
		  //tp = m_prev;
		  //tn = m_next;
			np = n.getPrev();
			nn = n.getNext();
			if (m_prev != null)
			{
				// There is a sibling after this to switch with
				pp = p.getPrev();
				pn = p.getNext();

				// Logically, the link-list pointer values are laid out like this:
				//			 / pp = (prev).prev, points back to previous sibling (prev's prev)
				//		(prev)
				//			 \ pn = (prev).next, points forward to next sibling (prev's next), or (this)
				//			 / tp = (this).prev, points backward to previous sibling (this's prev) or (prev)
				//		(this)
				//			 \ tn = (this).next, points forward to next sibling (this's next) or (next)
				//			 / np = (next).prev, points backward to prev sibling (next's prev) or (this)
				//		(next)
				//			 \ nn = (next).next, points forward to next sibling (next's next)
				//
				// Arrangement before the node move:
				//		(prev)
				//		(this)
				//		(next)
				// After the move:
				//		(prev)
				//		(next)
				//		(this)

				// Each of the relative pointers have to be updated to their new
				// logical arrangement:
				p.setNext(n);			// (prev).next = (next)
				n.setPrev(p);			// (next).prev = (prev)
				n.setNext(this);		// (next).next = (this)
				m_prev = n;				// (this).prev = (next)
				m_next = nn;			// (this).next = (next).next

				// Update possible new "first child" or "first attribute" conditions
				if (m_parent != null)
				{
					// based on if (this) is now moved into the first position
					if (m_parent.getFirstChild() == this)
						m_parent.setFirstChild(p);
					else if (m_parent.getFirstAttribute() == this)
						m_parent.setFirstAttribute(p);
				}

			} else {
				// There is no sibling before this to switch with, which means
				// these two are just switching position
				// Logically, the link-list pointer values are laid out like this:
				//			 / null
				//		(this)
				//			 \ tn = (this).next, points forward to next sibling (this's next) or (next)
				//			 / np = (next).prev, points backward to prev sibling (next's prev) or (this)
				//		(next)
				//			 \ nn = (next).next, points forward to next sibling (next's next)
				//
				// Arrangement before the node move:
				//		(this)
				//		(next)
				// After the move:
				//		(next)
				//		(this)

				// Each of the relative pointers have to be updated to their new
				// logical arrangement:
				n.setPrev(null);		// (next).prev = null
				n.setNext(this);		// (next).next = (this)
				m_prev = n;				// (this).prev = (next)
				m_next = nn;			// (this).next = (next).next

				// Update possible new "first child" or "first attribute" conditions
				if (m_parent != null)
				{
					// based on if (this) is now moved into the first position
					if (m_parent.getFirstChild() == this)
						m_parent.setFirstChild(n);
					else if (m_parent.getFirstAttribute() == this)
						m_parent.setFirstAttribute(n);
				}

			}

			return(true);
		}
		return(false);
	}

	/**
	 * Moves a node up, before its previous entry (switches positions with its
	 * previous sibling -- if any, does not move if no previous sibling).
	 */
	public boolean moveNodeUp()
	{
		Xml p, n, pp, pn, np, nn;

		if (m_prev != null)
		{
			// There is a sibling before this to switch with
			p = m_prev;
			n = m_next;
			pp = p.getPrev();
			pn = p.getNext();
		  //tp = m_prev;
		  //tn = m_next;
			if (m_next != null)
			{
				np = n.getPrev();
				nn = n.getNext();
				// Logically, the link-list pointer values are laid out like this:
				//			 / pp = (prev).prev, points back to previous sibling (prev's prev)
				//		(prev)
				//			 \ pn = (prev).next, points forward to next sibling (prev's next), or (this)
				//			 / tp = (this).prev, points backward to previous sibling (this's prev) or (prev)
				//		(this)
				//			 \ tn = (this).next, points forward to next sibling (this's next) or (next)
				//			 / np = (next).prev, points backward to prev sibling (next's prev) or (this)
				//		(next)
				//			 \ nn = (next).next, points forward to next sibling (next's next)
				//
				// Arrangement before the node move:
				//		(prev)
				//		(this)
				//		(next)
				// After the move:
				//		(this)
				//		(prev)
				//		(next)
				// Each of the relative pointers have to be updated to their new
				// logical arrangement:
				m_prev = pp;			// (this).prev = (prev).prev
				m_next = p;				// (this).next = (prev)
				p.setPrev(this);		// (prev).prev = (this)
				p.setNext(n);			// (prev).next = (next)
				n.setPrev(p);			// (next).prev = (prev)
				if (pp != null)
					pp.setNext(this);

			} else {
				// Nothing after, which means this is the last one, and these
				// two are just switching position.
				//
				// Logically, the link-list pointer values are laid out like this:
				//			 / pp = (prev).prev, points back to previous sibling (prev's prev)
				//		(prev)
				//			 \ pn = (prev).next, points forward to next sibling (prev's next), or (this)
				//			 / tp = (this).prev, points backward to previous sibling (this's prev) or (prev)
				//		(this)
				//			 \ null
				//
				// Arrangement before the node move:
				//		(prev)
				//		(this)
				// After the move:
				//		(this)
				//		(prev)
				// Each of the relative pointers have to be updated to their new
				// logical arrangement:
				m_prev = pp;			// (this).prev = (prev).prev
				m_next = p;				// (this).next = (prev)
				p.setPrev(this);		// (prev).prev = (this)
				p.setNext(null);		// (prev).next = null
				if (pp != null)
					pp.setNext(this);
			}

			// Update possible new "first child" or "first attribute" conditions
			if (m_parent != null)
			{
				// based on if (this) is now moved into the first position
				if (m_parent.getFirstChild() == p)
					m_parent.setFirstChild(this);
				else if (m_parent.getFirstAttribute() == p)
					m_parent.setFirstAttribute(this);
			}

			return(true);
		}
		return(false);
	}

	/**
	 * From where we are, count the number of siblings to the end at this level.
	 * Note:  To count the entire range of all siblings at this level, use
	 * <code>xml.getParent().getFirstChild().countSiblings();</code>
	 *
	 * @return number of siblings from this point forward
	 */
	public int countSiblings()
	{
		int num;
		Xml sib;

		num = 0;
		sib = this;
		while (sib != null)
		{
			sib = sib.getNext();
			++num;
		}
		return(num);
	}

	/**
	 * Saves the current level node and all attributes and children to the
	 * specified filename.  Non-static version of saveNode(Xml root, String fileName).
	 *
	 * @param fileName file name to write output
	 * @see #saveNode(opbm.Xml, java.lang.String)
	 */
	public boolean saveNode(String fileName)
	{
		return(saveNode(this, fileName));
	}

	/**
	 * Saves node (itself, attributes, and all children) to the specified
	 * filename.
	 *
	 * @param root <code>Xml</code> node to
	 * @param fileName file name to write output
	 */
	public static boolean saveNode(Xml root, String fileName)
	{
		boolean result = false;

		try {
			File f;
			FileOutputStream fo;

			f = new File(fileName);
			fo = new FileOutputStream(f);
			DataOutputStream dos = new DataOutputStream(fo);
			dos.writeBytes("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			writeNode(root, dos, 0);
			result = true;
			dos.close();

		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
		return(result);
	}

	/**
	 * Writes a specific node, itself, and all its attributes, to the specified
	 * <code>DataOutputStream</code>.  This method should not be called directly,
	 * but is called from <code>saveNode()</code>.
	 *
	 * @param node <code>Xml</code> node to save
	 * @param dos <code>DataOutputStream</code> to write to
	 * @param level indentation level
	 * @throws IOException for failed writes
	 * @see #saveNode(java.lang.String)
	 */
	public static void writeNode(Xml node, DataOutputStream dos, int level)
			throws IOException
	{
		int i;

		if (node != null)
		{
			// Prefix this level with tabs for each level
			for (i = 0; i < level; i++) {
				dos.writeBytes("\t");
			}

			// Write the opening tag
			dos.writeBytes("<" + node.getName().trim());
			if (node.getFirstAttribute() != null) {
				// Write any attributes
				writeNodeAttributes(node.getFirstAttribute(), dos);
			}
			dos.writeBytes(">");

			if (node.getText() != null) {
				dos.writeBytes(node.getText().trim());
			}

			if (node.getFirstChild() != null) {
				dos.writeBytes("\n");
				writeNode(node.getFirstChild(), dos, level + 1);

				// Prefix this level with tabs for each level
				for (i = 0; i < level; i++) {
					dos.writeBytes("\t");
				}
			}

			dos.writeBytes("</" + node.getName().trim() + ">\n");

			// Write the next sibling at this level
			writeNode(node.getNext(), dos, level);
		}
	}

	/**
	 * Writes a specific node's attribute's list in the form of attrib="value"
	 * repeating until all attributes are included.  This method should not be
	 * called directly, but is called from <code>saveNode()</code>.
	 *
	 * @param node <code>Xml</code> node possibly having attributes
	 * @param dos <code>DataOutputStream</code> to write to
	 * @throws IOException for failed writes
	 */
	public static void writeNodeAttributes(Xml node, DataOutputStream dos)
			throws IOException
	{
		while (node != null) {
			dos.writeBytes(" " + node.getName().trim() + "=\"" + node.getText().trim() + "\"");
			node = node.getNext();
		}
	}

	/**
	 * Getter. Returns the node name
	 *
	 * @return node name
	 */
	public String getName() {
		return(m_name);
	}

	/**
	 * Getter. Returns the node's CDATA
	 *
	 * @return raw cdata string, excluding leading <code><![CDATA[</code> and
	 * closing <code>]]></code> theseTags
	 */
	public String getCdata() {
		return(m_cdata);
	}

	/**
	 * Getter. Returns the node's text
	 *
	 * @return raw text string, as in "text" in <tag>text</tag>
	 */
	public String getText() {
		return(m_text);
	}

	/**
	 * Getter. Returns the node's comment
	 *
	 * @return comment string, excluding leading <code><!--</code> and closing
	 * </code>--></code> theseTags
	 */
	public String getComment() {
		return(m_comment);
	}

	/**
	 * Getter. Returns the Xml parent node
	 *
	 * @return parent Xml node
	 */
	public Xml getParent() {
		return(m_parent);
	}

	/**
	 * Getter. Returns the previous Xml sibling
	 *
	 * @return previous Xml sibling at this level
	 */
	public Xml getPrev() {
		return(m_prev);
	}

	/**
	 * Getter. Returns the next Xml sibling
	 *
	 * @return next Xml sibling at this level
	 */
	public Xml getNext() {
		return(m_next);
	}

	/**
	 * Getter. Returns the first attribute for this node
	 *
	 * @return Xml node for first attribute
	 */
	public Xml getFirstAttribute() {
		return(m_firstAttribute);
	}

	/**
	 * Getter. Returns the first child for this node
	 *
	 * @return Xml node for first child
	 */
	public Xml getFirstChild() {
		return(m_firstChild);
	}

	/**
	 * Holds "tag" in <tag>some data</tag>
	 */
	private	String	m_name;

	/**
	 * Holds "test" in <tag><![CDATA[test]]></tag>
	 */
	private	String	m_cdata;

	/**
	 * Holds "some data" in <tag>some data</tag>
	 */
	private	String	m_text;

	/**
	 * Holds the full comment from beginning to end, as in "<!-- comment here -->"
	 */
	private String	m_comment;

	/**
	 * Parent Xml object in the tree
	 */
	private	Xml		m_parent;

	/**
	 * First attribute of this Xml object
	 */
	private	Xml		m_firstAttribute;

	/**
	 * First child of this Xml object
	 */
	private	Xml		m_firstChild;

	/**
	 * Previous sibling at this level for this Xml object
	 */
	private	Xml		m_prev;

	/**
	 * Next sibling at this level for this Xml object
	 */
	private	Xml		m_next;
}
