f = open('PURGE.csv','w')
for line in open('SPATIALML.csv'):
	if '??' in line:
		continue;
	f.write(line);
	