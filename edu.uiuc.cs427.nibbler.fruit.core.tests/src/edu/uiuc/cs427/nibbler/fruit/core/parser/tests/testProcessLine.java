package edu.uiuc.cs427.nibbler.fruit.core.parser.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import junit.framework.TestCase;
import edu.uiuc.cs427.nibbler.fruit.core.parser.FruitParser;


public class testProcessLine extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParseLine()
	{
		FruitParser parser = new FruitParser();
		try {
			
			Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + File.separator +"myoutput.txt"));
			while (scanner.hasNextLine())
			{
				parser.parseLine(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assertTrue(parser.getElapsedTime() < 0.1F);
		assertTrue(parser.getNumberOfAssertions() == 4);
		assertTrue(parser.getNumberOfFailedAssertions() == 1);
	}

}
