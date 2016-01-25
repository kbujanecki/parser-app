/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package pl.kb.app;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import pl.kb.app.domain.Sentence;
import pl.kb.app.util.SentenceReader;
import pl.kb.app.writers.BaseWriter;
import pl.kb.app.writers.CsvWriter;
import pl.kb.app.writers.ObjectWriter;
import pl.kb.app.writers.XmlWriter;

/**
 * Main application class using {@link SentenceReader} to read sentences char by char from file.
 * After sentence is read instance of {@link Sentence} is created and pass asynchronously to instances of {@link ObjectWriter}
 * for example {@link CsvWriter} and {@link XmlWriter}. 
 * 
 * 
 * @author kb
 */
public class Parser {
	
	final static Logger logger = Logger.getLogger(Parser.class);
	
	private final SentenceReader sentenceReader = new SentenceReader();

	private final ExecutorService executor;

	private final List<ObjectWriter<Sentence>> sentenceWritters;

	public Parser(List<ObjectWriter<Sentence>> sentenceWritters) {
		this.sentenceWritters = sentenceWritters; 
		executor = Executors.newFixedThreadPool(sentenceWritters.size());
	}

	public Parser(ExecutorService executor, List<ObjectWriter<Sentence>> sentenceWritters) {
		super();
		this.executor = executor;
		this.sentenceWritters = sentenceWritters;
	}

	/**
	 * Main process method
	 * 
	 * @param  inFile - file to read 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void process(String inFile) throws InterruptedException, ExecutionException, IOException {

		Path path = Paths.get(inFile);
		try (Reader reader = Files.newBufferedReader(Paths.get(inFile), Charset.forName("UTF-8"))) {
			process(path.getParent().toString(), reader);
		}

	}

	/**
	 * Main process method
	 * 
	 * @param  inFile - file to read 
	 * @param reader
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void process(String inFile, Reader reader) throws InterruptedException, ExecutionException, IOException {

		try {
			int readChars = 0;
			char[] buff = new char[1024];
			openAll();

			while((readChars = reader.read(buff)) != -1) {

				for(int i = 0; i < readChars; i++) {

					if(sentenceReader.append(buff[i])) {						
						Sentence createSentence = sentenceReader.createSentence();
						writeToAll(createSentence);
					}
				}
			}
			if(sentenceReader.append('.')) {
				Sentence createSentence = sentenceReader.createSentence();
				writeToAll(createSentence);
			}
		} finally {
			closeAll();
			this.executor.shutdown();

		}
	}

	/**
	 * Call open method of {@link ObjectWriter}
	 * 
	 * @param inFile
	 * @throws IOException
	 */
	private void openAll() throws IOException {
		for(int i = 0; i < sentenceWritters.size(); i++) {
			sentenceWritters.get(i).open();
		}
	}

	/**
	 * Write sentence asynchronously
	 * 
	 * @param sentence
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void writeToAll(final Sentence sentence) throws InterruptedException, ExecutionException {
		List<Future<?>> tasks = new ArrayList<Future<?>>();
		for(final ObjectWriter<Sentence> sentenceWritter: sentenceWritters) {
			tasks.add(executor.submit(new Runnable() {

				@Override
				public void run() {

					sentenceWritter.write(sentence);
				}
			}));
		}
		for(Future<?> future: tasks) {
			future.get();
		}

	}

	
	/**
	 * Close all writters  
	 */
	private void closeAll() {
		for(ObjectWriter<Sentence> sentenceWritter: sentenceWritters) {
			sentenceWritter.close();
		}
	}

	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {		
		if (args.length == 0) {
			logger.info("No file!");
			return;
		}
		logger.info("Init parser");
		String fileName = args[0];
		List<ObjectWriter<Sentence>> sentenceWritters = new ArrayList<>();
		sentenceWritters.add(new CsvWriter(BaseWriter.getOutFileName(fileName, "csv")));
		sentenceWritters.add(new XmlWriter(BaseWriter.getOutFileName(fileName, "xml")));		
		Parser nordea = new Parser(sentenceWritters);
		logger.info("Start");
		nordea.process(args[0]);
		logger.info("Finito!");
	}

}
