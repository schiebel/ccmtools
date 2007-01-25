DOCUMENT_NAME=uml2idl_ger
LINKED_PDF=$(DOCUMENT_NAME)L.pdf

all: dvi

dvi: $(DOCUMENT_NAME).dvi
$(DOCUMENT_NAME).dvi: $(DOCUMENT_NAME).tex
	latex $(DOCUMENT_NAME).tex
	latex $(DOCUMENT_NAME).tex
	latex $(DOCUMENT_NAME).tex

linkedpdf: $(LINKED_PDF)
$(LINKED_PDF): $(DOCUMENT_NAME).tex
	pdflatex $(DOCUMENT_NAME).tex
	pdflatex $(DOCUMENT_NAME).tex
	pdflatex $(DOCUMENT_NAME).tex
	mv $(DOCUMENT_NAME).pdf $(LINKED_PDF)

bi: $(DOCUMENT_NAME).tex
	bibtex $(DOCUMENT_NAME)
	latex $(DOCUMENT_NAME).tex
	latex $(DOCUMENT_NAME).tex

pdf: $(DOCUMENT_NAME).pdf
$(DOCUMENT_NAME).pdf: $(DOCUMENT_NAME).dvi
	dvipdf -sPAPERSIZE=a4 $(DOCUMENT_NAME)

ps: $(DOCUMENT_NAME).ps
$(DOCUMENT_NAME).ps: $(DOCUMENT_NAME).dvi
	dvips -f $(DOCUMENT_NAME).dvi > $(DOCUMENT_NAME).ps

CLEANFILES=\
*.aux *.toc *.log \
$(DOCUMENT_NAME).pdf $(LINKED_PDF) \
$(DOCUMENT_NAME).dvi $(DOCUMENT_NAME).ps  \
$(DOCUMENT_NAME).bbl $(DOCUMENT_NAME).blg \
$(DOCUMENT_NAME).lof $(DOCUMENT_NAME).lot

clean:
	@-rm $(CLEANFILES)>/dev/null 2>&1
 









