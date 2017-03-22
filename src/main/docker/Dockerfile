# ========================================================================
# SchemaCrawler
# http://www.schemacrawler.com
# Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

FROM openjdk

ARG SCHEMACRAWLER_VERSION=14.15.01

LABEL "us.fatehi.schemacrawler.product-version"="SchemaCrawler ${SCHEMACRAWLER_VERSION}" \
      "us.fatehi.schemacrawler.website"="http://www.schemacrawler.com" \
      "us.fatehi.schemacrawler.docker-hub"="https://hub.docker.com/r/sualeh/schemacrawler"

# Install GraphViz
RUN \
    apt-get update \
 && apt-get install -y graphviz \
 && rm -rf /var/lib/apt/lists/*
 
# Map directories
VOLUME /tmp

ADD schemacrawler-webapp-${SCHEMACRAWLER_VERSION}.01.jar schemacrawler-webapp.jar

RUN sh -c 'touch /schemacrawler-webapp.jar'

ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /schemacrawler-webapp.jar" ]

MAINTAINER Sualeh Fatehi <sualeh@hotmail.com>
