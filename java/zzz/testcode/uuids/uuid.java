
		Xml test = loadXml("test.xml");
		Xml found = test.getNodeByUUID("c932d4da-d2e8-4c2d-95c6-2bfb8328142d", false);
		if (found != null)
		{	// Found it, print the history
			while (found != null)
			{
				System.out.println("Xml: " + found.getName());
				found = found.getParent();
			}
		} else {
			System.out.printf("UUID \"c932d4da-d2e8-4c2d-95c6-2bfb8328142d\" not found");
		}
