package filesystem

import (
	"fmt"
	"image/jpeg"
	"os"
)

type ImageStore struct{}

func NewImageStore() *ImageStore {
	return &ImageStore{}
}

func (i *ImageStore) StoreImage(imageHash string, imgBytes []byte) error {
	out, err := os.Create(
		fmt.Sprintf(
			// TODO change this path to be configurable, remove only .jpg extension
			"./images/%s.jpg",
			imageHash,
		),
	)
	defer out.Close()
	if err != nil {
		panic(err)
	}

	var opts jpeg.Options
	opts.Quality = 1

	// write into a file
	if _, err := out.Write(imgBytes); err != nil {
		// TODO add logging
		panic(err)
	}

	return nil
}
