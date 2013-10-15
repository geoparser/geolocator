Read Me

GeoLocator
==========

The geolocation algorithm contains both geoparser that extract locations and a geo-coder that assigns latitude and longitude to each location. Both the geoparser results and geo-coder results can be scored separately using score algorithms included in the package, provided the gold standard results are input also. 

The geolocation algorithm contains 4 English parsers (building parser, Toponym heuristic parser, Stanford NER and our CRF tweet-trained parser) and 3 Spanish parsers (building parser, toponym heuristic parser, CRF trained parser) which are included in edu/cmu/geoparser/ folder.   The common interface for those parsers is in the folder.

The algorithm takes a .txt file as input, or else, use the command line tool by entering one sentence per line. Please follow the format of the sample tests at the root folder for input. 
 
In addition to a gelocation algorithm, the package contains a fuzzy match0 algorithm that takes web 2.0 tags plus latitude and longitude as input, and compares them with location entries in the GeoNames gazetteer to determine whether the web 2.0 entries match with the gazetteer entries or they are novel.


///////////////  Introduction  ///////////////
The geoparser.jar contains an index from the gazetteer geonames.org which indexes allCountries.txt.

Tagging the command line input

The output  format for the commandline and batch file:  Each recognized location is wraped as XX{location}XX, where XX could be any of the eight tags: TP,tp, ST,st,BD,bd,AB,ab.   TP, ST, BD, AB are output from the Named Entity Recognizer.  tp,st,bd,ab are the output from the rule based and toponym lookup parsers.


///////////////  How to Install: ///////////////

The algorithm can run on Windows, Mac, or Linux/Unix platforms. 

There are two packages to install: (1) the geoparser.zip, which includes the geolocation algorithm (geoparser and geocoder) in addition to the separate fuzzy match algorithm.  (2) res.zip are the resources necessary to run the gelocation algorithm or the fuzzy match algorithm.    

unzip geoparser.zip
unzip res.zip 

A general install procedure is in install.sh in geoparser folder when you unzip geoparser.zip

Download allCountries.txt and cities1000.txt file from Geonames.org, cities1000.txt is a smaller version, which is a small set of all the locations, which have more than 1000 population.  You may want it for testing the program, because allCOuntries.txt is big.  

It is necessary to build the gazetteer index to run either the geolocation algorithm or the fuzzy match algorithm. Run the geoparser in -index mode, to generate the gaz index. The location of storing the index is GazIndex/ , which is hard coded into the program.

Then, run geoparser.jar in -sysin mode, to make sure every resource has been successfully loaded. Play with it a little bit, if you find some problem, please contact wei.zhang@cs.cmu.edu.

You could also run geoparser.jar in -batch mode, which takes a file as input, then output another tagged file. See commandline, and test.csv for instructions.




/////////////// Runnning the code with source:  ////////////////////

Import geoparser.jar to your project, and it's good to go.

The interface for the English and Spanish geolocation algorithms is .......

To run the geolocation algorithm in English or Spanish
-batch -[mis/nmis] geonames/allcountries.txt test.csv
-sysin -[mis/nmis] geonames/allcountries.txt output.csv
-index [-write/-read] [file to index]

To run the fuzzy match algorithm in edu.cmu.geoparser.nlp.spelling
java -Xmx [numbers]...m -jar.gazindexer.jar -indx [write/-read] [file to index]


Please send email to gelern@cs.cmu.edu or wei.zhang@cs.cmu.edu if you find any bug or have any question, or any suggestions.

Thank you.
