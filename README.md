# DropWizard PlayDead

## Introduction [![Build Status](https://travis-ci.org/commercehub-oss/dropwizard-playdead.svg?branch=master)](https://travis-ci.org/commercehub-oss/dropwizard-playdead)
The PlayDead DropWizard bundle provides a way to programmatically quiesce nodes in a cluster by faking their deaths.
PlayDead adds an alternate endpoint to `/ping` for a load-balancer's health check. The existing `/ping` endpoint always
responds with `200 "pong"` if the node is alive. PlayDead exposes a new endpoint, `/ready` by default, that either returns
`200 "ready"` or `503 "standby"`. The state of the the standby mode can be changed by sending a `PUT` to start playing dead, or a `DELETE` to stop. 
The state is determined by the existence of a state file defined in the config. 

### But... why?
Rolling deployments, access, and automation. Our goal is to be able to able to deploy our software in production in a clustered environment without downtime. Currently, this is possible with access to our load-balancers---we simply disable the node that we are upgrading to stop its flow of traffic.  This method works, but it is not perfect:

- Our deployment team needs access to the load-balancers or needs to coordinate with the networking team.
- Given access to the load-balancers, automating the quiescing of a node poses an additional set of challenges.
- It stop requests from being sent to the server, but does not stop the server from participating in the cluster in other ways (read: clustered Quartz).

The goal of this bundle is the trick the load-balancer into thinking the node is dead. This moves the control of node removal out of the networking realm and into that of the deployment teams. It also simplifies the process, making automation trivial.

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
       <version>0.0.3</version>
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
        compile 'com.commercehub.dropwizard:dropwizard-playdead:0.0.3'
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
            public PlayDeadConfiguration getPlayDeadConfiguration(HelloWorldConfiguration helloWorldConfiguration) {
                return helloWorldConfiguration.getPlaydead();
            }
        });
    }
    ...
}
```
## Configuration
```yaml
  ...
  playdead:
    stateFilePath: /tmp/example-playdead
    contextPath: /ready
    environment: application
    accessKey: someNotSoSecretKey
    showMessageOnError: false
  ...

```

#### stateFilePath

  required, no default

  The path to the file that determines if the DropWizard instance is in `standby` state. If they file exists, the server is in `standby` state. Conversely, if it does not exists, the server is in `ready` state.

#### contextPath

  default is `/ready`

  The path segment of the URL where the PlayDead will respond. The default is `/ready`.

#### environment
  
  default is `application`

  The environment in which the PlayDead Servlet is added. DropWizard can be configured to listen on two interfaces, one for your application and one for administrative needs such as healthchecks. There are two possible values, `application` and `admin` with the former being the default.

#### accessKey
  
  not required, no default

  Provides an extremely thin layer of security for changing the state of PlayDead via HTTP. When using `PUT` and `DELETE`, the access should be provided as the query portion of the URL. E.g. `http://example.com/ready?someNotSoSecret`

#### showMessageOnError
 
  default is `false`

  If an exception occurs while manipulating the state file, the stacktrace will be sent in the response body. Should only be used for debugging. The default is `false` and, naturally, the other possible value is `true`.


## Remote Usage of the Service

Once DropWizard is running, you can query the ready state:
```
$ http some-remote-host:8080/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:50:25 GMT

ready
```

Make the node play dead:
```
$ http PUT some-remote-host:8080/ready?someNotSoSecretKey
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 0
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:52:50 GMT
```

Veryfy it is playing dead:
```
$ http some-remote-host:8080/ready
HTTP/1.1 503 Service Unavailable
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 7
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:54:08 GMT

standby
```

Bring the node back to life:
```
$ http DELETE some-remote-host:8080/ready?someNotSoSecretKey
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 0
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:54:51 GMT
```

Verify it is alive:
```
$ http some-remote-host:8080/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 14:55:25 GMT

ready
```

## Host Based Usage of the Service

```
$ http localhost:8080/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 15:00:31 GMT

ready

$ grep stateFilePath dev.yml
dev.yml:  stateFilePath: /tmp/example-playdead

$ touch /tmp/example-playdead

$ http localhost:8080/ready
HTTP/1.1 503 Service Unavailable
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 7
Content-Type: text/plain
Date: Wed, 19 Nov 2014 15:00:32 GMT

standby

$ rm /tmp/example-playdead

$ http localhost:8080/ready
HTTP/1.1 200 OK
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 5
Content-Type: text/plain
Date: Wed, 19 Nov 2014 15:07:08 GMT

ready
```

## Programmatic Usage
The PlayDead bundle exposes the `PlayDead` class which has the following public methods:
```java
    PlayDead(PlayDeadConfiguration config)

    boolean isPlayingDead()

    void startPlayingDead()

    void stopPlayingDead()
```
Sample usage:
```java
    PlayDead playDead = new PlayDead(playDeadConfiguration)
    if (playDead.isPlayingDead()) {
      //stop participating in Quartz cluster
    }
```

## Security
With the `accessKey` configuration parameter aside, security is beyond this scope of this bundle. That is, a security solution should be implemented at the DropWizard application level. It may behoove one to look into SSL and basic authentication, or two-way SSL.

# License
This library is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

(c) All rights reserved Commerce Technologies, Inc.