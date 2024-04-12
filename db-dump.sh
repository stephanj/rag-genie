#!/bin/bash

echo "DEPRECATED: Use 'task db:dump' instead"

export PGPASSWORD='mysecretpassword'

# Connect to PostgreSQL and dump db
pg_dump -h localhost -p 5432 -U postgres -d postgres -F c -f dump_file_name.dump

unset PGPASSWORD
