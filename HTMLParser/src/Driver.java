import org.htmlparser.util.ParserException;

import net.charliemeyer.htmlparser.HTMLParser;


public class Driver 
{
	public static void main(String[] args) throws ParserException
	{
		HTMLParser parser = new HTMLParser("http://finance.yahoo.com/q/bs?s=IBM&annual");
	}
}
