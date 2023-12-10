package services

import "http3-server-poc/internal/domain/models"

type ImagePartsRepository interface {
	DoesImagePartListExist(imageHash string) (bool, error)
	DeleteImagePartList(imageHash string) error
	StoreImagePart(imagePart models.ImagePart) error
	GetNumberOfPartsInStorage(imageHash string) (int, error)
	GetImagePartsList(imageHash string) ([]models.ImagePart, bool, error)
}
