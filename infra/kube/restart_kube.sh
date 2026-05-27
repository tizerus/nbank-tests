#!/bin/bash

# Запуск локального Kubernetes-кластера с использованием Docker как драйвера
minikube start --driver=docker

# Создание ConfigMap для конфигурации Selenoid
kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json

# Установка Helm чарта с релизом nbank
helm install nbank ./nbank-chart

# Получение списка сервисов в кластере
kubectl get svc
# output:
#NAME          TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
#backend       NodePort    10.102.85.214    <none>        4111:31934/TCP   21h
#frontend      ClusterIP   10.98.3.200      <none>        80/TCP           21h
#kubernetes    ClusterIP   10.96.0.1        <none>        443/TCP          21h
#selenoid      NodePort    10.108.181.173   <none>        4444:31702/TCP   21h
#selenoid-ui   NodePort    10.109.53.213    <none>        8080:30412/TCP   21h

# Получение списка подов в кластере
kubectl get pods
# output:
#NAME                           READY   STATUS    RESTARTS        AGE
#backend-6fb6d77977-8qb8x       1/1     Running   2 (6m42s ago)   21h
#frontend-58454c5fc5-grdpw      1/1     Running   4 (6m6s ago)    21h
#selenoid-598c7f7699-lx4fg      1/1     Running   2 (6m43s ago)   21h
#selenoid-ui-6744f65fcd-km8ql   1/1     Running   2 (6m33s ago)   21h

# Просмотр логов конкретного сервиса (например, backend)
kubectl logs deployment/backend
# output:
#  .   ____          _            __ _ _
# /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
#( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
# \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
#  '  |____| .__|_| |_|_| |_\__, | / / / /
# =========|_|==============|___/=/_/_/_/
# :: Spring Boot ::                (v3.2.5)
#
#{"timestamp":"2026-05-27T07:22:29.164782935Z","logger_name":"me.nobugs.bank.BankApplication","thread_name":"main","level":"INFO","message":"Starting BankApplication v0.0.1-SNAPSHOT using Java 17.0.16 with PID 1 (/app/app.jar started by root in /app)"}
#{"timestamp":"2026-05-27T07:22:29.176053746Z","logger_name":"me.nobugs.bank.BankApplication","thread_name":"main","level":"INFO","message":"No active profile set, falling back to 1 default profile: \"default\""}
#{"timestamp":"2026-05-27T07:22:34.183806005Z","logger_name":"org.springframework.boot.web.embedded.tomcat.TomcatWebServer","thread_name":"main","level":"INFO","message":"Tomcat initialized with port 4111 (http)"}
#{"timestamp":"2026-05-27T07:22:34.192308023Z","logger_name":"org.apache.catalina.core.StandardService","thread_name":"main","level":"INFO","message":"Starting service [Tomcat]"}
#{"timestamp":"2026-05-27T07:22:34.192579232Z","logger_name":"org.apache.catalina.core.StandardEngine","thread_name":"main","level":"INFO","message":"Starting Servlet engine: [Apache Tomcat/10.1.20]"}
#{"timestamp":"2026-05-27T07:22:34.355893577Z","logger_name":"org.apache.catalina.core.ContainerBase.[Tomcat].[localhost].[/]","thread_name":"main","level":"INFO","message":"Initializing Spring embedded WebApplicationContext"}
#{"timestamp":"2026-05-27T07:22:34.357531821Z","logger_name":"org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext","thread_name":"main","level":"INFO","message":"Root WebApplicationContext: initialization completed in 5078 ms"}
#🧪 MeterRegistry class = io.micrometer.prometheus.PrometheusMeterRegistry
#{"timestamp":"2026-05-27T07:22:41.255007669Z","logger_name":"org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver","thread_name":"main","level":"INFO","message":"Exposing 3 endpoint(s) beneath base path '/actuator'"}
#{"timestamp":"2026-05-27T07:22:41.857862564Z","logger_name":"org.springframework.security.web.DefaultSecurityFilterChain","thread_name":"main","level":"INFO","message":"Will secure any request with [org.springframework.security.web.session.DisableEncodeUrlFilter@15e08615, org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@19dac2d6, org.springframework.security.web.context.SecurityContextHolderFilter@21c75084, org.springframework.security.web.header.HeaderWriterFilter@2ef041bb, org.springframework.web.filter.CorsFilter@7e2bc2f4, org.springframework.security.web.authentication.logout.LogoutFilter@41e8d917, org.springframework.security.web.authentication.www.BasicAuthenticationFilter@1c72189f, org.springframework.security.web.savedrequest.RequestCacheAwareFilter@75527e36, org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@76596288, org.springframework.security.web.authentication.AnonymousAuthenticationFilter@67eeb310, org.springframework.security.web.access.ExceptionTranslationFilter@1426370c, org.springframework.security.web.access.intercept.AuthorizationFilter@322b09da]"}

# Проброс портов на локальную машину
kubectl port-forward svc/frontend 3000:80
kubectl port-forward svc/backend 4111:4111
kubectl port-forward svc/selenoid 4444:4444
kubectl port-forward svc/selenoid-ui 8080:8080

