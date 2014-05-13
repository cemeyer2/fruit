package net.charliemeyer.htmlparser;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;


/**
 * @author cemeyer2
 * represents one row of data, currently can be exported to JDOM xml element
 * or a row of jxl cells
 */
public class Row
{
	ArrayList<String> keys;
	ArrayList<String> values;
	ArrayList<String[]> attributekeys;
	ArrayList<String[]> attributevalues;
	String header;
	String[] attributeNames;
	String[] attributeValues;
	
	
	public Row(int intervalNumber)
	{
		this("Interval", "number", ""+intervalNumber);
		
	}
	
	public Row(Object descriptor)
	{
		this(descriptor.toString(), new String[]{}, new String[]{});
	}
	
	public Row(Object descriptor, Object attributename, Object attributevalue)
	{
		this(descriptor, new String[]{attributename.toString()}, new String[]{attributevalue.toString()});
	}
	
	public Row(Object descriptor, String[] attributenames, String[] attributevals)
	{
		if(attributenames.length != attributevals.length)
			throw new IllegalArgumentException("attribute names and values must have same cardinality");
		header = descriptor.toString();
		keys = new ArrayList<String>();
		values = new ArrayList<String>();
		attributekeys = new ArrayList<String[]>();
		attributevalues = new ArrayList<String[]>();
		attributeNames = attributenames;
		attributeValues = attributevals;
	}
	
	public String getHeader()
	{
		return header;
	}
	
	public void addElement(Object descriptor, Object value, int index)
	{
		if(descriptor != null)
			keys.add(index, descriptor.toString());
		else
			keys.add(index, "");
		
		if(value != null)
			values.add(index,value.toString());
		else
			values.add(index,"");
		
		attributekeys.add(index,new String[]{});
		attributevalues.add(index,new String[]{});
		trim();
		
	}
	
	private void trim()
	{
		keys.trimToSize();
		values.trimToSize();
		attributekeys.trimToSize();
		attributevalues.trimToSize();
	}
	
	public void addElement(Object descriptor, Object value)
	{
		addElement(descriptor, value, new String[]{}, new String[]{});
	}
	
	public void addElement(Object descriptor, Object value, Object attributeName, Object attributeValue)
	{		
		addElement(descriptor, value, new String[]{attributeName.toString()},new String[]{attributeValue.toString()});
	}
	
	public void addElement(Object descriptor, Object value, String[] attributeNames, String[] attributeValues)
	{
		if(attributeNames.length != attributeValues.length)
			throw new IllegalArgumentException("attribute names and values must have same cardinality");
		synchronized(keys)
		{
			if(descriptor != null)
				keys.add(descriptor.toString());
			else
				keys.add( "");
			
			if(value != null)
				values.add(value.toString());
			else
				values.add("");
			attributekeys.add(attributeNames);
			attributevalues.add(attributeValues);
			trim();
		}
	}
	

	public static String sanitizeHeaderForXML(String input)
	{
		StringTokenizer st = new StringTokenizer(input," ",false);
		String t="";
		while (st.hasMoreElements()) 
		{
			String s = st.nextToken();
			String str = s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase();
			t+=str;
		}
		return sanitizeForXML(t);

	}
	
	private static String sanitizeForXML(String input)
	{
		char[] arr = input.toCharArray();
		String buf = "";
		for(char c : arr)
		{
			int val = (int)c;
			if((val >= 56 && val <= 90) || (val >= 96 && val <= 122) || val == 32 || (val >= 48 && val <= 57)) 
				buf += c;
		}
		return buf;
	}
	
	
	public String getRowOfCSV()
	{
		synchronized(keys)
		{
			if(header.equals("Interval"))
				values.add(0, attributeValues[0]);
			
			PrintBuffer buf = new PrintBuffer();
			for(String s : values)
			{
				buf.print(s+",");
			}
			if(values.equals("Interval"))
				values.remove(0);
			return buf.toString().substring(0,buf.length()-1);
		}
	}
	
	protected ArrayList<String> getKeys()
	{
		return keys;
	}
	
	protected ArrayList<String> getValues()
	{
		return values;
	}
	
	
	
	public String getHeaderRowOfCSV()
	{
		synchronized(keys)
		{
			if(header.equals("Interval"))
				keys.add(0, "Interval Number");
			PrintBuffer buf = new PrintBuffer();
			for(String s : keys)
			{
				buf.print(s+",");
			}
			if(values.equals("Interval"))
				keys.remove(0);
			return buf.toString().substring(0,buf.length()-1);
		}
	}
	
	
	
	private double isNumeric(String input)
	{
		 try
		 {
			 return Double.parseDouble(input);
		 }
		 catch(NumberFormatException nfe){} //not numeric
		 return Double.MIN_VALUE;
	}
	
	public int getSize()
	{
		return keys.size();
	}
	
	public static PrintBuffer toTextTable(ArrayList<Row> sheet, int bufferSpace)
	{
		PrintBuffer buffer = new PrintBuffer();
		
		if(sheet.size() == 0)
			return buffer;
		
		int columns = sheet.get(0).getSize();
		
		for(Row row : sheet)
		{
			if(row.getSize() != columns)
				throw new IllegalArgumentException("("+row.getHeader()+") Each row in table must have same number of columns");
			//otherwise the headers from the first row wont line up right
		}
		
		buffer.println(sheet.get(0).getHeader()).println();
		
		int[] widths = new int[columns];
		
		for(int col = 0; col < columns; col++)
		{
			int width = 0;
			if(sheet.get(0).getKeys().get(col).length() > width)
				width = sheet.get(0).getKeys().get(col).length();
			for(Row row : sheet)
			{
				if(row.getValues().get(col).length() > width)
					width = row.getValues().get(col).length();
			}
			
			widths[col] = width;
		}
		
		String space = "";
		for(int i = 0; i < bufferSpace; i++)
			space += " ";
		
		ArrayList<String> headers = sheet.get(0).getKeys();
		
		//print the headers
		for(int col = 0; col < headers.size(); col++)
		{
			String header = headers.get(col);
			buffer.print(space);
			buffer.print(StringUtils.center(header, widths[col]));
			buffer.print(space);
		}
		buffer.println();
		
		//print divisor line
		for(int width : widths)
		{
			String line = "";
			for(int i = 0; i < (bufferSpace+width+bufferSpace); i++)
				line += "-";
			buffer.print(line);
		}
		buffer.println();
		
		//print the values
		for(Row row : sheet)
		{
			ArrayList<String> values = row.getValues();
			
			//print the headers
			for(int col = 0; col < values.size(); col++)
			{
				String header = values.get(col);
				buffer.print(space);
				buffer.print(StringUtils.center(header, widths[col]));
				buffer.print(space);
			}
			buffer.println();
		}
		buffer.println().println();
		
		return buffer;
		
	}
	
	
}
