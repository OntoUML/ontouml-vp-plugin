# Prompts e templates da IA

Esta pasta reúne **todos os textos que o plugin envia à IA** e o **template** que a IA preenche.
Eles são os *padrões de fábrica*: o que o plugin usa enquanto o usuário não personaliza nada.

Nenhum desses textos está escrito dentro do código Java. Mudar o comportamento da IA — o tom, as
regras, a estrutura da documentação, as perguntas da árvore de decisão — é editar um arquivo daqui,
ou o equivalente na janela de configurações, sem tocar em uma linha de código.

## Sumário

- [O que há nesta pasta](#o-que-há-nesta-pasta)
- [Como os arquivos são usados](#como-os-arquivos-são-usados)
- [Duas formas de personalizar](#duas-formas-de-personalizar)
- [Referência de cada arquivo](#referência-de-cada-arquivo)
  - [`documentation_template.md`](#documentation_templatemd)
  - [`documentation_prompt.md`](#documentation_promptmd)
  - [`ufo_decision_tree_prompt.md`](#ufo_decision_tree_promptmd)
  - [`ufo_node_prompt.md`](#ufo_node_promptmd)
  - [`ufo_decision_tree.json`](#ufo_decision_treejson)
- [Placeholders](#placeholders)
- [O que não pode ser mudado sem quebrar o plugin](#o-que-não-pode-ser-mudado-sem-quebrar-o-plugin)
- [Testando uma alteração](#testando-uma-alteração)
- [Adicionando um novo arquivo](#adicionando-um-novo-arquivo)

---

## O que há nesta pasta

| Arquivo | Funcionalidade | Papel |
|---|---|---|
| `documentation_template.md` | Documentar classe | A **estrutura** que a IA preenche |
| `documentation_prompt.md` | Documentar classe | As **instruções** de como preencher |
| `ufo_decision_tree_prompt.md` | Sugerir metaclasse (avaliação rápida) | *System prompt*: o guia da árvore de decisão UFO |
| `ufo_node_prompt.md` | Sugerir metaclasse (avaliação detalhada) | O prompt de **uma pergunta** da árvore |
| `ufo_decision_tree.json` | Sugerir metaclasse (avaliação detalhada) | A **árvore** percorrida, pergunta a pergunta |

> As regras de validação do modelo (`association_constraints*.json`, `generalization_constraints.json`)
> **não** ficam aqui. Elas não são prompts: são dados lidos pelo verificador de modelos e continuam
> na raiz de `src/main/resources`.

## Como os arquivos são usados

### Documentar uma classe

Clique direito na classe → **Document or improve documentation...**

```
documentation_prompt.md ──┐
                          ├─ {class}, {related}, {relatedSources}, {search} preenchidos pelo plugin
documentation_template.md ┴─ entra no lugar de {template}
                                        │
                                        ▼
                               um único prompt → IA → template preenchido
                                        │
                                        ▼
                    campo Description da classe (+ References, quando há URL)
```

O plugin monta o prompt lendo o `documentation_prompt.md` e substituindo cada
[placeholder](#placeholders) pelo conteúdo real do modelo: a classe, as classes vizinhas, o que as
fontes citadas dizem e os resultados da pesquisa na web. O `documentation_template.md` entra no
final, no lugar de `{template}`. O fluxo completo está em
[`docs/ai-documentation.md`](../../../../docs/ai-documentation.md).

### Sugerir a metaclasse (estereótipo) de uma classe

Existem duas estratégias, e cada uma usa arquivos diferentes:

- **Avaliação rápida** — uma única requisição. `ufo_decision_tree_prompt.md` vai como *system
  prompt* e a IA percorre a árvore "de cabeça", devolvendo o estereótipo e o caminho que seguiu.
- **Avaliação detalhada** — uma requisição **por pergunta**. O plugin lê `ufo_decision_tree.json`,
  faz a pergunta do nó atual usando `ufo_node_prompt.md`, lê a resposta, segue para o nó indicado e
  repete até chegar a um estereótipo. O usuário pode acompanhar, corrigir uma resposta e retomar
  dali.

## Duas formas de personalizar

### 1. Pela interface (recomendado)

**Settings → Prompts & decision tree…** abre uma janela com uma aba para cada texto:

| Aba | Arquivo correspondente |
|---|---|
| Quick classification prompt | `ufo_decision_tree_prompt.md` |
| Decision node prompt | `ufo_node_prompt.md` |
| Decision tree | `ufo_decision_tree.json` (visual, ou JSON para editar) |
| Documentation → Template | `documentation_template.md` |
| Documentation → Prompt | `documentation_prompt.md` |

Edite o texto e clique em **Save**. Vale a partir da próxima chamada à IA, sem reiniciar o Visual
Paradigm.

- A personalização é **sua**, não do projeto: fica no arquivo `.ontouml.config.json` do *workspace*
  do Visual Paradigm, nos campos `customSystemPrompt`, `customNodePrompt`, `customDecisionTree`,
  `customDocumentationPrompt` e `customDocumentationTemplate`.
- Ela **sobrevive à atualização** do plugin.
- **Restore defaults** devolve todas as abas ao texto desta pasta.
- Salvar um texto **idêntico** ao padrão não grava nada. Assim o plugin continua seguindo os padrões
  quando eles melhoram numa versão nova. Só o que você realmente mudou vira personalização.
- Um campo personalizado vazio significa "use o padrão desta pasta".
- A árvore só é salva se passar na validação descrita em
  [`ufo_decision_tree.json`](#ufo_decision_treejson); do contrário a janela lista os problemas e
  recusa.

### 2. Editando os arquivos desta pasta (para quem mantém o plugin)

Muda o **padrão** distribuído a todos. Use quando a melhoria deve valer para qualquer usuário.

1. Edite o arquivo em `src/main/resources/prompts/`.
2. Gere e instale de novo com `./mvnw install`. Isso copia a pasta para
   `<plugins do Visual Paradigm>/ontouml-vp-plugin-<versão>/prompts/`.
3. **Reinicie o Visual Paradigm.** Os padrões são lidos uma vez e guardados em memória enquanto o
   plugin roda.

Também dá para editar direto a cópia instalada e reiniciar o Visual Paradigm, o que é útil para
experimentar rápido — mas **a próxima instalação sobrescreve** essa cópia, então leve a alteração
para o repositório.

> Quem personalizou pela interface continua vendo o seu texto: uma personalização tem prioridade
> sobre o padrão.

**Ordem de precedência**, para cada texto:

```
personalização do usuário (Settings)  →  se vazia  →  arquivo desta pasta
```

---

## Referência de cada arquivo

### `documentation_template.md`

O esqueleto do texto que a IA devolve e que vira o campo *Description* da classe. Padrão:

```markdown
# {Nome da Classe}

## Descrição curta
{Descrição curta da classe. No máximo 40 palavras.}

## Descrição completa
{Descrição completa da classe. Tamanho livre.}

## Fontes Normativas
{Leis, Documentações Oficiais, Resoluções...}

## Fontes
{Blogs, Livros e Documentos explicativos que auxiliem na compreensão}
```

O que está entre `{chaves}` é **instrução de preenchimento para a IA**, não placeholder do plugin: a
IA troca cada trecho pelo conteúdo. Escreva ali o que você espera encontrar.

**Você pode:**

- reescrever as instruções entre chaves (por exemplo, trocar o limite de 40 palavras);
- acrescentar seções novas (`## Exemplos`, `## Termos relacionados`…) — a IA as preencherá e elas
  serão gravadas na documentação da classe;
- acrescentar texto fixo que deve aparecer em toda documentação.

**Cuidado com estes quatro títulos:** `Descrição curta`, `Descrição completa`, `Fontes Normativas` e
`Fontes` são **reconhecidos pelo código** (`DocumentationTemplate`). É por eles que o plugin:

- extrai a descrição curta e as fontes normativas de uma classe para **alimentar as classes
  vizinhas** com contexto enxuto, em vez de repetir o texto inteiro;
- **remove a seção de fontes que ficou sem itens**, cabeçalho incluso;
- separa as fontes que recebem URL para publicar na aba **References**.

Se você renomear ou remover algum deles, o plugin não quebra — ele passa a tratar a classe como
"documentação escrita à mão": tudo vira descrição completa e a curta vira um trecho dos primeiros
240 caracteres. Mas perde os três recursos acima. Os títulos são comparados sem diferenciar
maiúsculas de minúsculas e ignorando `#`, `**` e `:`.

Além disso, o `documentation_prompt.md` cita "Fontes Normativas" e "Fontes" pelo nome nas regras.
Se mudar os títulos aqui, **mude também lá**.

O idioma dos títulos é livre para a IA, mas o reconhecimento acima é em português.

### `documentation_prompt.md`

As instruções que acompanham o template. Em ordem, ele define:

1. a **tarefa** e o idioma da resposta;
2. o **contexto** que o plugin injeta (`{class}`, `{related}`, `{relatedSources}`, `{search}`);
3. como **pesar cada origem** de fonte (a própria classe pesa mais que uma classe vizinha, que pesa
   mais que o pacote);
4. as **regras**: partir da documentação existente, nunca inventar lei, artigo ou URL, marcar com
   `(não verificado)` o que veio de memória, omitir seção vazia, sem jargão de modelagem;
5. o **formato de saída**, terminando em `{template}`.

**Você pode** ajustar o tom, o público-alvo, o idioma, o tamanho, e acrescentar regras do seu
domínio (por exemplo, "cite sempre a legislação estadual quando houver").

**Mantenha:**

- todos os placeholders da seção 2 e o `{template}` no fim. Sem eles, a IA não recebe o que precisa
  — `{search}`, por exemplo, é onde entram as páginas lidas na web;
- a regra do `(não verificado)`. É ela que separa uma fonte lida de uma fonte lembrada de memória, e
  o plugin depende dessa marca para não tratar citação inventada como confirmada;
- a instrução de responder **somente** com o template preenchido. Texto extra antes ou depois vai
  parar na documentação da classe.

### `ufo_decision_tree_prompt.md`

O *system prompt* da avaliação **rápida**: o guia da Árvore de Decisão de Modelagem UFO
(<https://github.com/spdrnl/UFO-Modeling-Decision-Tree>). Não tem placeholders — os dados da classe
seguem numa mensagem separada, gerada pelo plugin (nome, estereótipo atual, documentação,
generalizações, atributos e associações).

Contém o vocabulário fechado de estereótipos, a explicação de como ler as evidências e as regras de
decisão de cada ramo. **Você pode** refinar as regras, acrescentar exemplos do seu domínio e ajustar
como pesar a documentação frente ao nome da classe.

O bloco **Output format** é um contrato com o código: a resposta é lida como JSON com os campos
`stereotype`, `decisionPath`, `rationale`, `alternativeStereotype` e `currentStereotypeIssue`. Não
renomeie esses campos. O valor de `stereotype` precisa ser um dos nomes de máquina do vocabulário
listado no início do arquivo.

### `ufo_node_prompt.md`

O prompt de **uma pergunta** na avaliação **detalhada**. É repetido a cada nó da árvore, com os
[placeholders](#placeholders) `{class}`, `{path}`, `{question}` e `{options}` preenchidos. A IA
responde apenas àquela pergunta — não escolhe estereótipo.

**Você pode** mudar a ordem de importância das evidências e o critério de justificativa.

**Mantenha** os quatro placeholders e o formato de saída JSON com `answer`, `justification` e
`confidence`. O `answer` é comparado com as opções permitidas; uma resposta fora delas interrompe o
percurso.

### `ufo_decision_tree.json`

A árvore percorrida pela avaliação detalhada. Estrutura:

```json
{
  "startNode": "natureOfTheThing",
  "nodes": {
    "natureOfTheThing": {
      "question": "Texto da pergunta, com definições e exemplos.",
      "options": [
        { "answer": "endurant",  "next": "independence" },
        { "answer": "perdurant", "stereotype": "event" }
      ]
    }
  }
}
```

- `startNode` é o id do primeiro nó e **precisa existir** em `nodes`.
- Cada nó tem uma `question` e uma lista de `options`.
- Cada opção tem um `answer` (o texto que a IA deve devolver) e **um dos dois**: `next` (id de outro
  nó, e o percurso continua) ou `stereotype` (nome de máquina do estereótipo, e o percurso termina).
- O `question` deve trazer as definições e exemplos de que a IA precisa: é o único texto que ela vê
  sobre aquele passo, além do prompt do nó.

**Você pode** reescrever perguntas, acrescentar novos pontos de decisão, reordenar nós e opções, e
mudar o `answer` de uma opção. Os caminhos novos precisam terminar em um dos estereótipos de classe
do OntoUML — os mesmos da lista do `ufo_decision_tree_prompt.md`.

**O que o Save verifica.** Ao salvar, a janela recusa a árvore e lista o que está errado quando:

- o JSON é inválido, ou o `startNode` não existe em `nodes`;
- um nó não tem opções, ou uma opção não tem `answer`;
- uma opção não tem `next` nem `stereotype`, ou tem os dois;
- um `next` aponta para um nó que não existe;
- um `stereotype` não é um estereótipo de classe do OntoUML (a comparação é exata, com o nome de
  máquina em minúsculas do vocabulário: `role`, não `Role` nem `Papel`).

**O que o Save não verifica:**

- **Ciclos.** O percurso é limitado a 15 perguntas e termina sem resultado ao chegar nesse limite.
  Uma árvore mais funda que isso também não chega a um estereótipo.
- **Nós inalcançáveis.** Um nó que nenhuma opção referencia é salvo normalmente e nunca é perguntado.
- **Se o estereótipo faz sentido para a classe.** Depois do percurso, o botão *Apply* ainda checa se
  o estereótipo é permitido pelas generalizações da classe, e bloqueia se não for. Um caminho novo
  não contorna essa regra.

**A avaliação rápida não segue a sua árvore.** O `ufo_decision_tree_prompt.md` descreve as regras
de decisão em texto e **não é gerado a partir do JSON**. Se você mudar só o JSON, a avaliação
detalhada passa a seguir a árvore nova e a rápida continua com o raciocínio antigo, e as duas podem
divergir. Ao alterar a árvore, revise também o `ufo_decision_tree_prompt.md`.

Na interface, a aba *Decision tree* mostra a árvore de forma navegável e deixa a edição do JSON
atrás do botão **JSON (advanced)**.

---

## Placeholders

Um placeholder é um trecho `{nome}` que o plugin troca pelo conteúdo real antes de enviar. A troca é
textual e exata: `{Class}` ou `{ class }` **não** funcionam, e um `{nome}` desconhecido segue como
está para a IA.

| Placeholder | Arquivo | Substituído por |
|---|---|---|
| `{class}` | `documentation_prompt.md` | A classe: nome, estereótipo, atributos e a documentação que ela já tem |
| `{related}` | `documentation_prompt.md` | Classes ligadas a ela, com descrição curta e fontes normativas que já citam |
| `{relatedSources}` | `documentation_prompt.md` | O que as fontes citadas no modelo dizem sobre a classe (resumo de 50 palavras cada) |
| `{search}` | `documentation_prompt.md` | Páginas encontradas na pesquisa web, marcadas `[page retrieved]` ou `[page not retrieved]` |
| `{template}` | `documentation_prompt.md` | O conteúdo do `documentation_template.md` (ou a sua personalização) |
| `{class}` | `ufo_node_prompt.md` | O resumo da classe para classificação: nome, estereótipo atual, documentação, generalizações, atributos e associações |
| `{path}` | `ufo_node_prompt.md` | As perguntas já respondidas, marcando as decididas pelo modelador |
| `{question}` | `ufo_node_prompt.md` | A pergunta do nó atual (campo `question` da árvore) |
| `{options}` | `ufo_node_prompt.md` | As respostas permitidas, separadas por ` \| ` |

Duas observações:

- O `{template}` é a **última** substituição. Um `{placeholder}` escrito *dentro* do template não é
  expandido — as chaves ali são só instruções para a IA, como descrito acima.
- Chaves que não sejam um dos nomes da tabela — como as do JSON no formato de saída — não são
  tocadas; pode usá-las à vontade.

## O que não pode ser mudado sem quebrar o plugin

| Não altere | Motivo |
|---|---|
| Os **nomes dos arquivos** | O código os carrega pelo nome (`LlmPromptResources`) |
| A **pasta** `prompts/` | Idem: é onde o código procura |
| Os **placeholders** de cada arquivo | São a única entrada de dados da IA |
| Os **campos do JSON de saída** dos dois prompts de classificação | O código os lê por nome |
| Os títulos `Descrição curta`, `Descrição completa`, `Fontes Normativas`, `Fontes` | Reconhecidos por `DocumentationTemplate` |
| A estrutura `startNode` / `nodes` / `options` da árvore | É o que `DecisionTree` desserializa |
| Os valores de `stereotype` da árvore | Devem ser nomes de máquina de estereótipos de classe do OntoUML; o Save recusa os demais |

## Testando uma alteração

- **Documentação:** clique direito numa classe → *Document or improve documentation...* e confira o
  resultado. Use a classe de teste do seu modelo e compare antes e depois.
- **Metaclasse:** rode a sugestão sobre uma classe cujo estereótipo você já conhece.
- **Ver o que foi enviado:** os botões **Test** e **Test search** nas configurações da IA abrem o
  *AI Conversation Log* com o prompt exato que foi montado e a resposta que voltou. É a forma mais
  direta de checar se um placeholder foi substituído e se a IA seguiu as instruções.
- Alterou a árvore? O **Save** recusa uma árvore com erro estrutural e diz onde está. Depois, abra
  a aba *Decision tree* no modo visual para conferir os caminhos e rode a avaliação detalhada numa
  classe conhecida.

Como a IA não é determinística, teste mais de uma vez antes de concluir que uma alteração
funcionou.

## Adicionando um novo arquivo

1. Crie o arquivo nesta pasta.
2. Em `LlmPromptResources`, declare a constante do nome, o `getX()` que devolve a personalização ou
   o padrão (`orDefault(...)`) e o `getDefaultX()`.
3. Para permitir edição na interface, acrescente o campo `customX` em `LlmConfigurations` e a aba
   correspondente em `LlmPromptSettingsView`, incluindo o **Restore defaults** e o `overrideOrEmpty`
   do **Save**.
4. Documente-o aqui: o que faz, os placeholders e o que não pode mudar.
