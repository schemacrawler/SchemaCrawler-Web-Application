# ========================================================================
# SchemaCrawler
# http://www.schemacrawler.com
# Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
# All rights reserved.
# ------------------------------------------------------------------------
#
# SchemaCrawler is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#
# SchemaCrawler and the accompanying materials are made available under
# the terms of the Eclipse Public License v1.0, GNU General Public License
# v3 or GNU Lesser General Public License v3.
#
# You may elect to redistribute this code under any of these licenses.
#
# The Eclipse Public License is available at:
# http://www.eclipse.org/legal/epl-v10.html
#
# The GNU General Public License v3 and the GNU Lesser General Public
# License v3 are available at:
# http://www.gnu.org/licenses/
#
# ========================================================================

FROM openjdk:8-jdk-alpine

ARG SCHEMACRAWLER_VERSION=16.2.5
ARG SCHEMACRAWLER_WEBAPP_VERSION=16.2.5.1

LABEL \
  "us.fatehi.schemacrawler.product-version"="SchemaCrawler ${SCHEMACRAWLER_VERSION}" \
  "us.fatehi.schemacrawler.website"="http://www.schemacrawler.com" \
  "us.fatehi.schemacrawler.docker-hub"="https://hub.docker.com/r/schemacrawler/schemacrawler"

# Install GraphViz
RUN \
  apk add --update --no-cache \
  bash \
  bash-completion \
  graphviz \
  ttf-freefont

# Run the image as a non-root user
RUN \
    addgroup -g 1000 -S schcrwlr \
 && adduser -u 1000 -S schcrwlr -G schcrwlr
USER schcrwlr
WORKDIR /home/schcrwlr

# Copy SchemaCrawler Web Application files for the current user
COPY \
  --chown=schcrwlr:schcrwlr \
  ./target/schemacrawler-webapp-${SCHEMACRAWLER_WEBAPP_VERSION}.jar \
  schemacrawler-webapp.jar

# Run the web-application.  CMD is required to run on Heroku
# $JAVA_OPTS and $PORT are set by Heroku
CMD java $JAVA_OPTS -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar schemacrawler-webapp.jar

MAINTAINER Sualeh Fatehi <sualeh@hotmail.com>
