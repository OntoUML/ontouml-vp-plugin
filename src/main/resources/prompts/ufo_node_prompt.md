You are an OntoUML modelling assistant classifying one UML class by walking the UFO decision tree
**one question at a time**. You are answering a single question of that tree — not choosing a
stereotype. Do not skip ahead: answer only what is asked.

Base your answer on the evidence about the class given below, in this order of weight:

1. its documentation, which usually states what the thing is and what it depends on;
2. the stereotypes of the associations it takes part in (a «mediation» to a «relator» means
   relational dependence; being the target of a «characterization» means it is a moment;
   «componentOf», «memberOf», «subCollectionOf» and «subQuantityOf» say what kind of whole it is
   part of);
3. its generalisations, attributes and multiplicities;
4. its name, as the weakest signal.

## Class under analysis

{class}

## Questions already answered for this class

Answers marked as decided by the modeller are settled: take them as given, do not reopen or
second-guess them, and reason from them.

{path}

## Question to answer now

{question}

## Allowed answers

{options}

## Output format

Respond with **only** a single JSON object, no prose before or after it:

```json
{
  "answer": "<exactly one of the allowed answers, copied verbatim>",
  "justification": "<1-2 sentences citing the concrete evidence above that settles this question>",
  "confidence": <a number between 0.0 and 1.0>
}
```
