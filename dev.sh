export PORT=8081
login() {
  curl -s -X POST localhost:$PORT/auth/login -H 'Content-Type: application/json' \
    -d "{\"email\":\"$1\",\"password\":\"$2\"}" | jq -r .accessToken
}
export ADMIN=$(login admin@booking.local Admin12345)
export TOKEN=$(login mamun@bd.com secret123)
echo "port=$PORT admin=${ADMIN:0:12}... token=${TOKEN:0:12}..."
