package main

import (
	"bytes"
	"encoding/base64"
	"encoding/json"
	"errors"
	"log"
	"os"
	"sync"

	"http3-client-poc/cmd/bootstrap"
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

	// initlize client
	client, roundTripper := bootstrap.NewClient()
	defer func() {
		err := roundTripper.Close()
		if err != nil {
			client.Logger.Errorf("Error closing round tripper: %s", err)
		}
	}()

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

		client.HashGenerator.Write(image)
		calculatedHash := base64.URLEncoding.EncodeToString(client.HashGenerator.Sum(nil))

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
					client.Logger.Infof("GET %s", addr)
					rsp, err := client.HttpClient.Post(addr, "application/json", bytes.NewBuffer(bdy))
					if err == nil {
						client.Logger.Infof("Got response for %s: %#v", addr, rsp)
						wg.Done()
						break
					}
					client.Logger.Errorf(err.Error())
				}
			}(i)
		}
		wg.Wait()
	}

}
