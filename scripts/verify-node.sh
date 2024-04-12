#!/bin/bash -u

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

if [ $# -ne 1 ]; then
    echo_warn "Usage: $0 <node_version>"
    exit 1
fi

NODE_VERSION=$1

if command -v node &> /dev/null; then
    CURRENT_NODE_SYSTEM_VERSION=$(node --version 2>&1)

    if [ "${CURRENT_NODE_SYSTEM_VERSION}" != "${NODE_VERSION}" ]; then
        echo_warn "Node version installed by default in your system is ${CURRENT_NODE_SYSTEM_VERSION} but ${NODE_VERSION} is required."
        echo_warn "You should take care to not use it and use only './npm' and './npx' scripts provided by this project."
    fi
fi

if [ ! -f node/node ]; then
    echo_error "Node is not installed in the project"
    exit 1
fi

if [ ! -d node_modules ]; then
    echo_error "Node modules are not installed in the project"
    exit 1
fi

CURRENT_NODE_VERSION=$(node/node --version 2>&1)

if [ "${CURRENT_NODE_VERSION}" != "${NODE_VERSION}" ]; then
    echo_error "Node is installed in the project in version ${CURRENT_NODE_VERSION} but ${NODE_VERSION} is required"
    exit 1
fi

echo_info "Node is installed in version ${CURRENT_NODE_VERSION}"

exit 0
