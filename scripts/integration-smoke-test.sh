set -euo pipefail

spa_base_url="${SPA_BASE_URL:-http://localhost:4200}"
api_url="$spa_base_url/api/v1/urls"
shortener_base_url="${SHORTENER_BASE_URL:-http://localhost:8080/sht}"
original_url="https://example.com/ci-smoke-test"

curl --fail --silent --show-error "$spa_base_url/" >/dev/null

request=$(jq -n --arg originalUrl "$original_url" '{originalUrl: $originalUrl}')
response=$(curl --fail --silent --show-error \
  --header 'Content-Type: application/json' \
  --data "$request" \
  "$api_url")

short_code=$(jq --exit-status --raw-output '.shortCode | select(test("^[A-Za-z0-9]{6}$"))' <<< "$response")
short_url=$(jq --exit-status --raw-output '.shortUrl' <<< "$response")
expected_short_url="${shortener_base_url%/}/$short_code"

if [[ "$short_url" != "$expected_short_url" ]]; then
  printf 'Unexpected short URL: %s\nExpected: %s\n' "$short_url" "$expected_short_url" >&2
  exit 1
fi

headers=$(mktemp)
trap 'rm -f "$headers"' EXIT
status=$(curl --silent --show-error --dump-header "$headers" --output /dev/null \
  --write-out '%{http_code}' "$short_url")
location=$(awk 'tolower($1) == "location:" { gsub("\r", ""); print $2 }' "$headers")

if [[ "$status" != 302 || "$location" != "$original_url" ]]; then
  printf 'Unexpected redirect: status=%s location=%s\n' "$status" "$location" >&2
  exit 1
fi

recent=$(curl --fail --silent --show-error "$api_url/recent")
jq --exit-status --arg code "$short_code" 'any(.[]; .shortCode == $code)' <<< "$recent" >/dev/null

click_count=0
for attempt in $(seq 1 15); do
  stats=$(curl --fail --silent --show-error "$api_url/$short_code/stats")
  click_count=$(jq --exit-status --raw-output '.totalClicks' <<< "$stats")
  if (( click_count >= 1 )); then
    break
  fi
  sleep 1
done

if (( click_count < 1 )); then
  printf 'Asynchronous click was not recorded for %s\n' "$short_code" >&2
  exit 1
fi

printf 'Integration smoke test passed for %s -> %s\n' "$short_url" "$original_url"