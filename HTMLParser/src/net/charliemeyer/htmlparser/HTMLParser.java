package net.charliemeyer.htmlparser;

import java.util.ArrayList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

public class HTMLParser 
{
	NodeList nodes;
	
	public HTMLParser(String url) throws ParserException
	{
		Parser parser = new Parser(url);
		nodes = parser.parse(new TableFilter());
		SimpleNodeIterator iterator = nodes.elements();
		while(iterator.hasMoreNodes())
		{
			processTable((TableTag)iterator.nextNode());
		}
		
	}
	
	public class TableFilter implements NodeFilter
	{

		@Override
		public boolean accept(Node node) 
		{
			if(node instanceof TableTag)
				return true;
			return false;
		}
		
	}
	
	private void processTable(TableTag table)
	{
		TableRow[] tableRows = table.getRows();
		ArrayList<Row> rows  = new ArrayList<Row>();
		for(TableRow row : tableRows)
		{
			rows.add(processRow(row));
		}
		System.out.println(Row.toTextTable(rows, 3));
	}
	
	private Row processRow(TableRow tableRow)
	{
		Row row = new Row("table");
		TableColumn[] columns = tableRow.getColumns();
		for(TableColumn column : columns)
		{
			row.addElement("", column.getStringText());
		}
		return row;
	}
}
