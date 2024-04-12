#!/bin/bash -u

# shellcheck source=scripts/common.sh
source "$(cd -P "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

if [ $(command -v clever &> /dev/null) ]; then
    echo_error "clever is not installed"
    exit 1
fi

CURRENT_CLEVER_VERSION=$(clever --version 2>&1)

echo_info "Clever is installed in version ${CURRENT_CLEVER_VERSION}"
