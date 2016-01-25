package pl.kb.app.writers;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import pl.kb.app.domain.Sentence;

@RunWith(Parameterized.class)
public class WritterTest {

	private static Path outpath = Paths.get(System.getProperty("user.dir")
			+ "//out.csv");

	

	@AfterClass
	public static void after(){
		if(outpath.toFile().exists()){
			outpath.toFile().delete();
		}
	}
	
	@Parameters
	public static List<BaseWriter[]> data() {

		List<BaseWriter[]> baseWriters = new ArrayList<BaseWriter[]>();
		baseWriters.add(new BaseWriter[]{new XmlWriter(outpath.toString())});
		baseWriters.add(new BaseWriter[]{new CsvWriter(outpath.toString())});
		return baseWriters;
	}

	@Parameter
	public BaseWriter writer;	

	@Test
	public void testEmptyOpenClose() throws IOException {
		Writer mock = mock(Writer.class);
		writer.open(mock);
		writer.close();
		verify(mock, atLeastOnce()).flush();
		verify(mock, atLeastOnce()).close();
	}
	

	@Test
	public void testResultOpenClose() throws IOException {
		Sentence mock = mock(Sentence.class);
		when(mock.getWord()).thenReturn(
				Arrays.asList("a", "had", "lamb", "little", "Mary"));
		writer.open();;
		writer.write(mock);
		writer.close();
		verify(mock, atLeastOnce()).getWord();		
		Assert.assertTrue(outpath.toFile().exists());		
	}	
}
