#!/bin/sh
set -eu
# Read-only checks; this never creates or removes app records.
base_url="${1:-http://localhost:8081}"
base_url="${base_url%/}"
command -v curl >/dev/null 2>&1 || { echo "curl is required." >&2; exit 1; }
check_status() {
    endpoint="$1"
    expected="$2"
    actual=$(curl --silent --show-error --max-time 15 -o /dev/null -w '%{http_code}' "$base_url$endpoint")
    if [ "$actual" != "$expected" ]; then
        echo "FAIL $endpoint: expected $expected, received $actual" >&2
        exit 1
    fi
    printf 'PASS %s (%s)\n' "$endpoint" "$actual"
}
check_status / 200
check_status /healthz 200
check_status /services 200
check_status /my-bookings 200
check_status /api/services 200
check_status /api/bookings 401
check_status /api/users 401
actual=$(curl --silent --show-error --max-time 15 -o /dev/null -w '%{http_code}' -H "Origin: $base_url" "$base_url/api/services")
[ "$actual" = 200 ] || { echo "FAIL same-origin API request ($actual)" >&2; exit 1; }
echo "PASS same-origin API forwarding"
echo "Docker smoke checks passed."
