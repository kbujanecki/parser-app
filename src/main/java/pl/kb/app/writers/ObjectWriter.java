/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.kb.app.writers;


/**
 * 
 * @author kb
 */
public interface ObjectWriter<T> {

    /**
     * 
     * write object to file;
     * 
     * @param t
     */
    public void write(T t);

    /**
     * close file for output
     */
    public void close();

	/**
	 * open file for output 
	 */
	public void open();

}
