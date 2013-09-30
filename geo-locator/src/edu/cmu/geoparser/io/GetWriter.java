package edu.cmu.geoparser.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;

import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class GetWriter {

	public static IndexWriter getIndexWriter(String indexdirectory) throws IOException {
		Directory dir = FSDirectory.open(new File(indexdirectory));
		org.apache.lucene.analysis.Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_36, analyzer));
		return writer;
	}
	public static IndexWriter getSpanishIndexWriter(String indexdirectory) throws IOException {
		Directory dir = FSDirectory.open(new File(indexdirectory));
		org.apache.lucene.analysis.Analyzer analyzer = new SpanishAnalyzer(
				Version.LUCENE_36, new HashSet<String>());
		IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(
				Version.LUCENE_36, analyzer));
		return writer;
	}
	
	public static BufferedWriter getFileWriter(String filename) throws IOException {
		
		FileOutputStream fos = new FileOutputStream(filename,false);//rewrite the file of the same name
		OutputStreamWriter ofw = new OutputStreamWriter(fos,"utf-8");
		BufferedWriter bw = new BufferedWriter(ofw);

		return bw;
	}

}
