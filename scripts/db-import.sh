#!/bin/bash -u

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

case "$#" in
  1)
    echo_info "Drop local database and reimport $1"
    ;;
  *)
    echo_info "Usage: task db:import -- <dump file path>"
    exit 1
    ;;
esac

export PGPASSWORD='mysecretpassword'

# Connect to PostgreSQL and drop the schema
psql -h localhost -p 5432 -U postgres -d postgres -c "DROP SCHEMA IF EXISTS public CASCADE;"

# Create the 'public' schema
psql -h localhost -p 5432 -U postgres -d postgres -c "CREATE SCHEMA public;"

# Restore the dump
pg_restore -h localhost -p 5432 -U postgres -d postgres -v --format=c --no-owner --schema=public --no-acl $1

unset PGPASSWORD
