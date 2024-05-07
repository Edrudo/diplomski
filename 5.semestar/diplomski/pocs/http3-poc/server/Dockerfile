FROM golang:1.21.3

# Set destination for COPY
WORKDIR /app

# Download Go modules
COPY . .
RUN go mod download

# Copy the source code. Note the slash at the end, as explained in
# https://docs.docker.com/engine/reference/builder/#copy
COPY *.go ./

ENV APP_FULL_NAME="server"
ENV APP_NAMESPACE="master-thesis"
ENV QUIC_CONFIG_HANDSHAKE_IDLE_TIMEOUT_MS=1000
ENV QUIC_CONFIG_KEEP_ALIVE_PERIOD=1000
ENV QUIC_CONFIG_MAX_IDLE_TIMEOUT_MS=1000
ENV SERVER_CONFIG_HTTP3_SERVER_PORT=4219
ENV SERVER_CONFIG_HTTP3_SERVER_URL="http3-server-url"

# Build
RUN go build -o /diplomski-server

EXPOSE 4219
# Run
CMD ["/diplomski-server"]
