package pl.kb.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.After;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import pl.kb.app.domain.Sentence;
import pl.kb.app.writers.CsvWriter;
import pl.kb.app.writers.ObjectWriter;
import pl.kb.app.writers.XmlWriter;

/**
 * @author kb
 *
 */
public class ParserTest {

	private String userDir = System.getProperty("user.dir");
	private Path small = Paths.get(userDir + "//small.in");
	private Path large = Paths.get(userDir + "//large.in");
	private Path expectedCsv = Paths.get(userDir + "//small.csv");
	private Path expectedXml = Paths.get(userDir + "//small.xml");
	private Path outCsv = Paths.get(userDir + "//out.csv");
	private Path outXml = Paths.get(userDir + "//out.xml");

	@After
	public void after() {
		for (File file : new File[] { outCsv.toFile(), outXml.toFile() }) {
			if (file.exists()) {
				file.delete();
			}
		}
	}

	@Test
	public void wellformedXml() throws IOException, SAXException,
			ParserConfigurationException, InterruptedException,
			ExecutionException {
		Parser parser = this.initParser();
		parser.process(small.toString());
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		SAXParser saxParser = factory.newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		SimpleErrorHandler simpleErrorHandler = new SimpleErrorHandler();
		reader.setErrorHandler(simpleErrorHandler);
		reader.parse(new InputSource(outXml.toString()));
		reader.parse(new InputSource(expectedXml.toString()));
	}

	@Test
	public void smallEquals() throws InterruptedException, ExecutionException,
			IOException, JAXBException, SAXException {
		Parser parser = initParser();
		parser.process(small.toString());
		String expectedC = new String(Files.readAllBytes(expectedCsv))
				.replaceAll("\\s", "").toLowerCase();
		String outC = new String(Files.readAllBytes(outCsv)).replaceAll("\\s",
				"").toLowerCase();
		/**
		 * replace &apos; from expected file with '. There is no need to change
		 * it, we can use apostrophe inside xml element.
		 * */
		String expectedX = new String(Files.readAllBytes(expectedXml))
				.replaceAll("&apos;", "'").replaceAll("\\s", "").toLowerCase();
		String outX = new String(Files.readAllBytes(outXml)).replaceAll("\\s",
				"").toLowerCase();
		assertEquals(expectedC, outC);
		assertEquals(expectedX, outX);
	}

	@Test
	public void justExist() throws InterruptedException, ExecutionException,
			IOException, JAXBException {
		Parser parser = initParser();
		parser.process(large.toString());
		assertTrue(outCsv.toFile().exists());
		assertTrue(outXml.toFile().exists());
	}

	private Parser initParser() {
		List<ObjectWriter<Sentence>> sentenceWritters = new ArrayList<>();
		sentenceWritters.add(new CsvWriter(outCsv.toString()));
		sentenceWritters.add(new XmlWriter(outXml.toString()));
		Parser parser = new Parser(sentenceWritters);
		return parser;
	}

	class SimpleErrorHandler implements ErrorHandler {
		private boolean bad = false;

		public boolean isBad() {
			return bad;
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {

		}

		@Override
		public void error(SAXParseException exception) throws SAXException {

			bad = true;
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			bad = true;
		}

	}
}
