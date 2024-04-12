#!/bin/bash -eu

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

if [ $# -ne 1 ]; then
    echo_warn "Usage: $0 <node_version>"
    exit 1
fi

NODE_VERSION=$1

# Install node and npm using the frontend-maven-plugin

./mvnw com.github.eirslett:frontend-maven-plugin:install-node-and-npm
./mvnw com.github.eirslett:frontend-maven-plugin:npm

# If ASDF is installed, use it to install the correct version of node and npm for this project

if [ $(command -v asdf) &> /dev/null ]; then
    echo_info "Installing node ${NODE_VERSION} with ASDF ..."
    asdf plugin add nodejs
    asdf install nodejs ${NODE_VERSION}
    asdf local nodejs ${NODE_VERSION}
fi
