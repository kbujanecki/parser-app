package pl.kb.app.writers;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

import org.beanio.builder.CsvParserBuilder;
import org.beanio.builder.FieldBuilder;
import org.beanio.builder.RecordBuilder;
import org.beanio.builder.StreamBuilder;

import pl.kb.app.beanio.IdTypeHandler;
import pl.kb.app.domain.Sentence;

/**
 * Xml writer based on http://beanio.org/.
 * 
 * @author kb
 *
 */
public class CsvWriter extends BaseWriter {

	private final static StreamBuilder builder = new StreamBuilder("text")
			.format("delimited")
			.parser(new CsvParserBuilder().delimiter(','))
			.addRecord(
					new RecordBuilder("sentence")
							.type(Sentence.class)
							.minOccurs(0)
							.addField(
									new FieldBuilder("id")
											.typeHandler(new IdTypeHandler()))
							.addField(
									new FieldBuilder("word")
											.collection(Collection.class)
											.minOccurs(0).maxOccurs(-1)));

	private Path filePath;

	private int maxHeders = 0;

	private String outFile;

	public CsvWriter(String outFile) {

		super(builder);
		this.outFile = outFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.kb.app.writers.ObjectWriter#open()
	 */
	@Override
	public void open() {

		try {
			filePath = Paths.get(outFile);
			super.open(Files.newBufferedWriter(filePath,
					Charset.forName("UTF-8"), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.kb.app.writers.BaseWriter#write(pl.kb.app.domain.Sentence)
	 */
	@Override
	public void write(Sentence t) {
		int size = t.getWord().size();
		maxHeders = size > maxHeders ? size : maxHeders;
		super.write(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.kb.app.writers.BaseWriter#close()
	 */
	@Override
	public void close() {
		super.close();
		if (maxHeders > 0) {
			this.writeHeader(createHeader(maxHeders));
		}
	}

	public String createHeader(int maxHeders) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= maxHeders; i++) {
			sb.append(", Word ");
			sb.append(i);
		}
		return sb.toString();
	}

	/**
	 * Writing header with word nr. to csv file.
	 * 
	 * @param header
	 */
	public void writeHeader(String header) {
		Path tmpPath = Paths.get(filePath.getParent() + "//tmp");
		try (Reader in = Files.newBufferedReader(filePath,
				Charset.forName("UTF-8"));
				Writer out = Files.newBufferedWriter(tmpPath,
						Charset.forName("UTF-8"), StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING)) {
			this.writeHeader(header, in, out);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				Files.move(tmpPath, filePath,
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * @param header
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public void writeHeader(String header, Reader in, Writer out)
			throws IOException {

		out.write(header);
		out.write(System.lineSeparator());
		int readChars = 0;
		char[] buff = new char[1024];
		while ((readChars = in.read(buff)) != -1) {
			out.write(buff, 0, readChars);
		}

	}
}
