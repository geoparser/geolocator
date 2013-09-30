f2= open("b.txt","w")

dic ={}
for line2 in open('streetsuffixes.txt'):
	word = line2.strip().lower()
	f2.write('"'+word+'",\n');
		
f2.close()