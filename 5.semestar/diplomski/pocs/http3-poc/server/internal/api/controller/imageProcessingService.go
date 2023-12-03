package controller

import "http3-server-poc/internal/domain/models"

type ImageStoringService interface {
	StoreImagePart(imagePart models.ImagePart) error
}
