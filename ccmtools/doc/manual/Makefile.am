## process this file with automake to produce Makefile.in

DOC_NAME = manual

TEX = $(DOC_NAME).tex             \
      AssemblyGeneratorTools.tex  \
      CCMMetamodelLib.tex         \
      ComponentGeneratorTools.tex \
      IDLParser.tex               \
      Installing.tex              \
      Introduction.tex            \
      TestingTools.tex

BIBS = cbse.bib doc.bib pattern.bib wx.bib

FIGS = \
       AssemblyTools.fig            \
       CCMMetamodelLib.fig          \
       ComponentGeneratorDesign.fig \
       ComponentGeneratorParts.fig  \
       ComponentGeneratorTools.fig  \
       ToolsOverview.fig

PDFS = $(FIGS:.fig=.pdf)
EPSS = $(FIGS:.fig=.eps)

dist_noinst_DATA = $(TEX) $(BIBS) $(FIGS)
noinst_DATA = $(DOC_NAME).ps $(DOC_NAME).pdf

%.pdf: %.fig
	$(FIG2DEV) -L pdf $< $@

%.eps: %.fig
	$(FIG2DEV) -L eps $< $@

%.ps: %.dvi $(EPSS)
	$(DVIPS) -f $< > $(DOC_NAME).ps

%.pdf: %.dvi $(PDFS)
	$(DVIPDF) $<

$(DOC_NAME).dvi: $(TEX) $(EPSS) $(BIBS)
	@ $(LATEX) $(DOC_NAME).tex
	@ $(BIBTEX) $(DOC_NAME)
	@ $(LATEX) $(DOC_NAME).tex
	$(LATEX) $(DOC_NAME).tex

clean-local:
	$(RM) -f *.eps *.aux *.toc *.lo? *.bbl *.blg *.dvi $(DOC_NAME).ps $(DOC_NAME).pdf

