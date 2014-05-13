/**
 * 
 */
package net.charliemeyer.htmlparser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


/**
 * Loosly wraps around a StringBuffer, but provides greater functionality,
 * such as auto terminating calls to println with a newline character, the
 * ability to print mulitple objects on one call to println or printthrough the 
 * use of varargs, and the various built in write capabilities
 * 
 * @author Charlie Meyer <cemeyer@us.ibm.com>
 * @version $Revision: 1.1 $ $Date: 2008/10/16 17:35:50 $
 */
public class PrintBuffer
{
	private StringBuffer buffer;
	String newline;
	
	public PrintBuffer()
	{
		this(-1, null, false);
	}
	
	public PrintBuffer(Object initialval,boolean terminateInitialValWithNewline)
	{
		this(-1, initialval, terminateInitialValWithNewline);
	}
	
	
	public PrintBuffer(int capacity)
	{
		this(capacity, null, false);
	}
	
	public PrintBuffer(int capacity, Object initialval, boolean terminateInitialValWithNewline)
	{
		newline = System.getProperties().getProperty("line.separator");
		try
		{
			if(capacity != -1)
				buffer = new StringBuffer(capacity);
			else buffer = new StringBuffer();
		}
		catch(NegativeArraySizeException nase)
		{
			throw new IllegalArgumentException("initial PrintBuffer capacity muse be positive");
		}
		if(initialval != null)
		{
			if(terminateInitialValWithNewline)
				println(initialval);
			else
				print(initialval);
		}
	}

	
	public PrintBuffer println(Object ... obs)
	{

		if(obs.length == 0)
			buffer.append(newline);
		else
			for(Object o : obs)
			{
				if( o == null)
					throw new NullPointerException("Cannot print null objects into a PrintBuffer");
				buffer.append(o).append(newline);
			}
		return this;
	}
	
	public PrintBuffer println()
	{
		buffer.append(newline);
		return this;
	}
	
	public PrintBuffer print(Object ... obs)
	{
		for(Object o : obs)
		{
			if( o == null)
				throw new NullPointerException("Cannot print null objects into a PrintBuffer");
			buffer.append(o);
		}
		return this;
	}
	
	public String toString()
	{
		return buffer.toString();
	}
	
	public StringBuffer toStringBuffer()
	{
		return buffer;
	}
	
	public PrintBuffer insert(int offset, Object o)
	{
		if(o == null)
			throw new NullPointerException("cannot insert null objects into a PrintBuffer");
		if(offset < 0)
			throw new IllegalArgumentException("PrintBufferOffsets must be greater than 0");
		if(offset > buffer.length())
			buffer.setLength(offset+o.toString().length());
		buffer.insert(offset, o);
		return this;
	}
	
	public int length()
	{
		return buffer.length();
	}
	
	public PrintBuffer replace(int start, int end, Object o)
	{
		if( o == null)
			throw new NullPointerException("Cannot replace null objects into a PrintBuffer");
		buffer.replace(start, end, o.toString());
		return this;
	}
	
	public String substring(int start)
	{
		if(start < 0 || start > length())
			throw new IllegalArgumentException("start parameter must be 0 <= start <= length()");
		return buffer.substring(start);
	}
	
	public PrintBuffer substringBuffer(int start)
	{
		if(start < 0 || start > length())
			throw new IllegalArgumentException("start parameter must be 0 <= start <= length()");
		PrintBuffer buf = new PrintBuffer(buffer.substring(start),false);
		return buf;
	}
	
	public String substring(int start, int end)
	{
		if(end < start)
			throw new IllegalArgumentException("end < start for PrintBuffer substring()");
		if(start < 0 || start > length())
			throw new IllegalArgumentException("start parameter must be 0 <= start <= end <= length()");
		if(end < 0 || end > length())
			throw new IllegalArgumentException("end parameter must be 0 <= start <= end <= length()");
		return buffer.substring(start, end);
	}
	
	public PrintBuffer substringBuffer(int start, int end)
	{
		if(end < start)
			throw new IllegalArgumentException("end < start for PrintBuffer substring()");
		if(start < 0 || start > length())
			throw new IllegalArgumentException("start parameter must be 0 <= start <= end <= length()");
		if(end < 0 || end > length())
			throw new IllegalArgumentException("end parameter must be 0 <= start <= end <= length()");
		PrintBuffer buf = new PrintBuffer(buffer.substring(start, end),false);
		return buf;
	}
	
	public void trimToSize()
	{
		buffer.trimToSize();
	}
	
	public int capacity()
	{
		return buffer.capacity();
	}
	
	public void setLength(int length)
	{
		if(length < 0)
			throw new IllegalArgumentException("length parameter must be positive");
		buffer.setLength(length);
	}
	
	public void write(String pathName) throws IOException
	{
		write(new File(pathName));
	}
	
	public void write(File f) throws IOException
	{
		PrintWriter out  = new PrintWriter(new FileWriter(f));		
		out.append(buffer.toString());
		out.flush();
		out.close();
	}
	
	public void write(OutputStream out) throws IOException
	{
		OutputStreamWriter output = new OutputStreamWriter(out);
		output.write(buffer.toString());
		output.flush();
		output.close();
	}
}
