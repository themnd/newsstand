How to integrate into your project
==================================

1. clone the repo and put it into your plugins folder.
2. modify the project pom.xml to include it

	```
	<modules>
		...
		<module>plugins/newsstand</module>
	</modules>


	<dependencies>
		...
		<dependency>
	      <groupId>com.atex.plugins</groupId>
	      <artifactId>newsstand</artifactId>
	      <version>1.1</version>
	    </dependency>
	    ...
	    <dependency>
	      <groupId>com.atex.plugins</groupId>
	      <artifactId>newsstand</artifactId>
	      <version>1.1</version>
	      <classifier>contentdata</classifier>
	    </dependency>
	    ...
	</dependencies>
	```

3. modify the project pom.xml in order to specify the cache directory (this is something you should do also in the tomcat configuration)

	```
	    <systemProperty>
	      <name>newsstand.cacheDir</name>
	      <value>${polopoly.work-dir}/newsstand</value>
	    </systemProperty>
	```

4. in you common content module specify the plugins configuration:

	```
	id:.
	major:appconfig
	inputtemplate:com.atex.plugins.newsstand.Configuration
	securityparent:p.siteengine.Configuration.d
	component:host:value:http\://vp-pro12.xorovo.it
	component:version:value:vpweb25
	component:catalogs:value:ASAR,ASBS,ASVI
	component:wsname:value:ViewerplusWS
	component:platform:value:web
	component:type:value:production
	component:device_id:value:polopoly
	```

5. There is a `Newsstand` element that you should add on the page.

