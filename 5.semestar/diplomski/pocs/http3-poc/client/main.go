package main

import (
	"bufio"
	"bytes"
	"context"
	"crypto/sha256"
	"crypto/tls"
	"crypto/x509"
	"encoding/base64"
	"encoding/json"
	"errors"
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
	"sync"

	"github.com/quic-go/quic-go"
	"github.com/quic-go/quic-go/http3"
	"github.com/quic-go/quic-go/logging"
	"github.com/quic-go/quic-go/qlog"

	"http3-client-poc/internal/testdata"
	"http3-client-poc/internal/utils"
)

type ImagePart struct {
	ImageHash  string `json:"imageHash"`
	PartNumber int    `json:"partNumber"`
	TotalParts int    `json:"totalParts"`
	PartData   []byte `json:"partData"`
}

func main() {
	// extracting image path from args
	args := os.Args
	if len(args) < 3 {
		log.Fatal(
			errors.New(
				"arguments needed for the program: " +
					"\t - url where image will be sent" +
					"\t - at least one path to image that needs to be sent",
			),
		)
	}
	addr := args[1]
	imagePaths := args[2:]
	imagePartSize := 1400

	logger := utils.DefaultLogger
	logger.SetLogLevel(utils.LogLevelError)

	hashGenerator := sha256.New()

	roundTripper := initilizeRoundTripper()
	defer roundTripper.Close()
	hclient := &http.Client{
		Transport: roundTripper,
	}

	for _, imagePath := range imagePaths {
		image, err := os.ReadFile(imagePath)
		if err != nil {
			panic(err)
		}

		imageParts := make([]ImagePart, 0)
		numImageParts := len(image) / imagePartSize
		if len(imageParts)%1450 > 0 {
			numImageParts++
		}

		hashGenerator.Write(image)
		calculatedHash := base64.URLEncoding.EncodeToString(hashGenerator.Sum(nil))

		var wg sync.WaitGroup
		wg.Add(numImageParts)
		if err != nil {
			panic(err)
		}
		for i := 0; i < numImageParts; i++ {
			go func(partNumber int) {
				bdy, err := json.Marshal(
					ImagePart{
						ImageHash:  calculatedHash,
						PartNumber: partNumber + 1,
						TotalParts: numImageParts,
						PartData:   image[partNumber*imagePartSize : (partNumber+1)*imagePartSize],
					},
				)
				if err != nil {
					panic(err)
				}

				for true {
					logger.Infof("GET %s", addr)
					rsp, err := hclient.Post(addr, "application/json", bytes.NewBuffer(bdy))
					if err == nil {
						logger.Infof("Got response for %s: %#v", addr, rsp)
						wg.Done()
						break
					}
					logger.Errorf(err.Error())
				}
			}(i)
		}
		wg.Wait()

	}

}

func initilizeRoundTripper() *http3.RoundTripper {
	insecure := flag.Bool("insecure", true, "skip certificate verification")
	enableQlog := flag.Bool("qlog", false, "output a qlog (in the same directory)")
	flag.Parse()

	pool, err := x509.SystemCertPool()
	if err != nil {
		log.Fatal(err)
	}
	testdata.AddRootCA(pool)

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
