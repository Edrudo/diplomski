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
	out, _ := os.Create(fmt.Sprintf("/Users/eduardduras/Desktop/faks/diplomski/5.semestar/diplomski/pocs/http3-poc/server/images/%s.jpg", imageHash))
	defer out.Close()

	var opts jpeg.Options
	opts.Quality = 1

	// write into a file
	if _, err := out.Write(imgBytes); err != nil {
		panic(err)
	}

	return nil
}
