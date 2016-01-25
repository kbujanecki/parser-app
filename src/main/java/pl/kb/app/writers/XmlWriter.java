package pl.kb.app.writers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.beanio.builder.FieldBuilder;
import org.beanio.builder.RecordBuilder;
import org.beanio.builder.StreamBuilder;
import org.beanio.builder.XmlParserBuilder;

import pl.kb.app.domain.Sentence;

/**
 * Xml writer based on http://beanio.org/.
 * 
 * @author kb
 *
 */
public class XmlWriter extends BaseWriter {
	
	final static Logger logger = Logger.getLogger(XmlWriter.class);
	
	private final static StreamBuilder builder = new StreamBuilder("text")
			.format("xml")
			.parser(new XmlParserBuilder().suppressHeader())
			.addRecord(
					new RecordBuilder("sentence")
							.type(Sentence.class)
							.minOccurs(0)
							.addField(new FieldBuilder("word").collection(Collection.class).minOccurs(0).maxOccurs(-1)));
	private String outFile;
	
	public XmlWriter(String outFile) {
		
		super(builder);	
		this.outFile = outFile;
		

	}

	/* (non-Javadoc)
	 * @see pl.kb.app.writers.ObjectWriter#open()
	 */
	@Override
	public void open() {

		try {
			BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(outFile), Charset.forName("UTF-8"),
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			super.open(bufferedWriter);
			bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}

}
