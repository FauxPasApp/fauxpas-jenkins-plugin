#!/bin/bash

set -o nounset
set -o errexit

THIS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DIST_DIR="$THIS_DIR/dist"

# Build
#
mvn package

# Read plugin version from pom.xml
#
PLUGIN_VERSION=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' "$THIS_DIR/pom.xml")

# Copy .hpi package into dist/ with the correct version number
#
mkdir -p "$DIST_DIR"
cp "$THIS_DIR/target/fauxpasapp-plugin.hpi" "$DIST_DIR/fauxpasapp-plugin-$PLUGIN_VERSION.hpi"
