FROM golang:1.21.3

# Set destination for COPY
WORKDIR /app

# Download Go modules
COPY . .
RUN go mod download

# Copy the source code. Note the slash at the end, as explained in
# https://docs.docker.com/engine/reference/builder/#copy
COPY *.go ./

# Build
RUN go build -o /diplomski-server

EXPOSE 4219
# Run
CMD ["/diplomski-server"]
