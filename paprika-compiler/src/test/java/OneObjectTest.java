import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import com.mrebhan.paprika.PaprikaProcessor;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class OneObjectTest {

    @Test public void testSimpleObject() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                "package test;",
                "import java.lang.String;",
                "import com.mrebhan.paprika.Table;",
                "@Table public class Test {",
                "int thingone;",
                "String thingTwo;",
                "long thingThree;",
                "}"
        ));

        JavaFileObject expectedSqlScripts = JavaFileObjects.forSourceString("test.paprika/PaprikaSqlScripts",
                "// Code Generated for Paprika. Do not modify! \n" +
                "package com.mrebhan.paprika; \n" +
                "import com.mrebhan.paprika.internal.SqlScripts;\n" +
                        "import java.lang.Integer;\n" +
                        "import java.lang.Override;\n" +
                        "import java.lang.String;\n" +
                        "import java.util.ArrayList;\n" +
                        "import java.util.HashMap;\n" +
                        "import java.util.List;\n" +
                        "import java.util.Map;\n" +
                        "\n" +
                        "public final class PaprikaSqlScripts implements SqlScripts {\n" +
                        "  private static final HashMap<String, String> selectQueries;\n" +
                        "\n" +
                        "  static {\n" +
                        "    selectQueries = new HashMap();\n" +
                        "    selectQueries.put(\"Test\",\"SELECT Test.thingone, Test.thingThree, Test.thingTwo FROM Test\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public List<String> getCreateScripts() {\n" +
                        "    List<String> statements = new ArrayList<>();\n" +
                        "    statements.add(\"CREATE TABLE Test( thingone INTEGER , thingThree INTEGER , thingTwo TEXT )\");\n" +
                        "    return statements;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public Map<Integer, List<String>> getUpgradeScripts() {\n" +
                        "    Map<Integer, List<String>> statementsMap = new HashMap<>();\n" +
                        "    return statementsMap;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int getVersion() {\n" +
                        "    return 1;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public String getSelectQuery(String className) {\n" +
                        "    return selectQueries.get(className);\n" +
                        "  }\n" +
                        "}"
        );

        JavaFileObject expectedMapper = JavaFileObjects.forSourceString("test/Test$$PaprikaMapper",
                "// Code Generated for Paprika. Do not modify!\n" +
                "package test;\n" +
                "import android.content.ContentValues;\n" +
                        "import android.database.Cursor;\n" +
                        "import com.mrebhan.paprika.internal.ContentValuesTree;\n" +
                        "import com.mrebhan.paprika.internal.ContentValuesWrapper;\n" +
                        "import com.mrebhan.paprika.internal.PaprikaMapper;\n" +
                        "import java.lang.Override;\n" +
                        "import java.lang.String;\n" +
                        "import java.lang.SuppressWarnings;\n" +
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "@SuppressWarnings(\"ParcelCreator\")\n" +
                        "public final class Test$$PaprikaMapper extends Test implements PaprikaMapper<Test> {\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test model) {\n" +
                        "    thingone = model.thingone;\n" +
                        "    thingThree = model.thingThree;\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    thingone = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    thingThree = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    thingTwo = cursor.getString(index);\n" +
                        "    index++;\n" +
                        "    return index;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValues getContentValues() {\n" +
                        "    ContentValues contentValues = new ContentValues();\n" +
                        "    contentValues.put(\"thingone\",thingone);\n" +
                        "    contentValues.put(\"thingThree\",thingThree);\n" +
                        "    contentValues.put(\"thingTwo\",thingTwo);\n" +
                        "    return contentValues;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValuesTree getContentValuesTree() {\n" +
                        "    ContentValuesTree.Builder builder = new ContentValuesTree.Builder();\n" +
                        "    ContentValuesWrapper rootWrapper = builder.setRootNode(getContentValues(), \"Test\", getExternalMappings());\n" +
                        "    return builder.build();\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ArrayList<String> getExternalMappings() {\n" +
                        "    ArrayList<String> externalMappings = new ArrayList<String>();\n" +
                        "    return externalMappings;\n" +
                        "  }\n" +
                        "}"

        );

        assertAbout(javaSource()).that(source)
                .processedWith(new PaprikaProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSqlScripts, expectedMapper);
    }


    @Test public void testUnsupportedDataType() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test2", Joiner.on('\n').join(
                "package test;",
                "import java.util.List;",
                "import com.mrebhan.paprika.Table;",
                "@Table public class Test2 {",
                "int thingone;",
                "List thingTwo;",
                "}"
        ));

        assertAbout(javaSource()).that(source)
                .processedWith(new PaprikaProcessor())
                .failsToCompile()
                .withErrorContaining("This type is currently not supported: DECLARED")
                .in(source).onLine(6);

    }



}
