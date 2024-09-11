SOURCES = $(wildcard */*.java)

CLASSES = $(SOURCES:.java=.class)

default: $(CLASSES)

%.class: %.java
	javac -cp $(CUP_DIR)/java-cup-11b.jar:. $<

clean:
	rm -f */*.class */*~

