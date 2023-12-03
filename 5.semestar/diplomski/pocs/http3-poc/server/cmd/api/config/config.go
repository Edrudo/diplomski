package config

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"encoding/pem"
	"math/big"
)

var Cfg Config

type Config struct {
	ServerConfig Http3ServerConfig
	OuicConfig   QuicConfig
}

type Http3ServerConfig struct {
	Http3ServerUrl  string `split_words:"true" required:"true" default:"localhost"`
	Http3ServerPort int    `split_words:"true" required:"true" default:"4242"`
}
type QuicConfig struct {
	HandshakeIdleTimeoutMs uint `split_words:"true"`
	MaxIdleTimeoutMs       uint `split_words:"true"`
	KeepAlivePeriod        uint `split_words:"true"`
}

// GenerateTLSConfig sets up a bare-bones TLS config for the server
func GenerateTLSConfig() *tls.Config {
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