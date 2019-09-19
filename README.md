# Docker Build

```
mvn clean install jib:dockerBuild
```
```
docker run --name order-service -p 8080:8080 -d marzelwidmer/order-service:latest
```

# API 
``` 
http :8080/api/v1/scientists/random
```
# API - Dev
``` 
http :8080/api/v1/scientists/random
```

# Jager

[Jeager UI ](http://localhost:16686/search)

## Initial run
```
docker run -d --name jaeger \                                                                                                                                
  -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.8
```

## Stop Jaeger
```
docker stop jaeger 
```
## Start Jaeger
```
docker start jaeger 
```

## Hit the Service
``` 
for x in (seq 20); http ":8080/api/v1/orders/random"; end
```


# ConfigMap
Apply `ConfigMap`
```bash
$ oc apply -f deployments/configmap.yaml
```

Create `ConfigMap` from file.
```bash
$ oc create configmap order-service --from-file=src/main/resources/application.yaml
```

Get all `ConfigMaps`
```bash
$ oc get configmaps
```

Get `ConfigMap` as `yaml`
```bash
$ oc get configmap order-service -o yaml
```

Describe `ConfigMap`
```bash
$ oc describe configmap order-service
```

Delete `ConfigMap`
```bash
$ oc delete configmap order-service
```

