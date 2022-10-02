<h1 align="center">
  <img alt="Eagle logo" src="assets/real-time.png" width="224px"/><br/>
  Config middleware base websocket and socket
</h1>

<p align="center">
The base library including publish websocket via url
<br/>
</p>


## ⚡️ Quick start

Build application:

```bash
/bin/bash gradlew jar
```
> output jar: <b><i>ngx-blobs-wss-1.0.0.jar</i></b>

## :rocket: Functions

#### Tunnel properties

:package: add file [`application-tunnels.yml`](src/main/resources/application-tunnels.yml)

```yml
spring:
  tunnel-socket-starter:
    enabled: true # enable websocket
    tunnels:
      - enabled: true # enable this url will be published
        endpoint-short-url: /publish/event # url websocket
        endpoint-description: tunning on socket v1
      - enabled: true
        endpoint-short-url: /publish/action
        endpoint-description: tunning on socket v2
```