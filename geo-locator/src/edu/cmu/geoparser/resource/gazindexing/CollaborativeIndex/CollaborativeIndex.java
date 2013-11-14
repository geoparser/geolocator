package edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.resource.Index;

public class CollaborativeIndex implements Index {

  private IndexSearcher stringSearcher, infoSearcher;

  private HashSet<String> ids;

  private BooleanQuery q;

  private ArrayList<Document> returnDocs;

  private String stringIndexName, infoIndexName, stringLoad, infoLoad;

  public CollaborativeIndex config(String stringIndexName, String infoIndexName, String stringLoad,
          String infoLoad) {
    this.stringIndexName = stringIndexName;
    this.infoIndexName = infoIndexName;
    this.stringLoad = stringLoad;
    this.infoLoad = infoLoad;
    return this;
  }

  public boolean inIndexStrict(String phrase) {

    TermQuery query = new TermQuery(new Term("LOWERED_ORIGIN", phrase.toLowerCase()));
    TopDocs res = null;
    try {
      res = stringSearcher.search(query, 1);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (res == null)
      return false;
    return res.totalHits > 0 ? true : false;
  }

  /**
   * Check if the original string is in index. If not, check the non-space version. However, we have
   * to add some heuristics.
   */
  @Override
  public boolean inIndex(String phrase) {
    if (inIndexStrict(phrase) == true)
      return true;
    TermQuery query = new TermQuery(new Term("LOWERED-NO-WS", phrase));
    TopDocs res = null;
    try {
      res = stringSearcher.search(query, 1);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (res == null)
      return false;
    return res.totalHits > 0 ? true : false;
  }

  @Override
  public ArrayList<Document> getDocuments(String phrase) {
    TermQuery query = new TermQuery(new Term("LOWERED-NO-WS", phrase));
    TopDocs res = null;
    try {
      res = stringSearcher.search(query, 200);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (res == null)
      return null;
    if (res.totalHits == 0)
      return null;
    ids = new HashSet<String>(res.totalHits);
    try {
      for (ScoreDoc doc : res.scoreDocs) {
        ids.add(stringSearcher.doc(doc.doc).get("ID"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("total number of String ids are:" + ids.size());
    q = new BooleanQuery();

    for (String id : ids) {
      q.add(new TermQuery(new Term("ID", id)), Occur.SHOULD);
    }
    // use a term filter instead of a query filter.
    try {
      TopDocs docs = infoSearcher.search(q, 200);
      System.out.println("total hits in info is:" + docs.totalHits);
      returnDocs = new ArrayList<Document>(docs.totalHits);
      for (ScoreDoc d : docs.scoreDocs) {
        returnDocs.add(infoSearcher.doc(d.doc));
      }
      return returnDocs;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  public ArrayList<Document> getDocumentsStrict(String phrase) {
    TermQuery query = new TermQuery(new Term("LOWERED_ORIGIN", phrase));
    TopDocs res = null;
    try {
      res = stringSearcher.search(query, 200);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (res == null)
      return null;
    if (res.totalHits == 0)
      return null;
    ids = new HashSet<String>(res.totalHits);
    try {
      for (ScoreDoc doc : res.scoreDocs) {
        ids.add(stringSearcher.doc(doc.doc).get("ID"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("total number of String ids are:" + ids.size());
    q = new BooleanQuery();

    for (String id : ids) {
      q.add(new TermQuery(new Term("ID", id)), Occur.SHOULD);
    }
    // use a term filter instead of a query filter.
    try {
      TopDocs docs = infoSearcher.search(q, 200);
      System.out.println("total hits in info is:" + docs.totalHits);
      returnDocs = new ArrayList<Document>(docs.totalHits);
      for (ScoreDoc d : docs.scoreDocs) {
        returnDocs.add(infoSearcher.doc(d.doc));
      }
      return returnDocs;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  
  public CollaborativeIndex open() {
    try {
      stringSearcher = GetReader.getIndexSearcher(stringIndexName, stringLoad);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      infoSearcher = GetReader.getIndexSearcher(infoIndexName, infoLoad);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return this;
  }

  @Override
  public void close() {
    // TODO Auto-generated method stub

  }

  public static void main(String argv[]) {
    CollaborativeIndex ci = new CollaborativeIndex().config("GazIndex/StringIndex",
            "GazIndex/InfoIndex", "mmap", "mmap").open();
    // System.out.println(ci.inIndex("shanghai"));
    // String[] locs = new String[] { "tianjin", "hefei", "mudanjiang", "qingfeng", "yuenan",
    // "china" };
    String[] locs = new String[] { "beijing shi", "qingyang", "ganzhou", "zhenjiang", "ganma",
        "xiaobao" };
    long st, e;
    for (String s : locs) {
      System.out.println(s);
      st = System.currentTimeMillis();
      ArrayList<Document> docs = ci.getDocuments(s);
      if (docs == null)
        continue;
      e = System.currentTimeMillis();
      for (Document d : docs)
        System.out.println(d);
      System.out.println("times spent are: " + (e - st));
    }

  }
}
