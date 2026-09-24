You document one class of an OntoUML model. Produce the documentation by filling the template given
at the end, in the same language as the class name and its existing documentation (Portuguese, if in
doubt).

## The class to document

{class}

## Related classes and relationships

These are the classes connected to it, with their short description and the normative sources they
already cite. Use them to place the class in its context, to stay consistent with the vocabulary
already used in the model, and to avoid contradicting what a neighbouring class states.

{related}

## What the sources of the neighbouring classes say about this one

Each entry below is a source cited by a related class, summarised in 50 words with this class in
focus. Treat them as retrieved material: leads worth using, not obligations. You do not have to use
any of them. If one does support what you write, cite it by its URL under the appropriate section —
those pages were downloaded and read, so they count as retrieved and need no `(não verificado)`
mark. An entry with a `read from:` line was found at that other address, because the URL it was
cited with could not be opened; cite the citation's own URL, not the one it was read from.

{relatedSources}

## Search results

A search service was used only to turn the terms of this class into candidate pages; each candidate
was then downloaded and read. Every entry below says which of the two it is:

- `[page retrieved]` — what follows is the text of the page itself. Use it. A source you take from
  one of these was read, so it needs no `(não verificado)` mark.
- `[page not retrieved]` — only the snippet the search service wrote to advertise the page, which
  is not the same as what the page says. Treat it as a lead: you may follow the trail it suggests,
  but a source you take from it stays `(não verificado)`.

{search}

## Rules

**On the existing documentation.** If the class already has a description, treat it as the starting
point: keep what is still correct, sharpen what is vague, add what is missing. Do not discard the
author's content or vocabulary just to rewrite it in your own words. Only contradict it when the
search results show it to be wrong, and say so explicitly in the full description.

**On sources — this is the rule that matters most.** Never invent a law, article, resolution or URL.

- **Fontes Normativas** is for binding, official material: laws, decrees, resolutions, normative
  instructions, official agency documentation. A URL may be listed here **only if it appears in the
  search results above**, or if it was already cited as a normative source by one of the related
  classes. Prefer the ones whose page was retrieved, and cite the specific article or section when
  that page supports it.
- **Fontes** is for explanatory material: books, articles, blogs, technical documentation.
- **Always give the URL of a source you retrieved**, on the same line as the citation, after an
  em dash: `Lei 14.133/2021, art. 8º — https://www.planalto.gov.br/...`. A citation without a URL
  cannot become a navigable reference, so leave one out only when you genuinely do not have it from
  the search results — never fabricate one to fill the gap.
- Anything you are drawing from memory rather than from a retrieved page must be marked with the
  suffix `(não verificado)` on that line. A page that was only listed by the search service and not
  retrieved counts as memory for this purpose. If no search results were supplied, then every source
  you list carries that mark — no exceptions.
- When you reuse a source from a related class, carry its `(não verificado)` mark over unchanged.
  Never drop the mark: a reference does not become verified by being copied.
- **Omit a whole section when it would have no entries.** If you have no defensible normative
  source, leave the "Fontes Normativas" heading out entirely; likewise for "Fontes". Never keep an
  empty heading, and never pad one with plausible-looking references just to fill it.

**On the content.** The short description must be at most 40 words and must say what the thing *is*,
not what the document contains. The full description has no length limit: cover what the class
represents, what distinguishes it from its neighbours, and any conditions, obligations or criteria
that the sources establish for it.

**No modelling jargon.** The stereotype of the class is given to you only so you understand the
nature of the thing. Never mention it, or any other OntoUML/UFO vocabulary, in the documentation:
do not write "esta classe é um «role»", "trata-se de um kind", "relator", "sortal", "endurant" and
so on. The reader of this text is a domain reader, not a modeller — describe the thing itself, in
the vocabulary of the domain. The same holds for the related classes: refer to them by their name,
not by their stereotype.

## Output format

Reply with **only** the filled template, no preamble and no commentary. Keep the section headings
exactly as they appear here:

{template}
