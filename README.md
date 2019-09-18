# Docker Build

```
mvn clean install jib:dockerBuild 
```

# API - dev 
``` 
http :8083
```


# Jaeger
[java-spring-jaeger](https://github.com/opentracing-contrib/java-spring-jaeger/blob/master/README.md)

## Create Jaeger Project
```bash
$ oc new-project jaeger --display-name="Distributed Tracing System" 
```

## Install Jaeger
Install Jaeger on OpenShift to collect the traces
```bash
$ oc process -f https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/all-in-one/jaeger-all-in-one-template.yml | oc create -f -
```

## Create Route
Create a route to access the Jaeger collector
```bash
$ oc expose service jaeger-collector --port=14268 -n jaeger
```

## Get Route Host
Get the route address
```bash
$ oc get route/jaeger-collector -n jaeger -o json | jq '.spec.host'
```

## Update Spring Configuration

```yaml
opentracing:
  jaeger:
    log-spans: true
    http-sender:
      url: http://jaeger-collector-jaeger.apps.c3smonkey.ch/api/traces
```

