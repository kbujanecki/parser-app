package pl.kb.app.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pl.kb.app.domain.Sentence;

public class SentenceReaderTest {
	private SentenceReader sr;

	@Before
	public void before() {
		sr = new SentenceReader();
	}

	@Test
	public void emptySentence() {
		String sentence = "!@#$%^&*(). ";
		char[] charArray = sentence.toCharArray();
		for (char c : charArray) {
			assertFalse(sr.append(c));
		}

	}
	
	@Test
	public void cant() {
		String sentence = "Can't.";
		char[] charArray = sentence.toCharArray();
		List<Sentence> list = new ArrayList<Sentence>();
		for (char c : charArray) {
			if (sr.append(c)) {
				list.add(sr.createSentence());
			}
		}
		assertEquals(1, list.size());
		assertEquals(Arrays.asList("Can't"), list.get(0).getWord());
	}
	@Test
	public void createSentence() {
		String sentence = "Mary had a little lamb. Peter called for the wolf, and Aesop came. Cinderella likes shoes.";
		char[] charArray = sentence.toCharArray();
		List<Sentence> list = new ArrayList<Sentence>();
		for (char c : charArray) {
			if (sr.append(c)) {
				list.add(sr.createSentence());
			}
		}
		assertEquals(3, list.size());
	}

	@Test
	public void sortOrder() {
		String sentenceString = "Mary had a little lamb.";
		char[] charArray = sentenceString.toCharArray();
		List<Sentence> list = new ArrayList<Sentence>();
		for (char c : charArray) {
			if (sr.append(c)) {
				list.add(sr.createSentence());
			}
		}
		assertEquals(1, list.size());
		Collection<String> words = list.get(0).getWord();
		assertEquals(5, words.size());
		assertEquals(Arrays.asList("a", "had", "lamb", "little", "Mary"), words);
	}
}
