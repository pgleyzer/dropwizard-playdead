# DropWizard PlayDead

## Introduction [![Build Status](https://travis-ci.org/commercehub-oss/dropwizard-playdead.svg?branch=master)](https://travis-ci.org/commercehub-oss/dropwizard-playdead)
The PlayDead DropWizard bundle provides a way to programmatically quiesce nodes in a cluster by faking their deaths.
PlayDead adds an alternate endpoint to `/ping` for a load-balancer's health check. The existing `/ping` endpoint always
responds with `200 "pong"` if the node is alive. PlayDead exposes a new admin endpoint, `/ready` by default, that either returns
`200 "ready"` or `503 "standby"`. The state of the the standby mode can be changed by sending a `PUT` to start playing dead, or a `DELETE` to stop. 
The state is determined by the existence of a state file defined in the config. 

## Maven (etc.) [ ![Download](https://api.bintray.com/packages/commercehub-oss/main/dropwizard-playdead/images/download.png) ](https://bintray.com/commercehub-oss/main/dropwizard-playdead/_latestVersion)

Maven

```xml

   ...
   <repositories>
       <repository>
         <id>jcenter</id>
         <url>http://jcenter.bintray.com</url>
       </repository>
     </repositories>

   ...

   <dependency>
       <groupId>com.commercehub.dropwizard</groupId>
       <artifactId>dropwizard-playdead</artifactId>
       <version>0.0.1</version>
   </dependency>
```
Gradle

```groovy

    ...
    repositories {
        jcenter()
    }

    ...
    dependencies {
        ...
        compile 'com.commercehub.dropwizard:dropwizard-playdead:0.0.1'
        ...
    }

```

## Usages
Using the bundle in DropWizard
```java
public class HelloWorldConfiguration extends Configuration {
    ...
    @NotNull
    private PlayDeadConfiguration playdead = new PlayDeadConfiguration();

    public PlayDeadConfiguration getPlaydead() {
        return playdead;
    }
    public void setPlaydead(PlayDeadConfiguration playdead) {
        this.playdead = playdead;
    }
    ...
}
```
```java
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    ...
    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(new PlayDeadBundle<HelloWorldConfiguration>() {
            @Override
            public String getStateFilePath(HelloWorldConfiguration helloWorldConfiguration) {
                return helloWorldConfiguration.getPlaydead().getstateFilePath();
            }

            @Override
            public String getContextPath(HelloWorldConfiguration helloWorldConfiguration) {
                return helloWorldConfiguration.getPlaydead().getContextPath();
            }
        });
    }
    ...
}
```
## Configuration
The only required config is the stateFilePath which determine the location of the state. Context defaults to `/ready`
```yaml
  ...
  playdead:
    stateFilePath: /tmp/example-playdead
    contextPath: /ready
  ...

```

## Remote Usage of the Service

Once DropWizard is running, you can query the ready state:
```
$ http some-remote-host:8081/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:50:25 GMT

ready
```

Make the node play dead:
```
$ http PUT some-remote-host:8081/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 0
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:52:50 GMT
```

Veryfy it is playing dead:
```
$ http some-remote-host:8081/ready
HTTP/1.1 503 Service Unavailable
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 7
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:54:08 GMT

standby
```

Bring the node back to life:
```
$ http DELETE some-remote-host:8081/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 0
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:54:51 GMT
```

Verify it is alive:
```
$ http some-remote-host:8081/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:55:25 GMT

ready
```

## Host Based Usage of the Service

```
$ http localhost:8081/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 15:00:31 GMT

ready

$ grep stateFilePath dev.yml
dev.yml:  stateFilePath: /tmp/example-playdead

$ touch /tmp/example-playdead

$ http localhost:8081/ready
HTTP/1.1 503 Service Unavailable
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 7
Content-Type: text/plain
Date: Wed, 19 Nov 2014 15:00:32 GMT

standby

$ rm /tmp/example-playdead

$ http localhost:8081/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 15:07:08 GMT

ready
```

## Programmatic Usage
The PlayDead bundle exposes the `PlayDead` class which uses the singleton pattern and has the following public methods:
```java
    PlayDead getInstance()

    instance.setLockFilePath(String path)

    boolean instance.isPlayingDead()

    instance.startPlayingDead() 

    instance.stopPlayingDead()
```
Sample usage:
```java
    if (PlayDead.getInstance().isPlayingDead()) {
      //stop participating in Quartz cluster
    }
```

# License
This library is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

(c) All rights reserved Commerce Technologies, Inc.