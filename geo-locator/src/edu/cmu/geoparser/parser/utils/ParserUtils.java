package edu.cmu.geoparser.parser.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.resource.dictionary.Dictionary;
import edu.cmu.geoparser.resource.dictionary.Dictionary.DicType;

public class ParserUtils {

	public static void main(String argv[]) {
		System.out.println(ParserUtils.isEsFilterword("de"));
		List<String> matches = Arrays.asList(new String[] { "g{chile}g", "g{chile stock}g", "g{stock exchange}g" });
		System.out.println(ResultReduce(matches));
		System.out.println(ParserUtils.esdictset.size());
		System.out.println(ParserUtils.isESBuildingPrefix("Oficinas"));
	}
	static HashSet<String> StreetSuffix;
	static HashSet<String> BuildingSuffix;
	static HashSet<String> CountryNames;;

	static HashSet<String> ESStreetPrefix;
	static HashSet<String> ESBuildingPrefix;

	static {
		StreetSuffix = new HashSet<String>(Arrays.asList(new String[] { "street", "st", "st.", "lane", "ln", "ln.",
				"dr", "dri", "dr.", "boulevard", "blvd", "blvd.", "circle", "cir", "cir.", "cl", "cl.", "pl", "pl.",
				"avenue", "ave", "ave.", "square", "sq", "sq.", "road", "rd" }));
		BuildingSuffix = new HashSet<String>(Arrays.asList(new String[] { "downs", "barn", "rescue", "coop",
				"greenhouse", "roadhouse", "silo", "stable", "cellar", "mill", "loft", "house", "shed", "grainery",
				"watermill", "mill", "bar", "pub", "brothel", "casino", "centre", "shoppe", "center", "forum",
				"station", "store", "hotel", "motel", "market", "nightclub", "club", "office", "building",
				"restaurant", "cafe", "skyscraper", "shop", "mall", "warehouse", "supermarket", "college", "gym",
				"gymnasium", "school", "library", "museum", "theater", "amphitheater", "cinema", "symphony",
				"university", "capitol", "consulate", "courthouse", "embassy", "station", "palace", "park",
				"bridge", "tower", "hospital", "parliament", "station", "prison", "jail", "diner", "brewery",
				"factory", "foundry", "mine", "plant", "refinery", "rig", "barracks", "bunker", "castle", "citadel",
				"gate", "fort", "church", "bascilica", "cathedral", "duomo", "chapel", "oratory", "martyrium",
				"mosque", "mihrab", "masjid", "surau", "imambargah", "monastery", "temple", "pyramid", "shrine",
				"synagogue", "pagoda", "gurdwara", "airport", "port", "terminal", "marina", "stall", "town", "bldg",
				"centre", "abbey", "apartment", "aquarium", "armory", "asylum", "auditorium", "bakery", "barn",
				"bistro", "boathouse", "cabin", "camp", "cemetery", "city hall", "clinic", "concert hall",
				"convent", "convention center", "cottage", "depot", "grove", "headquarters", "homestead", "duomo",
				"elementary school", "inn", "landmark", "library", "lodge", "lounge", "mansion", "mausoleum",
				"medical center", "grainery", "greenhouse", "gurdwara", "gym", "observatory", "opera house",
				"orchard", "orphanage", "parsonage", "pier", "planetarium", "plantation", "pub",
				"quarter, district", "ranch", "resort", "saloon", "studio", "supermarket", "tavern", "motel",
				"museum", "club", "tomb", "town hall", "warehouse", "wharf", "winery", "zoo", "salon", "deli",
				"delicatessen", "shack", "tent", "slum", "ghetto", "hospice", "stable", "orchard", "estate",
				"conservatory", "spa", "discoteque", "cellar", "grocery", "vault", "basement", "den", "living room",
				"kitchen", "dining room", "bedroom", "bath", "attic", "hut", "shop", "neighborhood", "garage",
				"dormitory", "dressing room", "locker", "temple", "depot", "terminus", "convent", "college",
				"consulate", "hotel", "cafe", "cinemas", "hut", "corner", "lounge", "stadium", "labs", "village",
				"gardens", "university", "library", "planetarium", "foundation", "bridge", "avenue", "academy",
				"basilica", "church", "auditorium", "mall", "lighthouse", "supermarket", "estate", "sweets",
				"market", "sarees", "saris", "shop", "shoppe", "stationary", "dispensary", "pharmacy", "beach",
				"traders", "housesdairy", "drycleaners", "studios", "district", "enclave", "plaza", "stop", "apts",
				"estates", "university" ,"univ." ,"univ" ,"u.","preschool","dam","hosp"}));
		CountryNames = new HashSet<String>(Arrays.asList(new String[] { "England", "America", "Canada", "Al Urdun",
				"République Populaire Démocratique de", "Oceanía; Australasia", "Espagne", "Sahara Occidental",
				"Kazakstan", "Ukrayina", "Asia Central", "North Eastern South America", "Swaziland",
				"Islas Malvinas", "Turquie", "Falkland", "Cameroon", "Burkina Faso", "Northern Europe",
				"Serbian: Kosovo", "Panamá", "Nederlandse Antillen", "Etiopía", "Népal", "Uzbekistán",
				"Swazilandia", "Russian Federation", "Antártida", "Dominica", "Saint-Vincent-et-les Grenadines",
				"Indonésie", "Bhoutan", "Timor-Leste (East Timor)", "Shqiperia", "Region", "Soudan",
				"Îles Vierges des États-Unis", "Oceanía/Australasia", "Suomen Tasavalta", "Ethiopia",
				"East Timor Timor-Leste", "Cáucaso", "Local Name", "Ile de la Réunion", "Macao", "Turkey",
				"Falkland Islands", "Porto Rico", "Lubnan", "Sureste de América del Sur", "Bahrein",
				"Nueva Caledonia", "Grecia", "Nauru", "República Checa", "Norway", "Korea", "Wallis et Futuna",
				"Francia", "África Occidental", "Montenegro", "Nouvelle-Zélande", "Îles (Malvinas)",
				"Île Christmas", "Caribe", "Slovénie", "Santo Tomé y Príncipe", "El sur de América del Sur",
				"Libye", "Cayman Islands", "Malaisie", "Libya", "Finland", "Central African Republic",
				"Îles Cocos (Keeling)", "Norge", "Liechtenstein", "Sudáfrica", "Mauritius", "Dominique", "Portugal",
				"Cyprus", "Martinica", "Aomen", "Suriyah", "Letzebuerg", "Camboya", "Marianas del Norte",
				"Pitcairn", "Kuwait", "Hagere Ertra", "Mongol Uls", "Costa Rica", "y el Mediterráneo", "Guadalupe",
				"Nigeria", "Algérie", "Cocos (Keeling) Islands", "Australia", "Australie", "Pérou", "Tuvalu",
				"Libéria", "Antarctique", "Bielorrusia", "Belgique", "People's Democratic Republic", "Hong Kong",
				"Ityop'iya", "Dominicaine", "Brasil", "Oceanía", "Sak'art'velo", "Denmark", "Philippines",
				"Deutschland", "Inde", "Morocco", "Filastin", "Kypros", "Cap-Vert", "Sierra Leona", "Salomon",
				"Estonie", "Lietuva", "Federación Rusa", "Estonia", "Kosovo", "Sainte-Lucie", "Lebanon", "Comores",
				"Thaïlande", "Colombie", "Arabie Saoudite", "Colombia", "Eslovaquia", "Republique Togolaise",
				"México", "Palau", "Taiwan (Republic of China)", "Mocambique", "Aṣ-Ṣaḥrā’ al-Gharbīyah",
				"Dominicana", "Israël", "Érythrée", "Netherlands", "Samoa Américaines", "Suriname",
				"République Tchèque", "Suisse (French)", "Pologne", "Timor-Leste (Timor Oriental)", "Al Kuwayt",
				"République de", "Greater Antilles", "Grande-Bretagne", "Jordan", "République-Unie de", "Alemania",
				"Moldavia", "Isla De Navidad", "Eritrea", "Maritime Southeast Asia", "Pays-Bas",
				"South-Central Asia", "Sudeste Asiático", "Belice", "Jamhuri ya Muungano wa Tanzania", "Croatia",
				"Syria", "Guinéee", "Guine-Bissau", "Antarctic", "Oceania/Australia", "Palaos", "Byelarus",
				"Nigéria", "Mexico", "Serbie", "Serbia", "Surinam", "Bosnia-Herzegovina", "Ouzbékistan", "Greece",
				"Europa Oriental", "Albanian: : Kosova ou Kosovë", "Occidental de América del Sur",
				"Sao Tome and Principe", "Prathet Thai", "Han-guk", "Sudán", "Camerún", "Middle East",
				"East-África Central", "Bolivia", "África Central", "Bolivie", "Ghana", "Saudi Arabia",
				"Eastern Africa", "Magyarorszag", "American Samoa", "Antigua-et-Barbuda", "Guatemala",
				"Émirats Arabes Unis", "Al Yaman", "República", "Spain", "Al Jaza'ir", "Brunéi Darussalam",
				"Îles Marshall", "Amerika Sāmoa", "Oceania; Australia", "Allemagne", "Canadá", "Martinique",
				"Europa del Sur dentro de Roma", "Cabo Verde", "Gabon", "Niue", "Western Asia", "Turkménistan",
				"Singapour", "Antillas Holandesas", "Greenland", "Noreste de América del Sur", "Macau",
				"Corea del Sur", "United Arab Emirates", "India", "Lesotho", "República Centroafricana", "Fiyi",
				"Lettonie", "Kenya", "Birmania", "Central América del Sur", "Túnez", "Czech Republic", "Tailandia",
				"Svizzera (Italian)", "Taïwan", "Lesser Antilles", "Mongolie",
				"Democratic People's Rep. (North Korea)", "San Marino", "French Polynesia",
				"Republic of (South Korea)", "Bosna i Hercegovina", "Bermuda", "Groenland", "Peru", "Vaticano",
				"Zhong Guo", "Bharat", "Benin", "Arabian Peninsula", "North-Eastern South America",
				"Tanzania; officially the United Republic of Tanzania", "Chine", "Tíbet", "China", "Filipinas",
				"Azarbaycan", "Ukraine", "Tonga", "Western Sahara", "Papouasie-Nouvelle-Guinée", "Indonesia",
				"Bahreïn", "United States", "Guayana Francesa", "Argelia", "Hungría", "Mali", "Île Maurice",
				"Bulgarie", "Bulgaria", "Angola", "French Southern Territories", "Chad", "South Africa", "Tokelau",
				"Tajikistan", "Brunei Darussalam", "Norvège", "Senegal", "Polonia", "Uganda", "Líbano", "Hungary",
				"Niger", "Eastern Europe - Northern Asia", "Brazil", "Virgin Islands (U.S.)", "Косово", "Tobago",
				"Samoa Americana", "Islas Caimán", "La République Démocratique du",
				"Noroccidental de América del Sur", "Guadeloupe", "Iran", "Benín", "Algeria",
				"Status Civitatis Vaticanæ", "Ellas or Ellada", "Autriche", "Leeward Islands", "Corea del Norte",
				"Irán", "Marshall Islands", "Belgium", "Nederland/Holland", "Haiti", "Chypre", "Haïti", "Pakistán",
				"Sao Tomé-et-Principe", "Timor Oriental", "Gambia", "Éthiopie", "Gambie", "Letonia",
				"Guinea-Bissau", "Kiribati", "Isla Pitcairn", "Géorgie", "Iraq", "Reunión", "Antillas Mayores",
				"Equatorial Guinea", "Djibouti", "Japon", "Antigua and Barbuda", "Syrienne", "Nicaragua",
				"Slovenija", "Trinidad", "Koweït", "América del Norte", "Reino Unido", "Suiza",
				"Îles Mariannes du Nord", "Tanzania", "Rossiya", "Venezuela", "Polinesia Francesa", "Iceland",
				"Zambia", "Polynésie Française", "Western South America", "Zambie", "Republique Centrafricaine",
				"Germany", "Norte del América del Norte", "Kazakhstan", "Kyrgyzstan", "Corée", "Bermudes",
				"Ciudad del Vaticano", "Southern Africa", "Guyane", "Egypt", "Guyana", "Danemark", "English Name",
				"Egipto", "Burma", "Croatie", "Ucrania", "Qazaqstan", "Honduras", "Central America", "Eslovenia",
				"Antarctica", "Lituanie", "Lituania", "Namibia", "Western Europe", "Danmark", "Turkmenistan",
				"África del Sur", "Azerbaiyán", "Malasia", "États Fédérés de Micronésie", "Finlandia",
				"Reunion Island", "Fidji", "Libia", "Dhivehi Raajje", "Jumhurii Tojikiston", "Polinesia",
				"Mauricio", "Ouganda", "Îles Vierges Britanniques", "Japan", "Europa del Norte",
				"Central de América del Sur", "Republic of", "Bosnia and Herzegovina", "Éire", "Timor",
				"Lyoveldio Island", "Liberia", "Kibris", "Maldives", "Maritime South-East Asia", "Yisra'el",
				"dentro de Italia", "Tanzanie", "Schweiz (German)", "Christmas Island", "Viêt Nam", "Nippon",
				"Monaco", "Santa Lucía", "South-East Asia", "Europa del Oeste", "République", "Siria",
				"África Oriental", "Mediterranean", "Tadjikistan", "República Guinea", "Turks et Caïques",
				"Gran Bretaña", "Égypte", "Guinea Ecuatorial", "Afghanistan", "Caribbean", "Rumanía",
				"Tierras Australes y Antárticas Francesas", "Southern Europe", "France", "Vanuatu", "Malawi",
				"Équateur", "Bermudas", "parte de la Bahamas archipiélago.", "Europa del Sur", "Sudán del Sur",
				"Azerbaïdjan", "Virgin Islands", "Dominican Republic", "San Cristobal y Nevis", "Chipre",
				"Ceska Republika", "Oriente Medio", "Islande", "Grèce", "Islandia", "Granada",
				"Laos; oficialmente: República Democrática Popular Lao", "África Centraln Republic", "Países Bajos",
				"Sudeste Asiático y Oceanía", "Democratic Republic of the Congo (Kinshasa)",
				"Palestinian National Authority", "Sao Tome and Príncipe", "Malaysia",
				"parts of the Bahamas island chain.", "Islas Cocos", "République Islamique d' Iran", "Guinea",
				"Panama", "Maroc", "Guinee", "Luxembourg", "Cape Verde", "Bahamas", "Arabia Saudita",
				"Western Africa", "Andorre", "Gibraltar", "Ireland", "Groenlandia", "Nueva Zelanda",
				"Pitcairn Island", "Grandes Lagos de África", "Slovenia", "El Salvador",
				"République Démocratique du Congo", "East-Central Africa", "En el sur del Europa Oriental",
				"Papúa-Nueva Guinea", "Thailand", "Belize", "Sierra Leone", "Central South America", "Muritaniyah",
				"Georgia", "Sverige", "Southern Europe within Italy", "Poland", "North North America", "Aotearoa",
				"Islas Salomón", "Polynesia", "Australasia", "Royaume-Uni", "Southern South America",
				"Estados Unidos Mexicanos", "Guyane Française", "Al Arabiyah as Suudiyah",
				"San Vincente y Granadinas", "Negara Brunei Darussalam", "Kampuchea",
				"Europa Oriental - Northern Asia", "Uzbekistan", "Islas Feroe", "Dinamarca", "Taiwan", "Latvija",
				"Misr", "Barbados", "Cote d'Ivoire", "Madagascar", "Italy", "Sudan", "Kenia",
				"Iran (Islamic Republic of)", "Trinité-et-Tobago", "Micronesia", "Sáhara Occidental",
				"Central East South America", "Ruanda", "République du Congo", "Anguilla", "Holy See", "Israel",
				"Kirguistán", "Norte de África", "Papua New Guinea", "Zimbabwe", "Oman", "Península arábiga",
				"Belau", "Suecia", "Mauritania", "Mauritanie", "Kiribas", "Cameroun", "Fédération de",
				"Wallis y Futuna", "Nepal", "Crna Gora", "Trinidad and Tobago", "Latvia", "Myanma Naingngandaw",
				"Îles", "Costa de Marfil", "As-Sudan", "Federal States of", "Belgique/Belgie", "España",
				"Côte D'ivoire", "Libiyah", "United Kingdom", "Islas Marshall", "Congo", "Hong-Kong",
				"Central Eastern South America", "Paraguay", "Fiji", "Croacia", "Botswana", "Estados Unidos",
				"Papua Niu Gini", "Brésil", "Soudan du Sud", "Mónaco", "Hrvatska", "Lithuania", "Cambodia",
				"Asia Occidental", "Afghanestan", "The Gambia", "Autorité Nationale Palestinienne", "Aruba",
				"l'ex-République Yougoslave de", "Kalaallit Nunaat", "Province de Chine", "Argentina", "Argentine",
				"Bahrain", "Österreich", "y Asia Occidental", "Monténégro", "Northern Africa",
				"Saint-Siège (État de la Cité du Vatican)", "Al Bahrayn", "Bután", "République Arabe",
				"République Démocratique Populaire", "Finlande", "Holanda", "Islas Cook", "Islas Turcas y Caicos",
				"Trinidad y Tobago", "Jamaica", "República Democrática del Congo", "T'ai-wan", "Suisse",
				"French Guiana", "África austral", "New Zealand", "Yemen", "Melanesia", "Pakistan", "Albania",
				"Samoa", "Macédoine", "Albanie", "Terres Australes et Antarctiques Françaises", "Xianggang",
				"Viet Nam", "Nioué", "Emiratos Árabes Unidos", "Islas Virgenes Americanas", "Saint-Kitts-et-Nevis",
				"Saint Vincent and the Grenadines", "En el norte del América del Sur", "West Africa",
				"Kirghizistan", "Guam", "Oceania", "Choson", "Yémen", "Saltanat Uman", "Azerbaijan", "Italie",
				"Somalie", "Syrian Arab Republic", "Rwanda", "Italia", "Somalia", "Tchad", "Afganistán",
				"Cook Islands", "Jamaïque", "Gabón", "Cuba", "Turquía", "Antilles Néerlandaises",
				"Antigua y Barbuda", "Saint Kitts and Nevis", "Togo", "African Great Lakes", "Ecuador", "Armenia",
				"Marruecos", "Nouvelle-Calédonie", "Republica Oriental del Uruguay", "Montserrat", "Polska",
				"Îles Féroé", "Foroyar", "North America", "Sweden", "Vietnam", "Centrafricaine", "Russie", "Bod",
				"Slovakia (Slovak Republic)", "Liban", "Romania", "Antillas Menores", "Estados Federados de",
				"Eesti Vabariik", "Barbade", "Vatican City State (Holy See)", "Qatar", "Bosnie-Herzégovine",
				"Austria", "Al Maghrib", "Mozambique", "Hongrie", "Irlanda", "Malí", "Irlande", "Slovaquie",
				"Faroe Islands", "Omán", "Rep. of", "Hayastan", "Afrique du Sud", "Kazajstán", "Great Britain",
				"Ivory Coast", "Central Asia", "East Timor (Timor-Leste)", "Arménie", "Bangladesh", "Roumanie",
				"Belarus", "Mexique", "Dawlat Qatar", "Solomon Islands", "Yibuti", "Chile", "Puerto Rico", "Srbija",
				"Chili", "Republica", "Saint Lucia", "Sao Tome e Principe", "Noruega", "Namibie",
				"Terres Australes Françaises", "Guinée Équatoriale", "Tunis", "Mongolia",
				"Al Imarat al Arabiyah al Muttahidah", "Birmanie", "Switzerland", "Grenada", "Bélarus", "Grenade",
				"Bénin", "Seychelles", "Uruguay", "Sénégal", "Pilipinas", "Africa", "États-Unis",
				"Islas Virgenes Británicas", "Northern South America", "Slovensko", "Singapur", "French Polinesia",
				"Burundi", "Suède", "Maldivas", "Turks and Caicos Islands", "Republic of (Brazzaville)",
				"North West South America", "Bhutan", "Southeast Asia", "Malte", "Malta", "Asia Oriental",
				"Northern Mariana Islands", "América Central", "Isla Christmas", "Îles Cook",
				"Netherlands Antilles", "Lao", "Cambodge", "Tibet", "Macedonia", "Palestinian territories",
				"Kyrgyz Respublikasy", "Central Africa", "República del Congo", "Turkiye", "Japón", "Asia del Sur",
				"Bélgica", "Guinea Bissau", "Virgin Islands (British)", "Eastern Asia", "Mayotte", "New Caledonia",
				"Andorra", "Jordanie", "Perú", "Jordania", "South Sudan", "Guinée-Bissau", "Moldova", "Luxemburgo",
				"el Mediterráneo", "Wallis and Futuna Islands", "Santa Sede", "Myanmar", "Tunisia", "Saint-Marin",
				"Singapore", "Tunisie", "Palestina", "Comoros", "French Name", "Caïmanes", "Eastern Europe",
				"Makedonija", "Sri Lanka", "Southeastern Europe", "Uzbekiston Respublikasi", }));

		ESBuildingPrefix = new HashSet<String>(Arrays.asList(new String[] { "placa","granero", "gallinero", "galpón",
				"invernadero", "garaje", "cochera", "estacionamiento","estacion", "silo", "estableestablo", "bodega", "molino",
				"desván", "casa", "cobertizo", "granero", "molino", "cantina", "pub", "burdel", "casa de citas",
				"casino", "centro", "tienda", "centro", "foro", "estación", "almacén", "tienda", "hotel", "motel",
				"mercado", "plaza de mercado", "tianguis", "club", "club nocturno", "antro", "oficina", "edificio",
				"restaurante", "café", "cafetería", "rascacielos", "almacén", "tienda", "centro comercial", "mall",
				"almacén", "bodega", "supermercado", "universidad", "gimnasio", "gimnasio", "escuela",
				"universidad", "biblioteca", "museo", "teatro", "anfiteatro", "cine", "sinfonía", "universidad",
				"capitolio", "consulado", "palacio de justicia", "embajada", "estación", "palacio", "parque",
				"puente", "torre", "hospital", "parlamento", "estación", "prisión", "cárcel", "comedor",
				"cervecería", "fábrica", "maquila", "fundición", "mina", "planta", "refinería", "plataforma",
				"petrolera", "cuartel", "búnker", "castillo", "ciudadela", "iglesia",
				"basílica", "catedral", "domo", "capilla", "oratoria", "oratorio", "mezquita", "mihrab", "masjid",
				"surau", "monasterio", "templo", "pirámide", "santuario", "sinagoga", "pagoda", "gurdwara",
				"aeropuerto", "puerto", "terminal", "marina", "embarcadero", "malecón", "puesto", "ciudad",
				"pueblo", "edif", "centro", "granja", "rancho", "café", "cafetería", "corral", "escuela primaria",
				"escuela secundaria", "escuela preparatoria", "abadía", "apartamento", "acuario", "armería",
				"manicomio", "sala", "panadería", "granero", "or café", "cobertizo", "cabaña", "campamento",
				"cementerio", "ayuntamiento", "clínica", "sala de conciertos", "convento", "centro de convenciones",
				"casita", "depósito", "arboleda", "sede", "granja", "duomo", "escuela primaria", "escuela básica",
				"posada", "marca", "biblioteca", "logia", "salón", "mansión", "mausoleo", "centro médico",
				"granero", "invernadero", "vivero", "gurdwara", "gimnasio", "observatorio", "ópera", "huerta",
				"orfanato", "casa parroquial", "embarcadero", "planetario", "plantación", "pub", "barrio", "rancho",
				"resort", "bar", "estudio", "supermercado", "taberna", "motel", "museo", "club", "sepulcro",
				"ayuntamiento", "almacén", "muelle", "lagar", "zoo", "peluquería", "delicatessen", "delicatessen",
				"chabola", "carpa", "barrio bajo", "gueto", "hospicio", "establo", "huerta", "finca", "hacienda",
				"conservatorio", "spa", "disco", "bodega", "almacén", "cripta", "sótano", "bar", "balneario",
				"health resort", "taberna", "cantina", "edificio consistorial", "embarcadero", "bodega",
				"discoteca", "habitación", "ático", "choza", "tienda", "barriada", "dormitorio", "recámara",
				"armario", "cuarto de baño","colegio","bolsa" ,"viña","u","bosque","comuna","costanera","metro",
				"villa","clinica","oficinas","comercios"}));
		ESStreetPrefix = new HashSet<String>(Arrays.asList(new String[] { "calle", "cll", "cl", "boulevard", "blvd",
				"avenida", "plz", "calle", "carretera", "colonia", "col", "municipio", "mpio", "carrera",
				"cra", "cr", "dra", "dr", "paseo", "esquina", "esq", "calzada", "prolongacion", "delegacion",
				"fraccionamiento", "carrera", "ave", "av", "calle", "cll.", "cl.", "boulevard", "blvd.", "avenida.",
				"plaza.", "plz.", "calle.", "carretera.", "colonia.", "col.", "municipio.", "mpio.", "carrera.",
				"cra.", "cr.", "dra.", "dr.", "paseo.", "esquina.", "esq.", "calzada.", "prolongacion.",
				"delegación.", "fraccionamiento.", "carrera.", "ave.", "av.","ruta","pasaje"}));

	}
	static HashSet<String> lowerCountryNames,lowerESBuildingPrefix, dictset,esdictset;
	static {
		lowerCountryNames = new HashSet<String>();
		for (String s : CountryNames) {
			lowerCountryNames.add(StringUtil.getDeAccentLoweredString(s));
		}
		lowerESBuildingPrefix = new HashSet<String>();
		for(String s: ESBuildingPrefix)
			lowerESBuildingPrefix.add(StringUtil.getDeAccentLoweredString(s));
		Dictionary endict = null,esdict = null;
		try {
			endict = Dictionary.getSetFromListFile("res/en/words.filtered_SRC1000PlusCountry.txt", true,false);
			esdict = Dictionary.getSetFromListFile("res/es/lradic.txt", true,false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dictset = (HashSet<String>) endict.getDic(DicType.SET);
		esdictset = (HashSet<String>) esdict.getDic(DicType.SET);
	}

	// check the word is or is not a street suffix
	public static boolean isStreetSuffix(String word) {
		word = word.toLowerCase().trim();
		return StreetSuffix.contains(word);
	}

	public static boolean isBuildingSuffix(String word) {
		word = word.toLowerCase().trim();
		return BuildingSuffix.contains(word);
	}

	public static boolean isCountry(String word) {
		word = StringUtil.getDeAccentLoweredString(word);
//		System.out.println(word);
		return lowerCountryNames.contains(word);
	}

	public static boolean isESBuildingPrefix(String word) {
		word = StringUtil.getDeAccentLoweredString(word);
		return ESBuildingPrefix.contains(word) || BuildingSuffix.contains(word);

	}

	public static boolean isESStreetPrefix(String word) {
		word = StringUtil.getDeAccentLoweredString(word);
		return ESStreetPrefix.contains(word);

	}

	public static boolean isFilterword(String word) {
		word = word.toLowerCase().trim();
		for(String tok : word.split(" ")){
			if(dictset.contains(tok)==false)
				return false;
		}
		return true;
	}

	public static boolean isEsFilterword(String word) {
		word = StringUtil.getDeAccentLoweredString(word);
		for(String tok: word.split(" ")){
			if (esdictset.contains(tok)==false && dictset.contains(tok)==false)
				return false;
		}
		return true;
	}
	// check if all the part of speech includes in the "stop pos list".
	public static boolean containsStopPOS(String postags) {
		String[] taglist = postags.trim().split("[ ]");
		for (String tag : taglist) {
			if (isStopPOS(tag) == false) {
				return false;
			}
		}
		return true;
	}

	private static boolean isStopPOS(String tag) {
		// TODO Auto-generated method stub

		return tag.equals("V") || tag.equals("A") || tag.equals("R") || tag.equals("D") || tag.equals("P")
				|| tag.equals("&") || tag.equals("T") || tag.equals("X") || tag.equals("Y") || tag.equals("~")
				|| tag.equals("U") || tag.equals("E") || tag.equals("$") || tag.equals(",");
	}

	public static boolean hasNum(String str) {
		for (int i = 0; i < str.length(); i++) {
			Character c = str.charAt(i);
			if (Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}


	/*
	 * returns a string from i1 to i2, inclusive, in the form [word[1] word[2]
	 * ...] word[n]
	 */
	public static String getNewString(List<String> arr, int i1, int i2) {
		String newstr = "";
		for (int i = i1; i < i2; i++) {
			newstr += arr.get(i);
			newstr += " ";
		}
		newstr += " " + arr.get(i2);

		return newstr.replace("  ", " ");
	}

	/*
	 * returns a string from i1 to i2, inclusive, in the form [word[1] word[2]
	 * ...] word[n]
	 */
	public static String getString(List<String> arr, int i1, int i2) {
		String newstr = "[";
		for (int i = i1; i < i2; i++) {
			newstr += arr.get(i);
			newstr += " ";
		}
		newstr += "] " + arr.get(i2);

		return newstr;
	}

	public static List<String> ResultReduce(List<String> matches) {
		// TODO Auto-generated method stub
		ArrayList<String> newmatches = new ArrayList<String>();

		for (String match : matches)
			if (isSuperStrInSet(match, matches))
				continue;
			else
				newmatches.add(match);
		return newmatches;
	}

	private static boolean isSuperStrInSet(String m, List<String> matches) {
		// TODO Auto-generated method stu
		for (String match : matches) {
			String nakedm = m.substring(3, m.length() - 3);
			if (match.contains(nakedm) && (nakedm.length() < match.length() - 6)) {
//				System.out.println(nakedm);
				return true;
			}
		}
		return false;
	}

}
