package common;

import java.io.*;

import webui.*;
import webui.http.*;

public class Pipe implements Runnable
{
	private final InputStream in;
	private final OutputStream out;
	private HTTPException he;
	private Pipe(InputStream in,OutputStream out)
	{
		this.in=in;
		this.out=out;
	}
	@Override
	public void run()
	{
		he=pipeCore(in,out);
	}
	private static HTTPException pipeCore(InputStream in,OutputStream out)
	{
		try
		{
			byte[] buffer=new byte[2048];
			int size;
			while((size=in.read(buffer))>0)
			{
				out.write(buffer,0,size);
				out.flush();
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			return new HTTPException(500,ioe);
		}
		return null;
	}
	public static void pipe(InputStream in,OutputStream out) throws HTTPException
	{
		HTTPException he=pipeCore(in,out);
		if(he!=null)
			throw he;
	}
	public static void pipe(InputStream in1,OutputStream out2,InputStream in2,OutputStream out1) throws HTTPException
	{
		Pipe p=new Pipe(in2,out1);
		Shared.THREAD_POOL.execute(p);
		pipe(in1,out2);
		if(p.he!=null)
			throw p.he;
	}
}
