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

	// if it's the first part, check if the list exists and delete it if it does
	if imagePart.PartNumber == 1 {
		imagePartListExists, err := i.imagePartsRepository.DoesImagePartListExist(imagePart.ImageHash)
		if err != nil {
			return errctx(err)
		}
		if imagePartListExists {
			err = i.imagePartsRepository.DeleteImagePartList(imagePart.ImageHash)
			if err != nil {
				return errctx(err)
			}
		}
	}

	err := i.imagePartsRepository.StoreImagePart(imagePart)
	if err != nil {
		return errctx(err)
	}

	numOfPartsInStorage, err := i.imagePartsRepository.GetNumberOfPartsInStorage(imagePart.ImageHash)
	if err != nil {
		return errctx(err)
	}

	if numOfPartsInStorage == imagePart.TotalParts {
		i.imageProcessingEngineChan <- imagePart.ImageHash
	}

	return nil
}
