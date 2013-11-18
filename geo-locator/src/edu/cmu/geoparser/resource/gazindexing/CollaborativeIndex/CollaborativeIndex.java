package edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.GazEntryAndFeature;
import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.gazindexing.Index;

public class CollaborativeIndex implements Index {

  private IndexSearcher stringSearcher, infoSearcher;

  private HashSet<String> ids;

  private BooleanQuery q;

  private ArrayList<Document> returnDocs;

  private String stringIndexName, infoIndexName, stringLoad, infoLoad;

  private static CollaborativeIndex ci;

  public static CollaborativeIndex getInstance() {
    if (ci == null)
      ci = new CollaborativeIndex().config("GazIndex/StringIndex", "GazIndex/InfoIndex", "mmap",
              "mmap").open();
    return ci;

  }

  public CollaborativeIndex config(String stringIndexName, String infoIndexName, String stringLoad,
          String infoLoad) {
    this.stringIndexName = stringIndexName;
    this.infoIndexName = infoIndexName;
    this.stringLoad = stringLoad;
    this.infoLoad = infoLoad;
    return this;
  }
/*
  public boolean inIndexStrict(String phrase) {
    if (phrase == null || phrase.length() == 0)
      throw new NullPointerException();
    TermQuery query = new TermQuery(new Term("LOWERED_ORIGIN", phrase.toLowerCase()));
    //If it's an abbreviation, then return true; ignore case.
     
    if (ResourceFactory.getCountryCode2CountryMap().isCountryAbbreviation(phrase))
      return true;
    TopDocs res = null;
    try {
      res = stringSearcher.search(query, 1);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (res == null)
      return false;
    else
      return res.totalHits > 0 ? true : false;
  }
*/
  /**
   * Check if the original string is in index. If not, check the non-space version. However, we have
   * to add some heuristics.
   */
  @Override
  public boolean inIndex(String phrase) {
    if (phrase == null || phrase.length() == 0)
      throw new NullPointerException();
    if (ResourceFactory.getCountryCode2CountryMap().isCountryAbbreviation(phrase))
      return true;
    phrase = phrase.toLowerCase().replace(" ", "");
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
  public ArrayList<Document> getDocumentsByPhrase(String phrase) {
    if (phrase == null || phrase.length() == 0)
      throw new NullPointerException();
    TermQuery query = new TermQuery(
            new Term("LOWERED-NO-WS", phrase.toLowerCase().replace(" ", "")));
    TopDocs res = null;
    try {
      res = stringSearcher.search(query, 200);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(res.totalHits);
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
    if (ResourceFactory.getCountryCode2CountryMap().isCountryAbbreviation(phrase))
      ids.add(ResourceFactory.getCountryCode2CountryMap().getValue(phrase).getId());
    // System.out.println("total number of String ids are:" + ids.size());
    q = new BooleanQuery();

    for (String id : ids) {
      q.add(new TermQuery(new Term("ID", id)), Occur.SHOULD);
    }
    // use a term filter instead of a query filter.
    try {
      TopDocs docs = infoSearcher.search(q, 200);
      // System.out.println("total hits in info is:" + docs.totalHits);
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
/*
  public ArrayList<Document> getDocumentsByPhraseStrict(String phrase) {
    if (phrase == null || phrase.length() == 0)
      throw new NullPointerException();
    TermQuery query = new TermQuery(new Term("LOWERED_ORIGIN", phrase.toLowerCase()));
    TopDocs res = null;
    try {
      res = stringSearcher.search(query, 200);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(res.totalHits);

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
    if (ResourceFactory.getCountryCode2CountryMap().isCountryAbbreviation(phrase))
      ids.add(ResourceFactory.getCountryCode2CountryMap().getValue(phrase).getId());
    // System.out.println("total number of String ids are:" + ids.size());
    q = new BooleanQuery();

    for (String id : ids) {
      q.add(new TermQuery(new Term("ID", id)), Occur.SHOULD);
    }
    // use a term filter instead of a query filter.
    try {
      TopDocs docs = infoSearcher.search(q, 200);
      // System.out.println("total hits in info is:" + docs.totalHits);
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
*/
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

  public String[] getAlternateNames(String id) {
    HashSet<String> names = null;
    Query query = new TermQuery(new Term("ID", id));
    try {
      TopDocs topDocs = infoSearcher.search(query, 300);
      names = new HashSet<String>(topDocs.totalHits);
      for (ScoreDoc doc : topDocs.scoreDocs) {
        names.add(stringSearcher.doc(doc.doc).get("LOWERED_ORIGIN"));
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return names.toArray(new String[names.size()]);
  }

  @Override
  public Document getDocumentsById(String id) {
    if (id == null || id.length() == 0)
      throw new NullPointerException();
    TermQuery query = new TermQuery(new Term("ID", id));
    TopDocs res = null;
    try {
      res = infoSearcher.search(query, 1);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (res == null)
      return null;
    if (res.totalHits == 0)
      return null;
    try {
      return infoSearcher.doc(res.scoreDocs[0].doc);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;

  }

  public static void main(String argv[]) throws IOException {
    CollaborativeIndex ci = ResourceFactory.getClbIndex();

    boolean mode = true; // string
    // mode = false; // id
    /**
     * search string //id
     */

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String s = null;
    while ((s = br.readLine()) != null) {
      if (mode) {
        ArrayList<Document> docs = ci.getDocumentsByPhrase(s);
        if (ResourceFactory.getCountryCode2CountryMap().isCountryAbbreviation(s))
          docs.add(ResourceFactory.getCountryCode2CountryMap().getCountryDoc(s));
        if (docs != null) {
          Document bPopDoc = null;
          long temppop = -1;
          for (Document d : docs) {
            long dpop = Long.parseLong(d.get("POPULATION"));
            System.out.println(dpop);
            System.out.println(d.get("ID") + "\t" + d.get("ORIGINAL-NAME") + "\t altNames:"
                    + d.get("ALTNAME-COUNT") + "\t ct:" + d.get("COUNTRY-CODE") + "\t adm1 :"
                    + d.get("ADM1-CODE") + "\t adm2: " + d.get("ADM2-CODE") + "\t lat: "
                    + d.get("LATITUDE") + "\t lon:" + d.get("LONGTITUDE") + "\t"
                    + d.get("FEATURE-CLASS") + "\t" + d.get("FEATURE") + "\t" + dpop);
            if (dpop > temppop) {
              bPopDoc = d;
              temppop = dpop;
            }
          }
          Document d = bPopDoc;
          System.out.println(" big place is : \n" + d.get("ID") + "\t" + d.get("ORIGINAL-NAME")
                  + "\t" + d.get("COUNTRY-CODE") + "\t" + d.get("ADM1-CODE") + "\t"
                  + d.get("ADM2-CODE") + "\t" + d.get("ADM3-CODE") + "\t" + d.get("ADM4-CODE")
                  + "\t" + d.get("FEATURE-CLASS") + "\t" + d.get("FEATURE") + "\t"
                  + d.get("POPULATION"));
        } else
          System.out.println("null.");

      } else {
        Document doc = ci.getDocumentsById(s);
        System.out.println(doc);
        GazEntryAndFeature gc = new GazEntryAndFeature(doc);
        System.out.println(gc.getId() + gc.getAsciiName() + gc.getCountryCode());
      }
    }

  }
}