kubectl scale deployment backend --replicas=3
kubectl get pods
# NAME                           READY   STATUS    RESTARTS      AGE
#backend-6fb6d77977-8qb8x       1/1     Running   2 (11m ago)   21h
#backend-6fb6d77977-lwsxj       1/1     Running   0             10s
#backend-6fb6d77977-xkpx2       1/1     Running   0             10s
#frontend-58454c5fc5-grdpw      1/1     Running   4 (10m ago)   21h
#selenoid-598c7f7699-lx4fg      1/1     Running   2 (11m ago)   21h
#selenoid-ui-6744f65fcd-km8ql   1/1     Running   2 (11m ago)   21h

kubectl describe deployment backend
#Name:                   backend
 #Namespace:              default
 #CreationTimestamp:      Tue, 26 May 2026 13:17:18 +0300
 #Labels:                 app.kubernetes.io/managed-by=Helm
 #Annotations:            deployment.kubernetes.io/revision: 1
 #                        meta.helm.sh/release-name: nbank
 #                        meta.helm.sh/release-namespace: default
 #Selector:               app=backend
 #Replicas:               3 desired | 3 updated | 3 total | 3 available | 0 unavailable
 #StrategyType:           RollingUpdate
 #MinReadySeconds:        0
 #RollingUpdateStrategy:  25% max unavailable, 25% max surge
 #Pod Template:
 #  Labels:       app=backend
 #  Annotations:  co.elastic.logs/enabled: true
 #                co.elastic.logs/json.add_error_key: true
 #                co.elastic.logs/json.keys_under_root: true
 #                co.elastic.logs/module: springboot
 #  Containers:
 #   backend:
 #    Image:        nobugsme/nbank:with_validation_fix
 #    Port:         4111/TCP
 #    Host Port:    0/TCP
 #    Environment:  <none>
 #    Mounts:       <none>
 #  Volumes:        <none>
 #Conditions:
 #  Type           Status  Reason
 #  ----           ------  ------
 #  Progressing    True    NewReplicaSetAvailable
 #  Available      True    MinimumReplicasAvailable
 #OldReplicaSets:  <none>
 #NewReplicaSet:   backend-6fb6d77977 (3/3 replicas created)
 #Events:
 #  Type    Reason             Age   From                   Message
 #  ----    ------             ----  ----                   -------
 #  Normal  ScalingReplicaSet  91s   deployment-controller  Scaled up replica set backend-6fb6d77977 to 3 from 1

kubectl get deployments
#NAME          READY   UP-TO-DATE   AVAILABLE   AGE
#backend       3/3     3            3           21h
#frontend      1/1     1            1           21h
#selenoid      1/1     1            1           21h
#selenoid-ui   1/1     1            1           21h


kubectl scale deployment frontend --replicas=2
# Посмотрите все поды
kubectl get pods
#NAME                           READY   STATUS    RESTARTS      AGE
 #backend-6fb6d77977-8qb8x       1/1     Running   2 (16m ago)   21h
 #backend-6fb6d77977-lwsxj       1/1     Running   0             5m23s
 #backend-6fb6d77977-xkpx2       1/1     Running   0             5m23s
 #frontend-58454c5fc5-g8jqm      1/1     Running   0             13s
 #frontend-58454c5fc5-grdpw      1/1     Running   4 (16m ago)   21h
 #selenoid-598c7f7699-lx4fg      1/1     Running   2 (16m ago)   21h
 #selenoid-ui-6744f65fcd-km8ql   1/1     Running   2 (16m ago)   21h

# Информация о деплойменте
kubectl get deployments
#NAME          READY   UP-TO-DATE   AVAILABLE   AGE
 #backend       3/3     3            3           21h
 #frontend      2/2     2            2           21h
 #selenoid      1/1     1            1           21h
 #selenoid-ui   1/1     1            1           21h

# Описание процесса масштабирования
kubectl describe deployment frontend
#Name:                   frontend
 #Namespace:              default
 #CreationTimestamp:      Tue, 26 May 2026 13:17:18 +0300
 #Labels:                 app.kubernetes.io/managed-by=Helm
 #Annotations:            deployment.kubernetes.io/revision: 1
 #                        meta.helm.sh/release-name: nbank
 #                        meta.helm.sh/release-namespace: default
 #Selector:               app=frontend
 #Replicas:               2 desired | 2 updated | 2 total | 2 available | 0 unavailable
 #StrategyType:           RollingUpdate
 #MinReadySeconds:        0
 #RollingUpdateStrategy:  25% max unavailable, 25% max surge
 #Pod Template:
 #  Labels:  app=frontend
 #  Containers:
 #   frontend:
 #    Image:        nobugsme/nbank-ui:with_nginx
 #    Port:         80/TCP
 #    Host Port:    0/TCP
 #    Environment:  <none>
 #    Mounts:       <none>
 #  Volumes:        <none>
 #Conditions:
 #  Type           Status  Reason
 #  ----           ------  ------
 #  Progressing    True    NewReplicaSetAvailable
 #  Available      True    MinimumReplicasAvailable
 #OldReplicaSets:  <none>
 #NewReplicaSet:   frontend-58454c5fc5 (2/2 replicas created)
 #Events:
 #  Type    Reason             Age   From                   Message
 #  ----    ------             ----  ----                   -------
 #  Normal  ScalingReplicaSet  57s   deployment-controller  Scaled up replica set frontend-58454c5fc5 to 2 from 1


# Просмотр событий
#kubectl get events --sort-by='.lastTimestamp'
wait