import com.google.testing.compile.JavaFileObjects;
import com.mrebhan.paprika.PaprikaProcessor;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class VersionTest {

    @Test public void testSimpleVersion() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
                "package test;\n" +
                "import com.mrebhan.paprika.Table;\n" +
                "import com.mrebhan.paprika.PrimaryKey;\n" +
                "@Table(version = 2)" +
                "public class Test {\n" +
                "@PrimaryKey long id;\n" +
                "int testVal;\n" +
                "}"
        );

        JavaFileObject expectedSqlScripts = JavaFileObjects.forSourceString("test.paprika/PaprikaSqlScripts",
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
                        "    selectQueries.put(\"Test\",\"SELECT Test._id, Test.testVal FROM Test\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public List<String> getCreateScripts() {\n" +
                        "    List<String> statements = new ArrayList<>();\n" +
                        "    statements.add(\"CREATE TABLE Test( _id INTEGER PRIMARY KEY AUTOINCREMENT , testVal INTEGER )\");\n" +
                        "    return statements;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public Map<Integer, List<String>> getUpgradeScripts() {\n" +
                        "    Map<Integer, List<String>> statementsMap = new HashMap<>();\n" +
                        "    List<String> statements2 = new ArrayList<>();\n" +
                        "    statements2.add(\"CREATE TABLE Test( _id INTEGER PRIMARY KEY AUTOINCREMENT , testVal INTEGER )\");\n" +
                        "    statementsMap.put(2, statements2);\n" +
                        "    return statementsMap;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int getVersion() {\n" +
                        "    return 2;\n" +
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
                        "    id = model.id;\n" +
                        "    testVal = model.testVal;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    id = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    testVal = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    return index;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValues getContentValues() {\n" +
                        "    ContentValues contentValues = new ContentValues();\n" +
                        "    contentValues.put(\"testVal\",testVal);\n" +
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

    @Test public void testMultipleVersionsSingleFile() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test",
                "package test;\n" +
                        "import com.mrebhan.paprika.Table;\n" +
                        "import com.mrebhan.paprika.Column;\n" +
                        "import com.mrebhan.paprika.Drop;\n" +
                        "import com.mrebhan.paprika.PrimaryKey;\n" +
                        "import java.lang.String;\n" +
                        "@Table public class Test {\n" +
                        "@PrimaryKey long id;\n" +
                        "int testVal;\n" +
                        "String desc;\n" +
                        "@Column(version = 3) String data;\n" +
                        "@Drop(version = 2) int tempId;\n" +
                        "@Column(version = 2) long otherVal;\n" +
                        "@Column(version = 5) long otherthing;\n" +
                        "}"
        );

        JavaFileObject expectedSqlScripts = JavaFileObjects.forSourceString("test.paprika/PaprikaSqlScripts",
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
                        "    selectQueries.put(\"Test\",\"SELECT Test._id, Test.otherthing, Test.desc, Test.otherVal, Test.data, Test.testVal, Test.tempId FROM Test\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public List<String> getCreateScripts() {\n" +
                        "    List<String> statements = new ArrayList<>();\n" +
                        "    statements.add(\"CREATE TABLE Test( _id INTEGER PRIMARY KEY AUTOINCREMENT , otherthing INTEGER , desc TEXT , otherVal INTEGER , data TEXT , testVal INTEGER , tempId INTEGER )\");\n" +
                        "    return statements;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public Map<Integer, List<String>> getUpgradeScripts() {\n" +
                        "    Map<Integer, List<String>> statementsMap = new HashMap<>();\n" +
                        "    List<String> statements2 = new ArrayList<>();\n" +
                        "    statements2.add(\"ALTER TABLE Test ADD COLUMN otherVal INTEGER \");\n" +
                        "    statementsMap.put(2, statements2);\n" +
                        "    List<String> statements3 = new ArrayList<>();\n" +
                        "    statements3.add(\"ALTER TABLE Test ADD COLUMN data TEXT \");\n" +
                        "    statementsMap.put(3, statements3);\n" +
                        "    List<String> statements5 = new ArrayList<>();\n" +
                        "    statements5.add(\"ALTER TABLE Test ADD COLUMN otherthing INTEGER \");\n" +
                        "    statementsMap.put(5, statements5);\n" +
                        "    return statementsMap;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int getVersion() {\n" +
                        "    return 5;\n" +
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
                        "    id = model.id;\n" +
                        "    otherthing = model.otherthing;\n" +
                        "    desc = model.desc;\n" +
                        "    otherVal = model.otherVal;\n" +
                        "    data = model.data;\n" +
                        "    testVal = model.testVal;\n" +
                        "    tempId = model.tempId;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    id = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    otherthing = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    desc = cursor.getString(index);\n" +
                        "    index++;\n" +
                        "    otherVal = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    data = cursor.getString(index);\n" +
                        "    index++;\n" +
                        "    testVal = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    tempId = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    return index;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValues getContentValues() {\n" +
                        "    ContentValues contentValues = new ContentValues();\n" +
                        "    contentValues.put(\"otherthing\",otherthing);\n" +
                        "    contentValues.put(\"desc\",desc);\n" +
                        "    contentValues.put(\"otherVal\",otherVal);\n" +
                        "    contentValues.put(\"data\",data);\n" +
                        "    contentValues.put(\"testVal\",testVal);\n" +
                        "    contentValues.put(\"tempId\",tempId);\n" +
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
}
