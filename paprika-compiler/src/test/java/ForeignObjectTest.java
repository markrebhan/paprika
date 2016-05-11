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
                        "@Table public class Test2 {" +
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
                        "import java.util.LinkedHashMap;\n" +
                        "import java.util.List;\n" +
                        "import java.util.Map;\n" +
                        "\n" +
                        "public final class PaprikaSqlScripts implements SqlScripts {\n" +
                        "  private static final HashMap<String, String> selectQueries;\n" +
                        "\n" +
                        "  static {\n" +
                        "    selectQueries = new HashMap();\n" +
                        "    selectQueries.put(\"Test\",\"SELECT Test._id, Test.thingThree, Test.thingTwo, Test.thingOne FROM Test\");\n" +
                        "    selectQueries.put(\"Test2\",\"SELECT Test2._id, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test2.thingTwo, Test2.thingOne FROM Test2 LEFT JOIN Test ON Test2.test = Test._id\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public List<String> getCreateScripts() {\n" +
                        "    List<String> statements = new ArrayList<>();\n" +
                        "    statements.add(\"CREATE TABLE Test2( _id INTEGER PRIMARY KEY AUTOINCREMENT , test INTEGER , thingTwo INTEGER , thingOne INTEGER )\");\n" +
                        "    statements.add(\"CREATE TABLE Test( _id INTEGER PRIMARY KEY AUTOINCREMENT , thingThree BLOB , thingTwo TEXT , thingOne INTEGER )\");\n" +
                        "    return statements;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public Map<Integer, List<String>> getUpgradeScripts() {\n" +
                        "    Map<Integer, List<String>> statementsMap = new LinkedHashMap<>();\n" +
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test$$PaprikaMapper extends Test implements PaprikaMapper<Test> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test model) {\n" +
                        "    thingThree = model.thingThree;\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    _id = cursor.getLong(index);\n" +
                        "    index++;\n" +
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test2$$PaprikaMapper extends Test2 implements PaprikaMapper<Test2> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test2 model) {\n" +
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
                        "    _id = cursor.getLong(index);\n" +
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

    @Test public void testWithManyRelations() {

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
                        "import java.lang.Integer;" +
                        "@Table public class Test2 {" +
                        "Integer thingOne;" +
                        "Integer thingTwo;" +
                        "}"
        );

        final JavaFileObject source3 = JavaFileObjects.forSourceString("test.Test3",
                "package test;" +
                        "import com.mrebhan.paprika.Table;" +
                        "import java.lang.Integer;" +
                        "@Table public class Test3 {" +
                        "Integer thingOne;" +
                        "long thingTwo;" +
                        "}"
        );

        final JavaFileObject source4 = JavaFileObjects.forSourceString("test.Test4",
                "package test;" +
                        "import com.mrebhan.paprika.Table;" +
                        "import com.mrebhan.paprika.ForeignObject;" +
                        "import java.lang.Integer;" +
                        "@Table public class Test4 {" +
                        "@ForeignObject Test test;" +
                        "@ForeignObject Test testTwo;" +
                        "@ForeignObject Test2 test2;" +
                        "Integer thingOne;" +
                        "Integer thingTwo;" +
                        "}"
                );

        final JavaFileObject source5 = JavaFileObjects.forSourceString("test.Test5",
                "package test;" +
                        "import com.mrebhan.paprika.Table;" +
                        "import com.mrebhan.paprika.ForeignObject;" +
                        "import java.lang.Integer;" +
                        "@Table public class Test5 {" +
                        "@ForeignObject Test test;" +
                        "@ForeignObject Test4 test4;" +
                        "Integer thingOne;" +
                        "Integer thingTwo;" +
                        "}"
        );


        final JavaFileObject source6 = JavaFileObjects.forSourceString("test.Test6",
                "package test;" +
                        "import com.mrebhan.paprika.Table;" +
                        "import com.mrebhan.paprika.ForeignObject;" +
                        "import java.lang.Integer;" +
                        "@Table public class Test6 {" +
                        "@ForeignObject Test3 test3;" +
                        "@ForeignObject Test5 test5;" +
                        "}"
        );
        ArrayList<JavaFileObject> javaFileObjects = new ArrayList<JavaFileObject>() {{
            add(source);
            add(source2);
            add(source3);
            add(source4);
            add(source5);
            add(source6);
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
                        "import java.util.LinkedHashMap;\n" +
                        "import java.util.List;\n" +
                        "import java.util.Map;\n" +
                        "\n" +
                        "public final class PaprikaSqlScripts implements SqlScripts {\n" +
                        "  private static final HashMap<String, String> selectQueries;\n" +
                        "\n" +
                        "  static {\n" +
                        "    selectQueries = new HashMap();\n" +
                        "    selectQueries.put(\"Test\",\"SELECT Test._id, Test.thingThree, Test.thingTwo, Test.thingOne FROM Test\");\n" +
                        "    selectQueries.put(\"Test2\",\"SELECT Test2._id, Test2.thingTwo, Test2.thingOne FROM Test2\");\n" +
                        "    selectQueries.put(\"Test3\",\"SELECT Test3._id, Test3.thingTwo, Test3.thingOne FROM Test3\");\n" +
                        "    selectQueries.put(\"Test4\",\"SELECT Test4._id, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test4.thingTwo, Test4.thingOne, Test2._id, Test2.thingTwo, Test2.thingOne FROM Test4 LEFT JOIN Test ON Test4.test = Test._id LEFT JOIN Test ON Test4.testTwo = Test._id LEFT JOIN Test2 ON Test4.test2 = Test2._id\");\n" +
                        "    selectQueries.put(\"Test5\",\"SELECT Test5._id, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test5.thingTwo, Test5.thingOne, Test4._id, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test4.thingTwo, Test4.thingOne, Test2._id, Test2.thingTwo, Test2.thingOne FROM Test5 LEFT JOIN Test ON Test5.test = Test._id LEFT JOIN Test ON Test4.test = Test._id LEFT JOIN Test ON Test4.testTwo = Test._id LEFT JOIN Test4 ON Test5.test4 = Test4._id LEFT JOIN Test2 ON Test4.test2 = Test2._id\");\n" +
                        "    selectQueries.put(\"Test6\",\"SELECT Test6._id, Test5._id, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test5.thingTwo, Test5.thingOne, Test4._id, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test._id, Test.thingThree, Test.thingTwo, Test.thingOne, Test4.thingTwo, Test4.thingOne, Test2._id, Test2.thingTwo, Test2.thingOne, Test3._id, Test3.thingTwo, Test3.thingOne FROM Test6 LEFT JOIN Test3 ON Test6.test3 = Test3._id LEFT JOIN Test ON Test5.test = Test._id LEFT JOIN Test ON Test4.test = Test._id LEFT JOIN Test ON Test4.testTwo = Test._id LEFT JOIN Test4 ON Test5.test4 = Test4._id LEFT JOIN Test5 ON Test6.test5 = Test5._id LEFT JOIN Test2 ON Test4.test2 = Test2._id\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public List<String> getCreateScripts() {\n" +
                        "    List<String> statements = new ArrayList<>();\n" +
                        "    statements.add(\"CREATE TABLE Test6( _id INTEGER PRIMARY KEY AUTOINCREMENT , test5 INTEGER , test3 INTEGER )\");\n" +
                        "    statements.add(\"CREATE TABLE Test4( _id INTEGER PRIMARY KEY AUTOINCREMENT , testTwo INTEGER , test INTEGER , thingTwo INTEGER , thingOne INTEGER , test2 INTEGER )\");\n" +
                        "    statements.add(\"CREATE TABLE Test2( _id INTEGER PRIMARY KEY AUTOINCREMENT , thingTwo INTEGER , thingOne INTEGER )\");\n" +
                        "    statements.add(\"CREATE TABLE Test3( _id INTEGER PRIMARY KEY AUTOINCREMENT , thingTwo INTEGER , thingOne INTEGER )\");\n" +
                        "    statements.add(\"CREATE TABLE Test5( _id INTEGER PRIMARY KEY AUTOINCREMENT , test INTEGER , thingTwo INTEGER , thingOne INTEGER , test4 INTEGER )\");\n" +
                        "    statements.add(\"CREATE TABLE Test( _id INTEGER PRIMARY KEY AUTOINCREMENT , thingThree BLOB , thingTwo TEXT , thingOne INTEGER )\");\n" +
                        "    return statements;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public Map<Integer, List<String>> getUpgradeScripts() {\n" +
                        "    Map<Integer, List<String>> statementsMap = new LinkedHashMap<>();\n" +
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test$$PaprikaMapper extends Test implements PaprikaMapper<Test> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test model) {\n" +
                        "    thingThree = model.thingThree;\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    _id = cursor.getLong(index);\n" +
                        "    index++;\n" +
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test2$$PaprikaMapper extends Test2 implements PaprikaMapper<Test2> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test2 model) {\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    _id = cursor.getLong(index);\n" +
                        "    index++;\n" +
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
                        "    return builder.build();\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ArrayList<String> getExternalMappings() {\n" +
                        "    ArrayList<String> externalMappings = new ArrayList<String>();\n" +
                        "    return externalMappings;\n" +
                        "  }\n" +
                        "}");

        JavaFileObject expectedMapper3 = JavaFileObjects.forSourceString("test/Test3$$PaprikaMapper",
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test3$$PaprikaMapper extends Test3 implements PaprikaMapper<Test3> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test3 model) {\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    _id = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    thingTwo = cursor.getLong(index);\n" +
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
                        "    ContentValuesWrapper rootWrapper = builder.setRootNode(getContentValues(), \"Test3\", getExternalMappings());\n" +
                        "    return builder.build();\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ArrayList<String> getExternalMappings() {\n" +
                        "    ArrayList<String> externalMappings = new ArrayList<String>();\n" +
                        "    return externalMappings;\n" +
                        "  }\n" +
                        "}");

        JavaFileObject expectedMapper4 = JavaFileObjects.forSourceString("test/Test4$$PaprikaMapper",
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test4$$PaprikaMapper extends Test4 implements PaprikaMapper<Test4> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test4 model) {\n" +
                        "    if (model.testTwo != null) {\n" +
                        "      testTwo = new Test$$PaprikaMapper();\n" +
                        "      ((Test$$PaprikaMapper)testTwo).setupModel(model.testTwo);\n" +
                        "    }\n" +
                        "    if (model.test != null) {\n" +
                        "      test = new Test$$PaprikaMapper();\n" +
                        "      ((Test$$PaprikaMapper)test).setupModel(model.test);\n" +
                        "    }\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "    if (model.test2 != null) {\n" +
                        "      test2 = new Test2$$PaprikaMapper();\n" +
                        "      ((Test2$$PaprikaMapper)test2).setupModel(model.test2);\n" +
                        "    }\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    _id = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    testTwo = new Test$$PaprikaMapper();\n" +
                        "    index = ((Test$$PaprikaMapper)testTwo).setupModel(cursor,index);\n" +
                        "    test = new Test$$PaprikaMapper();\n" +
                        "    index = ((Test$$PaprikaMapper)test).setupModel(cursor,index);\n" +
                        "    thingTwo = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    thingOne = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    test2 = new Test2$$PaprikaMapper();\n" +
                        "    index = ((Test2$$PaprikaMapper)test2).setupModel(cursor,index);\n" +
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
                        "    ContentValuesWrapper rootWrapper = builder.setRootNode(getContentValues(), \"Test4\", getExternalMappings());\n" +
                        "    if (testTwo != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test$$PaprikaMapper) testTwo).getContentValuesTree());\n" +
                        "    }\n" +
                        "    if (test != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test$$PaprikaMapper) test).getContentValuesTree());\n" +
                        "    }\n" +
                        "    if (test2 != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test2$$PaprikaMapper) test2).getContentValuesTree());\n" +
                        "    }\n" +
                        "    return builder.build();\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ArrayList<String> getExternalMappings() {\n" +
                        "    ArrayList<String> externalMappings = new ArrayList<String>();\n" +
                        "    externalMappings.add(\"testTwo\");\n" +
                        "    externalMappings.add(\"test\");\n" +
                        "    externalMappings.add(\"test2\");\n" +
                        "    return externalMappings;\n" +
                        "  }\n" +
                        "}");

        JavaFileObject expectedMapper5 = JavaFileObjects.forSourceString("test/Test5$$PaprikaMapper",
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test5$$PaprikaMapper extends Test5 implements PaprikaMapper<Test5> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test5 model) {\n" +
                        "    if (model.test != null) {\n" +
                        "      test = new Test$$PaprikaMapper();\n" +
                        "      ((Test$$PaprikaMapper)test).setupModel(model.test);\n" +
                        "    }\n" +
                        "    thingTwo = model.thingTwo;\n" +
                        "    thingOne = model.thingOne;\n" +
                        "    if (model.test4 != null) {\n" +
                        "      test4 = new Test4$$PaprikaMapper();\n" +
                        "      ((Test4$$PaprikaMapper)test4).setupModel(model.test4);\n" +
                        "    }\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    _id = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    test = new Test$$PaprikaMapper();\n" +
                        "    index = ((Test$$PaprikaMapper)test).setupModel(cursor,index);\n" +
                        "    thingTwo = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    thingOne = cursor.getInt(index);\n" +
                        "    index++;\n" +
                        "    test4 = new Test4$$PaprikaMapper();\n" +
                        "    index = ((Test4$$PaprikaMapper)test4).setupModel(cursor,index);\n" +
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
                        "    ContentValuesWrapper rootWrapper = builder.setRootNode(getContentValues(), \"Test5\", getExternalMappings());\n" +
                        "    if (test != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test$$PaprikaMapper) test).getContentValuesTree());\n" +
                        "    }\n" +
                        "    if (test4 != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test4$$PaprikaMapper) test4).getContentValuesTree());\n" +
                        "    }\n" +
                        "    return builder.build();\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ArrayList<String> getExternalMappings() {\n" +
                        "    ArrayList<String> externalMappings = new ArrayList<String>();\n" +
                        "    externalMappings.add(\"test\");\n" +
                        "    externalMappings.add(\"test4\");\n" +
                        "    return externalMappings;\n" +
                        "  }\n" +
                        "}\n");

        JavaFileObject expectedMapper6 = JavaFileObjects.forSourceString("test/Test6$$PaprikaMapper",
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
                        "import java.util.ArrayList;\n" +
                        "\n" +
                        "public final class Test6$$PaprikaMapper extends Test6 implements PaprikaMapper<Test6> {\n" +
                        "  public long _id;\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public long getId() {\n" +
                        "    return _id;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public void setupModel(Test6 model) {\n" +
                        "    if (model.test5 != null) {\n" +
                        "      test5 = new Test5$$PaprikaMapper();\n" +
                        "      ((Test5$$PaprikaMapper)test5).setupModel(model.test5);\n" +
                        "    }\n" +
                        "    if (model.test3 != null) {\n" +
                        "      test3 = new Test3$$PaprikaMapper();\n" +
                        "      ((Test3$$PaprikaMapper)test3).setupModel(model.test3);\n" +
                        "    }\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public int setupModel(Cursor cursor, int index) {\n" +
                        "    _id = cursor.getLong(index);\n" +
                        "    index++;\n" +
                        "    test5 = new Test5$$PaprikaMapper();\n" +
                        "    index = ((Test5$$PaprikaMapper)test5).setupModel(cursor,index);\n" +
                        "    test3 = new Test3$$PaprikaMapper();\n" +
                        "    index = ((Test3$$PaprikaMapper)test3).setupModel(cursor,index);\n" +
                        "    return index;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValues getContentValues() {\n" +
                        "    ContentValues contentValues = new ContentValues();\n" +
                        "    return contentValues;\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ContentValuesTree getContentValuesTree() {\n" +
                        "    ContentValuesTree.Builder builder = new ContentValuesTree.Builder();\n" +
                        "    ContentValuesWrapper rootWrapper = builder.setRootNode(getContentValues(), \"Test6\", getExternalMappings());\n" +
                        "    if (test5 != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test5$$PaprikaMapper) test5).getContentValuesTree());\n" +
                        "    }\n" +
                        "    if (test3 != null) {\n" +
                        "      builder.addChild(rootWrapper, ((Test3$$PaprikaMapper) test3).getContentValuesTree());\n" +
                        "    }\n" +
                        "    return builder.build();\n" +
                        "  }\n" +
                        "\n" +
                        "  @Override\n" +
                        "  public ArrayList<String> getExternalMappings() {\n" +
                        "    ArrayList<String> externalMappings = new ArrayList<String>();\n" +
                        "    externalMappings.add(\"test5\");\n" +
                        "    externalMappings.add(\"test3\");\n" +
                        "    return externalMappings;\n" +
                        "  }\n" +
                        "}");

        assertAbout(javaSources()).that(javaFileObjects)
                .processedWith(new PaprikaProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedScripts, expectedMapper1, expectedMapper2, expectedMapper3,
                        expectedMapper4, expectedMapper5, expectedMapper6);
    }
}
