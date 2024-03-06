package bootstrap

import (
	"bufio"
	"context"
	"crypto/sha256"
	"crypto/tls"
	"crypto/x509"
	"flag"
	"fmt"
	"hash"
	"log"
	"net/http"
	"os"

	"github.com/quic-go/quic-go"
	"github.com/quic-go/quic-go/http3"
	"github.com/quic-go/quic-go/logging"
	"github.com/quic-go/quic-go/qlog"

	"http3-client-poc/cmd/bootstrap/tlsconfig"
	"http3-client-poc/internal/utils"
)

type Client struct {
	HashGenerator hash.Hash
	HttpClient    *http.Client
	roundTriper   *http3.RoundTripper
	Logger        utils.Logger
}

func NewClient() (*Client, *http3.RoundTripper) {
	roundTripper := initilizeRoundTripper()
	httpClient := &http.Client{
		Transport: roundTripper,
	}

	logger := utils.DefaultLogger
	logger.SetLogLevel(utils.LogLevelError)

	return &Client{
		HashGenerator: sha256.New(),
		roundTriper:   roundTripper,
		HttpClient:    httpClient,
		Logger:        logger,
	}, roundTripper
}

func initilizeRoundTripper() *http3.RoundTripper {
	insecure := flag.Bool("insecure", false, "skip certificate verification")
	enableQlog := flag.Bool("qlog", false, "output a qlog (in the same directory)")
	flag.Parse()

	pool, err := x509.SystemCertPool()
	if err != nil {
		log.Fatal(err)
	}
	tlsconfig.AddRootCA(pool)

	var qconf quic.Config
	if *enableQlog {
		qconf.Tracer = func(
			ctx context.Context,
			p logging.Perspective,
			connID quic.ConnectionID,
		) *logging.ConnectionTracer {
			filename := fmt.Sprintf("client_%s.qlog", connID)
			f, err := os.Create(filename)
			if err != nil {
				log.Fatal(err)
			}
			log.Printf("Creating qlog file %s.\n", filename)
			return qlog.NewConnectionTracer(utils.NewBufferedWriteCloser(bufio.NewWriter(f), f), p, connID)
		}
	}

	return &http3.RoundTripper{
		TLSClientConfig: &tls.Config{
			RootCAs:            pool,
			InsecureSkipVerify: *insecure,
		},
		QuicConfig: &qconf,
	}
}
