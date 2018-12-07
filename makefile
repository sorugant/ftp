JCC = javac
JFLAGS = -g
default: ftp/Server.class ftp/Client.class ftp/AES.class ftp/ChecksumMethod.class ftp/ClientDB.class
ftp/Server.class: ftp/Server.java ftp/ChecksumMethod.java ftp/AES.java ftp/ClientDB.java
	$(JCC) $(JFLAGS) ftp/Server.java ftp/ChecksumMethod.java ftp/AES.java ftp/ClientDB.java
ftp/Client.class: ftp/Client.java ftp/ChecksumMethod.java ftp/AES.java ftp/ClientDB.java
	$(JCC) $(JFLAGS) ftp/Client.java ftp/ChecksumMethod.java ftp/AES.java ftp/ClientDB.java
ftp/AES.class: ftp/AES.java 
	$(JCC) $(JFLAGS) ftp/AES.java
ftp/ChecksumMethod.class: ftp/ChecksumMethod.java
	$(JCC) $(JFLAGS) ftp/ChecksumMethod.java
ftp/ClientDB.class: ftp/ClientDB.java
	$(JCC) $(JFLAGS) ftp/ClientDB.java
clean:
	$(RM) *.class

