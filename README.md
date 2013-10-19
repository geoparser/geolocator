Read Me

GeoLocator
==========

The geolocation algorithm contains both geoparser that extract locations and a geo-coder that assigns latitude and longitude to each location.

The geolocation algorithm contains 4 English parsers (building parser, Toponym heuristic parser, Stanford NER and our CRF tweet-trained parser) and 3 Spanish parsers (building parser, toponym heuristic parser, CRF trained parser) which are included in edu/cmu/geoparser/ folder. The common interface for those parsers is in the folder too.

The algorithm takes a .txt file as input, or else, use the command line tool by entering one sentence per line. The best way to use it is to look at the CmdInputParser.java included in edu.cmu.geoparser.ui.CommandLine.
 
In addition to a gelocation algorithm, the package contains a fuzzy match algorithm that takes web 2.0 tags plus latitude and longitude as input, and compares them with location entries in the GeoNames gazetteer to determine whether the web 2.0 entries match with the gazetteer entries or they are novel.

///////////////  Introduction  ///////////////

Tagging the command line input

The output format for the commandline and batch file:  Each recognized location is wraped as XX{location}XX, where XX could be any of the eight tags: TP,tp, ST,st,BD,bd,AB,ab.   TP, ST, BD, AB are output from the Named Entity Recognizer.  tp,st,bd,ab are the output from the rule based and toponym lookup parsers.


///////////////  How to Install: ///////////////

The algorithm can run on Windows, Mac, or Linux/Unix platforms. 

1. Check out the project.
2. Download allCountries.txt and cities1000.txt file from Geonames.org, cities1000.txt is a smaller version which only contains cities with more than 1000 population.  You may want it for testing the program, because allCountries.txt is big. Then, put both files in the GeoNames folder in the project. We didn't include those files just because they are big.

3. Build the gazetteer index to run either the geolocation algorithm or the fuzzy match algorithm. Run the geoparser in indexing mode to generate the gaz index. The location of storing the index is GazIndex/ , which is hard coded into the program. The program to generate the index is the MainProc.java, which included in the edu.cmu.geoparser.ui.CommandLine. The command is:

MainProc.java -index -write GazIndex

4. Use the CmdInputParser.java to test the program.
If you want to use the program, just follow the instructions in CmdInputParser.java. 


To run the fuzzy match algorithm in edu.cmu.geoparser.nlp.spelling
java -Xmx [numbers]...m -jar.gazindexer.jar -indx [write/-read] [file to index]


Please send email to gelern@cs.cmu.edu or wei.zhang@cs.cmu.edu if you find any bug or have any question, or any suggestions.

Thank you.
