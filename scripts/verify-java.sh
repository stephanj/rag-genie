#!/bin/bash -u

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

if [ $# -ne 1 ]; then
    echo_warn "Usage: $0 <java_version>"
    exit 1
fi

JAVA_VERSION=$1

if ! command -v java &> /dev/null; then
    echo_error "Java is not installed"
    exit 1
fi

CURRENT_JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk '{print $1}')

if [[ ! $CURRENT_JAVA_VERSION =~ ^$JAVA_VERSION ]]; then
    echo_error "Java is installed in version ${CURRENT_JAVA_VERSION} but ${JAVA_VERSION} is required"
    exit 1
fi

echo_info "Java is installed in version ${CURRENT_JAVA_VERSION} and is compatible with ${JAVA_VERSION}"

exit 0
