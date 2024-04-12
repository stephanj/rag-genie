#!/bin/bash -u

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

if [ $# -ne 1 ]; then
    echo_warn "Usage: $0 <postgresql_version>"
    exit 1
fi

POSTGRES_VERSION=$1

if [ $(command -v psql &> /dev/null) ]; then
    echo_error "psql is not installed"
    exit 1
fi

if [ $(command -v pg_dump &> /dev/null) ]; then
    echo_error "pg_dump is not installed"
    exit 1
fi

if [ $(command -v pg_restore &> /dev/null) ]; then
    echo_error "pg_restore is not installed"
    exit 1
fi

PSQL_CURRENT_VERSION=$(psql --version | cut -d' ' -f3)
vercomp "${PSQL_CURRENT_VERSION}" "${POSTGRES_VERSION}"
if [ $? -eq 2 ]; then
    echo_error "psql is installed in version ${PSQL_CURRENT_VERSION} but ${POSTGRES_VERSION} is required"
    exit 1
fi

PG_DUMP_CURRENT_VERSION=$(pg_dump --version | cut -d' ' -f3)
vercomp "${PG_DUMP_CURRENT_VERSION}" "${POSTGRES_VERSION}"
if [ $? -eq 2 ]; then
    echo_error "pg_dump is installed in version ${PG_DUMP_CURRENT_VERSION} but ${POSTGRES_VERSION} is required"
    exit 1
fi

PG_RESTORE_CURRENT_VERSION=$(pg_restore --version | cut -d' ' -f3)
vercomp "${PG_RESTORE_CURRENT_VERSION}" "${POSTGRES_VERSION}"
if [ $? -eq 2 ]; then
    echo_error "pg_restore is installed in version ${PG_RESTORE_CURRENT_VERSION} but ${POSTGRES_VERSION} is required"
    exit 1
fi

echo_info "psql ${PSQL_CURRENT_VERSION}, pg_dump ${PG_DUMP_CURRENT_VERSION} and pg_restore ${PG_RESTORE_CURRENT_VERSION} are installed in versions compatible with ${POSTGRES_VERSION}"

exit 0

