package filesystem

import (
	"bytes"
	"fmt"
	"image"
	"image/jpeg"
	"log"
	"os"
)

type ImageStore struct{}

func NewImageStore() *ImageStore {
	return &ImageStore{}
}

func (i *ImageStore) StoreImage(imageHash string, imgBytes []byte) error {
	img, _, err := image.Decode(bytes.NewReader(imgBytes))
	if err != nil {
		log.Fatalln(err)
	}
	out, _ := os.Create(fmt.Sprintf("./images/%s.jpg", imageHash))
	defer out.Close()

	var opts jpeg.Options
	opts.Quality = 1

	err = jpeg.Encode(out, img, &opts)
	if err != nil {
		log.Println(err)
	}

	return nil
}
