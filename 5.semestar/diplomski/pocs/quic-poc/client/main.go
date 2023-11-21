package main

import (
	"context"
	"crypto/tls"
	"fmt"
	"io"

	"github.com/quic-go/quic-go"
)

const addr = "localhost:4242"

const message = "foobar"

func main() {
	tlsConf := &tls.Config{
		InsecureSkipVerify: true,
		NextProtos:         []string{"quic-echo-example"},
	}
	conn, err := quic.DialAddr(context.Background(), addr, tlsConf, nil)
	if err != nil {
		panic(err)
	}

	stream, err := conn.OpenStreamSync(context.Background())
	if err != nil {
		panic(err)
	}

	fmt.Printf("Client: Sending '%s'\n", message)
	_, err = stream.Write([]byte(message))
	if err != nil {
		panic(err)
	}

	buf := make([]byte, len(message))
	_, err = io.ReadFull(stream, buf)
	if err != nil {
		panic(err)
	}
	fmt.Printf("Client: Got '%s'\n", buf)
}
