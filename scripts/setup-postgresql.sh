#!/bin/bash -eu

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

if [ $# -ne 1 ]; then
    echo_warn "Usage: $0 <postgresql_version>"
    exit 1
fi

POSTGRES_VERSION=$1

if [ $(command -v asdf) &> /dev/null ]; then
    echo_info "Installing postgresql ${POSTGRES_VERSION} with ASDF ..."
    asdf plugin add postgres
    asdf install postgres ${POSTGRES_VERSION}
    asdf local postgres ${POSTGRES_VERSION}
elif [ $(command -v brew) &> /dev/null ]; then
    POSTGRESQL_MAJOR_VERSION=$(echo ${POSTGRES_VERSION} | cut -d'.' -f1)
    echo_info "Installing postgresql ${POSTGRESQL_MAJOR_VERSION} with Homebrew (global to your system)"
    echo_warn "We advise you to use ASDF to manage multiple postgresql versions"
    read -r -p "Are you sure? [y/N] " response
    case "$response" in
        [yY][eE][sS]|[yY])
            ;;
        *)
            exit 0
            ;;
    esac
    brew install postgresql@${POSTGRESQL_MAJOR_VERSION}
    brew link --force postgresql@${POSTGRESQL_MAJOR_VERSION}
else
    echo_error "No package manager found to install postgresql ${POSTGRES_VERSION}"
    exit 1
fi

if [ $(psql --version | cut -d' ' -f3 | grep "${POSTGRES_VERSION}") ]; then
    echo_info "PostgreSQL ${POSTGRES_VERSION} installed"
    exit 0
else
    echo_error "PostgreSQL ${POSTGRES_VERSION} not installed"
    exit 1
fi
