package inmemorycache

import (
	"sync"

	"http3-server-poc/internal/domain/models"
)

type ImagePartsRepository struct {
	mu                sync.Mutex
	hashImagePartsMap map[string][]models.ImagePart
}

func NewImagePartsRepository() *ImagePartsRepository {
	return &ImagePartsRepository{
		hashImagePartsMap: map[string][]models.ImagePart{},
	}
}

func (r *ImagePartsRepository) DoesImagePartListExist(imageHash string) (bool, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	_, exists := r.hashImagePartsMap[imageHash]
	return exists, nil
}

func (r *ImagePartsRepository) DeleteImagePartList(imageHash string) error {
	r.mu.Lock()
	defer r.mu.Unlock()
	delete(r.hashImagePartsMap, imageHash)
	return nil
}

func (r *ImagePartsRepository) StoreImagePart(imagePart models.ImagePart) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	imageParts, ok := r.hashImagePartsMap[imagePart.ImageHash]
	if !ok {
		imageParts = make([]models.ImagePart, 0)
	}

	imageParts = append(imageParts, imagePart)
	r.hashImagePartsMap[imagePart.ImageHash] = imageParts
	return nil
}

func (r *ImagePartsRepository) GetNumberOfPartsInStorage(imageHash string) (int, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	imageParts, ok := r.hashImagePartsMap[imageHash]
	if !ok {
		return 0, nil
	}

	return len(imageParts), nil
}

func (r *ImagePartsRepository) GetImagePartsList(imageHash string) ([]models.ImagePart, bool, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	imageParts, ok := r.hashImagePartsMap[imageHash]

	return imageParts, ok, nil
}
