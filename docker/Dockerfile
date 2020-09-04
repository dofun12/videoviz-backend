# Adicionando os scripts SQL para serem executados na criação do banco
FROM mysql:5.7.12
COPY ./db/ /docker-entrypoint-initdb.d/
#RUN mysql -u ${MYSQL_USER} -p${MYSQL_ROOT_PASSWORD} -D lba < /docker-entrypoint-initdb.d/database.sql