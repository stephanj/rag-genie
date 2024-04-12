#!/bin/bash -eu

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

if [ $# -ne 1 ]; then
    echo_warn "Usage: $0 <java_version>"
    exit 1
fi

JAVA_VERSION=$1

if [ $(command -v asdf) &> /dev/null ]; then
    echo "Installing java ${JAVA_VERSION} with ASDF ..."
    asdf plugin add java
    asdf install java latest:zulu-${JAVA_VERSION}
    asdf local java latest:zulu-${JAVA_VERSION}
elif [ $(command -v sdk) &> /dev/null ]; then
    echo "Installing java ${JAVA_VERSION} with SDKMAN ..."
    sdk install java ${JAVA_VERSION}-zulu
    sdk use java ${JAVA_VERSION}-zulu
    sdk env init
elif [ $(command -v brew) &> /dev/null ]; then
    echo "Installing java ${JAVA_VERSION} with Homebrew (global to your system)"
    echo "We advise you to use ASDF or SDKMAN to manage multiple java versions"
    read -r -p "Are you sure? [y/N] " response
    case "$response" in
        [yY][eE][sS]|[yY])
            ;;
        *)
            exit 1
            ;;
    esac
    brew install openjdk@${JAVA_VERSION}
    brew link --force openjdk@${JAVA_VERSION}
else
    echo "No package manager found to install java ${JAVA_VERSION}"
    exit 1
fi

if [ $(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | grep "${JAVA_VERSION}.") ]; then
    echo "Java ${JAVA_VERSION} installed in version $(java -version 2>&1 | awk -F '"' '/version/ {print $2}')"
    exit 0
else
    echo "Java ${JAVA_VERSION} not installed"
    exit 1
fi
