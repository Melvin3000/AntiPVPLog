BUKKIT=../bukkit-1.12B1.jar
JAVA=1.8
PLUGIN=AntiPVPLog
TARGET_DIR=com

JFLAGS = -Xlint:all -classpath $(BUKKIT) -d ./ -source $(JAVA) -target $(JAVA)
JC = javac
SOURCEFILES = $(wildcard src/com/Melvin3000/AntiPVPLog/*.java)

default: jar_file

class_files:
	$(JC) $(JFLAGS) $(SOURCEFILES)

jar_file: class_files
	jar -cf ./$(PLUGIN).jar ./*


clean:
	rm -f *.jar
	rm -rf $(TARGET_DIR)
