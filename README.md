We use https://jitpack.io/#ttpro1995/SimpleNeo4j/0.7

Gradle  
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```
	dependencies {
	        implementation 'com.github.ttpro1995:SimpleNeo4j:0.7'
	}
```

Maven
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories> 
```

```
	<dependency>
	    <groupId>com.github.ttpro1995</groupId>
	    <artifactId>SimpleNeo4j</artifactId>
	    <version>0.7</version>
	</dependency>
```


Simple usage : see junit test


```
package me.thaithien.simpleneo4j;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Neo4jManagerTest {

    @Test
    public void query() throws Exception {
        Neo4jManager.initialDriver("localhost", "7687", "neo4j", "meowmeow");

        DateFormat df = new SimpleDateFormat("MMddyyyyHHmmss");

        Date today = Calendar.getInstance().getTime();

        String reportDate = df.format(today);


        System.out.println("Report Date: " + reportDate);
        String q = String.format("CREATE (t:TEST{name:'%s', testOk:'OK'}) return t", reportDate);
        System.out.println("test query");
        Neo4jManager.query(q).forEach(x->{
            String date = x.get("t.name");
            System.out.println(date);
            String testOk = x.get("t.testOk");
            assert (testOk.equals("OK"));
            assert (date.equals(reportDate));
        });
    }

}
```