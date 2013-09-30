package edu.cmu.geoparser.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

public class GetReader {

	public static BufferedReader getUTF8FileReader(String filename)
			throws FileNotFoundException, UnsupportedEncodingException {
		File file = new File(filename);
		BufferedReader bin = new BufferedReader(new InputStreamReader(
				new FileInputStream(file),"utf-8"));
		return bin;
	}
	
	public static IndexSearcher getIndexSearcher(String filename) throws IOException {
		IndexWriter iw = GetWriter.getIndexWriter(filename);
		IndexReader ir=IndexReader.open(iw, false);
		IndexSearcher searcher = new IndexSearcher(ir);
		return searcher;
	}
	
	public static BufferedReader getCommandLineReader() throws UnsupportedEncodingException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
		return br;
	}
	
}
