package inmemorycache

import (
	"http3-server-poc/internal/domain/models"

	"github.com/pkg/errors"
)

// TODO should I sync this with mutex?
var hashImagePartsMap = make(map[string][]models.ImagePart)

type ImagePartsRepository struct{}

func NewImagePartsRepository() *ImagePartsRepository {
	return &ImagePartsRepository{}
}

func (r *ImagePartsRepository) DoesImagePartListExist(imageHash string) (bool, error) {
	_, exists := hashImagePartsMap[imageHash]
	return exists, nil
}

func (r *ImagePartsRepository) DeleteImagePartList(imageHash string) error {
	delete(hashImagePartsMap, imageHash)
	return nil
}

func (r *ImagePartsRepository) StoreImagePart(imagePart models.ImagePart) error {
	imageParts, ok := hashImagePartsMap[imagePart.ImageHash]
	if !ok {
		imageParts = make([]models.ImagePart, 0)
	}

	imageParts = append(imageParts, imagePart)
	return nil
}

func (r *ImagePartsRepository) GetNumberOfPartsInStorage(imageHash string) (int, error) {
	imageParts, ok := hashImagePartsMap[imageHash]
	if !ok {
		return 0, nil
	}

	return len(imageParts), nil
}

func (r *ImagePartsRepository) GetImagePartsList(imageHash string) ([]models.ImagePart, error) {
	imageParts, ok := hashImagePartsMap[imageHash]
	if !ok {
		return nil, errors.New("no list found")
	}

	return imageParts, nil
}
