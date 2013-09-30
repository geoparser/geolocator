package edu.cmu.geoparser.ui.CommandLine;

import edu.cmu.geoparser.resource.gazindexing.GazIndexer;

public class MainProc {

  public static void main(String argv[]) throws Exception {

    if (argv.length < 1) {
      help();
    }

    String mode = argv[0];
    if (mode.equals("-sysin"))
      CmdInputParser.main(new String[] { argv[1], argv[2] });
    else if (mode.equals("-index")) {
      if (argv[1].equals("-write"))
        GazIndexer.main(new String[] { "-write", argv[2] });
      else if (argv[1].equals("-read"))
        GazIndexer.main(new String[] { "-read" });
      else {
        throw new Exception("wrong index usage command. Should be -read or -write.");
      }
    } else if (mode.equals("-batch"))
      formatter.main(new String[] { argv[1], argv[2], argv[3], argv[4] });
    else {
      help();

    }
  }

  static void help() {

    System.err
            .println("\n BATCH TAGGING EXAMPLE:\n java -jar geoparser.jar -batch -[mis/nmis] Geonames/allCountries.txt test.csv output.csv");

    System.err
            .println("\n COMMAND LINE INPUT EXAMPLE:\n java -jar geoparser.jar -sysin -[mis/nmis] Geonames/allCountries.txt ");

    System.err.println("\n GAZ INDEXING AND QUERYING: \n"
            + "java -Xmx...m -jar gazindexer.jar -index [-write/-read] [file to index] \n"
            + "WRITING EXAMPLE: java -jar geoparser.jar -index -write GeoNames/allCountires.txt ");

    System.err.println("READING EXAMPLE: java -jar geoparser.jar -index -read");

    System.err
            .println("\nIf you USE the allCountries.txt AS GAZETTEER, MAKE SURE THE MEMORY ALLOCATION IS ENOUGH.");

  }
}
