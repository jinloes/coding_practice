# CDC connector setup

Run these requests after the Docker Compose services are healthy.

## 1. Create the Elasticsearch users index

```bash
curl --fail --request PUT 'http://localhost:9200/users' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "settings": {
      "number_of_shards": 1
    },
    "mappings": {
      "properties": {
        "id": {
          "type": "integer"
        },
        "email": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        }
      }
    }
  }'
```

## 2. Create the PostgreSQL source connector

```bash
curl --fail --request POST 'http://localhost:8083/connectors' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "name": "users-connector",
    "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "database.hostname": "postgres",
      "database.port": "5432",
      "database.user": "postgres",
      "database.password": "postgres",
      "database.dbname": "test_db",
      "database.server.name": "dbserver1",
      "table.include.list": "public.users",
      "plugin.name": "pgoutput",
      "transforms": "route",
      "transforms.route.type": "org.apache.kafka.connect.transforms.RegexRouter",
      "transforms.route.regex": "([^.]+)\\.([^.]+)\\.([^.]+)",
      "transforms.route.replacement": "$3"
    }
  }'
```

## 3. Create the Elasticsearch sink connector

```bash
curl --fail --request POST 'http://localhost:8083/connectors' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "name": "es-sink-users",
    "config": {
      "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
      "tasks.max": "1",
      "topics": "users",
      "connection.url": "http://elasticsearch:9200",
      "transforms": "unwrap,key",
      "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
      "transforms.unwrap.drop.tombstones": "false",
      "transforms.unwrap.drop.deletes": "false",
      "transforms.key.type": "org.apache.kafka.connect.transforms.ExtractField$Key",
      "transforms.key.field": "id",
      "key.ignore": "false",
      "type.name": "_doc",
      "behavior.on.null.values": "delete"
    }
  }'
```

## 4. Inspect the connectors

```bash
curl --fail 'http://localhost:8083/connectors'
curl --fail 'http://localhost:8083/connectors/users-connector/status'
curl --fail 'http://localhost:8083/connectors/es-sink-users/status'
```

## Cleanup

```bash
curl --fail --request DELETE \
  'http://localhost:8083/connectors/es-sink-users'
curl --fail --request DELETE \
  'http://localhost:8083/connectors/users-connector'
```
