package edu.cmu.geoparser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import edu.cmu.geoparser.model.Token;
public class test implements Comparable<test>{

  long id;
  
  public test setId(int id){
    this.id = id;
    return this;
  }
  
  public String toString(){
    return id+" ";
  }
  test(){
    a= new Token[3];
    a[1] = new Token("a1","1",1);
    a[2] = new Token("a2","1",2);
    a[0] = new Token("a0","1",3);
  }

  public Token[] getTokens(){
    return a;
  }

  Token[] a;
  //increasing order.
  public int compareTo(test t){
    if(t.id==this.id)
      return 0;
    if (t.id< this.id)
       return 1;
    return -1;
     
  }
  
  public static void main(String argv[]){
    
    String test = "_a___";
    System.out.println(test.split("_").length);
    
    //
    String a = "#sdjkfl123u4";
    System.out.println(a.matches(".*[A-Za-z].*"));
    test t = new test();
    Token[] toks = t.getTokens();
    toks[2].setToken("ca2");
    System.out.println(t.getTokens()[2]);
    
    Scanner s = new Scanner("This is a test\tString.\nCome and see it.");
    s.useDelimiter("\t");
    while( s.hasNext()){
      System.out.println(s.next());
    }
    
    test t1 = new test().setId(1);
    test t2 = new test().setId(2);

    ArrayList<test> ct = new ArrayList<test>();
    ct.add(t1);ct.add(t2);
    Collections.sort(ct,new Comparator(){
      @Override
      public int compare(Object arg0, Object arg1) {
        test t1 = (test) arg0;
        test t2 = (test) arg1;
        return (int)t1.id-(int) t2.id;
      }
    });

//    Collections.sort(ct);
    System.out.println(ct);
  }
}
