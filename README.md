Paprika
=======

`Paprika` is a lightweight Android ORM library that creates all necessary SQL mappings during compile time avoiding the bloat needed for run time reflection based ORM's. It is more flexible than other code generated ORM's because the developer is in full control of how the objects should look and behave allowing the data objects to be reused for other data model tasks such as serialization.

### Example

Here's a simple example of initializing the library, creating a data object with `Paprika` annotations, and running basic operations against the library:

```java
import android.app.Application;
import com.mrebhan.paprika.Paprika;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Paprika.init(this, "MyDatabase");
	}
}
```

```java
@Table
public class DataModel {
	@PrimaryKey
	long id;

	String name;
	String desciption;
	int userId;
	byte[] image;
}
```

```java
import com.mrebhan.paprika.Paprika;

public class PaprikaMethodsDemo() {

	public void demo() {
		DataModel dataModel = new DataModel();
		dataModel.name = "John Doe";
		dataModel.description = "John likes Paprika";
		dataModel.userId = 1234;

		// add entry into database
		Paprika.create(dataModel);
		
		// retrieve list of dataModels;
		List<DataModel> dataModels = Paprika.getList(DataModel.class);

		// update dataModel in database
		dataModel = dataModels.get(0);
		dataModel.name = "Jane Doe"
		dataModel.image = new byte[] {};
		Paprika.update(dataModel, dataModel.id);
	
		// delete entry from database
		Paprika.delete(DataModel.class, dataModel.id);
	}
}
```
### Annotations

`Table` is used to specify if the data class should be added to the database.

`Column` is used to specify a field member that should be added as a column to the table. Columns are automatically inferred as non static, non private and table versioned so they are generally not required. They are generally used for adding a column with a different version. Members must not be private in order for the annotation processor to properly access the member. Static members and any methods will never be added as columns.

`Primary Key` is used to specify the member that will be mapped to the _id column in SQLite. this value must be of type long and the value will be ignored when `Paprika.create(...)` is called. This value is useful when attempting to fetch, update or delete single entities. This value must be specified on all top level data objects.

`ForeignColumn` is used when wanted to create a 1:1 mapping with another `Table`. The child object must follow the same rules and conventions as the parent object. The library will automatically recursively populate `ForeignColumn` data when fetching.

```java
@Table 
public class DataObjectParent {
	@PrimaryKey
	long id;

	String name;

	float measuredValue;

	@ForeignColumn
	DataObjectChild child;
}
```
```java
@Table
public class DataObjectChild {
	String code;
	int codeValue;
	String description;
}
```

`Ignore` is used for ignoring `Table` members that should not be inserted as columns into the database (non private and/or non static members).

`Default` is for specifying a default column value when inserting a row to the database. The value in the annotation is always a `String` regardless of member type.

`NonNull` is used for specifying that the member mapped to the column can't be null.

`Unique` is used for specifying that the member mapped must contain a unique value in the database.

`Drop` is used for dropping a column on a database migration. This can occur on a `Table` or `Column`. A version must be specified.

### Database Versioning

Versioning is automatically handled for you by `Paprika`. Versioning is supported for `Column`, `Drop`, `ForeignObject`, and `Table`. These work by specifiying the appropriate version number given an annotation such as:

```java
@Table(version = 2)
```

Versioning works by generating migration scripts that are run in the correct order on initalization. Therefore, the same rules when creating migration scripts directly into the SqliteDbHelper apply for the annotated object (versioning must be acsending order, already created columns/tables can't be inserted again, etc).

### Annotation Example

```java
@Table(version = 1)
public class ThingOne {

	@PrimaryKey
	long id;
	
	@NonNull
	String name;
	
	String description;
	
	@Column(version = 2)
	String description2;

	@ForeignColumn(version = 3);
	ThingTwo thingtwo;
}

@Table(version = 3)
public class ThingTwo {

	@NonNull
	@Unique
	@Default("000-000-0000")
	String phone;
	
	@NonNull
	@Default("Unknown")
	String adress;
	
	@NonNull
	@Default("Unknown")
	String city;

	@NonNull
	@Default("00000")
	long postalCode;
}

@Table(version = 4)
public class ThingThree {
	
	@PrimaryKey
	long id;

	@Ignore
	int someId;

	@Drop(version = 5)
	String something;

	String somethingElse;
	
	byte[] data;

	Double doubleThing;
	
	Integer integerThing;

}

```
