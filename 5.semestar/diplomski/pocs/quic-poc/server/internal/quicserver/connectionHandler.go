package quicserver

import (
	"context"
	"fmt"

	"github.com/quic-go/quic-go"
)

type ConnectionHandler struct{}

func (c *ConnectionHandler) handleConnection(conn quic.Connection) {
	for true {
		stream, err := conn.AcceptStream(context.Background())
		if err != nil {
			break
		}

		go logMessage(stream)
	}
}

func logMessage(stream quic.Stream) {
	bytes := make([]byte, 1024)

	_, err := stream.Read(bytes)
	if err != nil {
		panic(err)
	}

	fmt.Println(string(bytes))

	_, _ = stream.Write(bytes)
}
