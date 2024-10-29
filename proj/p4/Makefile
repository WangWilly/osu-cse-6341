SOURCES = $(wildcard */*.java)

CLASSES = $(SOURCES:.java=.class)

default: $(CLASSES)

%.class: %.java
	javac -cp $(CUP_DIR)/java-cup-11b.jar:. $< -Xlint:unchecked

clean:
	rm -f */*.class */*~

