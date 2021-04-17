package it.unibz.inf.ontouml.vp.model;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ModularizationServiceResultDeserializationTest {

  static ObjectMapper mapper;

  @BeforeAll
  static void setUp() throws IOException {
    mapper = new ObjectMapper();
  }

  @Test
  void shouldDeserializeEmptyResult() throws IOException {
    final String emptyResult1 = "{\n" + "  \"result\": null,\n" + "  \"issues\": []\n" + "}";
    final String emptyResult2 = "{\n" + "  \"result\": null,\n" + "  \"issues\": null\n" + "}";
    final String emptyResult3 = "{\n" + "  \"result\": null\n" + "}";
    final List<String> emptyResults = Arrays.asList(emptyResult1, emptyResult2, emptyResult3);

    for (String emptyResult : emptyResults) {
      ModularizationServiceResult serviceResult =
          mapper.readValue(emptyResult, ModularizationServiceResult.class);

      assertThat(serviceResult).isNotEqualTo(null);
      assertThat(serviceResult.getResult()).isNull();
      assertThat(serviceResult.getIssues()).isEmpty();
    }
  }

  @Test
  void shouldDeserializeModularizedProject() throws IOException {
    final String json =
        "{\n"
            + "  \"result\": {\n"
            + "    \"model\": {\n"
            + "      \"contents\": [\n"
            + "        {\n"
            + "          \"stereotype\": \"kind\",\n"
            + "          \"restrictedTo\": [\n"
            + "            \"functional-complex\"\n"
            + "          ],\n"
            + "          \"properties\": null,\n"
            + "          \"literals\": null,\n"
            + "          \"isAbstract\": false,\n"
            + "          \"isDerived\": false,\n"
            + "          \"isExtensional\": false,\n"
            + "          \"isPowertype\": false,\n"
            + "          \"order\": \"1\",\n"
            + "          \"propertyAssignments\": {},\n"
            + "          \"type\": \"Class\",\n"
            + "          \"id\": \"mNir2.6GAqACHQWX\",\n"
            + "          \"name\": \"Class\",\n"
            + "          \"description\": null\n"
            + "        },\n"
            + "        {\n"
            + "          \"stereotype\": \"subkind\",\n"
            + "          \"restrictedTo\": [\n"
            + "            \"functional-complex\"\n"
            + "          ],\n"
            + "          \"properties\": null,\n"
            + "          \"literals\": null,\n"
            + "          \"isAbstract\": false,\n"
            + "          \"isDerived\": false,\n"
            + "          \"isExtensional\": false,\n"
            + "          \"isPowertype\": false,\n"
            + "          \"order\": \"1\",\n"
            + "          \"propertyAssignments\": {},\n"
            + "          \"type\": \"Class\",\n"
            + "          \"id\": \"Y1yr2.6GAqACHQWv\",\n"
            + "          \"name\": \"Class2\",\n"
            + "          \"description\": null\n"
            + "        },\n"
            + "        {\n"
            + "          \"stereotype\": \"relator\",\n"
            + "          \"restrictedTo\": [\n"
            + "            \"relator\"\n"
            + "          ],\n"
            + "          \"properties\": null,\n"
            + "          \"literals\": null,\n"
            + "          \"isAbstract\": false,\n"
            + "          \"isDerived\": false,\n"
            + "          \"isExtensional\": false,\n"
            + "          \"isPowertype\": false,\n"
            + "          \"order\": \"1\",\n"
            + "          \"propertyAssignments\": {},\n"
            + "          \"type\": \"Class\",\n"
            + "          \"id\": \"P3Kr2.6GAqACHQXK\",\n"
            + "          \"name\": \"Class3\",\n"
            + "          \"description\": null\n"
            + "        },\n"
            + "        {\n"
            + "          \"general\": {\n"
            + "            \"type\": \"Class\",\n"
            + "            \"id\": \"mNir2.6GAqACHQWX\"\n"
            + "          },\n"
            + "          \"specific\": {\n"
            + "            \"type\": \"Class\",\n"
            + "            \"id\": \"Y1yr2.6GAqACHQWv\"\n"
            + "          },\n"
            + "          \"propertyAssignments\": {},\n"
            + "          \"type\": \"Generalization\",\n"
            + "          \"id\": \"H1yr2.6GAqACHQW1\",\n"
            + "          \"name\": null,\n"
            + "          \"description\": null\n"
            + "        },\n"
            + "        {\n"
            + "          \"stereotype\": \"mediation\",\n"
            + "          \"properties\": [\n"
            + "            {\n"
            + "              \"stereotype\": null,\n"
            + "              \"cardinality\": \"0..*\",\n"
            + "              \"propertyType\": {\n"
            + "                \"type\": \"Class\",\n"
            + "                \"id\": \"mNir2.6GAqACHQWX\"\n"
            + "              },\n"
            + "              \"subsettedProperties\": null,\n"
            + "              \"redefinedProperties\": null,\n"
            + "              \"aggregationKind\": \"NONE\",\n"
            + "              \"isDerived\": false,\n"
            + "              \"isOrdered\": false,\n"
            + "              \"isReadOnly\": false,\n"
            + "              \"propertyAssignments\": {},\n"
            + "              \"type\": \"Property\",\n"
            + "              \"id\": \"AvKr2.6GAqACHQXS\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null\n"
            + "            },\n"
            + "            {\n"
            + "              \"stereotype\": null,\n"
            + "              \"cardinality\": \"1\",\n"
            + "              \"propertyType\": {\n"
            + "                \"type\": \"Class\",\n"
            + "                \"id\": \"P3Kr2.6GAqACHQXK\"\n"
            + "              },\n"
            + "              \"subsettedProperties\": null,\n"
            + "              \"redefinedProperties\": null,\n"
            + "              \"aggregationKind\": \"NONE\",\n"
            + "              \"isDerived\": false,\n"
            + "              \"isOrdered\": false,\n"
            + "              \"isReadOnly\": true,\n"
            + "              \"propertyAssignments\": {},\n"
            + "              \"type\": \"Property\",\n"
            + "              \"id\": \"QvKr2.6GAqACHQXV\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null\n"
            + "            }\n"
            + "          ],\n"
            + "          \"isAbstract\": false,\n"
            + "          \"isDerived\": false,\n"
            + "          \"propertyAssignments\": {},\n"
            + "          \"type\": \"Relation\",\n"
            + "          \"id\": \"7PKr2.6GAqACHQXQ\",\n"
            + "          \"name\": null,\n"
            + "          \"description\": null\n"
            + "        }\n"
            + "      ],\n"
            + "      \"propertyAssignments\": {},\n"
            + "      \"type\": \"Package\",\n"
            + "      \"id\": \"Pz4r2.6GAqACHQBO_root\",\n"
            + "      \"name\": \"untitled\",\n"
            + "      \"description\": null\n"
            + "    },\n"
            + "    \"diagrams\": [\n"
            + "      {\n"
            + "        \"owner\": {\n"
            + "          \"type\": \"Package\",\n"
            + "          \"id\": \"Pz4r2.6GAqACHQBO_root\"\n"
            + "        },\n"
            + "        \"contents\": [\n"
            + "          {\n"
            + "            \"modelElement\": {\n"
            + "              \"type\": \"Class\",\n"
            + "              \"id\": \"Y1yr2.6GAqACHQWv\"\n"
            + "            },\n"
            + "            \"shape\": {\n"
            + "              \"width\": 88,\n"
            + "              \"height\": 40,\n"
            + "              \"x\": 198,\n"
            + "              \"y\": 231,\n"
            + "              \"type\": \"Rectangle\",\n"
            + "              \"id\": \"Y1yr2.6GAqACHQWu_shape\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null\n"
            + "            },\n"
            + "            \"type\": \"ClassView\",\n"
            + "            \"id\": \"Y1yr2.6GAqACHQWu\",\n"
            + "            \"name\": null,\n"
            + "            \"description\": null\n"
            + "          },\n"
            + "          {\n"
            + "            \"source\": {\n"
            + "              \"type\": \"ClassView\",\n"
            + "              \"id\": \"CNir2.6GAqACHQWW\"\n"
            + "            },\n"
            + "            \"target\": {\n"
            + "              \"type\": \"ClassView\",\n"
            + "              \"id\": \"Y1yr2.6GAqACHQWu\"\n"
            + "            },\n"
            + "            \"modelElement\": {\n"
            + "              \"type\": \"Generalization\",\n"
            + "              \"id\": \"H1yr2.6GAqACHQW1\"\n"
            + "            },\n"
            + "            \"shape\": {\n"
            + "              \"type\": \"Path\",\n"
            + "              \"id\": \"0Nyr2.6GAqACHQW2_path\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null,\n"
            + "              \"points\": [\n"
            + "                {\n"
            + "                  \"x\": 238,\n"
            + "                  \"y\": 179\n"
            + "                },\n"
            + "                {\n"
            + "                  \"x\": 238,\n"
            + "                  \"y\": 230\n"
            + "                }\n"
            + "              ]\n"
            + "            },\n"
            + "            \"type\": \"GeneralizationView\",\n"
            + "            \"id\": \"0Nyr2.6GAqACHQW2\",\n"
            + "            \"name\": null,\n"
            + "            \"description\": null\n"
            + "          },\n"
            + "          {\n"
            + "            \"modelElement\": {\n"
            + "              \"type\": \"Class\",\n"
            + "              \"id\": \"mNir2.6GAqACHQWX\"\n"
            + "            },\n"
            + "            \"shape\": {\n"
            + "              \"width\": 80,\n"
            + "              \"height\": 40,\n"
            + "              \"x\": 198,\n"
            + "              \"y\": 138,\n"
            + "              \"type\": \"Rectangle\",\n"
            + "              \"id\": \"CNir2.6GAqACHQWW_shape\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null\n"
            + "            },\n"
            + "            \"type\": \"ClassView\",\n"
            + "            \"id\": \"CNir2.6GAqACHQWW\",\n"
            + "            \"name\": null,\n"
            + "            \"description\": null\n"
            + "          },\n"
            + "          {\n"
            + "            \"modelElement\": {\n"
            + "              \"type\": \"Class\",\n"
            + "              \"id\": \"P3Kr2.6GAqACHQXK\"\n"
            + "            },\n"
            + "            \"shape\": {\n"
            + "              \"width\": 81,\n"
            + "              \"height\": 40,\n"
            + "              \"x\": 467,\n"
            + "              \"y\": 138,\n"
            + "              \"type\": \"Rectangle\",\n"
            + "              \"id\": \"33Kr2.6GAqACHQXJ_shape\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null\n"
            + "            },\n"
            + "            \"type\": \"ClassView\",\n"
            + "            \"id\": \"33Kr2.6GAqACHQXJ\",\n"
            + "            \"name\": null,\n"
            + "            \"description\": null\n"
            + "          },\n"
            + "          {\n"
            + "            \"source\": {\n"
            + "              \"type\": \"ClassView\",\n"
            + "              \"id\": \"33Kr2.6GAqACHQXJ\"\n"
            + "            },\n"
            + "            \"target\": {\n"
            + "              \"type\": \"ClassView\",\n"
            + "              \"id\": \"CNir2.6GAqACHQWW\"\n"
            + "            },\n"
            + "            \"modelElement\": {\n"
            + "              \"type\": \"Relation\",\n"
            + "              \"id\": \"7PKr2.6GAqACHQXQ\"\n"
            + "            },\n"
            + "            \"shape\": {\n"
            + "              \"type\": \"Path\",\n"
            + "              \"id\": \"a_.r2.6GAqACHQZv_path\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null,\n"
            + "              \"points\": [\n"
            + "                {\n"
            + "                  \"x\": 466,\n"
            + "                  \"y\": 158\n"
            + "                },\n"
            + "                {\n"
            + "                  \"x\": 279,\n"
            + "                  \"y\": 158\n"
            + "                }\n"
            + "              ]\n"
            + "            },\n"
            + "            \"type\": \"RelationView\",\n"
            + "            \"id\": \"a_.r2.6GAqACHQZv\",\n"
            + "            \"name\": null,\n"
            + "            \"description\": null\n"
            + "          }\n"
            + "        ],\n"
            + "        \"type\": \"Diagram\",\n"
            + "        \"id\": \"mtCr2.6GAqACHQWL\",\n"
            + "        \"name\": \"Class Diagram1\",\n"
            + "        \"description\": null\n"
            + "      },\n"
            + "      {\n"
            + "        \"owner\": {\n"
            + "          \"type\": \"Package\",\n"
            + "          \"id\": \"Pz4r2.6GAqACHQBO_root\"\n"
            + "        },\n"
            + "        \"contents\": [\n"
            + "          {\n"
            + "            \"modelElement\": {\n"
            + "              \"type\": \"Class\",\n"
            + "              \"id\": \"P3Kr2.6GAqACHQXK\"\n"
            + "            },\n"
            + "            \"shape\": {\n"
            + "              \"width\": 100,\n"
            + "              \"height\": 50,\n"
            + "              \"x\": 40,\n"
            + "              \"y\": 40,\n"
            + "              \"type\": \"Rectangle\",\n"
            + "              \"id\": \"3yjrith5k0vkm3qglf0\",\n"
            + "              \"name\": null,\n"
            + "              \"description\": null\n"
            + "            },\n"
            + "            \"type\": \"ClassView\",\n"
            + "            \"id\": \"3yjrith5k0vkm3qglez\",\n"
            + "            \"name\": null,\n"
            + "            \"description\": null\n"
            + "          }\n"
            + "        ],\n"
            + "        \"type\": \"Diagram\",\n"
            + "        \"id\": \"3yjrith5k0vkm3qgley\",\n"
            + "        \"name\": \"Cluster of Class3\",\n"
            + "        \"description\": null\n"
            + "      }\n"
            + "    ],\n"
            + "    \"type\": \"Project\",\n"
            + "    \"id\": \"Pz4r2.6GAqACHQBO\",\n"
            + "    \"name\": \"untitled\",\n"
            + "    \"description\": null\n"
            + "  },\n"
            + "  \"issues\": null\n"
            + "}";
    final ModularizationServiceResult serviceResult =
        mapper.readValue(json, ModularizationServiceResult.class);

    assertThat(serviceResult).isNotNull();
    assertThat(serviceResult.getResult()).isInstanceOf(Project.class);
    assertThat(serviceResult.getIssues()).isEmpty();
  }
}
