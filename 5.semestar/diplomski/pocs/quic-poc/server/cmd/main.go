package main

import (
	"quic-poc/cmd/config"
	"quic-poc/internal/quicserver"
)

const addr = "localhost:4242"

func main() {
	quicServer := quicserver.NewQuicServer(config.QuicConfig{
		Address: addr,
	})

	err := quicServer.Start()
	if err != nil {
		panic(err)
	}
}
