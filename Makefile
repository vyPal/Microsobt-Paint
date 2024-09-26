JFLAGS = --module-path $(PATH_TO_FX) --add-modules javafx.controls
JC = javac
.SUFFIXES: .java .class

CLASSES = Main.java

default: classes

run: classes
	java $(JFLAGS) Main

%.class: %.java
	$(JC) $(JFLAGS) $*.java

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
