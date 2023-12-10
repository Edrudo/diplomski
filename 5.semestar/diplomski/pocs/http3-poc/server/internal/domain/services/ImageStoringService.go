package services

import (
	"http3-server-poc/internal/domain/models"

	"github.com/pkg/errors"
)

type ImageStoringService struct {
	imagePartsRepository      ImagePartsRepository
	imageProcessingEngineChan chan string
}

func NewImageStoringService(
	imagePartsRepository ImagePartsRepository,
	imageProcessingEngineChan chan string,
) *ImageStoringService {
	return &ImageStoringService{
		imagePartsRepository:      imagePartsRepository,
		imageProcessingEngineChan: imageProcessingEngineChan,
	}
}

func (i *ImageStoringService) StoreImagePart(imagePart models.ImagePart) error {
	errctx := func(err error) error {
		return errors.WithMessagef(err, "image storing i, store image part")
	}

	// check if this image part already exists in stoage
	imageParts, ok, err := i.imagePartsRepository.GetImagePartsList(imagePart.ImageHash)
	if err != nil {
		return errctx(err)
	}

	// if it exists then do a noop
	if ok {
		for _, part := range imageParts {
			if part.PartNumber == imagePart.PartNumber {
				return nil
			}
		}
	}

	// otherwise store the image part
	err = i.imagePartsRepository.StoreImagePart(imagePart)
	if err != nil {
		return errctx(err)
	}

	if len(imageParts)+1 == imagePart.TotalParts {
		i.imageProcessingEngineChan <- imagePart.ImageHash
	}

	return nil
}
