/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package pl.kb.app.writers;

import java.io.Writer;

import org.apache.log4j.Logger;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.beanio.builder.StreamBuilder;

import pl.kb.app.domain.Sentence;

/**
 * Base class for writers based on http://beanio.org/.
 * 
 * @author kb
 */
public abstract class BaseWriter implements ObjectWriter<Sentence> {

	final static Logger logger = Logger.getLogger(BaseWriter.class);
	
	protected final StreamFactory factory;

	protected BeanWriter out;

	public BaseWriter(StreamBuilder builder) {

		this.factory = StreamFactory.newInstance();
		this.factory.define(builder);
	}

	/* 
	 * @see pl.kb.app.writers.ObjectWriter#write(java.lang.Object)
	 */
	@Override
	public void write(Sentence t) {

		out.write(t);
	}

	/**
	 * @param writer
	 */
	public void open(Writer writer) {
		
		out = factory.createWriter("text", writer);
		logger.info("Out file opened by: " + getClass().getSimpleName());
	}

	/* (non-Javadoc)
	 * @see pl.kb.app.writers.ObjectWriter#close()
	 */
	@Override
	public void close() {

		if (out != null) {
			out.flush();
			out.close();
			logger.info("Out file closed by: " + getClass().getSimpleName());
		}else{
			logger.error("Nothing to close by: " + getClass().getSimpleName());	
		}
		
		
	}

	/**
	 * @param in
	 * @param sufix
	 * @return
	 */
	public static String getOutFileName(String in, String sufix) {
		return in.split(",")[0] + "." + sufix;
	}
}
