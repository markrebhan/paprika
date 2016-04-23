import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import com.mrebhan.paprika.PaprikaProcessor;

import org.junit.Test;

import java.util.ArrayList;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class ForeignObjectTest {

    @Test public void testTwoObjectsWithRelation() {

        final JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                "package test;",
                "import java.lang.String;",
                "import com.mrebhan.paprika.Table;",
                "@Table public class Test {",
                "int thingOne;",
                "String thingTwo;",
                "byte[] thingThree;",
                "}"
        ));

        final JavaFileObject source2 = JavaFileObjects.forSourceString("test.Test2",
                        "package test;" +
                        "import com.mrebhan.paprika.Table;" +
                        "import com.mrebhan.paprika.ForeignObject;" +
                        "import java.lang.Integer;" +
                        "import com.mrebhan.paprika.PrimaryKey;" +
                        "@Table public class Test2 {" +
                        "@PrimaryKey long id;" +
                        "Integer thingOne;" +
                        "Integer thingTwo;" +
                        "@ForeignObject Test test;" +
                        "}"
                );

        ArrayList<JavaFileObject> javaFileObjects = new ArrayList<JavaFileObject>() {{
            add(source);
            add(source2);
        }};

        JavaFileObject expectedScripts = JavaFileObjects.forSourceString("com/mrebhan/paprika/PaprikaSqlScripts",
                "// Code Generated for Paprika. Do not modify!\n" +
                        "package com.mrebhan.paprika;\n" +
                        "\n" +
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
                        "    selectQueries.put(\"Test\",\"SELECT Test.thingThree, Test.thingTwo, Test.thingOne FROM Test\");\n" +
                        "    selectQueries.put(\"Test2\",\"SELECT Test2._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test2.thingTwo, Test2.thingOne FROM Test2 LEFT JOIN Test ON Test2.test = Test._id\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public List<String> getCreateScripts() {\n" +
                        "    List<String> statements = new ArrayList<>();\n" +
                        "    statements.add(\"CREATE TABLE Test2( _id INTEGER PRIMARY KEY AUTOINCREMENT , test INTEGER , thingTwo INTEGER , thingOne INTEGER )\");\n" +
                        "    statements.add(\"CREATE TABLE Test( thingThree BLOB , thingTwo TEXT , thingOne INTEGER )\");\n" +
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
                        "}");

        JavaFileObject expectedMapper1 = JavaFileObjects.forSourceString("test/Test$$PaprikaMapper",
                "// Code Generated for Paprika. Do not modify!\n" +
                        "package test;\n" +
                        "\n" +
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
                        "    thingThree = model.thingThree;\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    thingThree = cursor.getBlob(index);\n" +
                        "    index++;\n" +
                        "    thingTwo = cursor.getString(index);\n" +
                        "    index++;\n" +
                        "    thingOne = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    return index;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValues getContentValues() {\n" +
                        "    ContentValues contentValues = new ContentValues();\n" +
                        "    contentValues.put(\"thingThree\",thingThree);\n" +
                        "    contentValues.put(\"thingTwo\",thingTwo);\n" +
                        "    contentValues.put(\"thingOne\",thingOne);\n" +
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
                        "}");

        JavaFileObject expectedMapper2 = JavaFileObjects.forSourceString("test/Test2$$PaprikaMapper",
                "// Code Generated for Paprika. Do not modify!\n" +
                        "package test;\n" +
                        "\n" +
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
                        "public final class Test2$$PaprikaMapper extends Test2 implements PaprikaMapper<Test2> {\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test2 model) {\n" +
                        "    id = model.id;\n" +
                        "    if (model.test != null) {\n" +
                        "      test = new Test$$PaprikaMapper();\n" +
                        "      ((Test$$PaprikaMapper)test).setupModel(model.test);\n" +
                        "    }\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    id = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    test = new Test$$PaprikaMapper();\n" +
                        "    index = ((Test$$PaprikaMapper)test).setupModel(cursor,index);\n" +
                        "    thingTwo = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    thingOne = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    return index;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValues getContentValues() {\n" +
                        "    ContentValues contentValues = new ContentValues();\n" +
                        "    contentValues.put(\"thingTwo\",thingTwo);\n" +
                        "    contentValues.put(\"thingOne\",thingOne);\n" +
                        "    return contentValues;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValuesTree getContentValuesTree() {\n" +
                        "    ContentValuesTree.Builder builder = new ContentValuesTree.Builder();\n" +
                        "    ContentValuesWrapper rootWrapper = builder.setRootNode(getContentValues(), \"Test2\", getExternalMappings());\n" +
                        "    if (test != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test$$PaprikaMapper) test).getContentValuesTree());\n" +
                        "    }\n" +
                        "    return builder.build();\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ArrayList<String> getExternalMappings() {\n" +
                        "    ArrayList<String> externalMappings = new ArrayList<String>();\n" +
                        "    externalMappings.add(\"test\");\n" +
                        "    return externalMappings;\n" +
                        "  }\n" +
                        "}");

        assertAbout(javaSources()).that(javaFileObjects)
                .processedWith(new PaprikaProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedScripts, expectedMapper1, expectedMapper2);

    }
}
