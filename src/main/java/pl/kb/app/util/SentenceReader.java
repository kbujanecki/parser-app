/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.kb.app.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.kb.app.domain.Sentence;

/**
 * Create sentences from chars pass to it. 
 * 
 * @author kb
 */
public class SentenceReader {
	private long sentenceId = 0;
	private final List<String> sentenceWords = new ArrayList<>();
	private final StringBuilder wordBuilder = new StringBuilder();
	private final Comparator<String> comparator = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			return o1.compareToIgnoreCase(o2);
		}

	};

	/**
	 * @param c char from file. 
	 * @return true if sentence ready.
	 */
	public boolean append(char c) {
		boolean sentenceReady = false;
		boolean apostrophe = isApostrophe(c);
		boolean mrOrMrs = isMrOrMrs(c);
		
		if(c=='’'){
			c = '\'';
		}
		
		if (!apostrophe && !mrOrMrs && !Character.isAlphabetic(c)
				&& wordBuilder.length() > 0) {
			sentenceWords.add(wordBuilder.toString());
			wordBuilder.delete(0, wordBuilder.length());
		}

		if (Character.isAlphabetic(c) || apostrophe || mrOrMrs) {
			wordBuilder.append(c);
		} else if (!sentenceWords.isEmpty()
				&& (c == '.' || c == '!' || c == '?')) {
			sentenceReady = true;
		}
		return sentenceReady;
	}

	/**
	 * @param c char from file.
	 * @return true if word with apostrophe in it - e.g. can't  
	 */
	private boolean isApostrophe(char c) {
		return (c == '\'' || c== '’')
				&& wordBuilder.length() > 1
				&& Character.isAlphabetic(wordBuilder.charAt(wordBuilder
						.length() - 1));
	}

	/**
	 * 
	 * @param c char from file.
	 * @return true if pass mr.or mrs. abbrevations. 
	 */
	private boolean isMrOrMrs(char c) {
		boolean result = false;
		if (c == '.') {
			int length = wordBuilder.length();
			if (length == 2 && wordBuilder.toString().equalsIgnoreCase("mr")) {
				result = true;
			} else if (length == 3
					&& wordBuilder.toString().equalsIgnoreCase("mrs")) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * @return sentence with sorted words 
	 */
	public Sentence createSentence() {
		Sentence result = new Sentence();
		result.setId(++sentenceId);
		Collections.sort(sentenceWords, comparator);
		result.setWord(new ArrayList<>(sentenceWords));
		this.sentenceWords.clear();
		return result;
	}

}
