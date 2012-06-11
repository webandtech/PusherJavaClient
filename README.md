PusherJavaClient
================

Java class for the Pusher REST API (http://www.pusher.com)

This is based on work done by SScheuermann on a GAE version (https://github.com/SScheuermann/gae-java-libpusher).

How to use this
---------------
Make sure you include org.apache.httpcomponents httpclient and httpcore as well as apache commons-codec in your project. If you're using Maven, you can add something like this to your pom.xml:

	<dependency>
	    <groupId>org.apache.httpcomponents</groupId>
	    <artifactId>httpclient</artifactId>
	    <version>4.1.2</version>
	</dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcore</artifactId>
            <version>4.1.2</version>
        </dependency>
        <dependency>
	    <groupId>commons-codec</groupId>
	    <artifactId>commons-codec</artifactId>
	    <version>1.6</version>
	</dependency>

Replace the Pusher specific constants in PusherJavaClient.java with your own (APP_ID, API_KEY, API_SECRET) 

Call the static method "triggerPush" and pass channel name, event name and the message body (JSON encoded data) as parameters:

	PusherJavaClient.triggerPush("myChannel", "myEvent", "{}");

It works.
