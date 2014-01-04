f2= open("c.txt","w")

dic ={}
for line2 in open('/Users/Indri/git/geolocator/geo-locator/GeoNames/admin1CodesASCII.txt'):
	country = line2.strip().split('\t')[1].lower()
	dic[country]=1

for line in open('words.filtered_SRC1000PlusCountry.txt'):
	entry = line.strip().lower()
	if entry in dic:
		continue;
	else:
		f2.write(entry+'\n')
		
f2.close()