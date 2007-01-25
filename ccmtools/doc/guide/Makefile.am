## process this file with automake to produce Makefile.in

DOC_NAME = guide

TEX = $(DOC_NAME).tex     \
      Installation.tex    \
      Introduction.tex    \
      ProcessOverview.tex

BIBS = cbse.bib doc.bib pattern.bib se.bib wx.bib

FIGS = \
       Component.fig               \
       ComponentGeneratorTools.fig \
       ConnectComponents.fig       \
       Container.fig               \
       DevelopmentProcess.fig      \
       TestDrivenDevelopment.fig

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
	@ $(LATEX) $(DOC_NAME)
	@ $(BIBTEX) $(DOC_NAME)
	@ $(LATEX) $(DOC_NAME)
	$(LATEX) $(DOC_NAME)

clean-local:
	$(RM) -f *.eps *.aux *.toc *.lo? *.bbl *.blg *.dvi $(DOC_NAME).ps $(DOC_NAME).pdf

