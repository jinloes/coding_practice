FROM postgres:latest

COPY ./postgres.conf /home/postgres.conf
COPY ./init-db.sh /docker-entrypoint-initdb.d/init-db.sh
COPY ./setup.sql /home/setup.sql
COPY ./bootstrap_data.sql /home/bootstrap_data.sql