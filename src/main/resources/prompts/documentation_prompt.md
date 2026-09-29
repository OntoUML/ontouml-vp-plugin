You document one class of an OntoUML model. Produce the documentation by filling the template given
at the end. **Write the text you fill it with in {language}**, regardless of what language the class
name or its existing documentation are in. The template's own section headings stay exactly as
given, in English — only the content you write under each one is in {language}.

## The class to document

{class}

## Related classes and relationships

These are the classes connected to it, with their short description and the normative sources they
already cite. Use them to place the class in its context, to stay consistent with the vocabulary
already used in the model, and to avoid contradicting what a neighbouring class states.

{related}

## What the sources cited in the model say about this class

Each entry below is a reference found in the model, summarised in 50 words with this class in focus.
Its `origem:` says where it was cited, and how much weight it carries:

- `a própria classe` — attached by the author to the very class you are documenting. This is the
  primary material: if it contradicts a source from anywhere else, it prevails.
- `classe X` — cited by a neighbouring class.
- `diagrama X` — cited on a diagram this class is drawn on.
- `pacote X` — cited on a package or on the model containing it; it speaks about the domain, and
  only sometimes about this class in it.

Treat them as retrieved material: leads worth using, not obligations. You do not have to use any of
them. If one does support what you write, cite it under the appropriate section — those sources were
downloaded, opened or converted and read, so they count as retrieved and need no `(não verificado)`
mark. Cite a `URL:` entry by its URL; a source whose entry says `Arquivo:` has no URL to give, so
cite it by name.

Two lines change how an entry may be used:

- `read from:` — the source was found at that other address, because the one it was cited with could
  not be opened. Cite the citation's own address, not the one it was read from.
- `[not read]` — the source is cited in the model but could not be read, and the reason follows.
  You may still cite it, because the author put it there on purpose and it belongs in the
  documentation; but you know nothing about its content, so **anything you write from it must carry
  `(não verificado)`**, and you must not state what it says.

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

- **Normative sources** is for binding, official material: laws, decrees, resolutions, normative
  instructions, official agency documentation. A URL may be listed here **only if it appears in the
  search results above**, or if it was already cited as a source somewhere in the model. Prefer the
  ones whose page was retrieved, and cite the specific article or section when that page supports
  it.
- **Sources** is for explanatory material: books, articles, blogs, technical documentation.
- **Always give the URL of a source you retrieved**, on the same line as the citation, after an
  em dash: `Lei 14.133/2021, art. 8º — https://www.planalto.gov.br/...`. A citation without a URL
  cannot become a navigable reference, so leave one out only when you genuinely do not have it from
  the search results — never fabricate one to fill the gap.
- Anything you are drawing from memory rather than from a retrieved page must be marked with the
  suffix `(não verificado)` on that line. A page that was only listed by the search service and not
  retrieved counts as memory for this purpose. If no search results were supplied, then every source
  you list carries that mark — no exceptions.
- When you reuse a source cited elsewhere in the model, carry its `(não verificado)` mark over
  unchanged, and add the mark to any source listed as `[not read]`. Never drop a mark: a reference
  does not become verified by being copied, nor by having been cited by the author.
- **Omit a whole section when it would have no entries.** If you have no defensible normative
  source, leave the "Normative sources" heading out entirely; likewise for "Sources". Never keep an
  empty heading, and never pad one with plausible-looking references just to fill it.

**On the content.** The short description must be at most 40 words and must say what the thing *is*,
not what the document contains. The full description has no length limit: cover what the class
represents, what distinguishes it from its neighbours, and any conditions, obligations or criteria
that the sources establish for it.

**No modelling jargon.** The stereotype of the class is given to you only so you understand the
nature of the thing. Never mention it, or any other OntoUML/UFO vocabulary, in the documentation:
do not write "this class is a «role»", "it is a kind", "relator", "sortal", "endurant" and so on.
The reader of this text is a domain reader, not a modeller — describe the thing itself, in the
vocabulary of the domain. The same holds for the related classes: refer to them by their name, not
by their stereotype.

## Output format

Reply with **only** the filled template, no preamble and no commentary. Keep the section headings
exactly as they appear here:

{template}
