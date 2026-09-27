#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
if [ -e .env ]; then
    echo "Keeping your existing .env unchanged."
    exit 0
fi
command -v openssl >/dev/null 2>&1 || { echo "OpenSSL is required to generate local secrets." >&2; exit 1; }
umask 077
# Do not overwrite a file created by another setup process.
set -C
{
    printf 'POSTGRES_PASSWORD=%s\n' "$(openssl rand -hex 24)"
    printf 'JWT_SECRET=%s\n' "$(openssl rand -hex 48)"
    printf 'FIXLY_PORT=8081\n'
} > .env
echo "Created .env with unique local secrets. This file stays out of Git and images."
echo "Next: docker compose up --build -d --wait"
