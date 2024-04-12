#!/bin/bash -u

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

export PGPASSWORD='mysecretpassword'

FILE=local-db-$(date +"%Y-%m-%dT%H:%M:%S%z").dump

# Connect to PostgreSQL and dump db
pg_dump -h localhost -p 5432 -U postgres -d postgres -F c -f ${FILE}

echo_info "DB dumped to ${FILE}"

unset PGPASSWORD
