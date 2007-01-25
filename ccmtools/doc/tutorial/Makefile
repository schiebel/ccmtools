DOCUMENT_NAME=tutorial
PARTS=\
  CCMOverview.tex\
  CarRentalExample.tex \
  ConfixInstallation.tex \
  ConnectComponents.tex \
  FirstAssembly.tex \
  FirstComponent.tex \
  Installation.tex \
  Introduction.tex \
  LCAC.tex \
  ManPage.tex \
  MicoInstallation.tex \
  ProgrammingModel.tex \
  Remote.tex \
  RemoteComponent.tex \
  UMLComponentDesign.tex 

FIGURES= \
  Adapter1.fig \
  Assembly.fig \
  AssemblyGraph.fig \
  AssemblyGraph2.fig \
  CcmToolsArchitecture.fig \
  Component.fig \
  ComponentGeneratorTools.fig \
  ComponentLogicBusinessLogic.fig \
  ConnectComponents.fig \
  Container.fig \
  DesignerTask.fig \
  DeveloperTask.fig \
  DevelopmentProcess.fig \
  LCAC_Overview.fig \
  LCAC_ProcessModel.fig \
  RemoteAssembly.fig \
  TestDrivenDevelopment.fig \
  UMLComponentDesign.fig 

ALL_SOURCES=$(DOCUMENT_NAME).tex $(PARTS) $(FIGURES)

HTML_OUTPUT_DIR=$(DOCUMENT_NAME)-html
HTML_STAMP=$(HTML_OUTPUT_DIR).stamp
EPS_FIGURES=$(subst .fig,.eps,$(FIGURES))

all: ps pdf html

ps: $(DOCUMENT_NAME).ps

pdf: $(DOCUMENT_NAME).pdf

html: $(HTML_STAMP)

dvi: $(DOCUMENT_NAME).dvi

bi: $(DOCUMENT_NAME).tex
	bibtex $(DOCUMENT_NAME)
	latex $(DOCUMENT_NAME).tex
	latex $(DOCUMENT_NAME).tex
$(DOCUMENT_NAME).dvi: $(ALL_SOURCES) $(EPS_FIGURES)
	latex $(DOCUMENT_NAME).tex
clean::
	rm -f *.aux *.log *.toc *.bbl *.blg *.lof *.lot $(DOCUMENT_NAME).dvi

$(DOCUMENT_NAME).pdf: $(DOCUMENT_NAME).dvi
	dvipdf -sPAPERSIZE=a4 $(DOCUMENT_NAME)
clean::
	rm -f $(DOCUMENT_NAME).pdf

$(DOCUMENT_NAME).ps: $(DOCUMENT_NAME).dvi
	dvips -f $< > $@
clean::
	rm -f $(DOCUMENT_NAME).ps

eps: $(EPS_FIGURES)
$(EPS_FIGURES): %.eps: %.fig
	fig2dev -L eps $< $@
clean::
	rm -f $(EPS_FIGURES)

$(HTML_STAMP): $(ALL_SOURCES)
	rm -rf $(HTML_OUTPUT_DIR)
	latex2html -mkdir -nofootnode -dir $(HTML_OUTPUT_DIR) $(DOCUMENT_NAME).tex
	touch $@
clean::
	rm -f $(HTML_STAMP)
	rm -rf $(HTML_OUTPUT_DIR)



