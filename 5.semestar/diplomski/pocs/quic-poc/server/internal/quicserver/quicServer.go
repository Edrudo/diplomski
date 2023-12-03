package quicserver

import (
	"context"
	"crypto/rand"
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"encoding/pem"
	"math/big"

	"quic-poc/cmd/config"

	"github.com/quic-go/quic-go"
)

type QuicServer struct {
	tlsConfig         *tls.Config
	quicConfig        config.QuicConfig
	connectionHandler *ConnectionHandler
}

func NewQuicServer(quicConfig config.QuicConfig) *QuicServer {
	return &QuicServer{
		tlsConfig:         generateTLSConfig(),
		quicConfig:        quicConfig,
		connectionHandler: &ConnectionHandler{},
	}
}

func (s *QuicServer) Start() error {
	listener, err := quic.ListenAddr(s.quicConfig.Address, s.tlsConfig, nil)
	if err != nil {
		return err
	}

	for true {
		conn, err := listener.Accept(context.Background())
		if err != nil {
			return err
		}

		go s.connectionHandler.handleConnection(conn)
	}

	return nil
}

// Set up a bare-bones TLS config for the server
func generateTLSConfig() *tls.Config {
	key, err := rsa.GenerateKey(rand.Reader, 1024)
	if err != nil {
		panic(err)
	}
	template := x509.Certificate{SerialNumber: big.NewInt(1)}
	certDER, err := x509.CreateCertificate(rand.Reader, &template, &template, &key.PublicKey, key)
	if err != nil {
		panic(err)
	}
	keyPEM := pem.EncodeToMemory(&pem.Block{Type: "RSA PRIVATE KEY", Bytes: x509.MarshalPKCS1PrivateKey(key)})
	certPEM := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: certDER})

	tlsCert, err := tls.X509KeyPair(certPEM, keyPEM)
	if err != nil {
		panic(err)
	}
	return &tls.Config{
		Certificates: []tls.Certificate{tlsCert},
		NextProtos:   []string{"quic-echo-example"},
	}
}
