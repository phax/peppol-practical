#
# Copyright (C) Philip Helger
#
# All rights reserved.
#

# Stage 1

FROM ubuntu:latest as build

# Install wget and unzip
RUN apt-get update \
  && apt-get install -y unzip \
  && rm -rf /var/lib/apt/lists/*

COPY target/*.war app.war
RUN unzip app.war -d /app

# Stage 2

FROM tomcat:10.1-jdk17

ENV CATALINS_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/urandom"

WORKDIR $CATALINA_HOME/webapps

COPY --from=build /app $CATALINA_HOME/webapps/ROOT
