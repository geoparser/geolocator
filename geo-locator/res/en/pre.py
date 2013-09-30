f2= open("b.txt","w")

dic ={}
for line2 in open('TEMP_country_in_enesfr.txt'):
	country = line2.strip().lower()
	dic[country]=1

for line in open('words.filtered_SRC1000PlusCountry.txt'):
	entry = line.strip().lower()
	if entry in dic:
		continue;
	else:
		f2.write(entry+'\n')
		
f2.close()